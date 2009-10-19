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
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.xml.bind.annotation.XmlRootElement;

import org.peprframework.core.Activity;
import org.peprframework.core.Context;
import org.peprframework.resources.opencv.HighGUI;
import org.peprframework.resources.opencv.OpenCV;
import org.peprframework.resources.opencv.OpenCVCore;
import org.peprframework.resources.opencv.OpenCVCore.CvArr;
import org.peprframework.resources.opencv.OpenCVCore.CvPoint2D32f;
import org.peprframework.resources.opencv.OpenCVCore.CvSize;
import org.peprframework.resources.opencv.OpenCVCore.CvTermCriteria;
import org.peprframework.resources.opencv.OpenCVCore.IplImage;

import com.sun.jna.ptr.PointerByReference;


/**
 * @author Stefan Scherer
 * @version 1.0
 *
 */
@XmlRootElement(name = "LucasKanade")
public class LucasKanade extends Activity<LucasKanadeInput,LucasKanadeOutput,LucasKanadeConfiguration> {

	final static String LUCASKANADE_ID = "org.peprframework.activities.opencv.LucasKanade";

	private transient IplImage prev_image = null;
	private transient IplImage image = null;
	private transient IplImage curr_image = null;
	private transient IplImage eig_image = null;
	private transient IplImage temp_image = null;
	private transient IplImage pyramid1 = null;
	private transient IplImage pyramid2 = null;
	private transient IntBuffer corners = null;
	private transient ByteBuffer optical_flow_found_feature = null;
	private transient FloatBuffer optical_flow_feature_error = null;
	private transient CvSize.ByValue size = null, optical_flow_window = null;
	private transient CvTermCriteria.ByValue optical_flow_termination_criteria = null;
	private transient CvPoint2D32f[] frame1_features, frame2_features;
	
	@Override
	public void initialize() {
		super.initialize();
		if (getConfiguration() == null)
			setConfiguration(new LucasKanadeConfiguration());
		
		size = new CvSize.ByValue();
		// TODO: Put this in Configuration
		optical_flow_window = new CvSize.ByValue();
		optical_flow_window.height = getConfiguration().getOptical_flow_window_height();
		optical_flow_window.width = getConfiguration().getOptical_flow_window_width();
		optical_flow_termination_criteria = new CvTermCriteria.ByValue();
		optical_flow_termination_criteria.epsilon = getConfiguration().getOptical_flow_termination_criteria_epsilon();
		optical_flow_termination_criteria.max_iter = getConfiguration().getOptical_flow_termination_criteria_max_iter();
		optical_flow_termination_criteria.type = getConfiguration().getOptical_flow_termination_criteria_type();
		
		corners = IntBuffer.allocate(1);

		optical_flow_found_feature = ByteBuffer.allocate(getConfiguration().getNumFeatures());
		optical_flow_feature_error = FloatBuffer.allocate(getConfiguration().getNumFeatures());
		
	}

	@Override
	public String getId() {
		return LUCASKANADE_ID;
	}

