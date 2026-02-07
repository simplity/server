package org.simplity.server.core.filter;

/**
 * filter field for text values
 */
public class TextFilterField extends NonTextFilterField<String> {
	/**
	 * constructor
	 *
	 * @param builder   filter builder
	 * @param fieldName name of the field
	 */
	public TextFilterField(FilterBuilder builder, String fieldName) {
		super(builder, fieldName);
	}

	/**
	 * add contains condition for this field
	 *
	 * @param pattern pattern to be searched
	 * @return builder reference
	 */
	public FilterBuilder contains(String pattern) {
		this.builder.addCondition(this.fieldName, FilterOperator.CONTAINS, pattern, null);
		return this.builder;
	}

	/**
	 * add starts-with condition for this field
	 *
	 * @param pattern pattern to be searched
	 * @return builder reference
	 */
	public FilterBuilder startsWith(String pattern) {
		this.builder.addCondition(this.fieldName, FilterOperator.STARTS_WITH, pattern, null);
		return this.builder;
	}

	/**
	 * add ends-with condition for this field
	 *
	 * @param pattern pattern to be searched
	 * @return builder reference
	 */
	public FilterBuilder endsWith(String pattern) {
		this.builder.addCondition(this.fieldName, FilterOperator.ENDS_WITH, pattern, null);
		return this.builder;
	}

}
