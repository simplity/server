// SPDX-License-Identifier: MIT
package org.simplity.server.core.infra.defalt;

import org.simplity.server.core.DefaultUserContext;
import org.simplity.server.core.infra.ServiceContextFactory;
import org.simplity.server.core.service.DefaultServiceContext;
import org.simplity.server.core.service.OutputData;
import org.simplity.server.core.service.ServiceContext;

/**
 * @author simplity.org
 *
 */
public class DefaultContextFactory implements ServiceContextFactory {

	@Override
	public ServiceContext newContext(final DefaultUserContext session,
			OutputData outData) {
		return new DefaultServiceContext(session, outData);
	}

	@Override
	public ServiceContext newSessionLessContext(OutputData outData) {
		return new DefaultServiceContext(null, outData);
	}

}
