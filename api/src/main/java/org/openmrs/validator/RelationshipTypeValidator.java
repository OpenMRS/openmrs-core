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

package org.openmrs.validator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.RelationshipType;
import org.openmrs.annotation.Handler;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * Validates attributes on the {@link RelationshipType} object.
 * 
 * @since 1.10
 */
@Handler(supports = { RelationshipType.class }, order = 50)

public class RelationshipTypeValidator implements Validator {
	
	/** Log for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());
	
	/**
	 * Determines if the command object being submitted is a valid type
	 * 
	 * @see org.springframework.validation.Validator#supports(java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	public boolean supports(Class c) {
		return c.equals(RelationshipType.class);
	}
	
	/**
	 * Checks the form object for any inconsistencies/errors
	 * 
	 * @see org.springframework.validation.Validator#validate(java.lang.Object,
	 *      org.springframework.validation.Errors)
	 * @should 
	 * @should fail validation if nothing is passed into getIsToB() and getIsToA() thus Relationship type cannot be null
	 * @should pass validation if all required fields have proper values
	 */
	 public void validate(Object obj, Errors errors) {
		RelationshipType RelationshipType = (RelationshipType) obj;
		/**
		 *       NOTE:
		 *RelationshipType.aIsToB = A is to B
		 *RelationshipType.bIsToA = B is to A
		 *RelationshipType.aIsToB.required = A is to B name is required
		 *RelationshipType.bIsToA.required = B is to A name is required
		*/
 		if (RelationshipType.getaIsToB() == null || RelationshipType.getaIsToB().equals(""))
			errors.rejectValue("aIsToB", "RelationshipType.aIsToB.required");
			errors.rejectValue("localizedAIsToB.unlocalizedValue", "RelationshipType.aIsToB.required");
 		
 		if (RelationshipType.getbIsToA() == null || RelationshipType.getbIsToA().equals(""))
			errors.rejectValue("bIsToA", "RelationshipType.bIsToA.required");
			errors.rejectValue("localizedBIsToA.unlocalizedValue", "RelationshipType.bIsToA.required");
 		
	 }
}
