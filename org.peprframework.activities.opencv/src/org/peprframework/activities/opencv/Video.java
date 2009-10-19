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

import org.peprframework.core.Starter;
import org.peprframework.resources.opencv.HighGUI;
import org.peprframework.resources.opencv.OpenCV;
import org.peprframework.resources.opencv.OpenCVCore;
import org.peprframework.resources.opencv.OpenCVCore.CvArr;
import org.peprframework.resources.opencv.OpenCVCore.CvSize;
import org.peprframework.resources.opencv.OpenCVCore.IplImage;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;

/**
 * @author Volker Fritzsch
 * @version 1.0
 *
 */
@XmlRootElement(name="VideoConfiguration")
public class Video extends Starter<OpenCVImage,VideoConfiguration> {
	
	private static final String VIDEO_ID = "org.peprframework.activities.opencv.video";

	private transient Pointer capture = null;
	
	private transient IplImage image = null;
	
	private transient IplImage frame = null;
	
	private transient CvSize.ByValue size = null;
	
	@Override
	public void initialize() {
		super.initialize();
		if (getConfiguration() == null)
			setConfiguration(new VideoConfiguration());
		
		size = new CvSize.ByValue();
		
		capture = HighGUI.INSTANCE.cvCreateFileCapture(getConfiguration().getFilename());
		
		// retrieve the first frame
		HighGUI.INSTANCE.cvGrabFrame(capture);
		frame = HighGUI.INSTANCE.cvRetrieveFrame(capture);
		
		size.height = frame.height;
		size.width = frame.width;
		
		// create the 'working'-image
		image = OpenCVCore.INSTANCE.cvCreateImage(size, frame.depth, frame.nChannels);
		
		
	}
	
	@Override
	public void terminate() {
		// release the 'working'-image
		OpenCV.INSTANCE.cvReleaseImage(new PointerByReference(image.getPointer()));
		
		// release the capture-handle
		HighGUI.INSTANCE.cvReleaseCapture(new PointerByReference(capture));
		
		super.terminate();
	}
	
	@Override
	public void starterLoop() {
		int width = image.width;
		int height = image.height;
		int nChannels = image.nChannels;
		int widthStep = image.widthStep;
		int depth = image.depth;
		
		while(true) {
			// retrieve a frame
			HighGUI.INSTANCE.cvGrabFrame(capture);
			frame = HighGUI.INSTANCE.cvRetrieveFrame(capture);

			// resize and copy the frame contents into our 'working'-image
			OpenCV.INSTANCE.cvResize(new CvArr(frame.getPointer()), new CvArr(image.getPointer()), OpenCV.CV_INTER_LINEAR);

			// extract the pixel data
			int[] pixel = new int[width * height];
			for (int i = 0; i < pixel.length; i++) {
				int x = i % width;
				int y = i / width;
				int p = ( x * nChannels ) + ( y * widthStep );

				int b = image.imageData.getPointer().getByte(p) & 0xff;
				int g = (nChannels == 1) ? b : image.imageData.getPointer().getByte(p+1) & 0xff;
				int r = (nChannels == 1) ? b : image.imageData.getPointer().getByte(p+2) & 0xff;
				pixel[i] = 0xff000000 ^ r << 16 ^ g << 8 ^ b;
			}

			// assemble output
			OpenCVImage output = new OpenCVImage();
			output.width = width;
			output.height = height;
			output.pixel = pixel;
			output.depth = depth;
			output.nChannels = nChannels;
			output.widthStep = widthStep;
//			output.openCvImage = image;
			output.imageData = new byte[width * height * nChannels];
			for (int i = 0; i < output.imageData.length; i++) {
				output.imageData[i] = image.imageData.getPointer().getByte(i);
			}
			
			// spawn process
//			System.out.println("Spawned Process number " + (number++));
			startProcess(output);
		}
		
	}

	@Override
	public String getId() {
		return VIDEO_ID;
	}

}
