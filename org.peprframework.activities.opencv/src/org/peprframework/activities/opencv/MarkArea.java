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

import java.awt.Rectangle;

import javax.xml.bind.annotation.XmlRootElement;

import org.peprframework.core.Activity;

/**
 * @author Volker Fritzsch
 * @version 1.0
 *
 */
@XmlRootElement(name = "MarkArea")
public class MarkArea extends Activity<MarkAreaInput, MarkAreaOutput, MarkAreaConfiguration> {

	private static final String ID = "org.peprframework.activities.opencv.markarea";
	
	private transient int rgb;
	
	public MarkAreaOutput handleMessage(MarkAreaInput msg) {
		MarkAreaOutput result = new MarkAreaOutput();
		
		result.width = msg.width;
		result.height = msg.height;
		result.pixel = new int[msg.pixel.length];
		System.arraycopy(msg.pixel, 0, result.pixel, 0, result.pixel.length);
		
		for (Rectangle area : msg.areas) {
			int x0 = area.x;
			int y0 = area.y;
			int x1 = area.x + area.width;
			int y1 = area.y + area.height;
			
			// upper line
			for (int i = x0; i < x1; i++)
				result.pixel[i + (y0 * msg.width)] = rgb;
			
			// lower line
			for (int i = x0; i < x1; i++)
				result.pixel[i + (y1 * msg.width)] = rgb;
			
			// left line
			for (int i = y0; i < y1; i++)
				result.pixel[x0 + (i * msg.width)] = rgb;
			
			// right line
			for (int i = y0; i < y1; i++)
				result.pixel[x1 + (i * msg.width)] = rgb;
		}
		
		return result;
	}

	/* (non-Javadoc)
	 * @see org.peprframework.core.Activity#getId()
	 */
	@Override
	public String getId() {
		return ID;
	};
	
	/* (non-Javadoc)
	 * @see org.peprframework.core.Activity#initialize()
	 */
	@Override
	public void initialize() {
		super.initialize();
		
		int r = getConfiguration().getRed();
		int g = getConfiguration().getGreen();
		int b = getConfiguration().getBlue();
		
		this.rgb = 0xff000000 ^ ((r & 0xff) << 16) ^ ((g & 0xff) << 8) ^ (b & 0xff); 
	}
}
