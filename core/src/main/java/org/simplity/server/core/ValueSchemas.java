// SPDX-License-Identifier: MIT
package org.simplity.server.core;

import org.simplity.server.core.valueschema.ValueSchema;

/**
 * @author simplity.org
 *
 */
public interface ValueSchemas {
	/**
	 * 
	 * @param name
	 * @return data type instance, or null if there is no such data type
	 */
	public abstract ValueSchema getValueSchema(String name);

}
