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
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.peprframework.core.Activity;
import org.peprframework.resources.opencv.OpenCV;
import org.peprframework.resources.opencv.OpenCVCore;
import org.peprframework.resources.opencv.OpenCVCore.CvArr;
import org.peprframework.resources.opencv.OpenCVCore.CvMemStorage;
import org.peprframework.resources.opencv.OpenCVCore.CvRect;
import org.peprframework.resources.opencv.OpenCVCore.CvSeq;
import org.peprframework.resources.opencv.OpenCVCore.CvSize;
import org.peprframework.resources.opencv.OpenCVCore.IplImage;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.PointerByReference;

/**
 * @author Volker Fritzsch
 * @version 1.0
 *
 */
@XmlRootElement(name = "Detection")
public class Detection extends Activity<DetectionInput, DetectionData, DetectionConfiguration> {

	private static final String ID = "org.peprframework.activities.opencv.detection";

	private transient Pointer cascade = Pointer.NULL;
	
	private transient CvMemStorage storage = null;
	
	private transient double scale;
	
	private transient int neighbours;
	
	private transient int flags;
	
//	private transient CvSize.ByValue size = null;
	
	private transient IplImage image = null;

	private transient CvSize.ByValue detectionSize = null;
	
	/* (non-Javadoc)
	 * @see org.peprframework.core.Activity#getId()
	 */
	@Override
	public String getId() {
		return ID;
	}

	/* (non-Javadoc)
	 * @see org.peprframework.core.Activity#handleMessage(java.lang.Object)
	 */
	@Override
	public DetectionData handleMessage(DetectionInput msg) {
		// create working image
		if (image == null) {
			System.out.println(this.name + ": creating working image (detection)");
			CvSize.ByValue size = new CvSize.ByValue();
			size.height = msg.height;
			size.width = msg.width;
			image = OpenCVCore.INSTANCE.cvCreateImage(size, msg.depth, msg.nChannels);
		}
		
//		// inject incoming image data into our 'working'-image
//		for (int i = 0; i < msg.pixel.length; i++) {
//			int x = i % msg.width;
//			int y = i / msg.width;
//			int p = ( x * msg.nChannels ) + ( y * msg.widthStep );
//
//			int b = msg.pixel[i] & 0xff;
//			int g = (msg.nChannels == 1) ? b : msg.pixel[i] & 0xff00 >> 8;
//			int r = (msg.nChannels == 1) ? b : msg.pixel[i] & 0xff0000 >> 16;
//			
//			Pointer imageDataPointer = image.imageData.getPointer();
//			imageDataPointer.setByte(p, (byte) b);
//			imageDataPointer.setByte(p+1, (byte) g);
//			imageDataPointer.setByte(p+2, (byte) r);
//		}
		for (int i = 0; i < msg.imageData.length; i++) {
			image.imageData.getPointer().setByte(i, msg.imageData[i]);
		}
		
		Rectangle[] regionsOfInterest = msg.regionsOfInterest;
		if (regionsOfInterest == null) {
			regionsOfInterest = new Rectangle[] { new Rectangle(0, 0, msg.width, msg.height)};
		}
		
		List<Rectangle> areas = new LinkedList<Rectangle>();
		for (Rectangle currentRegionOfInterest : regionsOfInterest) {
			// set ROI
			OpenCVCore.INSTANCE.cvResetImageROI(image);
//			System.out.println(this.name + ": Creating rectangle for ROI");
			CvRect.ByValue rect = new CvRect.ByValue();
			rect.x = currentRegionOfInterest.x;
			rect.y = currentRegionOfInterest.y;
			rect.width = currentRegionOfInterest.width;
			rect.height = currentRegionOfInterest.height;
			OpenCVCore.INSTANCE.cvSetImageROI(image, rect);
			
			// haar detection
//			System.out.println(this.name + ": Doing Haar Detection");
			CvSeq sequence = OpenCV.INSTANCE.cvHaarDetectObjects(new CvArr(image.getPointer()), cascade, storage, scale, neighbours, flags, detectionSize);
//			System.out.println(this.name + ": Found total " + sequence.total + " faces");
			
			// extract detected areas
			for (int i = 0; i < sequence.total; i++) {
//				System.out.println(this.name + ": Reading sequence rect");
				ByteByReference elementPointer = OpenCVCore.INSTANCE.cvGetSeqElem(sequence, i);
				CvRect detectedRect = new CvRect(elementPointer.getPointer(), 0);
				detectedRect.read();
				Rectangle area = new Rectangle(detectedRect.x + currentRegionOfInterest.x, detectedRect.y + currentRegionOfInterest.y, detectedRect.width, detectedRect.height);
				areas.add(area);
			}
//			System.out.println(this.name + ": Clear memory storage");
			OpenCVCore.INSTANCE.cvClearMemStorage(storage);
		}

		// assemble output
		DetectionData output = new DetectionData();
		output.areas = areas.toArray(new Rectangle[areas.size()]);
//		System.out.println(this.name + ": Finished No: " + (number++));
		return output;
	}
	
	private transient int number = 0;

	/* (non-Javadoc)
	 * @see org.peprframework.core.Activity#initialize()
	 */
	@Override
	public void initialize() {
		super.initialize();
		
//		//preload OpenCV.Instance
//		Object test = OpenCVCore.INSTANCE;
//		Object test2 = OpenCVCore.INSTANCE;
		
		// load the cascade
		do {
			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {
			}
			cascade = OpenCVCore.INSTANCE.cvLoad(getConfiguration().getFilename().getBytes(), null, null, null);
			if (cascade == Pointer.NULL){
				System.err.println(this.name + ": Reloading cascade");
			}
		} while (cascade == Pointer.NULL);
		
		
		// allocate memory for calculation
		storage = OpenCVCore.INSTANCE.cvCreateMemStorage(0);
		
		// Initialise variables from configuration
		scale = getConfiguration().getScale();
		neighbours = getConfiguration().getNeighbours();
		flags = getConfiguration().getFlags();
		detectionSize  = new CvSize.ByValue();
		detectionSize.width = getConfiguration().getWidth();
		detectionSize.height = getConfiguration().getHeight();
	}

	/* (non-Javadoc)
	 * @see org.peprframework.core.Activity#terminate()
	 */
	@Override
	public void terminate() {
		OpenCVCore.INSTANCE.cvReleaseMemStorage(new PointerByReference(storage.getPointer()));
		if (image != null) {
			OpenCV.INSTANCE.cvReleaseImage(new PointerByReference(image.getPointer()));
		}
		
		super.terminate();
	}

}
