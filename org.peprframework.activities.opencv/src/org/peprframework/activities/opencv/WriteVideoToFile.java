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
import org.peprframework.resources.opencv.HighGUI;
import org.peprframework.resources.opencv.OpenCV;
import org.peprframework.resources.opencv.OpenCVCore;
import org.peprframework.resources.opencv.HighGUI.CvVideoWriter;
import org.peprframework.resources.opencv.OpenCVCore.CvSize;
import org.peprframework.resources.opencv.OpenCVCore.IplImage;

import com.sun.jna.ptr.PointerByReference;

/**
 * @author Stefan Scherer
 * @version 1.0
 *
 */
@XmlRootElement(name = "WriteVideoToFile")
public class WriteVideoToFile extends Activity<WriteVideoToFileInput,Void,WriteVideoToFileConfiguration> {

	final static String WRITETOFILE_ID = "org.peprframework.activities.opencv.writevideotofile";
	private transient String filename;
	private transient int fourcc, isColor, fps;
	private transient CvVideoWriter writer = null;
	private transient CvSize.ByValue size = null;
	private transient IplImage image = null;
	
	
	@Override
	public void initialize() {
		super.initialize();
		if (getConfiguration() == null)
			setConfiguration(new WriteVideoToFileConfiguration());
		
		filename = getConfiguration().getFilename();
		fourcc = cvFourCC('D','I','V','X');
		isColor	= getConfiguration().getIsColor();
		fps = getConfiguration().getFps();
		size = new CvSize.ByValue();
	}

	@Override
	public String getId() {
		return WRITETOFILE_ID;
	}
	
	public int cvFourCC(char c1, char c2, char c3, char c4){
		
		System.out.println((int)(((c1)&255) + (((c2)&255)<<8) + (((c3)&255)<<16) + (((c4)&255)<<24)));
		
		return (((c1)&255) + (((c2)&255)<<8) + (((c3)&255)<<16) + (((c4)&255)<<24));
	}

	@Override
	public Void handleMessage(WriteVideoToFileInput msg) {
		size.width = msg.width;
		size.height = msg.height;
		
		if (writer == null) {
			System.out.println(this.name + ": creating CvVideoWriter");
			writer = HighGUI.INSTANCE.cvCreateVideoWriter(filename, 
								fourcc, 
								fps, 
								size, 
								isColor);
			System.out.println(this.name + ": done");
		}
		
		// generate and allocate image
		if (image == null) {
			System.out.println(this.name + ": creating working image (CvVideoWriter) image");
			image = OpenCVCore.INSTANCE.cvCreateImage(size, msg.depth, msg.nChannels);
			System.out.println(this.name + ": done");
		}
		
		for (int i = 0; i < msg.imageData.length; i++) {
			image.imageData.getPointer().setByte(i, msg.imageData[i]);
		}
		
		HighGUI.INSTANCE.cvWriteFrame(writer, image);
		
		return null;
	}
	
	@Override
	public void terminate() {
		if (image != null) {
			OpenCV.INSTANCE.cvReleaseImage(new PointerByReference(image.getPointer()));
		}
		HighGUI.INSTANCE.cvReleaseVideoWriter(new PointerByReference(writer.getPointer()));
		super.terminate();
	}

}
