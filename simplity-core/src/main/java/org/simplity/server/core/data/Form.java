// SPDX-License-Identifier: MIT
package org.simplity.server.core.data;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.simplity.server.core.Conventions;
import org.simplity.server.core.Message;
import org.simplity.server.core.app.AppManager;
import org.simplity.server.core.db.ReadonlyHandle;
import org.simplity.server.core.filter.FilterDetails;
import org.simplity.server.core.filter.FilterParams;
import org.simplity.server.core.json.JsonUtil;
import org.simplity.server.core.service.InputData;
import org.simplity.server.core.service.OutputData;
import org.simplity.server.core.service.ServiceContext;
import org.simplity.server.core.service.ServiceWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Form is a client-side component. If a form is based on a record for its data,
 * then this class is generated to deliver services for that client-side
 * component.
 *
 * @author simplity.org
 * @param <T> primary record that describes the data behind this form
 *
 */
public abstract class Form<T extends Record> {
	protected static final Logger logger = LoggerFactory.getLogger(Form.class);
	/*
	 * name of this form. unique within an app
	 */
	private final String name;

	/*
	 * record that this form is based on
	 */
	protected T record;

	/**
	 * is this form open to guests
	 */
	protected boolean serveGuests;

	/*
	 * what operations are allowed on this form
	 */
	private final boolean[] operations;

	/*
	 * child forms
	 */
	protected final ChildForm<?>[] childForms;
	private final boolean isDb;

	protected Form(final String name, final T record, final boolean[] operations, final ChildForm<?>[] childForms) {
		this.name = name;
		this.record = record;
		this.operations = operations;
		this.isDb = record instanceof DbRecord;
		this.childForms = childForms;
		if (childForms != null && childForms.length > 0) {
			for (final ChildForm<?> lf : childForms) {
				lf.init(record);
			}
		}
	}

	/**
	 * @return true if this form is based on a db record. false otherwise
	 */
	public boolean isDb() {
		return this.isDb;
	}

	/**
	 *
	 * @return true if this form has child forms. false otherwise.
	 */
	public boolean hasChildren() {
		return this.childForms != null;
	}

	/**
	 * read rows for the child-forms
	 *
	 * @param rawData for the record for this form
	 * @param outData to which the read rows are to be serialized into
	 * @param handle
	 * @throws SQLException
	 */
	public void readChildForms(final Object[] rawData, final OutputData outData, final ReadonlyHandle handle)
			throws SQLException {
		if (this.childForms != null) {
			for (final ChildForm<?> child : Form.this.childForms) {
				child.read((DbRecord) this.record, outData, handle);
			}
		}
	}

	/**
	 * load keys from the input. input is suspect.
	 *
	 * @param inputObject non-null
	 * @param ctx         non-null. any validation error is added to it
	 * @return true record with parsed values. null if any input fails validation.
	 */
	public boolean parseKeys(final InputData inputObject, final ServiceContext ctx) {
		if (!this.isDb) {
			logger.error("This form is based on {} that is not a DbRecord. Keys can not be parsed");
			return false;
		}
		return ((DbRecord) this.record).parseKeys(inputObject, ctx);
	}

	/**
	 *
	 * @param operation
	 * @return a service for this operation on the form. null if the operation is
	 *         not allowed.
	 */
	public ServiceWorker getServiceWorker(final IoType operation) {
		if (!this.operations[operation.ordinal()]) {
			logger.info("{} operation is not allowed on record {}", operation, this.name);
			return null;
		}

		/*
		 * forms with children require form-based service
		 */
		if (this.childForms != null) {
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
				logger.error("Operation {} is not designed for forms-based srvice.", operation.name(), this.name);
				return null;
			}
		}

		/*
		 * This is a form without child forms. Is the underlying record is a DbRecord?
		 */
		if (this.isDb) {
			return ((DbRecord) this.record).getServiceWorker(operation);
		}

