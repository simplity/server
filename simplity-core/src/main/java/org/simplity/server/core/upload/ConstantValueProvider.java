// SPDX-License-Identifier: MIT
package org.simplity.server.core.upload;

import java.util.Map;

import org.simplity.server.core.service.ServiceContext;

/**
 * Value is a constant
 *
 */
public class ConstantValueProvider implements InputValueProvider {
	private final String constant;

	/**
	 * @param constant non-null.
	 *
	 */
	public ConstantValueProvider(String constant) {
		this.constant = constant;
	}

	@Override
	public String getValue(Map<String, String> input, ServiceContext ctx) {
		return this.constant;
	}
}
