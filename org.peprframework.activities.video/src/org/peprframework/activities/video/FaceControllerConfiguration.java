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
package org.peprframework.activities.video;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.peprframework.core.Configuration;

/**
 * @author Stefan Scherer
 * @author Volker Fritzsch
 * @version 1.0
 *
 */
@XmlRootElement(name = "FaceControllerConfiguration")
public class FaceControllerConfiguration extends Configuration {

	@XmlElement
	private float minOverlap = 0.2f;
	
	@XmlElement
	private float minProbability = 0.01f;
	
	@XmlElement
	private float decay = 0.9f;

	public float getMinOverlap() {
		return minOverlap;
	}

	public void setMinOverlap(float minOverlap) {
		this.minOverlap = minOverlap;
	}

	public void setDecay(float decay) {
		this.decay = decay;
	}

	public float getDecay() {
		return decay;
	}

	public void setMinProbability(float minProbability) {
		this.minProbability = minProbability;
	}

	public float getMinProbability() {
		return minProbability;
	}
}
