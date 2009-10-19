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
 * @version 1.0
 *
 */
@XmlRootElement(name = "GetUpperBodyConfiguration")
public class GetUpperBodyConfiguration extends Configuration {

	// width of a body in comparison with the detected face
	@XmlElement
	private float bodyWidthFactor = 2.5f;
	
	// how far does the body go downwards (if -1 it goes until the bottom of the image)
	@XmlElement
	private int bodyHeight = -1;

	public void setBodyWidthFactor(float bodyWidthFactor) {
		this.bodyWidthFactor = bodyWidthFactor;
	}

	public float getBodyWidthFactor() {
		return bodyWidthFactor;
	}

	public void setBodyHeight(int bodyHeight) {
		this.bodyHeight = bodyHeight;
	}

	public int getBodyHeight() {
		return bodyHeight;
	}
	
}
