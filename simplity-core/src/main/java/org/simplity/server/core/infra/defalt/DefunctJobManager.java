// SPDX-License-Identifier: MIT
package org.simplity.server.core.infra.defalt;

import java.io.Reader;

import org.simplity.server.core.infra.JobManager;
import org.simplity.server.core.job.JobHandle;

/**
 * @author simplity.org
 *
 */
public class DefunctJobManager implements JobManager {

	@Override
	public JobHandle newJob(final Reader reader, final String serviceName) {
		return null;
	}

	@Override
	public JobHandle getJob(final String jobId) {
		return null;
	}

}
