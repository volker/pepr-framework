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
 * @author Stefan Scherer
 * @version 1.0
 *
 */
@XmlRootElement(name = "WriteVideoToFileConfiguration")
public class WriteVideoToFileConfiguration extends Configuration {

	@XmlElement
	private String filename = "";
	
	@XmlElement
	private int isColor = 1;
	
	@XmlElement
	private int fps = 30;

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getFilename() {
		return filename;
	}

	public void setIsColor(int iscolor) {
		this.isColor = iscolor;
	}

	public int getIsColor() {
		return isColor;
	}

	public void setFps(int fps) {
		this.fps = fps;
	}

	public int getFps() {
		return fps;
	}
}
