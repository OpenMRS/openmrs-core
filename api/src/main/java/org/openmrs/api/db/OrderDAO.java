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
package org.openmrs.api.db;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.openmrs.*;

/**
 * Order-related database functions
 * <p>
 * This class should never be used directly. It should only be used through the
 * {@link org.openmrs.api.OrderService}
 * 
 * @see org.openmrs.api.OrderService
 */
public interface OrderDAO {
	
	// methods for the OrderType java pojo object
	
	// methods for the Order java pojo object
	
	/**
	 * @see org.openmrs.api.OrderService#saveOrder(org.openmrs.Order, org.openmrs.api.OrderContext)
	 */
	public Order saveOrder(Order order) throws DAOException;
	
	/**
	 * @see org.openmrs.api.OrderService#purgeOrder(Order)
	 */
	public void deleteOrder(Order order) throws DAOException;
	
	/**
	 * @see org.openmrs.api.OrderService#getOrder(Integer)
	 */
	public Order getOrder(Integer orderId) throws DAOException;
	
	/**
	 * @see org.openmrs.api.OrderService#getOrders(org.openmrs.OrderType, java.util.List, java.util.List, java.util.List, java.util.List)
	 */
	public List<Order> getOrders(OrderType orderType, List<Patient> patients, List<Concept> concepts, List<User> orderers,
	        List<Encounter> encounters);
	
	/**
	 * Auto generated method comment
	 * 
	 * @param uuid
	 * @return
	 */
	public Order getOrderByUuid(String uuid);
	
	/**
	 * Delete Obs that references an order
	 */
	public void deleteObsThatReference(Order order);
	
	/**
	 * @see org.openmrs.api.OrderService#getOrderByOrderNumber(java.lang.String)
	 */
	public Order getOrderByOrderNumber(String orderNumber);
	
	/**
	 * Gets the next available order number seed
	 * 
	 * @return the order number seed
	 */
	public Long getNextOrderNumberSeedSequenceValue();
	
	/**
	 * @see org.openmrs.api.OrderService#getActiveOrders(org.openmrs.Patient, org.openmrs.OrderType, org.openmrs.CareSetting, java.util.Date)
	 */
	public List<Order> getActiveOrders(Patient patient, OrderType orderType, CareSetting careSetting, Date asOfDate);
	
	/**
	 * Get care setting by type
	 * 
	 * @param careSettingId
	 * @return the care setting type
	 */
	public CareSetting getCareSetting(Integer careSettingId);
	
	/**
	 * @see org.openmrs.api.OrderService#getCareSettingByUuid(String)
	 */
	public CareSetting getCareSettingByUuid(String uuid);
	
	/**
	 * @see org.openmrs.api.OrderService#getCareSettingByName(String)
	 */
	public CareSetting getCareSettingByName(String name);
	
	/**
	 * @see org.openmrs.api.OrderService#getCareSettings(boolean)
	 */
	public List<CareSetting> getCareSettings(boolean includeRetired);
	
	/**
	 * @See OrderService#getOrderTypeByName
	 */
	public OrderType getOrderTypeByName(String orderTypeName);
	
	/**
	 * @See OrderService#getOrderFrequency
	 */
	public OrderFrequency getOrderFrequency(Integer orderFrequencyId);
	
	/**
	 * @See OrderService#getOrderFrequencyByUuid
	 */
	public OrderFrequency getOrderFrequencyByUuid(String uuid);
	
	/**
	 * @See OrderService#getOrderFrequencies(boolean)
	 */
	List<OrderFrequency> getOrderFrequencies(boolean includeRetired);
	
	/**
	 * @See OrderService#getOrderFrequencies(String, java.util.Locale, boolean, boolean)
	 */
	public List<OrderFrequency> getOrderFrequencies(String searchPhrase, Locale locale, boolean exactLocale,
	        boolean includeRetired);
	
	/**
	 * @see org.openmrs.api.OrderService#saveOrderFrequency(org.openmrs.OrderFrequency)
	 */
	public OrderFrequency saveOrderFrequency(OrderFrequency orderFrequency);
	
	/**
	 * @see org.openmrs.api.OrderService#purgeOrderFrequency(org.openmrs.OrderFrequency)
	 */
	public void purgeOrderFrequency(OrderFrequency orderFrequency);
	
	/**
	 * Checks if an order frequency is being referenced by any order
	 * 
	 * @param orderFrequency the order frequency
	 * @return true if in use, else false
	 */
	public boolean isOrderFrequencyInUse(OrderFrequency orderFrequency);
	
	/**
	 * @See OrderService#getOrderFrequencyByConcept
	 */
	public OrderFrequency getOrderFrequencyByConcept(Concept concept);
}
