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
@XmlRootElement(name = "FaceTracker")
public class FaceTracker extends Activity<FaceTrackerInput, FaceTrackerOutput, FaceTrackerConfiguration> {

	public static final String FACETRACKER_ID = "org.peprframework.activities.video.facetracker";
	private transient List<FaceContainer> tracked_faces = new LinkedList<FaceContainer>();
	
	@Override
	public void initialize() {
		super.initialize();
		if (getConfiguration() == null)
			setConfiguration(new FaceTrackerConfiguration());
		System.out.println("FaceTracker: initialized");
	}
	
	@Override
	public String getId() {
		return FACETRACKER_ID;
	}

	@Override
	public FaceTrackerOutput handleMessage(FaceTrackerInput msg) {
		FaceTrackerOutput output = new FaceTrackerOutput();
		
		// if there are no faces currently detected (reset all ids)
		if(msg.faces.length == 0){
			tracked_faces.removeAll(tracked_faces);
			output.faces = tracked_faces;
			return output;
		}
		// if there were no tracked faces use the input directly
		if(tracked_faces.isEmpty()){
			for(int i = 0; i < msg.faces.length; i++){
				tracked_faces.add(new FaceContainer(msg.faces[i], tracked_faces.size()+1));
			}
			output.faces = tracked_faces;
			return output;
		}
		// else find the matching face in tracked_faces and msg.faces
		// the matching face is according to the minimal distance
		else{	
			for(int j = 0; j < msg.faces.length; j++){
				int minDist = Integer.MAX_VALUE;
				int index = -1;
				for(int i = 0; i < tracked_faces.size(); i++){
					int curr_dist = (int) msg.faces[j].getLocation().distance(tracked_faces.get(i).getFace().getLocation());
					if(curr_dist < minDist){
						minDist = curr_dist;
						index = i;
					}
				}
				// if the minimal distance is below the maximal allowed distance
				// update the face
				if(minDist < getConfiguration().getMaxDistance()){
					tracked_faces.get(index).setFace(msg.faces[j]);
				}
				// if the minimal distance is above the maximal allowed distance
				// add a new face
				else{
					tracked_faces.add(new FaceContainer(msg.faces[j], tracked_faces.size()+1));
				}
			}

			output.faces = tracked_faces;
			
			return output;
		}
	}

}
