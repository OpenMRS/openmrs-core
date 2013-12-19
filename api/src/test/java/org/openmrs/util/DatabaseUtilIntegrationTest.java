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
import org.openmrs.GlobalProperty;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.DAOException;
import org.openmrs.test.BaseContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

public class DatabaseUtilIntegrationTest extends BaseContextSensitiveTest {
	
	@Autowired
	AdministrationService adminService;
	
	/**
	 * @verifies return concept_id for drug_order_quantity_units
	 * @see DatabaseUtil#getConceptIdForUnits(java.sql.Connection, String)
	 */
	@Test
	public void getConceptIdForUnits_shouldReturnConcept_idForDrug_order_quantity_units() throws Exception {
		adminService.saveGlobalProperty(new GlobalProperty(OpenmrsConstants.GP_ORDER_ENTRY_UNITS_TO_CONCEPTS_MAPPINGS,
		        "mg:5401,drug_order_quantity_units:5403,ounces:5402"));
		Context.flushSession();
		
		Integer conceptId = DatabaseUtil.getConceptIdForUnits(getConnection(), "drug_order_quantity_units");
		
		Assert.assertThat(conceptId, Is.is(5403));
	}
	
	/**
	 * @verifies fail if units is not specified
	 * @see DatabaseUtil#getConceptIdForUnits(java.sql.Connection, String)
	 */
	@Test(expected = DAOException.class)
	public void getConceptIdForUnits_shouldFailIfUnitsIsNotSpecified() throws Exception {
		adminService.saveGlobalProperty(new GlobalProperty(OpenmrsConstants.GP_ORDER_ENTRY_UNITS_TO_CONCEPTS_MAPPINGS,
		        "mg:5401,ounces:5402"));
		Context.flushSession();
		
		DatabaseUtil.getConceptIdForUnits(getConnection(), "drug_order_quantity_units");
	}
}