	@Override
	public LucasKanadeOutput handleMessage(LucasKanadeInput msg) {
		LucasKanadeOutput output = new LucasKanadeOutput();
		
		size.height = msg.height;
		size.width = msg.width;
		
		
		// create prev_image
		if (image == null) {
			System.out.println(this.name + ": creating working image (LucasKanade) image");
			image = OpenCVCore.INSTANCE.cvCreateImage(size, msg.depth, msg.nChannels);
			System.out.println(this.name + ": done");
		}
		
		for (int i = 0; i < msg.imageData.length; i++) {
			image.imageData.getPointer().setByte(i, msg.imageData[i]);
		}
		
		if (prev_image == null) {
			System.out.println(this.name + ": creating working image (LucasKanade) prev_image");
			prev_image = OpenCVCore.INSTANCE.cvCreateImage(size, OpenCV.IPL_DEPTH_8U, 1);
			HighGUI.INSTANCE.cvConvertImage(new CvArr(image.getPointer()), new CvArr(prev_image.getPointer()), OpenCV.CV_CVTIMG_FLIP);
			System.out.println(this.name + ": done");
			
			return output;
		}
		if (curr_image == null) {
			System.out.println(this.name + ": creating working image (LucasKanade) curr_image");
			curr_image = OpenCVCore.INSTANCE.cvCreateImage(size, OpenCV.IPL_DEPTH_8U, 1);
			System.out.println(this.name + ": done");
		}
		if (eig_image == null) {
			System.out.println(this.name + ": creating working image (LucasKanade) eig_image");
			eig_image = OpenCVCore.INSTANCE.cvCreateImage(size, OpenCV.IPL_DEPTH_32F, 1);
			System.out.println(this.name + ": done");
		}
		if (temp_image == null) {
			System.out.println(this.name + ": creating working image (LucasKanade) temp_image");
			temp_image = OpenCVCore.INSTANCE.cvCreateImage(size, OpenCV.IPL_DEPTH_32F, 1);
			System.out.println(this.name + ": done");
		}
		if (pyramid1 == null) {
			System.out.println(this.name + ": creating working image (LucasKanade) pyramid1");
			pyramid1 = OpenCVCore.INSTANCE.cvCreateImage(size, OpenCV.IPL_DEPTH_8U, 1);
			System.out.println(this.name + ": done");
		}
		if (pyramid2 == null) {
			System.out.println(this.name + ": creating working image (LucasKanade) pyramid2");
			pyramid2 = OpenCVCore.INSTANCE.cvCreateImage(size, OpenCV.IPL_DEPTH_8U, 1);
			System.out.println(this.name + ": done");
		}
		
		//System.out.println("before cvConvert");
		HighGUI.INSTANCE.cvConvertImage(new CvArr(image.getPointer()), new CvArr(curr_image.getPointer()), OpenCV.CV_CVTIMG_FLIP);
		//System.out.println("after cvConvert");
		
		//OpenCV.INSTANCE.cvConvertImage(new CvArr(prev_image.getPointer()), new CvArr(temp_image.getPointer()), 0);
		//OpenCV.INSTANCE.cvConvertImage(new CvArr(prev_image.getPointer()), new CvArr(curr_image.getPointer()), 0);

		frame1_features = new CvPoint2D32f[getConfiguration().getNumFeatures()];
		frame2_features = new CvPoint2D32f[getConfiguration().getNumFeatures()];
		
		//System.out.println(prev_image.depth+" "+prev_image.height+" "+prev_image.width+" "+prev_image.nChannels);
		
		corners.put(0, getConfiguration().getNumFeatures());
		
		
		//System.out.println("before goodFeatures");
		OpenCV.INSTANCE.cvGoodFeaturesToTrack(new CvArr(prev_image.getPointer()), new CvArr(eig_image.getPointer()), new CvArr(temp_image.getPointer()),
						frame1_features, corners, .01, .01, null, 3, 0, 0.0d);
		//System.out.println("after goodFeatures");
		//System.out.println(frame1_features.x+" "+frame1_features.y);
		
		
		
		
		//System.out.println("before optflow");
		OpenCV.INSTANCE.cvCalcOpticalFlowPyrLK(new CvArr(prev_image.getPointer()), new CvArr(curr_image.getPointer()), new CvArr(pyramid1.getPointer()), 
				new CvArr(pyramid2.getPointer()), frame1_features, frame2_features, getConfiguration().getNumFeatures(), optical_flow_window, 5, 
				optical_flow_found_feature, optical_flow_feature_error, optical_flow_termination_criteria, 0 );
		//System.out.println("after optflow");
		//System.out.print(optical_flow_found_feature.getValue()+" "+optical_flow_feature_error.getValue());
		
		
		//System.out.println("before copy");
		OpenCVCore.INSTANCE.cvCopy(new CvArr(curr_image.getPointer()), new CvArr(prev_image.getPointer()), null);
		//System.out.println("after copy");
		
//		float[] err = new float[optical_flow_feature_error.array().length];
//		for(int i = 0; i < err.length; i++)
//			System.out.print(optical_flow_feature_error.array()[i]+" ");
//		System.out.println("Errors Length: "+err.length);
		
		output.optical_flow_found_feature = optical_flow_found_feature.array();
		output.optical_flow_feature_error = optical_flow_feature_error.array();
		
		output.frame1_features = new Point[frame1_features.length];
		output.frame2_features = new Point[frame2_features.length];
		
		for(int i = 0; i < frame1_features.length; i++) {
			output.frame1_features[i] = new Point((int)frame1_features[i].x, (int)frame1_features[i].y);
		}
		for(int i = 0; i < frame2_features.length; i++) {
			output.frame2_features[i] = new Point((int)frame2_features[i].x, (int)frame2_features[i].y);
		}
		
		output.numFeatures = getConfiguration().getNumFeatures();
		
		// maybe not
		optical_flow_feature_error.rewind();
		optical_flow_found_feature.rewind();
		
		return output;
	}
	
	@Override
	public void onMessage(Context ctx) {
		try {
			LucasKanadeInput input = createInput();
			input = inputFilter.filter(input, ctx);
			LucasKanadeOutput output = handleMessage(input);
			ctx.set(name, output);
			ctx.setLatestActivity(name);
			if(output.opticalFlow != null)
				callback.publish(ctx);
		} catch (Exception ex) {
			System.err.println("Exception in Buffer '" + name + "':");
			ex.printStackTrace();
		}
	}
	
	@Override
	public void terminate() {
		if (prev_image != null) {
			OpenCV.INSTANCE.cvReleaseImage(new PointerByReference(prev_image.getPointer()));
		}
		if (eig_image != null) {
			OpenCV.INSTANCE.cvReleaseImage(new PointerByReference(eig_image.getPointer()));
		}
		if (temp_image != null) {
			OpenCV.INSTANCE.cvReleaseImage(new PointerByReference(temp_image.getPointer()));
		}
		if (curr_image != null) {
			OpenCV.INSTANCE.cvReleaseImage(new PointerByReference(curr_image.getPointer()));
		}
		if (pyramid1 != null) {
			OpenCV.INSTANCE.cvReleaseImage(new PointerByReference(pyramid1.getPointer()));
		}
		if (pyramid2 != null) {
			OpenCV.INSTANCE.cvReleaseImage(new PointerByReference(pyramid2.getPointer()));
		}
		super.terminate();
	}

}
