// SPDX-License-Identifier: MIT
package org.simplity.server.core.infra.defalt;

import org.simplity.server.core.ApplicationError;
import org.simplity.server.core.infra.ExceptionListener;
import org.simplity.server.core.service.ServiceContext;

/**
 * @author simplity.org
 *
 */
public class DefunctExceptionListener implements ExceptionListener {

	@Override
	public void listen(final ServiceContext ctx, final Throwable e) {
		// great listeners just listen :-) :-)

	}

	@Override
	public void listen(final ServiceContext ctx, final ApplicationError e) {
		// great listeners just listen :-) :-)

	}

}
