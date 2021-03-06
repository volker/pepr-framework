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

import java.awt.Point;

/**
 * @author Sascha Meudt
 * @author Lutz Bigalke
 * @version 1.0
 *
 */
public class DrawOpticalFlowInput {
	public int width;
	public int height;
	public byte[] imageData;
	public int[] pixel;
	public int depth;
	public int nChannels;
	public int widthStep;
	
	public int numFeatures;
	public Point[] frame1_features, frame2_features;
	public byte[] optical_flow_found_feature;
	public float[] optical_flow_feature_error;
}
