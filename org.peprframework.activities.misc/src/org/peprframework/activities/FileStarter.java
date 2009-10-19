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
package org.peprframework.activities;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

import javax.xml.bind.annotation.XmlRootElement;

import org.peprframework.core.Starter;

/**
 * @author Volker Fritzsch
 * @version 1.0
 *
 */
@XmlRootElement(name = "FileStarter")
public class FileStarter extends Starter<FileStarterOutput, FileStarterConfiguration> {

	private transient BufferedReader reader = null;

	@Override
	public void initialize() {
		super.initialize();

		try {
			reader = new BufferedReader(new FileReader(getConfiguration().getFilename()));
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void starterLoop() {
		try {
			String line = null;
			long frame = 0;
			
			while ((line = reader.readLine()) != null) {
				FileStarterOutput output = new FileStarterOutput();			
				StringTokenizer tokenizer = new StringTokenizer(line, ",");
				output.rowData = new double[tokenizer.countTokens()];
				for (int i = 0; i < output.rowData.length; i++) {
					output.rowData[i] = Double.parseDouble(tokenizer.nextToken());
				}
//				System.out.println(frame / 10d);
				frame++;
				startProcess(output);
				Thread.sleep(100);
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void terminate() {
		super.terminate();

		try {
			if (reader != null)
				reader.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see org.peprframework.core.Activity#getId()
	 */
	@Override
	public String getId() {
		return "org.peprframework.starters.misc.file";
	}
}
