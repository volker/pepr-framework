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

import javax.xml.bind.annotation.XmlRootElement;

import org.peprframework.core.Activity;

/**
 * @author Stefan Scherer
 * @version 1.0
 *
 */
@XmlRootElement(name = "GetUpperBody")
public class GetUpperBody extends Activity<GetUpperBodyInput, GetUpperBodyOutput, GetUpperBodyConfiguration> {

	public static final String UPPERBODY_ID = "org.peprframework.activities.video.getupperbody";
	
	@Override
	public void initialize() {
		super.initialize();
		if (getConfiguration() == null)
			setConfiguration(new GetUpperBodyConfiguration());
		System.out.println("UpperBodies: initialized");
	}
	
	@Override
	public String getId() {
		return UPPERBODY_ID;
	}

	@Override
	public GetUpperBodyOutput handleMessage(GetUpperBodyInput msg) {
		GetUpperBodyOutput output = new GetUpperBodyOutput();
		output.bodies = getBodiesFromFaces(msg.faces, msg.height, msg.width);
		
//		printBodies(output.bodies);
		
		return output;
	}
	
//	private void printBodies(Rectangle[] bodies) {
//			System.out.println("Bodies: ");
//			for(Rectangle rect : bodies)
//				System.out.println(rect.x+" "+rect.y+" "+rect.height+" "+rect.width);
//	}

	private Rectangle[] getBodiesFromFaces(Rectangle[] faces, int height, int width) {
		Rectangle[] bodies = new Rectangle[faces.length];
		
		for (int i = 0; i < bodies.length; i++) {
//			bodies[i] = new Rectangle();
//			bodies[i].width = Math.round(faces[i].width*getConfiguration().getBodyWidthFactor());
//			bodies[i].width = (bodies[i].width > width) ? width : bodies[i].width;
//			
//			bodies[i].y = faces[i].y+faces[i].height;
//			bodies[i].y = (bodies[i].y > height) ? height : bodies[i].y;
//			
//			bodies[i].x = Math.round(faces[i].x + (faces[i].width/2)
//						- (faces[i].width*getConfiguration().getBodyWidthFactor()/2));
//			bodies[i].x = (bodies[i].x < 0) ? 0 : bodies[i].x;
//			
//			bodies[i].height = (getConfiguration().getBodyHeight() == -1) ? 
//							height - bodies[i].y : bodies[i].y + getConfiguration().getBodyHeight();
//			bodies[i].height = (bodies[i].height > height) ? height : bodies[i].height;
			
			
			bodies[i] = new Rectangle();
			
			bodies[i].x = (int) (faces[i].x + (faces[i].width / 2) - (faces[i].width*getConfiguration().getBodyWidthFactor()/2));
			bodies[i].x = (bodies[i].x < 0) ? 0 : bodies[i].x;
			
			bodies[i].y = faces[i].y + faces[i].height;
			bodies[i].y = (bodies[i].y >= height) ? (height - 1) : bodies[i].y;
			
			bodies[i].width = (int) (faces[i].width * getConfiguration().getBodyWidthFactor());
			bodies[i].width = (bodies[i].x + bodies[i].width >= width) ? (width - bodies[i].x - 1) : bodies[i].width;
			
			bodies[i].height = (getConfiguration().getBodyHeight() == -1) ? height - bodies[i].y : bodies[i].y + getConfiguration().getBodyHeight();
			bodies[i].height = (bodies[i].y + bodies[i].height >= height) ? (height - bodies[i].y - 1) : bodies[i].height;
		}
		
		return bodies;
	}


}
