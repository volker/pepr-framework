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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.MemoryImageSource;

import javax.swing.JFrame;
import javax.xml.bind.annotation.XmlRootElement;

import org.peprframework.core.Activity;
import org.peprframework.core.Configuration;

/**
 * @author Stefan Scherer
 * @version 1.0
 *
 */
@XmlRootElement(name = "MarkFaces")
public class MarkFaces extends Activity<FacesInput, Void, Configuration> {

final static String MARKFACES_ID = "org.peprframework.activities.video.markfaces";
	
	private transient JFrame frame;
	
	private transient Image image;
	private transient Rectangle[] faces = new Rectangle[0];

	private transient Rectangle[] smilingMouth = new Rectangle[0];
	
	
	@SuppressWarnings("serial")
	@Override
	public void initialize() {
		super.initialize();
		frame = new JFrame(this.name) {		
			@Override
			public void paint(Graphics g) {
				super.paint(g);
				
				if (image != null) {
					g.drawImage(image, 0, 0, null);
					
					// draw squares
					g.setColor( Color.RED );
					for( Rectangle rect : faces )
						g.drawRect( rect.x, rect.y, rect.width, rect.height );
					
					
					// draw squares
					g.setColor( Color.GREEN );
					for( Rectangle rect : smilingMouth)
						g.drawRect( rect.x, rect.y, rect.width, rect.height );
				
				}
			}
			
		};
		frame.setSize(320, 240);
		frame.setVisible(true);
		System.out.println("MarkFaces: initialized.");
	}
	
	@Override
	public void terminate() {
		super.terminate();
		
		frame.setVisible(false);
		frame.dispose();
	}
	
	@Override
	public String getId() {
		return MARKFACES_ID;
	}

	@Override
	public Void handleMessage(FacesInput msg) {
		int[] pixel = msg.pixel;
		faces = msg.faces;
		if (this.faces == null)
			this.faces = new Rectangle[0];
		
		smilingMouth = msg.smilingMouth;
		if (this.smilingMouth == null)
			this.smilingMouth = new Rectangle[0];
		
		MemoryImageSource imageProducer = new MemoryImageSource(msg.width, msg.height, pixel, 0, msg.width);
		if (image != null) {
			image.flush();
		}
		
		image = frame.createImage(imageProducer);
		frame.repaint();
		return null;
	}

}

