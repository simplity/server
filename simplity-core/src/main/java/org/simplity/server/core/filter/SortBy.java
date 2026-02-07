package org.simplity.server.core.filter;

/**
 * Way to sort the output rows
 */
public class SortBy {
	/**
	 * field to be sorted on
	 */
	public String field;
	/**
	 * default is to sort ascending. set this to true to sort by descending values
	 */
	public boolean descending;

	/**
	 * default constructor
	 */
	public SortBy() {
		// default constructor
	}

	/**
	 * constructor with all fields
	 *
	 * @param field
	 * @param descending set this to true to sort by descending values
	 */
	public SortBy(String field, boolean descending) {
		this.field = field;
		this.descending = descending;
	}
}
