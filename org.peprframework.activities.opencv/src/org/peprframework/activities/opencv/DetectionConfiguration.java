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
@XmlRootElement(name = "DetectionConfiguration")
public class DetectionConfiguration extends Configuration {

	private String filename = "/opt/local/share/opencv/haarcascades/haarcascade_frontalface_alt.xml";
	
	private double scale = 1.8d;
	
	private int flags = 0;
	
	private int neighbours = 2;
	
	private int width = 20;
	
	private int height = 20;
	
	/**
	 * @return the filename
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * @param filename the filename to set
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}

	/**
	 * @return the scale
	 */
	public double getScale() {
		return scale;
	}

	/**
	 * @param scale the scale to set
	 */
	public void setScale(double scale) {
		this.scale = scale;
	}

	/**
	 * @return the flags
	 */
	public int getFlags() {
		return flags;
	}

	/**
	 * @param flags the flags to set
	 */
	public void setFlags(int flags) {
		this.flags = flags;
	}

	/**
	 * @return the neighbours
	 */
	public int getNeighbours() {
		return neighbours;
	}

	/**
	 * @param neighbours the neighbours to set
	 */
	public void setNeighbours(int neighbours) {
		this.neighbours = neighbours;
	}

	/**
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * @param width the width to set
	 */
	public void setWidth(int width) {
		this.width = width;
	}

	/**
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * @param height the height to set
	 */
	public void setHeight(int height) {
		this.height = height;
	}

}
