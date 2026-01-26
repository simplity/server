// SPDX-License-Identifier: MIT
package org.simplity.server.core.data;

/**
 * db operation
 *
 * @author simplity.org
 *
 */
public enum IoType {
	/**
	 * read/fetch/get one row for the primary key
	 */
	GET,
	/**
	 * insert/new/create one row
	 */
	CREATE,
	/**
	 * edit/update/save/submit one row based identified by primary key
	 */
	UPDATE,
	/**
	 * delete/remove/archive one row based on primary key
	 */
	DELETE,
	/**
	 * filter rows based on filter criterion
	 */
	FILTER
}
