package org.simplity.server.core.service;

/**
 * An assistant to Service implementations to carry out the all important method
 * serve()
 *
 * @author simplity.org
 */
public interface ServiceWorker {
	/**
	 *
	 * @param ctx       service context provides certain data structures and
	 *                  methods.
	 * @param inputData non-null, could be empty if no pay-load was received from
	 *                  the client
	 * @throws Exception so that the caller can wire exceptions to the right
	 *                   exception handler that is configured for the app
	 */
	void serve(ServiceContext ctx, InputData inputData) throws Exception;
}
