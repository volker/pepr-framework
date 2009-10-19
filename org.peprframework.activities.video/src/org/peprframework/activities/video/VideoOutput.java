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

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.MemoryImageSource;

import javax.swing.JFrame;
import javax.xml.bind.annotation.XmlRootElement;

import org.peprframework.core.Activity;

/**
 * @author Volker Fritzsch
 * @version 1.0
 *
 */
@XmlRootElement(name = "VideoOutput")
public class VideoOutput extends Activity<VideoInput, Void, VideoOutputConfiguration> {

	final static String VIDEOOUTPUT_ID = "org.peprframework.activities.video.videooutput";
	
	private transient JFrame frame;
	
	private transient Image image;
	
	private transient boolean warned = false;
	
	
	@SuppressWarnings("serial")
	@Override
	public void initialize() {
		super.initialize();
		if (getConfiguration() == null)
			setConfiguration(new VideoOutputConfiguration());
		frame = new JFrame(this.name) {
			
			
			@Override
			public void paint(Graphics g) {
				super.paint(g);
				
				if (image != null) {
					g.drawImage(image, 0, 0, null);
				}
			}
			
		};
		frame.setSize(getConfiguration().getWidth(), getConfiguration().getHeight());
		frame.setVisible(true);
	}
	
	@Override
	public void terminate() {
		super.terminate();
		
		frame.setVisible(false);
		frame.dispose();
	}
	
	@Override
	public String getId() {
		return VIDEOOUTPUT_ID;
	}

	@Override
	public Void handleMessage(VideoInput msg) {
		int[] pixel = msg.pixel;
		MemoryImageSource imageProducer = new MemoryImageSource(msg.width, msg.height, pixel, 0, msg.width);
		
		// warn the user once that the input video has a different size as the display (frame)
		if(!warned)
			if(getConfiguration().getWidth() != msg.width || getConfiguration().getHeight() != msg.height)
			{
				System.out.println("Warning: size of input video ("+msg.width+"x"+msg.height+") is different from the display canvas "+
				"("+getConfiguration().getWidth()+"x"+getConfiguration().getHeight()+")");
				warned = true;
			}
		
		if (image != null) {
			image.flush();
		}
		image = frame.createImage(imageProducer);
		frame.repaint();
		
		return null;
	}

}
