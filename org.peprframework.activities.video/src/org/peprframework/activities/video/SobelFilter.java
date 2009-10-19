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

import javax.xml.bind.annotation.XmlRootElement;

import org.peprframework.core.Activity;

/**
 * @author Sascha Meudt
 * @author Lutz Bigalke
 * @version 1.0
 *
 */
@XmlRootElement(name = "SobelFilter")
public class SobelFilter extends Activity<SobelFilterInput, SobelFilterOutput, SobelFilterConfiguration> {

	public static final String SOBELFILTER_ID = "org.peprframework.activities.video.sobelfilter";
	
	@Override
	public void initialize() {
		super.initialize();
		if (getConfiguration() == null)
			setConfiguration(new SobelFilterConfiguration());
		System.out.println("SobelFilter: initialized");
	}
	
	@Override
	public String getId() {
		return SOBELFILTER_ID;
	}

	@Override
	public SobelFilterOutput handleMessage(SobelFilterInput msg) {
		SobelFilterOutput out = new SobelFilterOutput();
		 
			//compute the gray scale image in color image space
			//use arithmetic gray image
			int[] gray = new int[msg.width*msg.height];
			for(int i = 0; i<msg.height;i++){
				for(int j = 0; j<msg.width;j++){
//					int a = msg.pixel[i*msg.width+j]&0xFF000000;
					int r = (msg.pixel[i*msg.width+j]&0x00FF0000)>>16;
					int g = (msg.pixel[i*msg.width+j]&0x0000FF00)>>8;
					int b = msg.pixel[i*msg.width+j]&0x000000FF;
					
					gray[i*msg.width+j]=0xFF000000|(((r+g+b)/3)<<16|((r+g+b)/3)<<8|((r+g+b)/3));
				}
			}
			
			//compute the filter in direction of X and Y
			float[] template={-1,0,1,-2,0,2,-1,0,1};
			int templateSize=3;
			float[] GY = new float[msg.width*msg.height];
			float[] GX = new float[msg.width*msg.height];
			int[] total = new int[msg.width*msg.height];
			int sum=0;
			int max=0;
			for(int x=(templateSize-1)/2; x<msg.width-(templateSize+1)/2;x++) {
				for(int y=(templateSize-1)/2; y<msg.height-(templateSize+1)/2;y++) {
					sum=0;
					for(int x1=0;x1<templateSize;x1++) {
						for(int y1=0;y1<templateSize;y1++) {
							int x2 = (x-(templateSize-1)/2+x1);
							int y2 = (y-(templateSize-1)/2+y1);
							float value = (gray[y2*msg.width+x2] & 0xff) * (template[y1*templateSize+x1]);
							sum += value;
						}
					}
					GY[y*msg.width+x] = sum;
					for(int x1=0;x1<templateSize;x1++) {
						for(int y1=0;y1<templateSize;y1++) {
							int x2 = (x-(templateSize-1)/2+x1);
							int y2 = (y-(templateSize-1)/2+y1);
							float value = (gray[y2*msg.width+x2] & 0xff) * (template[x1*templateSize+y1]);
							sum += value;
						}
					}
					GX[y*msg.width+x] = sum;
				}
			}
			//combine the directions
			for(int x=0; x<msg.width;x++) {
				for(int y=0; y<msg.height;y++) {
					total[y*msg.width+x]=(int)Math.sqrt(GX[y*msg.width+x]*GX[y*msg.width+x]+GY[y*msg.width+x]*GY[y*msg.width+x]);
					//direction[y*msg.width+x] = Math.atan2(GX[y*msg.width+x],GY[y*msg.width+x]);
					if(max<total[y*msg.width+x])
						max=total[y*msg.width+x];
				}
			}
			//norm to 255 and save as gray image in color image space
			float ratio=(float)max/255;
			int[] output = new int[msg.width*msg.height];
			for(int x=0; x<msg.width;x++) {
				for(int y=0; y<msg.height;y++) {
					sum=(int)(total[y*msg.width+x]/ratio);
					output[y*msg.width+x] = 0xff000000 | ((int)sum << 16 | (int)sum << 8 | (int)sum);
				}
			}
			out.width = msg.width;
			out.height = msg.height;
			out.pixel_sobel=output;
		return out;
	}
}
