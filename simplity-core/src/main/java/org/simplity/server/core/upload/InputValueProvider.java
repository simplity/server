// SPDX-License-Identifier: MIT
package org.simplity.server.core.upload;

import java.util.Map;

import org.simplity.server.core.service.ServiceContext;

/**
 * @author simplity.org
 *
 */
public interface InputValueProvider {
	/**
	 * get the value for this field
	 * @param input values
	 * @param ctx service context
	 * @return the value for this field
	 */
	public String getValue(Map<String, String> input, ServiceContext ctx);
}
