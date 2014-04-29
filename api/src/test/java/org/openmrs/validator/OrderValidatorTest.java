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

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.CareSetting;
import org.openmrs.DrugOrder;
import org.openmrs.Encounter;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.TestOrder;
import org.openmrs.api.OrderService;
import org.openmrs.api.context.Context;
import org.openmrs.order.OrderUtilTest;
import org.openmrs.test.BaseContextSensitiveTest;
import org.openmrs.test.Verifies;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;

/**
 * Tests methods on the {@link OrderValidator} class.
 */
public class OrderValidatorTest extends BaseContextSensitiveTest {
	
	private class SomeDrugOrder extends DrugOrder {}
	
	private OrderService orderService;
	
	@Before
	public void setup() {
		orderService = Context.getOrderService();
	}
	
	/**
	 * @verifies fail validation if order is null
	 * @see OrderValidator#validate(Object, org.springframework.validation.Errors)
	 */
	@Test
	public void validate_shouldFailValidationIfOrderIsNull() throws Exception {
		Errors errors = new BindException(new Order(), "order");
		new OrderValidator().validate(null, errors);
		
		Assert.assertTrue(errors.hasErrors());
		Assert.assertEquals("error.general", ((List<ObjectError>) errors.getAllErrors()).get(0).getCode());
	}
	
	/**
	 * @see {@link OrderValidator#validate(Object,Errors)}
	 */
	@Test
	@Verifies(value = "should fail validation if order and encounter have different patients", method = "validate(Object,Errors)")
	public void validate_shouldFailValidationIfOrderAndEncounterHaveDifferentPatients() throws Exception {
		Order order = new Order();
		order.setConcept(Context.getConceptService().getConcept(88));
		order.setPatient(Context.getPatientService().getPatient(2));
		order.setEncounter(Context.getEncounterService().getEncounter(3));
		order.setOrderer(Context.getProviderService().getProvider(1));
		Errors errors = new BindException(order, "order");
		new OrderValidator().validate(order, errors);
		
		Assert.assertTrue(errors.hasFieldErrors("encounter"));
	}
	
	/**
	 * @verifies fail validation if startDate is before encounter's encounterDatetime
	 * @see OrderValidator#validate(Object, org.springframework.validation.Errors)
	 */
	@Test
	public void validate_shouldFailValidationIfStartDateIsBeforeEncountersEncounterDatetime() throws Exception {
		Date encounterDate = new Date();
		Date orderDate = DateUtils.addDays(encounterDate, -1);
		Encounter encounter = Context.getEncounterService().getEncounter(3);
		encounter.setEncounterDatetime(encounterDate);
		Order order = new Order();
		order.setStartDate(orderDate);
		order.setConcept(Context.getConceptService().getConcept(88));
		order.setPatient(Context.getPatientService().getPatient(2));
		order.setEncounter(encounter);
		order.setOrderer(Context.getProviderService().getProvider(1));
		Errors errors = new BindException(order, "order");
		new OrderValidator().validate(order, errors);
		
		Assert.assertTrue(errors.hasFieldErrors("startDate"));
	}
	
	/**
	 * @see {@link OrderValidator#validate(Object,Errors)}
	 */
	@Test
	@Verifies(value = "should fail validation if voided is null", method = "validate(Object,Errors)")
	public void validate_shouldFailValidationIfVoidedIsNull() throws Exception {
		Order order = new Order();
		order.setVoided(null);
		order.setConcept(Context.getConceptService().getConcept(88));
		order.setPatient(Context.getPatientService().getPatient(2));
		order.setOrderer(Context.getProviderService().getProvider(1));
		
		Errors errors = new BindException(order, "order");
		new OrderValidator().validate(order, errors);
		
		Assert.assertFalse(errors.hasFieldErrors("discontinued"));
		Assert.assertTrue(errors.hasFieldErrors("voided"));
		Assert.assertFalse(errors.hasFieldErrors("concept"));
		Assert.assertFalse(errors.hasFieldErrors("patient"));
		Assert.assertFalse(errors.hasFieldErrors("orderer"));
	}
	
