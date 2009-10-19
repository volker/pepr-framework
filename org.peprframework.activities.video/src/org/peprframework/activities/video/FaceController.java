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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.peprframework.core.Activity;

/**
 * @author Stefan Scherer
 * @author Volker Fritzsch
 * @version 1.0
 *
 */
@XmlRootElement(name = "FaceController")
public class FaceController extends Activity<FacesInput, FaceControllerOutput, FaceControllerConfiguration>{

	public static final String FACECONTROLLER_ID = "org.peprframework.activities.video.FaceController";
	private transient Rectangle[] prev_faces;
	private transient float[] prev_faces_prob;

	@Override
	public void initialize() {
		super.initialize();
		if (getConfiguration() == null)
			setConfiguration(new FaceControllerConfiguration());
		prev_faces = new Rectangle[0];
		prev_faces_prob = new float[0];
	}

	@Override
	public FaceControllerOutput handleMessage(FacesInput msg) {
		FaceControllerOutput output = new FaceControllerOutput();
		//System.out.println("Number of Faces in Input: "+msg.faces.length);
		FacesInput prunedInput = removeOverlappingFaces(msg);
		//System.out.println("Number of Faces in pruned: "+prunedInput.faces.length);

		// if we have a face that lost probability remove it from prev_faces
		if(faceGone())
			removeGoneFace();

		// if no faces were detected before use newly detected faces
		// or there are as many faces as we had before
		// or more: use them
		if(prev_faces.length <= prunedInput.faces.length){
			output.faces = prunedInput.faces;
			prev_faces = prunedInput.faces;
			prev_faces_prob = new float[prev_faces.length];

			for(int i = 0; i < prev_faces_prob.length; i++)
				prev_faces_prob[i] = 1.0f;

			output.smilingMouth = setSmiles(msg, prunedInput.faces);
			
			//prev_max_faces = prunedInput.faces.length;
			return output;
		}
		// if we lost one or more of the faces find the missing ones
		else{
			int n_lostFaces = prev_faces.length-prunedInput.faces.length;
			Rectangle[] reconstructed = new Rectangle[prev_faces.length];

			// fill the reconstruction with the currently detected faces
			for(int i = 0; i < prunedInput.faces.length; i++){
				reconstructed[i] = prunedInput.faces[i];
			}


			List<Rectangle> previousFaces = new ArrayList<Rectangle>(Arrays.asList(this.prev_faces));
			List<Rectangle> currentFaces = Arrays.asList(prunedInput.faces);

			for(int i = 0; i < n_lostFaces; i++) {
				int indexOfNextMissingFace = findMissingFace(previousFaces, currentFaces);
				Rectangle missingFace = previousFaces.remove(indexOfNextMissingFace);
//				System.out.println("currentFaces.size(): " + currentFaces.size() + " / i: " + i + " / missing Face: " + indexOfNextMissingFace + " / previousFaces.size(): " + previousFaces.size());


				//				reconstructed[prunedInput.faces.length+i] = missingFace(reconstructed, prunedInput.faces.length);
				reconstructed[prunedInput.faces.length+i] = missingFace;
				prev_faces_prob[prunedInput.faces.length+i] *= getConfiguration().getDecay();
//				printProbabilities();
			}

			prev_faces = reconstructed;
			output.faces = prev_faces;
			output.smilingMouth = setSmiles(msg, reconstructed);


			return output;
		}	
	}

	private Rectangle[] setSmiles(FacesInput msg, Rectangle[] facesIn) {
		if (msg.smilingMouth == null)
			return new Rectangle[0];
		
		// remove smiles from non-controlled faces
		List<Rectangle> smiles = new LinkedList<Rectangle>();
		//if(msg.smilingMouth != null) {
			for(Rectangle rect : facesIn) {
				for(Rectangle smile : msg.smilingMouth) {
//					System.out.println("Rectangle: x "+rect.x+" y "+rect.y+" width "+rect.width+" height "+rect.height);
//					System.out.println("Smile: x "+smile.x+" y "+smile.y+" width "+smile.width+" height "+smile.height);
//					System.out.println("Rect contains Smile: "+rect.contains(smile));
					if(rect.contains(smile)) {
						smiles.add(smile);
					}
				}
			}
				
		//}
		
		return smiles.toArray(new Rectangle[smiles.size()]);
	}

