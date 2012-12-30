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
package org.openmrs.api.db.hibernate.search;

import java.util.List;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Version;
import org.hibernate.Session;
import org.hibernate.search.FullTextQuery;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.openmrs.collection.ListPart;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;

/**
 * Performs Lucene queries.
 */
public abstract class LuceneQuery<T> extends SearchQuery<T> {
	
	private FullTextQuery fullTextQuery;
	
	private String skipIds;
	
	private String skipIdField;
	
	public static <T> LuceneQuery<T> newQuery(final String query, final Session session, final Class<T> type) {
		return new LuceneQuery<T>(
		                          session, type) {
			
			@Override
			protected Query prepareQuery() throws ParseException {
				return newQueryParser().parse(query);
			}
			
		};
	}
	
	public static String escapeQuery(final String query) {
		return QueryParser.escape(query);
	}
	
	public LuceneQuery(Session session, Class<T> type) {
		super(session, type);
		
		buildQuery();
	}
	
	private void buildQuery() {
		Query query;
		try {
			query = prepareQuery();
			
			if (skipIds != null) {
				QueryBuilder queryBuilder = newQueryBuilder();
				query = queryBuilder.bool().must(query).must(
				    queryBuilder.keyword().onField(skipIdField).matching(skipIds).createQuery()).not().createQuery();
			}
		}
		catch (ParseException e) {
			throw new IllegalStateException("Invalid query", e);
		}
		
		fullTextQuery = getFullTextSession().createFullTextQuery(query, getType());
		adjustFullTextQuery(fullTextQuery);
	}
	
	protected abstract Query prepareQuery() throws ParseException;
	
	protected void adjustFullTextQuery(FullTextQuery fullTextQuery) {
	}
	
	protected QueryBuilder newQueryBuilder() {
		return getFullTextSession().getSearchFactory().buildQueryBuilder().forEntity(getType()).get();
	}
	
	protected QueryParser newQueryParser() {
		Analyzer analyzer = getFullTextSession().getSearchFactory().getAnalyzer(getType());
		return new QueryParser(Version.LUCENE_31, null, analyzer);
	}
	
	protected FullTextSession getFullTextSession() {
		return Search.getFullTextSession(getSession());
	}
	
	public LuceneQuery<T> skipSame(String field, String idField) {
		List<Object> documents = listProjection(idField, field);
		
		Set<Object> uniqueDocuments = Sets.newHashSet();
		Set<Object> skipDocuments = Sets.newHashSet();
		for (Object document : documents) {
			Object[] row = (Object[]) document;
			if (!uniqueDocuments.add(row[1])) {
				skipDocuments.add(row[0]);
			}
		}
		
		if (!skipDocuments.isEmpty()) {
			skipIds = Joiner.on(" ").join(skipDocuments);
		} else {
			skipIds = null;
		}
		
		skipIdField = idField;
		
		buildQuery();
		
		return this;
	}
	
	@Override
	public T uniqueResult() {
		@SuppressWarnings("unchecked")
		T result = (T) fullTextQuery.uniqueResult();
		
		return result;
	}
	
	@Override
	public List<T> list() {
		@SuppressWarnings("unchecked")
		List<T> list = fullTextQuery.list();
		
		return list;
	}
	
	@Override
	public ListPart<T> listPart(Long firstResult, Long maxResults) {
		applyPartialResults(fullTextQuery, firstResult, maxResults);
		
		@SuppressWarnings("unchecked")
		List<T> list = fullTextQuery.list();
		
		return ListPart.newListPart(list, firstResult, maxResults, Long.valueOf(fullTextQuery.getResultSize()),
		    !fullTextQuery.hasPartialResults());
	}
	
	/**
	 * @see org.openmrs.api.db.hibernate.search.SearchQuery#resultSize()
	 */
	@Override
	public long resultSize() {
		return fullTextQuery.getResultSize();
	}
	
	public List<Object> listProjection(String... fields) {
		fullTextQuery.setProjection(fields);
		
		@SuppressWarnings("unchecked")
		List<Object> list = fullTextQuery.list();
		
		return list;
	}
	
	public ListPart<Object> listPartProjection(Long firstResult, Long maxResults, String... fields) {
		applyPartialResults(fullTextQuery, firstResult, maxResults);
		
		fullTextQuery.setProjection(fields);
		
		@SuppressWarnings("unchecked")
		List<Object> list = fullTextQuery.list();
		
		return ListPart.newListPart(list, firstResult, maxResults, Long.valueOf(fullTextQuery.getResultSize()),
		    !fullTextQuery.hasPartialResults());
		
	}
	
	public ListPart<Object> listPartProjection(Integer firstResult, Integer maxResults, String... fields) {
		Long first = (firstResult != null) ? Long.valueOf(firstResult) : null;
		Long max = (maxResults != null) ? Long.valueOf(maxResults) : null;
		return listPartProjection(first, max, fields);
	}
	
	private void applyPartialResults(FullTextQuery fullTextQuery, Long firstResult, Long maxResults) {
		if (firstResult != null) {
			fullTextQuery.setFirstResult(firstResult.intValue());
		}
		
		if (maxResults != null) {
			fullTextQuery.setMaxResults(maxResults.intValue());
		}
	}
}
