package org.simplity.server.gen;

import java.util.Map;

import org.simplity.server.core.valueschema.ValueType;

/**
 *
 * @author simplity.org
 *
 */
public interface IField {

	/**
	 *
	 * @return name
	 */
	String getName();

	/**
	 * @return value type
	 */
	ValueType getValueType();

	/**
	 * @return index in the fields array
	 */
	int getIndex();

	/**
	 * @param schemas
	 * @param idx
	 */
	void init(Map<String, ValueSchema> schemas, int idx);

}