		logger.error("Form {} is based on non-db record {}. No operations are possible on this form.", this.name,
				this.name);
		return null;
	}

	protected class Reader implements ServiceWorker {

		@Override
		public void serve(final ServiceContext ctx, final InputData payload) throws Exception {
			if (!Form.this.parseKeys(payload, ctx)) {
				logger.error("Error while reading keys from the input payload");
				return;
			}

			final DbRecord rec = (DbRecord) Form.this.record;
			AppManager.getApp().getDbDriver().doReadonlyOperations(handle -> {
				if (!rec.read(handle)) {
					logger.error("No data found for the requested keys");
					ctx.addMessage(Message.newError(Conventions.MessageId.INVALID_DATA));
					return false;
				}
				/*
				 * instead of storing data and then serializing it, we have designed this
				 * service to serialize data then-and-there
				 */
				final OutputData outData = ctx.getOutputData();
				outData.beginObject();
				outData.addValues(rec.fetchFieldNames(), rec.fieldValues);

				for (final ChildForm<?> child : Form.this.childForms) {
					child.read(rec, outData, handle);
				}
				outData.endObject();
				return true;
			});
		}
	}

	protected class Creater implements ServiceWorker {
		@Override
		public void serve(final ServiceContext ctx, final InputData payload) throws Exception {
			final DbRecord rec = (DbRecord) Form.this.record;
			if (!rec.parse(payload, true, ctx, null, 0)) {
				logger.error("Error while validating the input payload");
				return;
			}
			AppManager.getApp().getDbDriver().doReadWriteOperations(handle -> {
				if (!rec.insert(handle)) {
					logger.error("Insert operation failed silently");
					ctx.addMessage(Message.newError(Conventions.MessageId.INVALID_DATA));
					return false;
				}
				for (final ChildForm<?> lf : Form.this.childForms) {
					if (!lf.insert(rec, payload, handle, ctx)) {
						logger.error("Insert operation failed for a child form");
						ctx.addMessage(Message.newError(Conventions.MessageId.INVALID_DATA));
						return false;
					}
				}
				return true;
			});
		}
	}

	protected class Updater implements ServiceWorker {
		@Override
		public void serve(final ServiceContext ctx, final InputData payload) throws Exception {
			final DbRecord rec = (DbRecord) Form.this.record;
			if (!rec.parse(payload, false, ctx, null, 0)) {
				logger.error("Error while validating the input payload");
				return;
			}

			AppManager.getApp().getDbDriver().doReadWriteOperations(handle -> {
				if (!rec.update(handle)) {
					logger.error("update operation failed silently");
					ctx.addMessage(Message.newError(Conventions.MessageId.INVALID_DATA));
					return false;
				}
				for (final ChildForm<?> lf : Form.this.childForms) {
					if (!lf.update(rec, payload, handle, ctx)) {
						logger.error("Update operation failed for a child form");
						ctx.addMessage(Message.newError(Conventions.MessageId.INVALID_DATA));
						return false;

					}
				}
				return true;
			});
		}
	}

	protected class Deleter implements ServiceWorker {

		@Override
		public void serve(final ServiceContext ctx, final InputData payload) throws Exception {
			final DbRecord rec = (DbRecord) Form.this.record;
			if (!rec.parseKeys(payload, ctx)) {
				logger.error("Error while validating keys");
				return;
			}

			AppManager.getApp().getDbDriver().doReadWriteOperations(handle -> {
				if (!rec.delete(handle)) {
					logger.error("Delete operation failed silently");
					ctx.addMessage(Message.newError(Conventions.MessageId.INVALID_DATA));
					return false;
				}

				for (final ChildForm<?> lf : Form.this.childForms) {
					if (!lf.delete(rec, handle, ctx)) {
						logger.error("Insert operation failed for a child form");
						ctx.addMessage(Message.newError(Conventions.MessageId.INVALID_DATA));
						return false;
					}
				}
				return true;
			});
		}
	}

	protected class Filter implements ServiceWorker {

		@Override
		public void serve(final ServiceContext ctx, final InputData payload) throws Exception {
			final DbRecord rec = (DbRecord) Form.this.record;
			FilterParams params = JsonUtil.load(payload, FilterParams.class);
			if (params == null) {
				logger.error("Input data had errors while parsing it as a FilerRequestJson");
				ctx.addMessage(Message.newError(Conventions.MessageId.INVALID_DATA));
				return;
			}

			final FilterDetails filter = rec.dba.prepareFilterDetails(params, ctx);

			if (filter == null) {
				logger.error("Error while parsing filter conditions from the input payload");
				return;
			}

			final List<Object[]> rows = new ArrayList<>();
			AppManager.getApp().getDbDriver().doReadonlyOperations(handle -> {
				handle.readMany(filter.getSql(), filter.getParamValues(), filter.getParamTypes(),
						filter.getOutputTypes(), rows);

				/*
				 * instead of storing data and then serializing it, we have designed this
				 * service to serialize data then-and-there
				 */
				final OutputData outData = ctx.getOutputData();
				outData.beginObject();
				outData.addName(Conventions.Request.TAG_LIST);
				outData.beginArray();

				if (rows.size() == 0) {
					logger.warn("No rows filtered. Responding with empty list");
				} else {
					for (final Object[] row : rows) {
						final DbRecord r = rec.newInstance(row);
						outData.beginObject();
						outData.addValues(r.fetchFieldNames(), r.fieldValues);
						for (final ChildForm<?> child : Form.this.childForms) {
							child.read(r, outData, handle);
						}
						outData.endObject();
					}
				}

				outData.endArray();
				outData.endObject();
				return true;
			});
		}
	}

	/**
	 * @return underlying record for this form
	 */
	public T getRecord() {
		return this.record;
	}

	/**
	 * @param ctx
	 */
	@SuppressWarnings("unchecked")
	public void override(final ServiceContext ctx) {
		final String recordName = this.record.fetchName();
		this.record = (T) AppManager.getApp().getCompProvider().getRecord(recordName, ctx);
		if (this.childForms != null) {
			for (final ChildForm<?> lf : this.childForms) {
				lf.override(this.record, ctx);
			}
		}
	}

	/**
	 * @return unique name of this form
	 */
	public String getName() {
		return this.name;
	}
}
