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
package org.peprframework.activities.svm;

import java.io.IOException;

import javax.xml.bind.annotation.XmlRootElement;

import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;

import org.peprframework.core.Activity;

/**
 * @author Volker Fritzsch
 * @version 1.0
 *
 */
@XmlRootElement(name="SupportVectorMachine")
public class SupportVectorMachine extends Activity<SupportVectorMachineInput, SupportVectorMachineOutput, SupportVectorMachineConfiguration> {

	private transient svm_model model;
	
	public void initialize() {
		super.initialize();
		try {
			model = svm.svm_load_model(getConfiguration().getModelFile());
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public SupportVectorMachineOutput handleMessage(SupportVectorMachineInput msg) {
		svm_node[] svmNodes = new svm_node[msg.features.length];
		for (int i = 0; i < msg.features.length; i++) {
			svmNodes[i] = new svm_node();
			svmNodes[i].index = (int) msg.features[i][0];
			svmNodes[i].value = msg.features[i][1];
		}
		
		double value = svm.svm_predict(model,svmNodes);
		
		SupportVectorMachineOutput result = new SupportVectorMachineOutput();
		result.prediction = value;
		
		return result;
	}
	
	public void terminate() {
		super.terminate();
	}

	/* (non-Javadoc)
	 * @see org.peprframework.core.Activity#getId()
	 */
	@Override
	public String getId() {
		return "org.peprframework.activities.svm";
	}

}
