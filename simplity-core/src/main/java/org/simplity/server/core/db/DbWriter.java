// SPDX-License-Identifier: MIT
package org.simplity.server.core.db;

import java.sql.SQLException;

/**
 * interface/lambda to carry out read-write operation under a transaction that
 * is managed by the driver. This interface is created instead of using standard
 * functions in java.util because the function needs to throw an exception
 *
 *
 * @author simplity.org
 *
 */
public interface DbWriter {

	/**
	 * function that accesses the db within a transaction boundary. The transaction
	 * is managed by the called driver, and not this function.
	 *
	 * @param handle
	 * @return true if all OK. false in case you detect some condition because of
	 *         which the transaction is to be cancelled/rolled back. DbDriver uses
	 *         this returned value to decide whether to commit or roll-back the
	 *         transaction.
	 * @throws SQLException
	 */
	boolean readWrite(ReadWriteHandle handle) throws SQLException;

}
