// SPDX-License-Identifier: MIT
package org.simplity.server.core.data;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.simplity.server.core.Conventions;
import org.simplity.server.core.Message;
import org.simplity.server.core.app.AppManager;
import org.simplity.server.core.db.ReadWriteHandle;
import org.simplity.server.core.db.ReadonlyHandle;
import org.simplity.server.core.filter.FilterDetails;
import org.simplity.server.core.filter.FilterParams;
import org.simplity.server.core.json.JsonUtil;
import org.simplity.server.core.service.InputData;
import org.simplity.server.core.service.ServiceContext;
import org.simplity.server.core.service.ServiceWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Extends a record to add functionality to link the underlying data-structure
 * to a database/persistence
 * </p>
 * <p>
 * concrete classed must add named getters and setters to the record so that the
 * user-code is type-safe
 * </p>
 *
 * @author simplity.org
 *
 */
public abstract class DbRecord extends Record {
	protected static final Logger logger = LoggerFactory.getLogger(DbRecord.class);

	protected final Dba dba;

	protected DbRecord(final Dba dba, final RecordMetaData meta, final Object[] fieldValues) {
		super(meta, fieldValues);
		this.dba = dba;
	}

	@Override
	public boolean parse(final InputData inputObject, final boolean forInsert, final ServiceContext ctx,
			final String tableName, final int rowNbr) {
		if (!super.parse(inputObject, forInsert, ctx, tableName, rowNbr)) {
			return false;
		}
		/*
		 * validate db-specific fields
		 */
		return this.dba.validate(this.fieldValues, forInsert, ctx, tableName, rowNbr);
	}

	/**
	 * load keys from a JSON. input is suspect.
	 *
	 * @param inputObject non-null
	 * @param ctx         non-null. any validation error is added to it
	 * @return true if all ok. false if any parse error is added the ctx
	 */
	public boolean parseKeys(final InputData inputObject, final ServiceContext ctx) {
		return this.dba.parseKeys(inputObject, this.fieldValues, ctx);
	}

	/**
	 * Use this method if the key values are known but not with their names
	 *
	 * Sets the values in the array to the key fields, in that order, after
	 * validating them.
	 *
	 *
	 * @param keyValues inputObject non-null
	 * @param ctx       non-null. any validation error is added to it
	 * @return true if all ok. false if any parse error is added the ctx
	 */
	public boolean setkeys(final Object[] keyValues, final ServiceContext ctx) {
		return this.dba.setKeys(keyValues, this.fieldValues, ctx);
	}

	/**
	 * fetch data for this form from a db
	 *
	 * @param handle
	 *
	 * @return true if it is read.false if no data found for this form (key not
	 *         found...)
	 * @throws SQLException
	 */
	public boolean read(final ReadonlyHandle handle) throws SQLException {
		return this.dba.read(handle, this.fieldValues);
	}

	/**
	 * read is expected to succeed. hence an exception is thrown in case if no row
	 * is not read
	 *
	 * @param handle
	 *
	 * @throws SQLException
	 */
	public void readOrFail(final ReadonlyHandle handle) throws SQLException {
		if (!this.dba.read(handle, this.fieldValues)) {
			throw new SQLException("Read failed for " + this.fetchName() + this.dba.emitKeys(this.fieldValues));
		}
	}

	/**
	 * get the count of rows from the underlying table as per the filter conditions
	 *
	 * @param handle       readOnly handle
	 * @param filterParams required parameters for the filter operation
	 * @param ctx          In case of any errors in filterParams, they are added to
	 *                     the service context
	 * @return number of rows. -1 in case of any errors.
	 * @throws SQLException
	 */
	public long countRows(final ReadonlyHandle handle, FilterParams filterParams, ServiceContext ctx)
			throws SQLException {
		return this.dba.countRows(handle, filterParams, ctx);
	}

	/**
	 * insert/create this form data into the db.
	 *
	 * @param handle
	 *
	 * @return true if it is created. false in case it failed because of an an
	 *         existing form with the same id/key
	 * @throws SQLException
	 */
	public boolean insert(final ReadWriteHandle handle) throws SQLException {
		return this.dba.insert(handle, this.fieldValues);
	}

