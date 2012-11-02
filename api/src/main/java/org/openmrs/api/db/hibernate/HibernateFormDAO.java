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
package org.openmrs.api.db.hibernate;

import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Field;
import org.openmrs.FieldAnswer;
import org.openmrs.FieldType;
import org.openmrs.Form;
import org.openmrs.FormField;
import org.openmrs.FormResource;
import org.openmrs.api.APIException;
import org.openmrs.api.db.DAOException;
import org.openmrs.api.db.FormDAO;
import org.openmrs.util.OpenmrsUtil;

/**
 * Hibernate-specific Form-related functions. This class should not be used directly. All calls
 * should go through the {@link org.openmrs.api.FormService} methods.
 * 
 * @see org.openmrs.api.db.FormDAO
 * @see org.openmrs.api.FormService
 */
public class HibernateFormDAO extends HibernateOpenmrsDAO implements FormDAO {
	
	protected final Log log = LogFactory.getLog(getClass());
		
	/**
	 * Returns the form object originally passed in, which will have been persisted.
	 * 
	 * @see org.openmrs.api.FormService#createForm(org.openmrs.Form)
	 */
	public Form saveForm(Form form) throws DAOException {
		sessionFactory.getCurrentSession().saveOrUpdate(form);
		return form;
	}
	
	/**
	 * @see org.openmrs.api.FormService#duplicateForm(org.openmrs.Form)
	 */
	public Form duplicateForm(Form form) throws DAOException {
		return (Form) sessionFactory.getCurrentSession().merge(form);
	}
	
	/**
	 * @see org.openmrs.api.FormService#deleteForm(org.openmrs.Form)
	 */
	public void deleteForm(Form form) throws DAOException {
		sessionFactory.getCurrentSession().delete(form);
	}
	
	/**
	 * @see org.openmrs.api.FormService#getForm(java.lang.Integer)
	 */
	public Form getForm(Integer formId) throws DAOException {
		return (Form) sessionFactory.getCurrentSession().get(Form.class, formId);
	}
	
	/**
	 * @see org.openmrs.api.FormService#getFormFields(Form)
	 */
	@SuppressWarnings("unchecked")
	public List<FormField> getFormFields(Form form) throws DAOException {
		return sessionFactory.getCurrentSession().createCriteria(FormField.class, "ff").add(Expression.eq("ff.form", form))
		        .list();
	}
	
