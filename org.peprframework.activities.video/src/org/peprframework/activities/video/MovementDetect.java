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

import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;


import org.peprframework.activities.video.domain.FaceContainer;
import org.peprframework.core.Activity;

/**
 * @author Stefan Scherer
 * @version 1.0
 *
 */
@XmlRootElement(name = "MovementDetect")
public class MovementDetect extends Activity<MovementDetectInput, MovementDetectOutput, MovementDetectConfiguration> {

	public static final String MOVEMENT_ID = "org.peprframework.activities.video.movementdetect";

	private transient int[] prev_pixel;
	private transient List<FaceContainer> prev_areas;
	
	private transient PixelGrabber pg = null;
	
	private transient float prev_max_movement, prev_min_movement;


	@Override
	public void initialize() {
		super.initialize();
		if (getConfiguration() == null)
			setConfiguration(new MovementDetectConfiguration());
		prev_max_movement = Long.MIN_VALUE;
		prev_min_movement = Long.MAX_VALUE;
		System.out.println("MovementDetection: initialized");
	}

	@Override
	public String getId() {
		return MOVEMENT_ID;
	}

	@Override
	public MovementDetectOutput handleMessage(MovementDetectInput msg) {
		MovementDetectOutput output = new MovementDetectOutput();
		// sum of absolute differences for activity measure
		List<Long> sad = new LinkedList<Long>();

		
		
		if(prev_pixel == null || prev_areas == null){
			prev_pixel = msg.pixel;
			prev_areas = msg.areas;
		}

		int[] pixel_current_area = null, pixel_prev_area = null;

		MemoryImageSource mis = new MemoryImageSource(msg.width, msg.height, msg.pixel, 0, msg.width ); // not used
		MemoryImageSource mis_prev = new MemoryImageSource(msg.width, msg.height, prev_pixel, 0, msg.width ); // not used
		for (FaceContainer area : msg.areas) {
			pixel_current_area = new int[area.getFace().width * area.getFace().height];
			pg = new PixelGrabber(mis, area.getFace().x, area.getFace().y, area.getFace().width, area.getFace().height, pixel_current_area, 0, area.getFace().width);
			try {
				pg.grabPixels();
			} catch (InterruptedException e) {
				System.err.println("interrupted waiting for pixels!");
				return null;
			}
			for(FaceContainer prev_area : prev_areas){
				if(prev_area.getId() == area.getId()){
					pixel_prev_area = new int[area.getFace().width * area.getFace().height];
					// start coordinates according to previous face however the width and height according to the new one..
					// -> same size of array necessary...
					pg = new PixelGrabber(mis_prev, prev_area.getFace().x, prev_area.getFace().y, area.getFace().width, area.getFace().height, pixel_prev_area, 0, area.getFace().width);
					try {
						pg.grabPixels();
					} catch (InterruptedException e) {
						System.err.println("interrupted waiting for pixels!");
						return null;
					}
					long temp_sad = 0;
					for(int i = 0; i < pixel_current_area.length; i++)
						temp_sad += Math.abs(pixel_current_area[i]-pixel_prev_area[i]);
					sad.add(temp_sad/pixel_current_area.length);
				}	
			}
		}
		
		prev_pixel = msg.pixel;
		prev_areas = msg.areas;
	
		// TODO: check for easier transformation from list to array!
		output.movement = new float[sad.size()];
		for(int i = 0; i < sad.size(); i++){
			output.movement[i] = sad.get(i);
			prev_max_movement = Math.max(prev_max_movement, output.movement[i]);
			prev_min_movement = Math.min(prev_min_movement, output.movement[i]);
		}
		
		if(getConfiguration().isNormalize())
			output.movement = normalize(output.movement);
		
		if(output.movement.length == 0){
			output.movement = new float[1];
			output.movement[0] = 0.0f;
		}

//		printMovement(output.movement);
		
		return output;
	}

	private float[] normalize(float[] fs) {
		int norm_min = -1, norm_max = 1;
		for(int i = 0; i < fs.length; i++)
			fs[i] = (fs[i]-prev_min_movement)/(prev_max_movement-prev_min_movement+0.1f)
						  *(norm_max - norm_min)+norm_min;
		return fs;
	}

//	private void printMovement(float[] fs) {
//		for(int i = 0; i < fs.length; i++)
//			System.out.println("area id: "+i+" sum of absolute difference: "+fs[i]);
//	}

}
