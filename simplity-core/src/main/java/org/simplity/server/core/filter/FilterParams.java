package org.simplity.server.core.filter;

import org.simplity.server.core.Conventions;
import org.simplity.server.core.Message;
import org.simplity.server.core.json.JsonUtil;
import org.simplity.server.core.service.InputData;
import org.simplity.server.core.service.ServiceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Data structure with details that are received from the client for a filter
 * operations Also, this is the data structure used for configuring a report.
 * Not surprising because the core of reporting is filtering data from a a data
 * source
 *
 * All fields are public at this time for easy access and population from JSON.
 * A builder is also provided for good DX. This will be converted to an
 * immutable object later
 */
public class FilterParams {
	private static final Logger logger = LoggerFactory.getLogger(FilterParams.class);

	/**
	 * optional. maximum number of rows to be filtered.
	 */
	public int maxRows;
	/**
	 * optional. default is to get all the fields
	 */
	public String[] fields;
	/**
	 * Generally, should have at least one condition. However, if this is empty or
	 * null, then all the rows will be retrieved
	 */
	public FilterCondition[] filters;
	/**
	 * optional. How the rows are to be sorted
	 */
	public SortBy[] sorts;

	/**
	 * parse filter parameters from a payload
	 *
	 * @param input
	 * @param ctx   optional serviceCOntext. If present, an error message is added
	 *              in case of parse error;
	 * @return parse FilterParams, or null if there is a parse error
	 */
	public static FilterParams parse(InputData input, ServiceContext ctx) {
		try {
			return JsonUtil.load(input, FilterParams.class);
		} catch (Exception e) {
			logger.error(e.getMessage());
			if (ctx != null) {
				ctx.addMessage(Message.newError(Conventions.MessageId.INVALID_DATA));
			}

			return null;
		}
	}

	/**
	 * default constructor
	 */
	public FilterParams() {
		// default constructor
	}

	/**
	 * constructor with all fields
	 *
	 * @param maxRows maximum number of rows to be filtered.
	 * @param fields  fields to be retrieved
	 * @param filters filter conditions
	 * @param sorts   sort details
	 */
	public FilterParams(int maxRows, String[] fields, FilterCondition[] filters, SortBy[] sorts) {
		this.maxRows = maxRows;
		this.fields = fields;
		this.filters = filters;
		this.sorts = sorts;
	}

}
