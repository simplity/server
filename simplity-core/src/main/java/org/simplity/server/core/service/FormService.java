package org.simplity.server.core.service;

import org.simplity.server.core.Conventions;
import org.simplity.server.core.Message;
import org.simplity.server.core.app.AppManager;
import org.simplity.server.core.data.DbRecord;
import org.simplity.server.core.data.Form;
import org.simplity.server.core.data.IoType;
import org.simplity.server.core.infra.CompProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is implemented as a singleton service. It is an internal service that
 * routes form-related requests to the appropriate form-service
 *
 * @author simplity.org
 *
 */
public class FormService extends AbstractService {
	private static final Logger logger = LoggerFactory.getLogger(FormService.class);

	/**
	 *
	 * @return non-null instance
	 */
	public static FormService getInstance() {
		return new FormService();
	}

	private FormService() {
		super("formService");
	}

	@Override
	public void serve(final ServiceContext ctx, final InputData payload) throws Exception {

		// this service is an 'internal' service. Hence, no detailed error reporting.

		final String formName = payload.getString(Conventions.Request.TAG_FORM_NAME);
		final String operation = payload.getString(Conventions.Request.TAG_FORM_OPERATION);
		final InputData inputData = payload.getData(Conventions.Request.TAG_FORM_DATA);

		ServiceWorker worker = getWorker(formName, operation, ctx);
		if (worker == null) {
			ctx.addMessage(Message.newError(Conventions.MessageId.INVALID_DATA));
			return;
		}
		worker.serve(ctx, inputData);

	}

	/**
	 * Important to note that this method is invoked before serve(). When this is
	 * invoked, this service is unable to decide whether to allow access to a guest
	 * user. Hence our design is to say 'allow' at this time, and let the
	 * form-service decide later.
	 *
	 * @return true always, so that the service.serve() is invoked, at which time it
	 *         is checked again before actually serving..
	 */
	@Override
	public boolean serveGuests() {
		return true;
	}

	private static ServiceWorker getWorker(final String formName, final String operation, final ServiceContext ctx) {
		if (formName == null || formName.isEmpty() || operation == null || operation.isEmpty()) {
			return null;
		}

		IoType ioType = null;
		try {
			ioType = IoType.valueOf(operation.toUpperCase());
		} catch (Exception e) {
			logger.error("Invalid ioType {} received for for form '{}'", operation, formName);
			return null;
		}

		CompProvider cp = AppManager.getApp().getCompProvider();

		// is it a form?
		Form<?> form = cp.getForm(formName, ctx);
		if (form != null) {
			return form.getServiceWorker(ioType);
		}

		// is it a record?

		final org.simplity.server.core.data.Record record = cp.getRecord(formName, ctx);
		if (record != null && record instanceof DbRecord) {
			return ((DbRecord) record).getServiceWorker(ioType);
		}

		logger.error("{} is not a form or DbRecord and hence a service is not generated for oepration {}", formName,
				operation);
		return null;
	}

}
