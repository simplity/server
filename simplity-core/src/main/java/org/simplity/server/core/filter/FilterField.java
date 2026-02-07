package org.simplity.server.core.filter;

/**
 * Base class for a field in a filter. Provides common operations like eq, ne,
 * sort
 *
 * @param <T> valueType of the field
 */
public class FilterField<T> {

	protected final FilterBuilder builder;
	protected final String fieldName;

	/**
	 * constructor
	 *
	 * @param builder   filter builder
	 * @param fieldName name of the field
	 */
	public FilterField(FilterBuilder builder, String fieldName) {
		this.builder = builder;
		this.fieldName = fieldName;
	}

	/**
	 * add equality condition for this field
	 *
	 * @param value value to be compared
	 * @return builder reference
	 */
	public FilterBuilder eq(T value) {
		this.builder.addCondition(this.fieldName, FilterOperator.EQ, value, null);
		return this.builder;
	}

	/**
	 * add not-equal condition for this field
	 *
	 * @param value value to be compared
	 * @return builder reference
	 */
	public FilterBuilder ne(T value) {
		this.builder.addCondition(this.fieldName, FilterOperator.NE, value, null);
		return this.builder;
	}

	/**
	 * add has-value condition for this field
	 *
	 * @return builder reference
	 */
	public FilterBuilder hasValue() {
		this.builder.addCondition(this.fieldName, FilterOperator.HAS_VALUE, null, null);
		return this.builder;
	}

	/**
	 * add has-no-value condition for this field
	 *
	 * @return builder reference
	 */

	public FilterBuilder hasNoValue() {
		this.builder.addCondition(this.fieldName, FilterOperator.HAS_NO_VALUE, null, null);
		return this.builder;
	}

	/**
	 * sort the results by this field in ascending ascending
	 *
	 * @return builder reference
	 */
	public FilterBuilder sortAscending() {
		this.builder.addSort(this.fieldName, true);
		return this.builder;
	}

	/**
	 * sort the results by this field in descending ascending
	 *
	 * @return builder reference
	 */
	public FilterBuilder sortDescending() {
		this.builder.addSort(this.fieldName, false);
		return this.builder;
	}
}
