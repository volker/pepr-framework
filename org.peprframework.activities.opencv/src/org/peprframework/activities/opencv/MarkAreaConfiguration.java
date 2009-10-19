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

import javax.xml.bind.annotation.XmlRootElement;

import org.peprframework.core.Configuration;

/**
 * @author Volker Fritzsch
 * @version 1.0
 *
 */
@XmlRootElement(name = "MarkAreaConfiguration")
public class MarkAreaConfiguration extends Configuration {

	private int red;
	
	private int green;
	
	private int blue;

	/**
	 * @return the blue
	 */
	public int getBlue() {
		return blue;
	}

	/**
	 * @return the green
	 */
	public int getGreen() {
		return green;
	}

	/**
	 * @return the red
	 */
	public int getRed() {
		return red;
	}

	/**
	 * @param blue the blue to set
	 */
	public void setBlue(int blue) {
		this.blue = blue;
	}

	/**
	 * @param green the green to set
	 */
	public void setGreen(int green) {
		this.green = green;
	}

	/**
	 * @param red the red to set
	 */
	public void setRed(int red) {
		this.red = red;
	}
	
}