	private void removeGoneFace() {
		ArrayList<Rectangle> new_prev_faces = new ArrayList<Rectangle>();
		ArrayList<Float> new_prev_faces_prob = new ArrayList<Float>();

		for(int i = 0; i < prev_faces_prob.length; i++){
			if(prev_faces_prob[i] > getConfiguration().getMinProbability()){
				new_prev_faces.add(prev_faces[i]);
				new_prev_faces_prob.add(prev_faces_prob[i]);
			}	
		}

		prev_faces = new Rectangle[new_prev_faces.size()];
		prev_faces_prob = new float[new_prev_faces_prob.size()];

		for(int i = 0; i < prev_faces.length; i++){
			prev_faces[i] = new_prev_faces.get(i);
			prev_faces_prob[i] = new_prev_faces_prob.get(i);
		}

	}

	private boolean faceGone() {
		for(float prob : prev_faces_prob)
			if(prob < getConfiguration().getMinProbability())
				return true;
		return false;
	}

//	private void printProbabilities() {
//		System.out.print("Probabilities: ");
//		for(float prob : prev_faces_prob)
//			System.out.print(prob+" ");
//		System.out.println();
//	}

	// calculate the maximal minimal distance between the elements in reconstructed and prev_faces
	private int findMissingFace(List<Rectangle> previousFaces, List<Rectangle> currentFaces) {
		int maximalMinimalDistance = Integer.MIN_VALUE;
		int previousFaceWithMaximalMinimalDistance = 0;

		for (int i = 0; i < previousFaces.size(); i++) {
			if (previousFaces.get(i) == null)
				continue;

			int minimalDistance = Integer.MAX_VALUE;
			for (int j = 0; j < currentFaces.size(); j++) {
				if (currentFaces.get(j) == null)
					continue;

				int distance = (int) previousFaces.get(i).getLocation().distance(currentFaces.get(j).getLocation());
				if (distance < minimalDistance) {
					minimalDistance = distance;
				}
			}

			if (minimalDistance > maximalMinimalDistance) {
				maximalMinimalDistance = minimalDistance;
				previousFaceWithMaximalMinimalDistance = i;
			}
		}

		return previousFaceWithMaximalMinimalDistance;
	}

	private FacesInput removeOverlappingFaces(FacesInput msg) {
		FacesInput prunedInput = new FacesInput();

		ArrayList<Rectangle> faceList = new ArrayList<Rectangle>();
		for(int i = 0; i < msg.faces.length; i++)
			faceList.add(msg.faces[i]);

		for(int i = 0; i < msg.faces.length-1; i++){
			for(int j = i+1; j < msg.faces.length; j++){
				if(msg.faces[i].intersects(msg.faces[j]))
				{
					Rectangle inter = msg.faces[i].intersection(msg.faces[j]);
					int sizeInter = inter.height*inter.width;

					// get the size of the larger Rectangle
					int sizeI = msg.faces[i].height*msg.faces[i].width;
					int sizeJ = msg.faces[j].height*msg.faces[j].width;
					int smallerSize = (sizeI < sizeJ) ? sizeI : sizeJ;

					// if the overlapping size is bigger than minOverlap remove the item
					if(sizeInter/smallerSize > getConfiguration().getMinOverlap()){
						// leave the smaller rectangle out
						int leaveOut = (sizeI < sizeJ) ? i : j;
						faceList.remove(msg.faces[leaveOut]);
					}
				}
			}
		}

		prunedInput.faces = new Rectangle[faceList.size()];

		for(int i = 0; i < prunedInput.faces.length; i++){
			prunedInput.faces[i] = faceList.get(i);
		}

		//prunedInput.faces = (Rectangle[]) faceList.toArray();

		return prunedInput;
	}

	@Override
	public String getId() {
		return FACECONTROLLER_ID;
	}

}
