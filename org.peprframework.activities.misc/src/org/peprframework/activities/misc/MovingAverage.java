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

import org.peprframework.core.Activity;

/**
 * @author Volker Fritzsch
 * @version 1.0
 *
 */
@XmlRootElement(name = "MovingAverage")
public class MovingAverage extends Activity<MovingAverageInput, MovingAverageOutput, MovingAverageConfiguration> {

	private transient double[] movingAverage;
	
	/* (non-Javadoc)
	 * @see org.peprframework.core.Activity#handleMessage(java.lang.Object)
	 */
	@Override
	public MovingAverageOutput handleMessage(MovingAverageInput msg) {
		if (this.movingAverage == null) {
			movingAverage = new double[msg.current.length];
		}
		
		for (int i = 0; i < msg.current.length; i++) {
			this.movingAverage[i] = msg.current[i] * (1d - getConfiguration().getAlpha()) + this.movingAverage[i] * getConfiguration().getAlpha();	
		}
		
		MovingAverageOutput output = new MovingAverageOutput();
		output.movingAverage = this.movingAverage;
		
		return output;
	}

	/* (non-Javadoc)
	 * @see org.peprframework.core.Activity#getId()
	 */
	@Override
	public String getId() {
		return "org.peprframework.activities.misc.movingaverage";
	}
	
}
