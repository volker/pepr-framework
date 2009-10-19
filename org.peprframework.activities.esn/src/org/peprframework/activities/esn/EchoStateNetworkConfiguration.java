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
package org.peprframework.activities.esn;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.peprframework.core.ConfigurableProperty;
import org.peprframework.core.Configuration;

/**
 * @author Volker Fritzsch
 * @version 1.0
 *
 */
@XmlRootElement(name="EchoStateNetworkConfiguration")
@XmlAccessorType(XmlAccessType.FIELD)
public class EchoStateNetworkConfiguration extends Configuration {

	//	@ConfigurableProperty(label="Alpha")
//	@XmlElement
//	private double alpha;
	
	@ConfigurableProperty(label="Direct Output Weights")
	@XmlElement
	private String directOutputWeightsFile;
	
	@ConfigurableProperty(label="Input Weights")
	@XmlElement
	private String inputWeightsFile;
	
	@ConfigurableProperty(label="Internal Weights")
	@XmlElement
	private String internalWeightsFile;
	
@ConfigurableProperty(label="Output Weights")
	@XmlElement
	private String outputWeightsFile;

//	public double getAlpha() {
//		return alpha;
//	}

	public String getDirectOutputWeightsFile() {
		return directOutputWeightsFile;
	}

	public String getInputWeightsFile() {
		return inputWeightsFile;
	}

	public String getInternalWeightsFile() {
		return internalWeightsFile;
	}

	public String getOutputWeightsFile() {
		return outputWeightsFile;
	}

//	public void setAlpha(double alpha) {
//		this.alpha = alpha;
//	}

	public void setDirectOutputWeightsFile(String directOutputWeightsFile) {
		String oldValue = this.directOutputWeightsFile;
		this.directOutputWeightsFile = directOutputWeightsFile;
		firePropertyChanged("directOutputWeightsFile", oldValue, directOutputWeightsFile);
	}

	public void setInputWeightsFile(String inputWeightsFile) {
		String oldValue = this.inputWeightsFile;
		this.inputWeightsFile = inputWeightsFile;
		firePropertyChanged("inputWeightsFile", oldValue, inputWeightsFile);
	}

	public void setInternalWeightsFile(String internalWeightsFile) {
		String oldValue = this.internalWeightsFile;
		this.internalWeightsFile = internalWeightsFile;
		firePropertyChanged("internalWeightsFile", oldValue, internalWeightsFile);
	}

	public void setOutputWeightsFile(String outputWeightsFile) {
		String oldValue = this.outputWeightsFile;
		this.outputWeightsFile = outputWeightsFile;
		firePropertyChanged("outputWeightsFile", oldValue, outputWeightsFile);
	}
	
}
