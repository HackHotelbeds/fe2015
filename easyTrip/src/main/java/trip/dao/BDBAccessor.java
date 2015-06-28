package trip.dao;

import com.sleepycat.je.*;
import org.slf4j.LoggerFactory;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Repository;
import trip.Services.HyperSqlDbServer;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
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
@Repository
public class BDBAccessor implements SmartLifecycle {
	private final org.slf4j.Logger logger = LoggerFactory.getLogger(BDBAccessor.class);
	private Environment myDbEnvironment = null;
	private Database myDatabase = null;
	private boolean running = false;
	private EnvironmentConfig envConfig;
	DatabaseConfig dbConfig;

	public BDBAccessor() {
		Properties properties = new Properties();
		// sets the DB to work "In Memory"
		properties.put(EnvironmentConfig.LOG_MEM_ONLY, "true");
		// create an enviroment configuration object with the immutable parameter
		envConfig = new EnvironmentConfig(properties);
		envConfig.setAllowCreate(true);
		envConfig.setCacheSize(134217728); // 128 x 1024 x 1024
		envConfig.setTransactional(true);

		dbConfig = new DatabaseConfig();
		dbConfig.setAllowCreate(true);
	}


	public void write (String key, String value) {
		try {
			DatabaseEntry theKey = new DatabaseEntry(key.getBytes("UTF-8"));
			DatabaseEntry theData = new DatabaseEntry(value.getBytes("UTF-8"));
			myDatabase.put(null, theKey, theData);
		} catch (Exception e) {
			// Exception handling
			logger.error("Error writing to database: ", e);
		}
	}

	public String read (String key) {
		String foundData = "";
		try {
			// Create two DatabaseEntry instances:
			// theKey is used to perform the search
			// theData will hold the value associated to the key, if found
			DatabaseEntry theKey = new DatabaseEntry(key.getBytes("UTF-8"));
			DatabaseEntry theData = new DatabaseEntry();

			// Call get() to query the database
			if (myDatabase.get(null, theKey, theData, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
				// Translate theData into a String.
				byte[] retData = theData.getData();
				foundData = new String(retData, "UTF-8");
				logger.info("key: '" + key + "' data: '" + foundData + "'.");
			} else {
				logger.info("No record found with key '" + key + "'.");
			}
		} catch (Exception e) {
			// Exception handling
			logger.error("Error reading from database: ", e);
		}
		return foundData;
	}

	public List<String> readAll () {
		Cursor myCursor = null;
		List<String> responseValues = null;
		try {
			myCursor = myDatabase.openCursor(null, null);

			// Cursors returns records as pairs of DatabaseEntry objects
			DatabaseEntry foundKey = new DatabaseEntry();
			DatabaseEntry foundData = new DatabaseEntry();

			// Retrieve records with calls to getNext() until the
			// return status is not OperationStatus.SUCCESS
			while (myCursor.getNext(foundKey, foundData, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
				String keyString = null;
				String dataString = null;
				try {
					keyString = new String(foundKey.getData(), "UTF-8");
					dataString = new String(foundData.getData(), "UTF-8");
				} catch (UnsupportedEncodingException e) {
					logger.error("Unhandled error, ", e);
				}
				logger.info("Key| Data : " + keyString + " | " + dataString + "");
				if (responseValues == null) {
					responseValues =  new ArrayList<>();
				}
				responseValues.add(dataString);
			}
		} catch (DatabaseException de) {
			logger.error("Error reading from database: ", de);
		} finally {
			try {
				if (myCursor != null) {
					myCursor.close();
				}
			} catch(DatabaseException dbe) {
				logger.error("Error closing cursor:  ", dbe);
			}
		}
		return responseValues;
	}

	public void update (String key, String value) {
		String response = this.read(key);
		this.write(key, response);
	}

	public void delete (String key) {
		try {
			DatabaseEntry theKey = new DatabaseEntry(key.getBytes("UTF-8"));
			// Delete the entry (or entries) with the given key
			myDatabase.delete(null, theKey);
		} catch (Exception e) {
			// Exception handling
			logger.error("Error starting BDB server. ", e);
		}
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

	@Override
	public void start() {
		boolean serverUp = false;
		boolean databaseUp = false;

		try {
			if (myDbEnvironment == null) {
				logger.info("Starting BDB server environment...");
				// Open the environment, creating one if it does not exist

				File file = new File("/tmp/tempServer");
				myDbEnvironment = new Environment(file, envConfig);
				serverUp = true;
			}

			if (myDatabase == null) {
				logger.info("Starting BDB database...");
				// Open the database, creating one if it does not exist
				myDatabase = myDbEnvironment.openDatabase(null, "TestDatabase", dbConfig);
				databaseUp = true;
			}

			running = serverUp && databaseUp;

		} catch (DatabaseException dbe) {
			//  Exception handling
			logger.error("Error starting BDB server. ", dbe);
		}
	}

	@Override
	public void stop() {
		logger.info("Stopping BDB server...");
		try {
			if (myDatabase != null) {
				myDatabase.close();
			}
			if (myDbEnvironment != null) {
				myDbEnvironment.close();
			}
		} catch (DatabaseException dbe) {
			// Exception handling
			logger.error("Error stopping BDB. ", dbe);
		}
	}

	@Override
	public boolean isRunning() {
		if (myDbEnvironment == null || myDatabase == null) {
			running = false;
			logger.error("Database or environment is not running.");
		} else {
			running = true;
			logger.info("Database is running");
		}
		return running;
	}

	@Override
	public int getPhase() {
		return 0;
	}
}
