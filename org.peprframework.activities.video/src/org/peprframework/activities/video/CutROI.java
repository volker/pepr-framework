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

import java.awt.Rectangle;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;

import javax.xml.bind.annotation.XmlRootElement;

import org.peprframework.core.Activity;

/**
 * @author Stefan Scherer
 * @version 1.0
 *
 */
@XmlRootElement(name = "CutROI")
public class CutROI extends Activity<CutROIInput, CutROIOutput, CutROIConfiguration> {

	public static final String CUTROI_ID = "org.peprframework.activities.video.cutroi";
	private transient PixelGrabber pg = null;
	
	@Override 
	public void initialize() {
		super.initialize();
		if (getConfiguration() == null)
			setConfiguration(new CutROIConfiguration());
	}
	
	@Override
	public String getId() {
		return CUTROI_ID;
	}

	@Override
	public CutROIOutput handleMessage(CutROIInput msg) {
		CutROIOutput output = new CutROIOutput();
		MemoryImageSource mis = new MemoryImageSource(msg.width, msg.height, msg.pixel, 0, msg.width );
		Rectangle rect = getConfiguration().getROI();

		int[] pixel_ROI = new int[rect.width * rect.height];
		pg = new PixelGrabber(mis, rect.x, rect.y, rect.width, rect.height, pixel_ROI, 0, rect.width);
		try {
		    pg.grabPixels();
		} catch (InterruptedException e) {
		    System.err.println("interrupted waiting for pixels!");
		    return null;
		}
		
		output.pixel_ROI = pixel_ROI;
		output.region = rect;
		return output;
	}

}