	/**
	 * @see {@link OrderValidator#validate(Object,Errors)}
	 */
	@Test
	@Verifies(value = "should fail validation if concept is null", method = "validate(Object,Errors)")
	public void validate_shouldFailValidationIfConceptIsNull() throws Exception {
		Order order = new Order();
		order.setPatient(Context.getPatientService().getPatient(2));
		order.setOrderer(Context.getProviderService().getProvider(1));
		
		Errors errors = new BindException(order, "order");
		new OrderValidator().validate(order, errors);
		
		Assert.assertFalse(errors.hasFieldErrors("discontinued"));
		Assert.assertTrue(errors.hasFieldErrors("concept"));
		Assert.assertFalse(errors.hasFieldErrors("patient"));
		Assert.assertFalse(errors.hasFieldErrors("orderer"));
	}
	
	/**
	 * @see {@link OrderValidator#validate(Object,Errors)}
	 */
	@Test
	@Verifies(value = "should fail validation if patient is null", method = "validate(Object,Errors)")
	public void validate_shouldFailValidationIfPatientIsNull() throws Exception {
		Order order = new Order();
		order.setConcept(Context.getConceptService().getConcept(88));
		order.setOrderer(Context.getProviderService().getProvider(1));
		
		Errors errors = new BindException(order, "order");
		new OrderValidator().validate(order, errors);
		
		Assert.assertFalse(errors.hasFieldErrors("discontinued"));
		Assert.assertFalse(errors.hasFieldErrors("concept"));
		Assert.assertTrue(errors.hasFieldErrors("patient"));
		Assert.assertFalse(errors.hasFieldErrors("orderer"));
	}
	
	/**
	 * @see {@link OrderValidator#validate(Object,Errors)}
	 */
	@Test
	@Verifies(value = "should fail validation if orderer is null", method = "validate(Object,Errors)")
	public void validate_shouldFailValidationIfOrdererIsNull() throws Exception {
		Order order = new Order();
		order.setConcept(Context.getConceptService().getConcept(88));
		order.setPatient(Context.getPatientService().getPatient(2));
		
		Errors errors = new BindException(order, "order");
		new OrderValidator().validate(order, errors);
		
		Assert.assertFalse(errors.hasFieldErrors("discontinued"));
		Assert.assertFalse(errors.hasFieldErrors("concept"));
		Assert.assertTrue(errors.hasFieldErrors("orderer"));
		Assert.assertFalse(errors.hasFieldErrors("patient"));
	}
	
	/**
	 * @see {@link OrderValidator#validate(Object,Errors)}
	 */
	@Test
	@Verifies(value = "should fail validation if encounter is null", method = "validate(Object,Errors)")
	public void validate_shouldFailValidationIfEncounterIsNull() throws Exception {
		Order order = new Order();
		order.setConcept(Context.getConceptService().getConcept(88));
		order.setPatient(Context.getPatientService().getPatient(2));
		order.setEncounter(null);
		
		Errors errors = new BindException(order, "order");
		new OrderValidator().validate(order, errors);
		
		Assert.assertTrue(errors.hasFieldErrors("encounter"));
	}
	
	/**
	 * @see {@link OrderValidator#validate(Object,Errors)}
	 */
	@Test
	@Verifies(value = "should fail validation if urgency is null", method = "validate(Object,Errors)")
	public void validate_shouldFailValidationIfUrgencyIsNull() throws Exception {
		Order order = new Order();
		order.setConcept(Context.getConceptService().getConcept(88));
		order.setPatient(Context.getPatientService().getPatient(2));
		order.setUrgency(null);
		
		Errors errors = new BindException(order, "order");
		new OrderValidator().validate(order, errors);
		
		Assert.assertTrue(errors.hasFieldErrors("urgency"));
	}
	
	/**
	 * @see {@link OrderValidator#validate(Object,Errors)}
	 */
	@Test
	@Verifies(value = "should fail validation if action is null", method = "validate(Object,Errors)")
	public void validate_shouldFailValidationIfActionIsNull() throws Exception {
		Order order = new Order();
		order.setConcept(Context.getConceptService().getConcept(88));
		order.setPatient(Context.getPatientService().getPatient(2));
		order.setAction(null);
		
		Errors errors = new BindException(order, "order");
		new OrderValidator().validate(order, errors);
		
		Assert.assertTrue(errors.hasFieldErrors("action"));
	}
	
