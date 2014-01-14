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
package org.openmrs;

import org.apache.commons.lang.StringUtils;
import org.openmrs.api.context.Context;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * Drug
 */
public class Drug extends BaseOpenmrsMetadata implements java.io.Serializable {
	
	public static final long serialVersionUID = 285L;
	
	// Fields
	
	private Integer drugId;
	
	private Boolean combination = false;
	
	private Concept dosageForm;
	
	private Double doseStrength;
	
	private Double maximumDailyDose;
	
	private Double minimumDailyDose;
	
	private Concept route;
	
	private String units;
	
	private Concept concept;
	
	private Set<DrugReferenceMap> drugReferenceMaps;
	
	// Constructors
	
	/** default constructor */
	public Drug() {
	}
	
	/** constructor with id */
	public Drug(Integer drugId) {
		this.drugId = drugId;
	}
	
	// Property accessors
	
	/**
	 * Gets the internal identification number for this drug
	 * 
	 * @return Integer
	 */
	public Integer getDrugId() {
		return this.drugId;
	}
	
	/**
	 * Sets the internal identification number for this drug
	 * 
	 * @param drugId
	 */
	public void setDrugId(Integer drugId) {
		this.drugId = drugId;
	}
	
	/**
	 * Gets the entires concept drug name in the form of CONCEPTNAME (Drug: DRUGNAME)
	 * 
	 * @param locale
	 * @return full drug name (with concept name appended)
	 */
	public String getFullName(Locale locale) {
		if (concept == null)
			return getName();
		else
			return getName() + " (" + concept.getName(locale).getName() + ")";
	}
	
	/**
	 * Gets whether or not this is a combination drug
	 * 
	 * @return Boolean
	 */
	public Boolean isCombination() {
		return this.combination;
	}
	
	public Boolean getCombination() {
		return isCombination();
	}
	
	/**
	 * Sets whether or not this is a combination drug
	 * 
	 * @param combination
	 */
	public void setCombination(Boolean combination) {
		this.combination = combination;
	}
	
	/**
	 * Gets the dose strength of this drug
	 * 
	 * @return Double
	 */
	public Double getDoseStrength() {
		return this.doseStrength;
	}
	
	/**
	 * Sets the dose strength
	 * 
	 * @param doseStrength
	 */
	public void setDoseStrength(Double doseStrength) {
		this.doseStrength = doseStrength;
	}
	
	/**
	 * Gets the units
	 * 
	 * @return String
	 */
	public String getUnits() {
		return this.units;
	}
	
	/**
	 * Sets the units
	 * 
	 * @param units
	 */
	public void setUnits(String units) {
		this.units = units;
	}
	
	/**
	 * Gets the concept this drug is tied to
	 * 
	 * @return Concept
	 */
	public Concept getConcept() {
		return this.concept;
	}
	
	/**
	 * Sets the concept this drug is tied to
	 * 
	 * @param concept
	 */
	public void setConcept(Concept concept) {
		this.concept = concept;
	}
	
	public Concept getDosageForm() {
		return dosageForm;
	}
	
	public void setDosageForm(Concept dosageForm) {
		this.dosageForm = dosageForm;
	}
	
	public Double getMaximumDailyDose() {
		return maximumDailyDose;
	}
	
	public void setMaximumDailyDose(Double maximumDailyDose) {
		this.maximumDailyDose = maximumDailyDose;
	}
	
	public Double getMinimumDailyDose() {
		return minimumDailyDose;
	}
	
	public void setMinimumDailyDose(Double minimumDailyDose) {
		this.minimumDailyDose = minimumDailyDose;
	}
	
	/**
	 * @deprecated moving it to order entry where it belongs.
	 */
	@Deprecated
	public Concept getRoute() {
		return route;
	}
	
	/**
	 * @deprecated moving it to order entry where it belongs.
	 */
	@Deprecated
	public void setRoute(Concept route) {
		this.route = route;
	}
	
	/**
	 * @since 1.5
	 * @see org.openmrs.OpenmrsObject#getId()
	 */
	public Integer getId() {
		
		return getDrugId();
	}
	
	/**
	 * @since 1.5
	 * @see org.openmrs.OpenmrsObject#setId(java.lang.Integer)
	 */
	public void setId(Integer id) {
		setDrugId(id);
		
	}
	
	/**
	 * Convenience method that returns a display name for the drug, defaults to drug.name
	 * 
	 * @return the display name
	 * @since 1.8.5, 1.9.4, 1.10
	 */
	public String getDisplayName() {
		if (StringUtils.isNotBlank(getName()))
			return getName();
		if (getConcept() != null)
			return getConcept().getName().getName();
		return "";
	}
	
	/**
	 * @return Returns the drugReferenceMaps.
	 * @since 1.10
	 */
	public Set<DrugReferenceMap> getDrugReferenceMaps() {
		if (drugReferenceMaps == null) {
			drugReferenceMaps = new HashSet<DrugReferenceMap>();
		}
		return drugReferenceMaps;
	}
	
	/**
	 * @param drugReferenceMaps The drugReferenceMaps to set.
	 * @since 1.10
	 */
	public void setDrugReferenceMaps(Set<DrugReferenceMap> drugReferenceMaps) {
		this.drugReferenceMaps = drugReferenceMaps;
	}
	
	/**
	 * Add the given DrugReferenceMap object to this drug's list of drug reference mappings. If there is
	 * already a corresponding DrugReferenceMap object for this concept, this one will not be added.
	 *
	 * @param drugReferenceMap
	 * @since 1.10
	 *
	 * @should set drug as the drug to which a mapping is being added
	 *
	 * @should should not add duplicate drug reference maps
	 */
	public void addDrugReferenceMap(DrugReferenceMap drugReferenceMap) {
		if (drugReferenceMap != null && !getDrugReferenceMaps().contains(drugReferenceMap)) {
			drugReferenceMap.setDrug(this);
			if (drugReferenceMap.getConceptMapType() == null) {
				drugReferenceMap.setConceptMapType(Context.getConceptService().getDefaultConceptMapType());
			}
			getDrugReferenceMaps().add(drugReferenceMap);
		}
	}
}
