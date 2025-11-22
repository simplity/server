package org.simplity.server.core.service;

import org.simplity.server.core.app.RequestStatus;

/**
 * response from the App for a service request
 *
 */
public interface ServiceResponse {
	/**
	 *
	 * @return status of the service request. This may be used by the server to
	 *         set response code in the transport protocol, like HTTP Status
	 */
	RequestStatus getStatus();
	/**
	 * payload/response to the service request that conforms to the ResponseData
	 * schema
	 *
	 * @return non-null response text
	 */
	String getResponseData();
}
