package org.simplity.server.gen;

import java.util.Map;

import org.simplity.server.core.valueschema.ValueType;

/**
 * output field for a SQL
 *
 * @author simplity.org
 *
 */
public class OutputField implements IField {
	String name;
	String valueType;
	String nameInDb;

	ValueType vt;
	int index;

	@Override
	public void init(Map<String, ValueSchema> schemas, int i) {
		this.index = i;
		try {
			this.vt = ValueType.valueOf(this.valueType);
		} catch (Exception e) {
			this.vt = ValueType.Text;
		}

	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public ValueType getValueType() {
		return this.vt;
	}

	@Override
	public int getIndex() {
		return this.index;
	}

}
