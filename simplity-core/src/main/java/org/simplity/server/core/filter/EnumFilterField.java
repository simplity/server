package org.simplity.server.core.filter;

/**
 * filter field for enums
 *
 * @param <T> value type of the field. It can be date, number, boolean etc.
 *
 */
public class EnumFilterField<T extends Enum<?>> extends FilterField<T> {
	/**
	 * constructor
	 *
	 * @param builder   filter builder
	 * @param fieldName name of the field
	 */
	public EnumFilterField(FilterBuilder builder, String fieldName) {
		super(builder, fieldName);
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
			strValues[i] = values[i].name();
		}
		this.builder.addCondition(this.fieldName, FilterOperator.ONE_OF, String.join(",", strValues), null);
		return this.builder;
	}

}
