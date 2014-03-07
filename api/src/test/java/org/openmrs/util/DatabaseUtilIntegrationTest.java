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
package org.openmrs.util;

import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.openmrs.api.APIException;
import org.openmrs.test.BaseContextSensitiveTest;

public class DatabaseUtilIntegrationTest extends BaseContextSensitiveTest {
	
	/**
	 * @verifies return concept_id for drug_order_quantity_units
	 * @see DatabaseUtil#getConceptIdForUnits(String)
	 */
	@Test
	public void getConceptIdForUnits_shouldReturnConcept_idForDrug_order_quantity_units() throws Exception {
		DatabaseUtil.createOrderEntryUpgradeFileWithTestData("mg=5401" + "\n" + "drug_order_quantity_units=5403" + "\n"
		        + "ounces=5402");
		
		Integer conceptId = DatabaseUtil.getConceptIdForUnits("drug_order_quantity_units");
		
		Assert.assertThat(conceptId, Is.is(5403));
	}
	
	/**
	 * @verifies fail if units is not specified
	 * @see DatabaseUtil#getConceptIdForUnits(String)
	 */
	@Test(expected = APIException.class)
	public void getConceptIdForUnits_shouldFailIfUnitsIsNotSpecified() throws Exception {
		DatabaseUtil.createOrderEntryUpgradeFileWithTestData("mg=540" + "\n" + "ounces=5402");
		
		DatabaseUtil.getConceptIdForUnits("drug_order_quantity_units");
	}
}
