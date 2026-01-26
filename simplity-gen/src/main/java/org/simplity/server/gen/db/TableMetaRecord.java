package org.simplity.server.gen.db;

import org.simplity.server.core.data.Field;
import org.simplity.server.core.valueschema.ValueType;

/**
 * class that represents structure of meta data about a db table/view
 */
public class TableMetaRecord extends org.simplity.server.core.data.Record {
	private static final Field[] FIELDS = { new Field("name", 0, ValueType.Text, null, true, null),
			new Field("nameInDb", 1, ValueType.Text, null, true, null),
			new Field("description", 2, ValueType.Text, null, false, null),
			new Field("isView", 3, ValueType.Boolean, null, false, null) };

	/** default constructor */
	public TableMetaRecord() {
		super(FIELDS, null);
	}

	/**
	 * @param values initial values
	 */
	public TableMetaRecord(Object[] values) {
		super(FIELDS, values);
	}

	/**
	 * * create new instance of this record
	 *
	 * @param name
	 */
	public void setName(String name) {
		this.assignStringValue(0, name);
	}

	/**
	 *
	 * @return name
	 */
	public String getName() {
		return this.fetchStringValue(0);
	}

	/**
	 *
	 * @param nameInDb
	 */
	public void setNameInDb(String nameInDb) {
		this.assignStringValue(1, nameInDb);
	}

	/**
	 *
	 * @return nameInDb
	 */
	public String getNameInDb() {
		return this.fetchStringValue(1);
	}

	/**
	 *
	 * @param description
	 */
	public void setDescription(String description) {
		this.assignStringValue(2, description);
	}

	/**
	 *
	 * @return description
	 */
	public String getDescription() {
		return this.fetchStringValue(2);
	}

	/**
	 *
	 * @param isView
	 */
	public void setIsView(boolean isView) {
		this.assignBoolValue(3, isView);
	}

	/**
	 *
	 * @return isView
	 */
	public boolean getIsView() {
		return this.fetchBoolValue(3);
	}

}
