/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.api.impl;

import java.util.Locale;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.api.ConceptNameType;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.test.BaseContextSensitiveTest;

/**
 * Unit tests for methods that are specific to the {@link ConceptServiceImpl}. General tests that
 * would span implementations should go on the {@link ConceptService}.
 */
public class ConceptServiceImplTest extends BaseContextSensitiveTest {
	
	/**
	 * @see ConceptServiceImpl#saveConcept(Concept)
	 * @verifies return the concept with new conceptID if creating new concept
	 */
	@Test
	public void saveConcept_shouldReturnTheConceptWithNewConceptIDIfCreatingNewConcept() throws Exception {
		Concept c = new Concept();
		ConceptName fullySpecifiedName = new ConceptName("requires one name min", new Locale("fr", "CA"));
		c.addName(fullySpecifiedName);
		Concept savedC = Context.getConceptService().saveConcept(c);
		Assert.assertNotNull(savedC);
		Assert.assertTrue(savedC.getConceptId() > 0);
	}
	
	/**
	 * @see ConceptServiceImpl#saveConcept(Concept)
	 * @verifies return the concept with same conceptID if updating existing concept
	 */
	
	@Test
	public void saveConcept_shouldReturnTheConceptWithSameConceptIDIfUpdatingExistingConcept() throws Exception {
		Concept c = new Concept();
		ConceptName fullySpecifiedName = new ConceptName("requires one name min", new Locale("fr", "CA"));
		c.addName(fullySpecifiedName);
		Concept savedC = Context.getConceptService().saveConcept(c);
		Assert.assertNotNull(savedC);
		Concept updatedC = Context.getConceptService().saveConcept(c);
		Assert.assertNotNull(updatedC);
		Assert.assertEquals(updatedC.getConceptId(), savedC.getConceptId());
	}
	
	/**
	 * @see ConceptServiceImpl#saveConcept(Concept)
	 * @verifies leave preferred name preferred if set
	 */
	@Test
	public void saveConcept_shouldLeavePreferredNamePreferredIfSet() throws Exception {
		Locale loc = new Locale("fr", "CA");
		ConceptName fullySpecifiedName = new ConceptName("fully specified", loc);
		fullySpecifiedName.setConceptNameType(ConceptNameType.FULLY_SPECIFIED); //be explicit for test case
		ConceptName shortName = new ConceptName("short name", loc);
		shortName.setConceptNameType(ConceptNameType.SHORT); //be explicit for test case
		ConceptName synonym = new ConceptName("synonym", loc);
		synonym.setConceptNameType(null); //synonyms are id'd by a null type
		ConceptName indexTerm = new ConceptName("indexTerm", loc);
		indexTerm.setConceptNameType(ConceptNameType.INDEX_TERM); //synonyms are id'd by a null type
		
		//saveConcept never picks an index term for default, so we'll use it for the test
		indexTerm.setLocalePreferred(true);
		
		Concept c = new Concept();
		java.util.HashSet<ConceptName> allNames = new java.util.HashSet<ConceptName>(4);
		allNames.add(fullySpecifiedName);
		allNames.add(synonym);
		allNames.add(indexTerm);
		allNames.add(shortName);
		c.setNames(allNames);
		
		//The API will throw a validation error because preferred name is an index term
		//ignore it so we can test the set default preferred name  functionality
		try {
			Context.getConceptService().saveConcept(c);
		}
		catch (org.openmrs.api.APIException e) {
			; //ignore it
		}
		Assert.assertNotNull("there's a preferred name", c.getPreferredName(loc));
		Assert.assertTrue("name was explicitly marked preferred", c.getPreferredName(loc).isPreferred());
		Assert.assertEquals("name matches", c.getPreferredName(loc).getName(), indexTerm.getName());
	}
	
	/**
	 * @see ConceptServiceImpl#saveConcept(Concept)
	 * @verifies not set default preferred name to short or index terms
	 */
	@Test
	public void saveConcept_shouldNotSetDefaultPreferredNameToShortOrIndexTerms() throws Exception {
		Locale loc = new Locale("fr", "CA");
		ConceptName shortName = new ConceptName("short name", loc);
		shortName.setConceptNameType(ConceptNameType.SHORT); //be explicit for test case
		ConceptName indexTerm = new ConceptName("indexTerm", loc);
		indexTerm.setConceptNameType(ConceptNameType.INDEX_TERM); //synonyms are id'd by a null type
		
		Concept c = new Concept();
		java.util.HashSet<ConceptName> allNames = new java.util.HashSet<ConceptName>(4);
		allNames.add(indexTerm);
		allNames.add(shortName);
		c.setNames(allNames);
		
		//The API will throw a validation error because preferred name is an index term
		//ignore it so we can test the set default preferred name  functionality
		try {
			Context.getConceptService().saveConcept(c);
		}
		catch (org.openmrs.api.APIException e) {
			; //ignore it
		}
		Assert.assertNull("there's a preferred name", c.getPreferredName(loc));
		Assert.assertFalse("name was explicitly marked preferred", shortName.isPreferred());
		Assert.assertFalse("name was explicitly marked preferred", indexTerm.isPreferred());
	}
	
