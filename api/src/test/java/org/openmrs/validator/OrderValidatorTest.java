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

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Assert;
import org.junit.Test;
import org.openmrs.CareSetting;
import org.openmrs.DrugOrder;
import org.openmrs.Encounter;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.test.BaseContextSensitiveTest;
import org.openmrs.test.Verifies;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

/**
 * Tests methods on the {@link OrderValidator} class.
 */
public class OrderValidatorTest extends BaseContextSensitiveTest {
	
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
	 * @see {@link OrderValidator#validate(Object,Errors)}
	 */
	@Test
	@Verifies(value = "should fail validation if startDate is before encounter's encounterDatetime", method = "validate(Object,Errors)")
	public void validate_shouldFailValidationIfStartDateIsBeforeEncounterDateTime() throws Exception {
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
	@Verifies(value = "should fail validation if startDate is null", method = "validate(Object,Errors)")
	public void validate_shouldFailValidationIfStartDateIsNull() throws Exception {
		Order order = new Order();
		order.setConcept(Context.getConceptService().getConcept(88));
		order.setPatient(Context.getPatientService().getPatient(2));
		order.setStartDate(null);
		
		Errors errors = new BindException(order, "order");
		new OrderValidator().validate(order, errors);
		
		Assert.assertTrue(errors.hasFieldErrors("startDate"));
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
	public void validate_shouldFailValidationIfStartDateAfterDiscontinuedDate() throws Exception {
		Order order = new Order();
		order.setConcept(Context.getConceptService().getConcept(88));
		order.setPatient(Context.getPatientService().getPatient(2));
		order.setOrderer(Context.getProviderService().getProvider(1));
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) - 1);
		order.setStartDate(new Date());
		order.setDateStopped(cal.getTime());
		
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
	 * @see {@link OrderValidator#validate(Object,Errors)}
	 */
	@Test
	@Verifies(value = "should fail validation if scheduledDate is set and urgency is not set as ON_SCHEDULED_DATE", method = "validate(Object,Errors)")
	public void validate_shouldFailValidationIfScheduledDateIsSetUrgencyShouldBeSetAsOnScheduleDate() throws Exception {
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
	 * @see {@link OrderValidator#validate(Object,Errors)}
	 */
	@Test
	@Verifies(value = "should fail validation if scheduledDate is null when urgency is ON_SCHEDULED_DATE", method = "validate(Object,Errors)")
	public void validate_shouldFailValidationIfScheduledDateIsNullIfUrgencyIsOnScheduleDate() throws Exception {
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
	 * @see {@link OrderValidator#validate(Object,Errors)}
	 */
	@Test
	@Verifies(value = "should fail validation if orderType.javaClass does not match order.class", method = "validate(Object,Errors)")
	public void validate_shouldFailValidationIfOrderTypeJavaClassAsClassDoesNotMatchOrderClass() throws Exception {
		Order order = new DrugOrder();
		order.setConcept(Context.getConceptService().getConcept(88));
		order.setPatient(Context.getPatientService().getPatient(2));
		order.setOrderer(Context.getProviderService().getProvider(1));
		order.setOrderType(Context.getOrderService().getOrderTypeByName("Test order"));
		
		Errors errors = new BindException(order, "order");
		new OrderValidator().validate(order, errors);
		Assert.assertTrue(errors.hasFieldErrors("orderType"));
		Assert.assertTrue(Arrays.asList(errors.getFieldError("orderType").getCodes()).contains(
		    "error.orderTypeClassMismatchesOrderClass"));
	}
	
	/**
	 * @see {@link OrderValidator#validate(Object,Errors)}
	 */
	@Test
	@Verifies(value = "should pass validation if orderType.javaClass matches order.class' subclass", method = "validate(Object,Errors)")
	public void validate_shouldFailValidationIfOrderTypeJavaClassAsClassDoesNotMatchOrderClassSubclass() throws Exception {
		Order order = new Order();
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
		Order order = new Order();
		Encounter encounter = new Encounter();
		order.setConcept(Context.getConceptService().getConcept(88));
		order.setOrderer(Context.getProviderService().getProvider(1));
		Patient patient = Context.getPatientService().getPatient(2);
		encounter.setPatient(patient);
		order.setPatient(patient);
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) - 1);
		order.setStartDate(cal.getTime());
		order.setDateStopped(new Date());
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
}
