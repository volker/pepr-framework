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

import javax.xml.bind.annotation.XmlRootElement;

import org.peprframework.core.Activity;
import org.peprframework.resources.opencv.OpenCV;
import org.peprframework.resources.opencv.OpenCVCore;
import org.peprframework.resources.opencv.OpenCVCore.CvArr;
import org.peprframework.resources.opencv.OpenCVCore.CvPoint;
import org.peprframework.resources.opencv.OpenCVCore.CvScalar;
import org.peprframework.resources.opencv.OpenCVCore.CvSize;
import org.peprframework.resources.opencv.OpenCVCore.IplImage;

/**
 * @author Sascha Meudt
 * @author Lutz Bigalke
 * @version 1.0
 *
 */
@XmlRootElement(name = "DrawOpticalFlow")
public class DrawOpticalFlow extends Activity<DrawOpticalFlowInput, DrawOpticalFlowOutput, DrawOpticalFlowConfiguration> {

	final static String DRAWOPTICALFLOW_ID = "org.peprframework.activities.opencv.drawopticalflow";
	private transient int lineThickness;
	private transient float scaleFactor;
	private transient CvSize.ByValue size = null;
	private transient IplImage image = null;
	private transient CvPoint.ByValue p = null, q = null;
	private transient CvScalar.ByValue lineColor = null;
	

	@Override
	public void initialize() {
		super.initialize();
		if (getConfiguration() == null)
			setConfiguration(new DrawOpticalFlowConfiguration());
		
		size = new CvSize.ByValue();
		lineThickness = getConfiguration().getLineThickness();
		scaleFactor = getConfiguration().getScaleFactor();
		p = new CvPoint.ByValue();
		q = new CvPoint.ByValue();
		
		lineColor.val[0] = (double)((byte)(255) + ((byte)(0) << 8) + ((byte)(0) << 16));
		
	}
	
	@Override
	public String getId() {
		return DRAWOPTICALFLOW_ID;
	}

	@Override
	public DrawOpticalFlowOutput handleMessage(DrawOpticalFlowInput msg) {
		DrawOpticalFlowOutput output = new DrawOpticalFlowOutput();
		
		size.height = msg.height;
		size.width = msg.width;
		
		// generate and allocate image
		if (image == null) {
			System.out.println(this.name + ": creating working image (DrawOpticalFlow) image");
			image = OpenCVCore.INSTANCE.cvCreateImage(size, msg.depth, msg.nChannels);
			System.out.println(this.name + ": done");
		}
		
		for (int i = 0; i < msg.imageData.length; i++) {
			image.imageData.getPointer().setByte(i, msg.imageData[i]);
		}

		/* For fun (and debugging :)), let's draw the flow field. */
		for(int i = 0; i < msg.numFeatures; i++)
		{
			/* If Pyramidal Lucas Kanade didn't really find the feature, skip it. */
			if ( msg.optical_flow_found_feature[i] == 0 ) continue;
			
			p.x = msg.frame1_features[i].x;
			p.y = msg.frame1_features[i].y;
			q.x = msg.frame2_features[i].x;
			q.y = msg.frame2_features[i].y;
			double angle = java.lang.Math.atan2( (double) p.y - q.y, (double) p.x - q.x );
			double hypotenuse = java.lang.Math.sqrt( java.lang.Math.pow(p.y - q.y, 2) + 
								java.lang.Math.pow(p.x - q.x, 2));
			/* Here we lengthen the arrow by a factor of three. */
			q.x = (int) (p.x - scaleFactor * hypotenuse * java.lang.Math.cos(angle));
			q.y = (int) (p.y - scaleFactor * hypotenuse * java.lang.Math.sin(angle));

			OpenCVCore.INSTANCE.cvLine( new CvArr(image.getPointer()), p, q, lineColor, lineThickness, OpenCV.CV_AA, 0 );

			// draw the arrow (looks nicer)
			p.x = (int) (q.x + java.lang.Math.pow(scaleFactor, 2) * java.lang.Math.cos(angle + java.lang.Math.PI / 4));
			p.y = (int) (q.y + java.lang.Math.pow(scaleFactor, 2) * java.lang.Math.sin(angle + java.lang.Math.PI / 4));
			OpenCVCore.INSTANCE.cvLine( new CvArr(image.getPointer()), p, q, lineColor, lineThickness, OpenCV.CV_AA, 0 );
			p.x = (int) (q.x + java.lang.Math.pow(scaleFactor, 2) * java.lang.Math.cos(angle - java.lang.Math.PI / 4));
			p.y = (int) (q.y + java.lang.Math.pow(scaleFactor, 2) * java.lang.Math.sin(angle - java.lang.Math.PI / 4));
			OpenCVCore.INSTANCE.cvLine( new CvArr(image.getPointer()), p, q, lineColor, lineThickness, OpenCV.CV_AA, 0 );
		}
		
		int[] pixel = new int[size.width * size.height];
		for (int i = 0; i < pixel.length; i++) {
			int x = i % size.width;
			int y = i / size.width;
			int p = ( x * msg.nChannels ) + ( y * msg.widthStep );

			int b = image.imageData.getPointer().getByte(p) & 0xff;
			int g = (msg.nChannels == 1) ? b : image.imageData.getPointer().getByte(p+1) & 0xff;
			int r = (msg.nChannels == 1) ? b : image.imageData.getPointer().getByte(p+2) & 0xff;
			pixel[i] = 0xff000000 ^ r << 16 ^ g << 8 ^ b;
		}

		output.pixel = pixel;
		
		output.width = size.width;
		output.height = size.height;
		
		return output;
	}


}
