// SPDX-License-Identifier: MIT
package org.simplity.server.core.infra.defalt;

import org.simplity.server.core.infra.AccessController;
import org.simplity.server.core.service.Service;
import org.simplity.server.core.service.ServiceContext;

/**
 * @author simplity.org
 *
 */
public class DefunctAccessController implements AccessController {

	@Override
	public boolean okToServe(final Service service, final ServiceContext ctx) {
		return true;
	}

}
