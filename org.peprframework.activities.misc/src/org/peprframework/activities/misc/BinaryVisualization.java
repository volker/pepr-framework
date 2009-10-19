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

import javax.swing.JFrame;
import javax.xml.bind.annotation.XmlRootElement;

import org.peprframework.activities.misc.domain.ImagePanel;
import org.peprframework.core.Activity;


/**
 * @author Stefan Scherer
 * @version 1.0
 *
 */
@XmlRootElement(name = "BinaryVisualization")
public class BinaryVisualization extends Activity <BinaryVisualizationInput, Void, BinaryVisualizationConfiguration>{

	final static String BINARYVISUAL_ID = "org.peprframework.activities.misc.binaryvisualization";
	private transient JFrame frame;
	private transient ImagePanel imgPane;
	
	@Override
	public void initialize() {
		super.initialize();
		if (getConfiguration() == null)
			setConfiguration(new BinaryVisualizationConfiguration());
		
		imgPane = new ImagePanel(getConfiguration().getTrueImage(), getConfiguration().getFalseImage());
		
		frame = new JFrame(this.name);
		
		imgPane.getImageManager().show(imgPane, "false");
		
		frame.getContentPane().add(imgPane);
		frame.setSize(200, 200);
		frame.setVisible(true);
		System.out.println("BianryVisualization: initialized.");
	}
	
	@Override
	public void terminate() {
		super.terminate();
		
		frame.setVisible(false);
		frame.dispose();
	}
	
	
	@Override
	public String getId() {
		return BINARYVISUAL_ID;
	}

	@Override
	public Void handleMessage(BinaryVisualizationInput msg) {
		   if(msg.value != null){
			   if(msg.value[0] > getConfiguration().getThreshold())
				   imgPane.getImageManager().show(imgPane, "true");
			   else
				   imgPane.getImageManager().show(imgPane, "false");
		   }
		   else
			   imgPane.getImageManager().show(imgPane, "false");
		return null;
	}

}
