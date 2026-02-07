package org.simplity.server.core.filter;

/**
 * filter field for non-text values
 *
 * @param <T> value type of the field. It can be date, number, boolean etc.
 *
 */
public class NonTextFilterField<T> extends FilterField<T> {
	/**
	 * constructor
	 *
	 * @param builder   filter builder
	 * @param fieldName name of the field
	 */
	public NonTextFilterField(FilterBuilder builder, String fieldName) {
		super(builder, fieldName);
	}

	/**
	 * add greater-than condition for this field
	 *
	 * @param value value to be compared
	 * @return builder reference
	 */
	public FilterBuilder gt(T value) {
		this.builder.addCondition(this.fieldName, FilterOperator.GT, value, null);
		return this.builder;
	}

	/**
	 * add less-than condition for this field
	 *
	 * @param value value to be compared
	 * @return builder reference
	 */
	public FilterBuilder lt(T value) {
		this.builder.addCondition(this.fieldName, FilterOperator.LT, value, null);
		return this.builder;
	}

	/**
	 * add greater-than-or-equal condition for this field
	 *
	 * @param value value to be compared
	 * @return builder reference
	 */
	public FilterBuilder ge(T value) {
		this.builder.addCondition(this.fieldName, FilterOperator.GE, value, null);
		return this.builder;
	}

	/**
	 * add less-than-or-equal condition for this field
	 *
	 * @param value value to be compared
	 * @return builder reference
	 */
	public FilterBuilder le(T value) {
		this.builder.addCondition(this.fieldName, FilterOperator.LE, value, null);
		return this.builder;
	}

	/**
	 * add between condition for this field
	 *
	 * @param start start value of the range
	 * @param end   end value of the range
	 * @return builder reference
	 */
	public FilterBuilder between(T start, T end) {
		this.builder.addCondition(this.fieldName, FilterOperator.BETWEEN, start, end);
		return this.builder;
	}

	/**
	 * add one-of condition for this field
	 *
	 * @param values values to be compared
	 * @return builder reference
	 */
	public FilterBuilder oneOf(T[] values) {
		final String[] strValues = new String[values.length];
		for (int i = 0; i < values.length; i++) {
			strValues[i] = values[i].toString();
		}
		this.builder.addCondition(this.fieldName, FilterOperator.ONE_OF, String.join(",", strValues), null);
		return this.builder;
	}

}
