/**
 * Copyright 2009 pepr Framework
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.peprframework.activities.misc;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Time;
import java.util.concurrent.TimeUnit;

import javax.xml.bind.annotation.XmlRootElement;

import org.peprframework.core.Activity;

/**
 * @author Stefan Scherer
 * @version 1.0
 *
 */
@XmlRootElement(name = "Logger")
public class Logger extends Activity<LoggerInput,Void,LoggerConfiguration> {

	public static final String LOGGER_ID = "org.peprframework.activities.misc.logger";
	private transient PrintWriter bufferedWriter = null;


	public void initialize() {
		super.initialize();

		try {
			bufferedWriter = new PrintWriter(new BufferedWriter(new FileWriter(getConfiguration().getLogfile())));
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("Logger initialized.");
	}

	@Override
	public String getId() {
		return LOGGER_ID;
	}

	@Override
	public Void handleMessage(LoggerInput msg) {

		String logLine = "";

		long temp = 0;

		if(msg.timeInMillis != 0)
			temp = msg.timeInMillis;
		else
			temp = TimeUnit.MILLISECONDS.convert(System.nanoTime(), TimeUnit.NANOSECONDS);
		
		Time time = new Time(temp);
		logLine += time.toString() + getConfiguration().getSeparator();

		for (Object logElement : msg.toLog) {
			logLine += logElement.toString() + getConfiguration().getSeparator();
		}
		
		logLine += "\n";

		//System.out.println(logLine);
		
		bufferedWriter.write(logLine);
		bufferedWriter.flush();
		return null;
	}

	@Override
	public void terminate() {
		bufferedWriter.close();
		super.terminate();
	}

}
