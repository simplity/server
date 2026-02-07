package org.simplity.server.core.filter;

import java.util.ArrayList;
import java.util.List;

/**
 * base class for Record-specific FilerParamBuilder. This is used by the
 * generator to generate a class with methods to add conditions and sort fields
 * for the record. The generated class will extend this class and use the
 * protected methods to add conditions and sort fields to the builder. The
 * builder will then be used to create a FilterParams object that can be used to
 * filter and sort records.
 */
public abstract class FilterBuilder {
	private final List<FilterCondition> conditions = new ArrayList<>();
	private final List<SortBy> sorts = new ArrayList<>();
	private int maxRows = 0;

	protected final FilterBuilder addCondition(String fieldName, FilterOperator op, Object v1, Object v2) {
		this.conditions
				.add(new FilterCondition(fieldName, op.name(), v1.toString(), v2 == null ? null : v2.toString()));
		return this;
	}

	protected final FilterBuilder addSort(String fieldName, boolean ascending) {
		this.sorts.add(new SortBy(fieldName, ascending));
		return this;
	}

	/**
	 * @param max maximum number of rows to be filtered.
	 * @return builder reference
	 */

	public final FilterBuilder withMaxRows(int max) {
		this.maxRows = max;
		return this;
	}

	/**
	 *
	 * @return built FilterParams object
	 */
	public final FilterParams build() {
		final SortBy[] sortArray = this.sorts.size() > 0 ? this.sorts.toArray(new SortBy[0]) : null;
		final FilterCondition[] conditionArray = this.conditions.size() > 0
				? this.conditions.toArray(new FilterCondition[0])
				: null;
		return new FilterParams(this.maxRows, null, conditionArray, sortArray);
	}
}
