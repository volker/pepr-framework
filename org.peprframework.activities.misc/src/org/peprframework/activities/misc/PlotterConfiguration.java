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
@XmlRootElement(name = "PlotterConfiguration")
public class PlotterConfiguration extends Configuration {

	private double upperBarrier = 1.3d;
	
	private double lowerBarrier = 0d;

	/**
	 * @return the upperBarrier
	 */
	public double getUpperBarrier() {
		return upperBarrier;
	}

	/**
	 * @param upperBarrier the upperBarrier to set
	 */
	public void setUpperBarrier(double upperBarrier) {
		this.upperBarrier = upperBarrier;
	}

	/**
	 * @return the lowerBarrier
	 */
	public double getLowerBarrier() {
		return lowerBarrier;
	}

	/**
	 * @param lowerBarrier the lowerBarrier to set
	 */
	public void setLowerBarrier(double lowerBarrier) {
		this.lowerBarrier = lowerBarrier;
	}
	
}