	/**
	 * insert is expected to succeed. hence an exception is thrown in case if no row
	 * is not inserted
	 *
	 * @param handle
	 *
	 * @throws SQLException
	 */
	public void insertOrFail(final ReadWriteHandle handle) throws SQLException {
		if (!this.dba.insert(handle, this.fieldValues)) {
			throw new SQLException(
					"Insert failed silently for " + this.fetchName() + this.dba.emitKeys(this.fieldValues));
		}
	}

	/**
	 * update this form data back into the db.
	 *
	 * @param handle
	 *
	 * @return true if it is indeed updated. false in case there was no row to
	 *         update
	 * @throws SQLException
	 */
	public boolean update(final ReadWriteHandle handle) throws SQLException {
		return this.dba.update(handle, this.fieldValues);
	}

	/**
	 * update is expected to succeed. hence an exception is thrown in case if no row
	 * is updated
	 *
	 * @param handle
	 *
	 * @throws SQLException
	 */
	public void updateOrFail(final ReadWriteHandle handle) throws SQLException {
		if (!this.dba.update(handle, this.fieldValues)) {
			throw new SQLException(
					"Update operation was not successful, but there was no Sql Exception. Probably some mismatch of parameters: Record='"
							+ this.fetchName() + "' with keyFeilds: " + this.dba.emitKeys(this.fieldValues));
		}
	}

	/**
	 * insert or update this, based on the primary key. possible only if the primary
	 * key is generated
	 *
	 * @param handle
	 * @return true if it was indeed saved
	 * @throws SQLException
	 */
	public boolean save(final ReadWriteHandle handle) throws SQLException {
		return this.dba.save(handle, this.fieldValues);
	}

	/**
	 * @param handle
	 * @throws SQLException
	 */
	public void saveOrFail(final ReadWriteHandle handle) throws SQLException {
		if (!this.dba.save(handle, this.fieldValues)) {
			throw new SQLException(
					"Save failed silently for " + this.fetchName() + this.dba.emitKeys(this.fieldValues));
		}

	}

	/**
	 * remove this form data from the db
	 *
	 * @param handle
	 *
	 * @return true if it is indeed deleted happened. false otherwise
	 * @throws SQLException
	 */
	public boolean delete(final ReadWriteHandle handle) throws SQLException {
		return this.dba.delete(handle, this.fieldValues);
	}

	/**
	 * delete is expected to succeed. hence an exception is thrown in case if no row
	 * is not deleted
	 *
	 * @param handle
	 *
	 * @throws SQLException
	 */
	public void deleteOrFail(final ReadWriteHandle handle) throws SQLException {
		if (!this.dba.delete(handle, this.fieldValues)) {
			throw new SQLException(
					"Delete failed silently for " + this.fetchName() + this.dba.emitKeys(this.fieldValues));
		}
	}

	@Override
	public DbRecord makeACopy() {
		return this.newInstance(this.fieldValues);
	}

	@Override
	public DbRecord newInstance() {
		return this.newInstance(null);
	}

	@Override
	public abstract DbRecord newInstance(Object[] values);

	/**
	 * get a service worker for the specific operation on this record
	 *
	 * @param operation non-null
	 * @return worker if this record is designed for this operation. null otherwise.
	 */
	public ServiceWorker getServiceWorker(final IoType operation) {
		if (!this.dba.operationAllowed(operation)) {
			logger.info("{} operation is not allowed on record {}", operation, this.fetchName());
			return null;
		}

		switch (operation) {
		case GET:
			return new Reader();
		case CREATE:
			return new Creater();
		case UPDATE:
			return new Updater();
		case DELETE:
			return new Deleter();
		case FILTER:
			return new Filter();
		default:
			logger.error("DbRecord needs to be designed for operation " + operation.name());
			return null;
		}
	}

	protected class Reader implements ServiceWorker {

		@Override
		public void serve(final ServiceContext ctx, final InputData payload) throws Exception {
			final DbRecord rec = DbRecord.this.newInstance();
			if (!rec.parseKeys(payload, ctx)) {
				logger.error("Error while reading keys from the input payload");
				return;
			}

			AppManager.getApp().getDbDriver().doReadonlyOperations(handle -> {
				boolean ok = rec.read(handle);
				if (!ok) {
					logger.error("No data found for the requested keys");
					ctx.addMessage(Message.newError(Conventions.MessageId.INVALID_DATA));
				}
				return ok;
			});

			if (ctx.allOk()) {
				ctx.setAsResponse(rec.fetchFieldNames(), rec.fieldValues);
			}
		}

	}

