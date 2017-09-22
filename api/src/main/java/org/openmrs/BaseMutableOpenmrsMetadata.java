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

import java.util.Date;

/**
 * Base superclass for all mutable OpenmrsMetadata.
 * 
 * @since 2.2
 */
public abstract class BaseMutableOpenmrsMetadata extends BaseOpenmrsMetadata implements MutableOpenmrsMetadata {
	
	/**
	 * @see BaseOpenmrsMetadata#getChangedBy()
	 */
	@Override
	public User getChangedBy() {
		return super.getChangedBy();
	}
	
	/**
	 * @see BaseOpenmrsMetadata#setChangedBy(User)
	 */
	@Override
	public void setChangedBy(User changedBy) {
		super.setChangedBy(changedBy);
	}
	
	/**
	 * @see BaseOpenmrsMetadata#getDateChanged()
	 */
	@Override
	public Date getDateChanged() {
		return super.getDateChanged();
	}
	
	/**
	 * @see BaseOpenmrsMetadata#setDateChanged(Date)
	 */
	@Override
	public void setDateChanged(Date dateChanged) {
		super.setDateChanged(dateChanged);
	}
}
