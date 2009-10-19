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
import org.peprframework.resources.opencv.OpenCVCore;


/**
 * @author Stefan Scherer
 * @version 1.0
 *
 */
@XmlRootElement(name = "LucasKanadeConfiguration")
public class LucasKanadeConfiguration extends Configuration {

	
	@XmlElement
	private int numFeatures = 400;
	
	@XmlElement
	private int optical_flow_window_height = 3;

	@XmlElement
	private int optical_flow_window_width = 3;
	
	@XmlElement
	private double optical_flow_termination_criteria_epsilon = 0.3;
	
	@XmlElement
	private int optical_flow_termination_criteria_max_iter = 20;
	
	@XmlElement
	private int optical_flow_termination_criteria_type = OpenCVCore.CV_TERMCRIT_ITER | OpenCVCore.CV_TERMCRIT_EPS;
	
	public int getOptical_flow_window_height() {
		return optical_flow_window_height;
	}

	public void setOptical_flow_window_height(int optical_flow_window_height) {
		this.optical_flow_window_height = optical_flow_window_height;
	}

	public int getOptical_flow_window_width() {
		return optical_flow_window_width;
	}

	public void setOptical_flow_window_width(int optical_flow_window_width) {
		this.optical_flow_window_width = optical_flow_window_width;
	}

	public double getOptical_flow_termination_criteria_epsilon() {
		return optical_flow_termination_criteria_epsilon;
	}

	public void setOptical_flow_termination_criteria_epsilon(
			double optical_flow_termination_criteria_epsilon) {
		this.optical_flow_termination_criteria_epsilon = optical_flow_termination_criteria_epsilon;
	}

	public int getOptical_flow_termination_criteria_max_iter() {
		return optical_flow_termination_criteria_max_iter;
	}

	public void setOptical_flow_termination_criteria_max_iter(
			int optical_flow_termination_criteria_max_iter) {
		this.optical_flow_termination_criteria_max_iter = optical_flow_termination_criteria_max_iter;
	}

	public int getOptical_flow_termination_criteria_type() {
		return optical_flow_termination_criteria_type;
	}

	public void setOptical_flow_termination_criteria_type(
			int optical_flow_termination_criteria_type) {
		this.optical_flow_termination_criteria_type = optical_flow_termination_criteria_type;
	}
	
	public int getNumFeatures() {
		return numFeatures;
	}

	public void setNumFeatures(int numFeatures) {
		this.numFeatures = numFeatures;
	}
}
