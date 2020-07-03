/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * FieldType
 */
@Entity
@Table(name = "field_type")
@AttributeOverride(name = "retired", column = @Column(name = "retired", columnDefinition = "boolean default false"))
@AttributeOverride(name = "name", column = @Column(name = "name", length = 50, nullable = false))
public class FieldType extends BaseChangeableOpenmrsMetadata {
	
	public static final long serialVersionUID = 35467L;
	
	// Fields
	
	@Id
	@Column(name = "field_type_id", nullable = false)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "field_type_id_gen")
	@SequenceGenerator(name = "field_type_id_gen", sequenceName = "field_type_field_type_id_seq")
	private Integer fieldTypeId;
	
	@Column(name = "is_set", length = 1, nullable = false)
	private Boolean isSet = false;
	
	// Constructors
	
	/** default constructor */
	public FieldType() {
	}
	
	/** constructor with id */
	public FieldType(Integer fieldTypeId) {
		this.fieldTypeId = fieldTypeId;
	}
	
	// Property accessors
	
	/**
	 * @return Returns the fieldTypeId.
	 */
	public Integer getFieldTypeId() {
		return fieldTypeId;
	}
	
	/**
	 * @param fieldTypeId The fieldTypeId to set.
	 */
	public void setFieldTypeId(Integer fieldTypeId) {
		this.fieldTypeId = fieldTypeId;
	}
	
	/**
	 * @return Returns the isSet.
	 */
	public Boolean getIsSet() {
		return isSet;
	}
	
	/**
	 * @param isSet The isSet to set.
	 */
	public void setIsSet(Boolean isSet) {
		this.isSet = isSet;
	}
	
	/**
	 * @since 1.5
	 * @see org.openmrs.OpenmrsObject#getId()
	 */
	@Override
	public Integer getId() {
		
		return getFieldTypeId();
	}
	
	/**
	 * @since 1.5
	 * @see org.openmrs.OpenmrsObject#setId(java.lang.Integer)
	 */
	@Override
	public void setId(Integer id) {
		setFieldTypeId(id);
		
	}
	
}
