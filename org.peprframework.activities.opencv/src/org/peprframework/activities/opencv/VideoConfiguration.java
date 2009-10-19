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
package org.peprframework.activities.opencv;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.peprframework.core.Configuration;

/**
 * @author Volker Fritzsch
 * @version 1.0
 *
 */
@XmlRootElement(name="VideoConfiguration")
public class VideoConfiguration extends Configuration {
	
	@XmlElement
	private int width = 320;
	
	@XmlElement
	private int height = 240;
	
	@XmlElement
	private String libraryPath = "/opt/local/lib";
	
	@XmlElement
	private String filename = "";

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public String getLibraryPath() {
		return libraryPath;
	}

	public void setLibraryPath(String libraryPath) {
		this.libraryPath = libraryPath;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

}
