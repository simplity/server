// SPDX-License-Identifier: MIT
package org.simplity.server.core.infra.defalt;

import org.simplity.server.core.ApplicationError;
import org.simplity.server.core.data.Form;
import org.simplity.server.core.data.Record;
import org.simplity.server.core.fn.FunctionDefinition;
import org.simplity.server.core.infra.CompProvider;
import org.simplity.server.core.service.Service;
import org.simplity.server.core.service.ServiceContext;
import org.simplity.server.core.validn.ValueList;
import org.simplity.server.core.valueschema.ValueSchema;

/**
 * 
 * @author simplity.org
 *
 */
public class DefunctCompProvider implements CompProvider {
	private static void error() {
		throw new ApplicationError("The app is not configured to provide components.");
	}

	@Override
	public Form<?> getForm(final String formId) {
		error();
		return null;
	}

	@Override
	public ValueSchema getValueSchema(final String schemaId) {
		error();
		return null;
	}

	@Override
	public ValueList getValueList(final String listId) {
		error();
		return null;
	}

	@Override
	public Service getService(final String serviceName, final ServiceContext ctx) {
		error();
		return null;
	}

	@Override
	public FunctionDefinition getFunction(final String functionName) {
		error();
		return null;
	}

	@Override
	public Record getRecord(final String recordName, final ServiceContext ctx) {
		error();
		return null;
	}

	@Override
	public Record getRecord(final String recordName) {
		error();
		return null;
	}

	@Override
	public Form<?> getForm(final String formId, final ServiceContext ctx) {
		error();
		return null;
	}
}