	/**
	 * @see ConceptServiceImpl#saveConcept(Concept)
	 * @verifies set default preferred name to fully specified first
	 * If Concept.getPreferredName(locale) returns null, saveConcept chooses one.
	 * The default first choice is the fully specified name in the locale.
	 * The default second choice is a synonym in the locale.
	 */
	@Test
	public void saveConcept_shouldSetDefaultPreferredNameToFullySpecifiedFirst() throws Exception {
		Locale loc = new Locale("fr", "CA");
		ConceptName fullySpecifiedName = new ConceptName("fully specified", loc);
		fullySpecifiedName.setConceptNameType(ConceptNameType.FULLY_SPECIFIED); //be explicit for test case
		ConceptName shortName = new ConceptName("short name", loc);
		shortName.setConceptNameType(ConceptNameType.SHORT); //be explicit for test case
		ConceptName synonym = new ConceptName("synonym", loc);
		synonym.setConceptNameType(null); //synonyms are id'd by a null type
		ConceptName indexTerm = new ConceptName("indexTerm", loc);
		indexTerm.setConceptNameType(ConceptNameType.INDEX_TERM); //synonyms are id'd by a null type
		
		Concept c = new Concept();
		java.util.HashSet<ConceptName> allNames = new java.util.HashSet<ConceptName>(4);
		allNames.add(fullySpecifiedName);
		allNames.add(synonym);
		allNames.add(indexTerm);
		allNames.add(shortName);
		c.setNames(allNames);
		Assert.assertFalse("check test assumption - the API didn't automatically set preferred vlag", c
		        .getFullySpecifiedName(loc).isPreferred());
		
		Assert.assertNotNull("Concept is legit, save succeeds", Context.getConceptService().saveConcept(c));
		
		Context.getConceptService().saveConcept(c);
		Assert.assertNotNull("there's a preferred name", c.getPreferredName(loc));
		Assert.assertTrue("name was explicitly marked preferred", c.getPreferredName(loc).isPreferred());
		Assert.assertEquals("name matches", c.getPreferredName(loc).getName(), fullySpecifiedName.getName());
	}
	
	/**
	 * @see ConceptServiceImpl#saveConcept(Concept)
	 * @verifies set default preferred name to a synonym second
	 * If Concept.getPreferredName(locale) returns null, saveConcept chooses one.
	 * The default first choice is the fully specified name in the locale.
	 * The default second choice is a synonym in the locale.
	 */
	@Test
	public void saveConcept_shouldSetDefaultPreferredNameToASynonymSecond() throws Exception {
		Locale loc = new Locale("fr", "CA");
		Locale otherLocale = new Locale("en", "US");
		//Create a fully specified name, but for another locale
		//so the Concept passes validation
		ConceptName fullySpecifiedName = new ConceptName("fully specified", otherLocale);
		fullySpecifiedName.setConceptNameType(ConceptNameType.FULLY_SPECIFIED); //be explicit for test case
		ConceptName shortName = new ConceptName("short name", loc);
		shortName.setConceptNameType(ConceptNameType.SHORT); //be explicit for test case
		ConceptName synonym = new ConceptName("synonym", loc);
		synonym.setConceptNameType(null); //synonyms are id'd by a null type
		ConceptName indexTerm = new ConceptName("indexTerm", loc);
		indexTerm.setConceptNameType(ConceptNameType.INDEX_TERM); //synonyms are id'd by a null type
		
		Concept c = new Concept();
		java.util.HashSet<ConceptName> allNames = new java.util.HashSet<ConceptName>(4);
		allNames.add(indexTerm);
		allNames.add(fullySpecifiedName);
		allNames.add(synonym);
		c.setNames(allNames);
		
		Assert.assertNull("check test assumption - the API hasn't promoted a name to a fully specified name", c
		        .getFullySpecifiedName(loc));
		
		//The API will throw a validation error because there isn't a fully specified name.
		try {
			Context.getConceptService().saveConcept(c);
		}
		catch (org.openmrs.api.APIException e) {
			; //ignore it
		}
		Assert.assertNotNull("there's a preferred name", c.getPreferredName(loc));
		Assert.assertTrue("name was explicitly marked preferred", c.getPreferredName(loc).isPreferred());
		Assert.assertEquals("name matches", c.getPreferredName(loc).getName(), synonym.getName());
		Assert.assertEquals("fully specified name unchanged", c.getPreferredName(otherLocale).getName(), fullySpecifiedName
		        .getName());
		
	}
	
}
