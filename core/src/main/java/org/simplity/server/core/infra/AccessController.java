// SPDX-License-Identifier: MIT
package org.simplity.server.core.infra;

import org.simplity.server.core.service.Service;
import org.simplity.server.core.service.ServiceContext;

/**
 * decide whether the user has access to the requested service
 *
 * @author simplity.org
 */
public interface AccessController {

	/**
	 * @param service
	 * @param ctx
	 * @return true if the user has access, false if prohibited
	 */
	boolean okToServe(Service service, ServiceContext ctx);
}
