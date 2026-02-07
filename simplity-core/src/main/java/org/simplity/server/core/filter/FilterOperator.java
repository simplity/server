// SPDX-License-Identifier: MIT
package org.simplity.server.core.filter;

import org.simplity.server.core.Conventions;

/**
 *
 * @author simplity.org
 *
 */
public enum FilterOperator {
	/** equal */
	EQ(Conventions.Filter.EQ),
	/** not equal */
	NE(Conventions.Filter.NE),
	/** translates to "is not null" in an RDBMS */
	HAS_VALUE(Conventions.Filter.HAS_VALUE),
	/** translates to "is null" in an RDBMS */
	HAS_NO_VALUE(Conventions.Filter.HAS_NO_VALUE),
	/** greater */
	GT(Conventions.Filter.GT),
	/** greater or equal */
	GE(Conventions.Filter.GE),
	/**
	 * less than / smaller than
	 */
	LT(Conventions.Filter.LT),
	/** less-than/smaller-than or equals */
	LE(Conventions.Filter.LE),
	/** between */
	BETWEEN(Conventions.Filter.BETWEEN),
	/** one in the list */
	ONE_OF(Conventions.Filter.ONE_OF),
	// //// for text fields only
	/** like */
	CONTAINS(Conventions.Filter.CONTAINS),
	/** starts with */
	STARTS_WITH(Conventions.Filter.STARTS_WITH),
	/** ends with */
	ENDS_WITH(Conventions.Filter.ENDS_WITH);

	private String textValue;

	FilterOperator(String text) {
		this.textValue = text;
	}

	/**
	 * get the text value of this enumeration: like "=" for Equal
	 *
	 * @return text value of this enumeration: like "=" for Equal
	 */
	public String getText() {
		return this.textValue;
	}

	/**
	 * parse a text into enum
	 *
	 * @param text text to be parsed into enum
	 * @return filter condition, or null if there is no filter for this text
	 */
	public static FilterOperator parse(String text) {
		if (text == null || text.isEmpty() || text.equals(EQ.textValue)) {
			return EQ;
		}
		for (FilterOperator f : FilterOperator.values()) {
			if (f.textValue.equals(text)) {
				return f;
			}
		}
		return null;
	}
}
