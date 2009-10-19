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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.peprframework.core.Configuration;


/**
 * @author Stefan Scherer
 * @version 1.0
 *
 */
@XmlRootElement(name = "BinaryVisualizationConfiguration")
public class BinaryVisualizationConfiguration extends Configuration {

	@XmlElement
	private float threshold = 0.0f;
	
	@XmlElement
	private String falseImage = "/Users/StefanScherer/Documents/normal.gif";
	
	@XmlElement
	private String trueImage = "/Users/StefanScherer/Documents/smile.gif";

	public void setThreshold(float threshold) {
		this.threshold = threshold;
	}

	public float getThreshold() {
		return threshold;
	}

	public void setFalseImage(String falseImage) {
		this.falseImage = falseImage;
	}

	public String getFalseImage() {
		return falseImage;
	}

	public void setTrueImage(String trueImage) {
		this.trueImage = trueImage;
	}

	public String getTrueImage() {
		return trueImage;
	}
	
	
}