	/**
	 * @see org.openmrs.api.db.FormDAO#getFields(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public List<Field> getFields(String search) throws DAOException {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Field.class);
		criteria.add(Restrictions.like("name", search, MatchMode.ANYWHERE));
		criteria.addOrder(Order.asc("name"));
		return criteria.list();
	}
	
	/**
	 * @see org.openmrs.api.FormService#getFieldsByConcept(org.openmrs.Concept)
	 */
	@SuppressWarnings("unchecked")
	public List<Field> getFieldsByConcept(Concept concept) throws DAOException {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Field.class);
		criteria.add(Expression.eq("concept", concept));
		criteria.addOrder(Order.asc("name"));
		return criteria.list();
	}
	
	/**
	 * @see org.openmrs.api.FormService#getFormField(org.openmrs.Form, org.openmrs.Concept,
	 *      java.util.Collection, boolean)
	 * @see org.openmrs.api.db.FormDAO#getFormField(org.openmrs.Form, org.openmrs.Concept,
	 *      java.util.Collection, boolean)
	 */
	@SuppressWarnings("unchecked")
	public FormField getFormField(Form form, Concept concept, Collection<FormField> ignoreFormFields, boolean force)
	        throws DAOException {
		if (form == null) {
			log.debug("form is null, no fields will be matched");
			return null;
		}
		Criteria crit = sessionFactory.getCurrentSession().createCriteria(FormField.class, "ff").createAlias("field",
		    "field").add(Expression.eq("field.concept", concept)).add(Expression.eq("form", form));
		
		// get the list of all formfields with this concept for this form
		List<FormField> formFields = crit.list();
		
		String err = "FormField warning.  No FormField matching concept '" + concept + "' for form '" + form + "'";
		
		if (formFields.size() < 1) {
			log.debug(err);
			return null;
		}
		
		// save the first formfield in case we're not a in a "force" situation
		FormField backupPlan = formFields.get(0);
		
		// remove the formfields we're supposed to ignore from the return list
		formFields.removeAll(ignoreFormFields);
		
		// if we ended up removing all of the formfields, check to see if we're
		// in a "force" situation
		if (formFields.size() < 1) {
			if (force == false)
				return backupPlan;
			else {
				log.debug(err);
				return null;
			}
		} else { // if formFields.size() is still greater than 0
			FormField ff = (FormField) formFields.get(0);
			return ff;
		}
	}
	
	/**
	 * @see org.openmrs.api.FormService#getForms()
	 */
	@SuppressWarnings("unchecked")
	public List<Form> getAllForms(boolean includeRetired) throws DAOException {
		Criteria crit = sessionFactory.getCurrentSession().createCriteria(Form.class);
		
		if (includeRetired == false)
			crit.add(Expression.eq("retired", false));
		
		crit.addOrder(Order.asc("name"));
		crit.addOrder(Order.asc("formId"));
		
		return crit.list();
	}
	
	/**
	 * @see org.openmrs.api.db.FormDAO#getFormsContainingConcept(org.openmrs.Concept)
	 */
	@SuppressWarnings("unchecked")
	public List<Form> getFormsContainingConcept(Concept c) throws DAOException {
		String q = "select distinct ff.form from FormField ff where ff.field.concept = :concept";
		Query query = sessionFactory.getCurrentSession().createQuery(q);
		query.setEntity("concept", c);
		
		return query.list();
	}
	
	/**
	 * @see org.openmrs.api.db.FormDAO#getFields(java.util.Collection, java.util.Collection,
	 *      java.util.Collection, java.util.Collection, java.util.Collection, java.lang.Boolean,
	 *      java.util.Collection, java.util.Collection, java.lang.Boolean)
	 */
	@SuppressWarnings("unchecked")
	public List<Field> getFields(Collection<Form> forms, Collection<FieldType> fieldTypes, Collection<Concept> concepts,
	        Collection<String> tableNames, Collection<String> attributeNames, Boolean selectMultiple,
	        Collection<FieldAnswer> containsAllAnswers, Collection<FieldAnswer> containsAnyAnswer, Boolean retired)
	        throws DAOException {
		
		Criteria crit = sessionFactory.getCurrentSession().createCriteria(Field.class);
		
		if (!forms.isEmpty())
			crit.add(Expression.in("form", forms));
		
		if (!fieldTypes.isEmpty())
			crit.add(Expression.in("fieldType", fieldTypes));
		
		if (!concepts.isEmpty())
			crit.add(Expression.in("concept", concepts));
		
		if (!tableNames.isEmpty())
			crit.add(Expression.in("tableName", tableNames));
		
		if (!attributeNames.isEmpty())
			crit.add(Expression.in("attributeName", attributeNames));
		
		if (selectMultiple != null)
			crit.add(Expression.eq("selectMultiple", selectMultiple));
		
		if (!containsAllAnswers.isEmpty())
			throw new APIException("containsAllAnswers must be empty because this is not yet implemented");
		
		if (!containsAnyAnswer.isEmpty())
			throw new APIException("containsAnyAnswer must be empty because this is not yet implemented");
		
		if (retired != null)
			crit.add(Expression.eq("retired", retired));
		
		return crit.list();
	}
	
	/**
	 * @see org.openmrs.api.db.FormDAO#getForm(java.lang.String, java.lang.String)
	 */
	public Form getForm(String name, String version) throws DAOException {
		Criteria crit = sessionFactory.getCurrentSession().createCriteria(Form.class);
		
		crit.add(Expression.eq("name", name));
		crit.add(Expression.eq("version", version));
		
		return (Form) crit.uniqueResult();
	}
	
	/**
	 * @see org.openmrs.api.db.FormDAO#getForms(java.lang.String, java.lang.Boolean,
	 *      java.util.Collection, java.lang.Boolean, java.util.Collection, java.util.Collection,
	 *      java.util.Collection)
	 */
	@SuppressWarnings("unchecked")
	public List<Form> getForms(String partialName, Boolean published, Collection<EncounterType> encounterTypes,
	        Boolean retired, Collection<FormField> containingAnyFormField, Collection<FormField> containingAllFormFields,
	        Collection<Field> fields) throws DAOException {
		
		Criteria crit = getFormCriteria(partialName, published, encounterTypes, retired, containingAnyFormField,
		    containingAllFormFields, fields);
		
		return crit.list();
	}
	
	/**
	 * @see org.openmrs.api.db.FormDAO#getFormCount(java.lang.String, java.lang.Boolean,
	 *      java.util.Collection, java.lang.Boolean, java.util.Collection, java.util.Collection,
	 *      java.util.Collection)
	 */
	public Integer getFormCount(String partialName, Boolean published, Collection<EncounterType> encounterTypes,
	        Boolean retired, Collection<FormField> containingAnyFormField, Collection<FormField> containingAllFormFields,
	        Collection<Field> fields) throws DAOException {
		
		Criteria crit = getFormCriteria(partialName, published, encounterTypes, retired, containingAnyFormField,
		    containingAllFormFields, fields);
		
		crit.setProjection(Projections.count("formId"));
		
		return OpenmrsUtil.convertToInteger((Long) crit.uniqueResult());
	}
	
	/**
	 * Convenience method to create the same hibernate criteria object for both getForms and
	 * getFormCount
	 * 
	 * @param partialName
	 * @param published
	 * @param encounterTypes
	 * @param retired
	 * @param containingAnyFormField
	 * @param containingAllFormFields
	 * @param fields
	 * @return
	 * @throws DAOException
	 */
	private Criteria getFormCriteria(String partialName, Boolean published, Collection<EncounterType> encounterTypes,
	        Boolean retired, Collection<FormField> containingAnyFormField, Collection<FormField> containingAllFormFields,
	        Collection<Field> fields) throws DAOException {
		
		Criteria crit = sessionFactory.getCurrentSession().createCriteria(Form.class, "form");
		
		if (partialName != null && !"".equals(partialName)) {
			crit.add(Expression.or(Expression.like("name", partialName, MatchMode.START), Expression.like("name", " "
			        + partialName, MatchMode.ANYWHERE)));
		}
		if (published != null)
			crit.add(Expression.eq("published", published));
		
		if (!encounterTypes.isEmpty())
			crit.add(Expression.in("encounterType", encounterTypes));
		
		if (retired != null)
			crit.add(Expression.eq("retired", retired));
		
		// TODO junit test
		if (!containingAnyFormField.isEmpty())
			crit.add(Expression.in("formField", containingAnyFormField));
		
		// TODO junit test
		//select * from form where len(containingallformfields) = (select count(*) from form_field ff where ff.form_id = form_id and form_field_id in (containingallformfields);
		if (!containingAllFormFields.isEmpty()) {
			DetachedCriteria detachedCrit = DetachedCriteria.forClass(FormField.class, "ff");
			detachedCrit.setProjection(Projections.count("formFieldId"));
			detachedCrit.add(Expression.eqProperty("ff.formId", "form.formId"));
			
			crit.add(Subqueries.eq(containingAllFormFields.size(), detachedCrit));
		}
		
		// get all forms (dupes included) that have this field on them
		if (!fields.isEmpty()) {
			Criteria crit2 = crit.createCriteria("formFields", "ff");
			crit2.add(Expression.eqProperty("ff.form.formId", "form.formId"));
			crit2.add(Expression.in("ff.field", fields));
		}
		
		return crit;
	}
	
	public FieldAnswer getFieldAnswerByUuid(String uuid) {
		return (FieldAnswer) sessionFactory.getCurrentSession().createQuery("from FieldAnswer f where f.uuid = :uuid")
		        .setString("uuid", uuid).uniqueResult();
	}
		
	/**
	 * @see org.openmrs.api.db.FormDAO#getFormsByName(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public List<Form> getFormsByName(String name) throws DAOException {
		Criteria crit = sessionFactory.getCurrentSession().createCriteria(Form.class);
		
		crit.add(Expression.eq("name", name));
		crit.add(Expression.eq("retired", false));
		crit.addOrder(Order.desc("version"));
		
		return crit.list();
	}
	
	/**
	 * @see org.openmrs.api.db.FormDAO#getFormFieldByField(org.openmrs.Field)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<FormField> getFormFieldsByField(Field field) {
		return sessionFactory.getCurrentSession().createQuery("from FormField f where f.field = :field").setEntity("field",
		    field).list();
	}
	
	/**
	 * @see org.openmrs.api.db.FormDAO#getFormResource(java.lang.Integer) 
	 */
	@Override
	public FormResource getFormResource(Integer formResourceId) {
		return (FormResource) sessionFactory.getCurrentSession().get(FormResource.class, formResourceId);
	}
	
	/**
	 * @see org.openmrs.api.db.FormDAO#getFormResourceByUuid(java.lang.String) 
	 */
	@Override
	public FormResource getFormResourceByUuid(String uuid) {
		Criteria crit = sessionFactory.getCurrentSession().createCriteria(FormResource.class).add(
		    Restrictions.eq("uuid", uuid));
		return (FormResource) crit.uniqueResult();
	}
	
	/**
	 * @see org.openmrs.api.db.FormDAO#getFormResource(org.openmrs.Form, java.lang.String) 
	 */
	@Override
	public FormResource getFormResource(Form form, String name) {
		Criteria crit = sessionFactory.getCurrentSession().createCriteria(FormResource.class).add(
		    Restrictions.and(Restrictions.eq("form", form), Restrictions.eq("name", name)));
		
		return (FormResource) crit.uniqueResult();
	}
	
	/**
	 * @see org.openmrs.api.db.FormDAO#saveFormResource(org.openmrs.FormResource) 
	 */
	@Override
	public FormResource saveFormResource(FormResource formResource) {
		sessionFactory.getCurrentSession().saveOrUpdate(formResource);
		return formResource;
	}
	
	/**
	 * @see org.openmrs.api.db.FormDAO#deleteFormResource(org.openmrs.FormResource) 
	 */
	@Override
	public void deleteFormResource(FormResource formResource) {
		sessionFactory.getCurrentSession().delete(formResource);
	}
	
	/**
	 * @see org.openmrs.api.db.FormDAO#getFormResourcesForForm(org.openmrs.Form) 
	 */
	@Override
	public Collection<FormResource> getFormResourcesForForm(Form form) {
		Criteria crit = sessionFactory.getCurrentSession().createCriteria(FormResource.class).add(
		    Restrictions.eq("form", form));
		return crit.list();
	}
	
}
