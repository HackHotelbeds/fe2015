package trip.Services;

import org.hsqldb.Server;
import org.hsqldb.persist.HsqlProperties;
import org.slf4j.LoggerFactory;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Service;

import java.util.Properties;

/**
 * Copyright 28/06/2015 the original author or authors.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
@Service
public class HyperSqlDbServer implements SmartLifecycle {
	private final org.slf4j.Logger logger = LoggerFactory.getLogger(HyperSqlDbServer.class);
	private HsqlProperties properties;
	private Server server;
	private boolean running = false;

	public HyperSqlDbServer() {
		properties = new HsqlProperties();
		properties.setProperty("server.database.0", "file:c:/hsqldb/demobase");
		properties.setProperty("server.dbname.0", "demobase");
		properties.setProperty("server.remote_open", "true");
		properties.setProperty("hsqldb.reconfig_logging", "false");
	}

	@Override
	public boolean isRunning() {
		if (server != null)
			server.checkRunning(running);
		return running;
	}

	@Override
	public void start() {
		if (server == null) {
			logger.info("Starting HSQL server...");
			server = new Server();
			try {
				server.setProperties(properties);
				server.start();
				running = true;
			} catch (Throwable afe) {
				logger.error("Error starting HSQL server. ", afe);
			}
		}
	}

	@Override
	public void stop() {
		logger.info("Stopping HSQL server...");
		if (server != null) {
			server.stop();
			running = false;
		}
	}

	@Override
	public int getPhase() {
		return 0;
	}

	@Override
	public boolean isAutoStartup() {
		return true;
	}

	@Override
	public void stop(Runnable runnable) {
		stop();
		runnable.run();
	}
}
