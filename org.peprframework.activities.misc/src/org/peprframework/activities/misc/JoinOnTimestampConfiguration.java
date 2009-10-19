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

import javax.xml.bind.annotation.XmlRootElement;

import org.peprframework.core.Configuration;

/**
 * @author Volker Fritzsch
 * @version 1.0
 *
 */
@XmlRootElement(name="JoinOnTimestampConfiguration")
public class JoinOnTimestampConfiguration extends Configuration {

	private long maximumDistanceMillis = 0;

	/**
	 * @return the maximumDistanceMillis
	 */
	public long getMaximumDistanceMillis() {
		return maximumDistanceMillis;
	}

	/**
	 * @param maximumDistanceMillis the maximumDistanceMillis to set
	 */
	public void setMaximumDistanceMillis(long maximumDistanceMillis) {
		this.maximumDistanceMillis = maximumDistanceMillis;
	}
	
}