	/**
	 * @see {@link OrderValidator#validate(Object,Errors)}
	 */
	@Test
	@Verifies(value = "should fail validation if startDate after dateStopped", method = "validate(Object,Errors)")
	public void validate_shouldFailValidationIfStartDateAfterDateStopped() throws Exception {
		Order order = new Order();
		order.setConcept(Context.getConceptService().getConcept(88));
		order.setPatient(Context.getPatientService().getPatient(2));
		order.setOrderer(Context.getProviderService().getProvider(1));
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) - 1);
		order.setStartDate(new Date());
		OrderUtilTest.setDateStopped(order, cal.getTime());
		
		Errors errors = new BindException(order, "order");
		new OrderValidator().validate(order, errors);
		
		Assert.assertTrue(errors.hasFieldErrors("startDate"));
		Assert.assertTrue(errors.hasFieldErrors("dateStopped"));
	}
	
	/**
	 * @see {@link OrderValidator#validate(Object,Errors)}
	 */
	@Test
	@Verifies(value = "should fail validation if startDate after autoExpireDate", method = "validate(Object,Errors)")
	public void validate_shouldFailValidationIfStartDateAfterAutoExpireDate() throws Exception {
		Order order = new Order();
		order.setConcept(Context.getConceptService().getConcept(88));
		order.setPatient(Context.getPatientService().getPatient(2));
		order.setOrderer(Context.getProviderService().getProvider(1));
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) - 1);
		order.setStartDate(new Date());
		order.setAutoExpireDate(cal.getTime());
		
		Errors errors = new BindException(order, "order");
		new OrderValidator().validate(order, errors);
		
		Assert.assertTrue(errors.hasFieldErrors("startDate"));
		Assert.assertTrue(errors.hasFieldErrors("autoExpireDate"));
	}
	
	/**
	 * @verifies fail validation if scheduledDate is null when urgency is ON_SCHEDULED_DATE
	 * @see OrderValidator#validate(Object, org.springframework.validation.Errors)
	 */
	@Test
	public void validate_shouldFailValidationIfScheduledDateIsNullWhenUrgencyIsON_SCHEDULED_DATE() throws Exception {
		Order order = new Order();
		order.setConcept(Context.getConceptService().getConcept(88));
		order.setPatient(Context.getPatientService().getPatient(2));
		order.setOrderer(Context.getProviderService().getProvider(1));
		
		order.setUrgency(Order.Urgency.ON_SCHEDULED_DATE);
		order.setScheduledDate(null);
		Errors errors = new BindException(order, "order");
		new OrderValidator().validate(order, errors);
		Assert.assertTrue(errors.hasFieldErrors("scheduledDate"));
		
		order.setScheduledDate(new Date());
		order.setUrgency(Order.Urgency.STAT);
		errors = new BindException(order, "order");
		new OrderValidator().validate(order, errors);
		Assert.assertFalse(errors.hasFieldErrors("scheduledDate"));
	}
	
	/**
	 * @verifies fail validation if scheduledDate is set and urgency is not set as ON_SCHEDULED_DATE
	 * @see OrderValidator#validate(Object, org.springframework.validation.Errors)
	 */
	@Test
	public void validate_shouldFailValidationIfScheduledDateIsSetAndUrgencyIsNotSetAsON_SCHEDULED_DATE() throws Exception {
		Order order = new Order();
		order.setConcept(Context.getConceptService().getConcept(88));
		order.setPatient(Context.getPatientService().getPatient(2));
		order.setOrderer(Context.getProviderService().getProvider(1));
		
		order.setScheduledDate(new Date());
		order.setUrgency(Order.Urgency.ROUTINE);
		Errors errors = new BindException(order, "order");
		new OrderValidator().validate(order, errors);
		Assert.assertTrue(errors.hasFieldErrors("urgency"));
		
		order.setScheduledDate(new Date());
		order.setUrgency(Order.Urgency.ON_SCHEDULED_DATE);
		errors = new BindException(order, "order");
		new OrderValidator().validate(order, errors);
		Assert.assertFalse(errors.hasFieldErrors("urgency"));
	}
	
	/**
	 * @verifies fail validation if orderType.javaClass does not match order.class
	 * @see OrderValidator#validate(Object, org.springframework.validation.Errors)
	 */
	@Test
	public void validate_shouldFailValidationIfOrderTypejavaClassDoesNotMatchOrderclass() throws Exception {
		Order order = new DrugOrder();
		order.setConcept(Context.getConceptService().getConcept(88));
		order.setPatient(Context.getPatientService().getPatient(2));
		order.setOrderer(Context.getProviderService().getProvider(1));
		order.setOrderType(Context.getOrderService().getOrderTypeByName("Test order"));
		
		Errors errors = new BindException(order, "order");
		new OrderValidator().validate(order, errors);
		Assert.assertTrue(errors.hasFieldErrors("orderType"));
		Assert.assertTrue(Arrays.asList(errors.getFieldError("orderType").getCodes()).contains(
		    "Order.error.orderTypeClassMismatchesOrderClass"));
	}
	
	/**
	 * @verifies pass validation if the class of the order is a subclass of orderType.javaClass
	 * @see OrderValidator#validate(Object, org.springframework.validation.Errors)
	 */
	@Test
	public void validate_shouldPassValidationIfTheClassOfTheOrderIsASubclassOfOrderTypejavaClass() throws Exception {
		SomeDrugOrder order = new SomeDrugOrder();
		order.setConcept(Context.getConceptService().getConcept(88));
		order.setPatient(Context.getPatientService().getPatient(2));
		order.setOrderer(Context.getProviderService().getProvider(1));
		order.setOrderType(Context.getOrderService().getOrderTypeByName("Drug order"));
		
		Errors errors = new BindException(order, "order");
		new OrderValidator().validate(order, errors);
		Assert.assertFalse(errors.hasFieldErrors("orderType"));
	}
	
	/**
	 * @see {@link OrderValidator#validate(Object,Errors)}
	 */
	@Test
	@Verifies(value = "should pass validation if all fields are correct", method = "validate(Object,Errors)")
	public void validate_shouldPassValidationIfAllFieldsAreCorrect() throws Exception {
		Order order = new DrugOrder();
		Encounter encounter = new Encounter();
		order.setConcept(Context.getConceptService().getConcept(88));
		order.setOrderer(Context.getProviderService().getProvider(1));
		Patient patient = Context.getPatientService().getPatient(2);
		encounter.setPatient(patient);
		order.setPatient(patient);
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) - 1);
		order.setStartDate(cal.getTime());
		order.setAutoExpireDate(new Date());
		order.setCareSetting(new CareSetting());
		order.setEncounter(encounter);
		order.setUrgency(Order.Urgency.ROUTINE);
		order.setAction(Order.Action.NEW);
		order.setOrderType(Context.getOrderService().getOrderTypeByName("Drug order"));
		
		Errors errors = new BindException(order, "order");
		new OrderValidator().validate(order, errors);
		
		Assert.assertFalse(errors.hasErrors());
	}
	
	/**
	 * @verifies not allow a future startDate
	 * @see OrderValidator#validate(Object, org.springframework.validation.Errors)
	 */
	@Test
	public void validate_shouldNotAllowAFutureStartDate() throws Exception {
		Patient patient = Context.getPatientService().getPatient(7);
		TestOrder order = new TestOrder();
		order.setPatient(patient);
		order.setOrderType(orderService.getOrderTypeByName("Test order"));
		order.setEncounter(Context.getEncounterService().getEncounter(3));
		order.setConcept(Context.getConceptService().getConcept(5497));
		order.setOrderer(Context.getProviderService().getProvider(1));
		order.setCareSetting(orderService.getCareSetting(1));
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.HOUR_OF_DAY, 1);
		order.setStartDate(cal.getTime());
		
		Errors errors = new BindException(order, "order");
		new OrderValidator().validate(order, errors);
		
		Assert.assertTrue(errors.hasFieldErrors("startDate"));
		Assert.assertEquals("Order.error.startDateInFuture", errors.getFieldError("startDate").getCode());
	}
}