	protected class Creater implements ServiceWorker {

		@Override
		public void serve(final ServiceContext ctx, final InputData payload) throws Exception {
			final DbRecord rec = DbRecord.this.newInstance();
			if (!rec.parse(payload, true, ctx, null, 0)) {
				logger.error("Error while validating the input payload");
				return;
			}

			AppManager.getApp().getDbDriver().doReadWriteOperations(handle -> {
				if (rec.insert(handle)) {
					return true;
				}

				logger.error("Insert operation failed silently");
				ctx.addMessage(Message.newError(Conventions.MessageId.INVALID_DATA));
				return false;
			});
		}
	}

	protected class Updater implements ServiceWorker {

		@Override
		public void serve(final ServiceContext ctx, final InputData payload) throws Exception {
			final DbRecord rec = DbRecord.this.newInstance();
			if (!rec.parse(payload, false, ctx, null, 0)) {
				logger.error("Error while validating data from the input payload");
				return;
			}

			AppManager.getApp().getDbDriver().doReadWriteOperations(handle -> {
				if (rec.update(handle)) {
					return true;
				}

				logger.error("Update operation failed silently");
				ctx.addMessage(Message.newError(Conventions.MessageId.INVALID_DATA));
				return false;
			});
		}
	}

	protected class Deleter implements ServiceWorker {
		@Override
		public void serve(final ServiceContext ctx, final InputData payload) throws Exception {
			final DbRecord rec = DbRecord.this.newInstance();
			if (!rec.parseKeys(payload, ctx)) {
				logger.error("Error while validating keys");
				return;
			}

			AppManager.getApp().getDbDriver().doReadWriteOperations(handle -> {
				if (rec.delete(handle)) {
					return true;
				}

				logger.error("Delete operation failed silently");
				ctx.addMessage(Message.newError(Conventions.MessageId.INVALID_DATA));
				return false;
			});

		}
	}

	protected class Filter implements ServiceWorker {
		@Override
		public void serve(final ServiceContext ctx, final InputData payload) throws Exception {
			final DbRecord rec = DbRecord.this.newInstance();
			String tableName = payload.getString(Conventions.Request.TAG_TABLE_NAME);
			if (tableName == null || tableName.isEmpty()) {
				tableName = Conventions.Request.TAG_LIST;
			}

			FilterParams params = JsonUtil.load(payload, FilterParams.class);
			if (params == null) {
				ctx.addMessage(Message.newError(Conventions.MessageId.INVALID_DATA));
				logger.error("Input data for filter parameter did not follow the required data structure", ctx);
				return;
			}

			final FilterDetails filter = rec.dba.prepareFilterDetails(params, ctx);
			if (filter == null) {
				logger.error("Error while parsing filter conditions from the input payload");
				return;
			}

			final List<Object[]> rows = new ArrayList<>();

			boolean readOk = AppManager.getApp().getDbDriver().doReadonlyOperations(handle -> {
				int n = handle.readMany(filter.getSql(), filter.getParamValues(), filter.getParamTypes(),
						filter.getOutputTypes(), rows);
				return n > 0;
			});
			if (!readOk) {
				logger.warn("No rows filtered. Responding with empty list");
			}

			ctx.setAsResponse(tableName, filter.getOutputNames(), rows);
		}

	}

	/**
	 * fetch is used instead of get to avoid clash with getters in generated classes
	 *
	 * @param fieldName
	 * @return db field specified by this name, or null if there is no such name
	 */
	@Override
	public DbField fetchField(final String fieldName) {
		return this.dba.getField(fieldName);
	}

	/**
	 * fetch is used instead of get to avoid clash with getters in generated classes
	 *
	 * @return index of the generated key, or -1 if this record has no generated key
	 */
	public int fetchGeneratedKeyIndex() {
		return this.dba.getGeneratedKeyIndex();
	}

}
