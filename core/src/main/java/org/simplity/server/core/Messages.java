// SPDX-License-Identifier: MIT
package org.simplity.server.core;

/**
 * @author simplity.org
 *
 */
public interface Messages {
	/**
	 * 
	 * @param messageId
	 * @return message instance for this message name/id. null if it does not
	 *         exist
	 */
	Message getMessage(String messageId);

}
