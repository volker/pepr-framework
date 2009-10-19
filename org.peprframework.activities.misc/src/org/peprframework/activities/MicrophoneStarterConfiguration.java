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
package org.peprframework.activities;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.peprframework.core.Configuration;

/**
 * @author Volker Fritzsch
 * @version 1.0
 *
 */
@XmlRootElement(name = "MicrophoneStarterConfiguration")
@XmlAccessorType(XmlAccessType.FIELD)
public class MicrophoneStarterConfiguration extends Configuration {

	// either 8000,11025,16000,22050,44100
	@XmlElement
//	@ConfigurableProperty(label = "Sample Rate")
	float sampleRate = 16000.0F;
	
	// either 8,16
	@XmlElement
//	@ConfigurableProperty(label = "Sample Size")
	int sampleSizeInBits = 16;
	
	// either 1,2
	@XmlElement
//	@ConfigurableProperty(label = "Channels")
	int channels = 1;
	
	// either true,false
	@XmlElement
//	@ConfigurableProperty(label = "Signed")
	boolean signed = true;
	
	// either true,false
	@XmlElement
//	@ConfigurableProperty(label = "Big Endian")
	boolean bigEndian = false;
	
	@XmlElement
//	@ConfigurableProperty(label = "Sample Length / ms")
	public int overlapLengthMs = 20;
	
	@XmlElement
//	@ConfigurableProperty(label = "Record Length / ms")
	public int recordLengthMs = 10;
	
	@XmlElement
//	@ConfigurableProperty(label = "Buffer Size")
	public int bufferSizeInBytes = 8 * 1024;

	public float getSampleRate() {
		return sampleRate;
	}

	public void setSampleRate(float sampleRate) {
		this.sampleRate = sampleRate;
	}

	public int getSampleSizeInBits() {
		return sampleSizeInBits;
	}

	public void setSampleSizeInBits(int sampleSizeInBits) {
		this.sampleSizeInBits = sampleSizeInBits;
	}

	public int getChannels() {
		return channels;
	}

	public void setChannels(int channels) {
		this.channels = channels;
	}

	public boolean isSigned() {
		return signed;
	}

	public void setSigned(boolean signed) {
		this.signed = signed;
	}

	public boolean isBigEndian() {
		return bigEndian;
	}

	public void setBigEndian(boolean bigEndian) {
		this.bigEndian = bigEndian;
	}

	public int getOverlapLengthMs() {
		return overlapLengthMs;
	}

	public void setOverlapLengthMs(int overlapLengthMs) {
		this.overlapLengthMs = overlapLengthMs;
	}

	public int getRecordLengthMs() {
		return recordLengthMs;
	}

	public void setRecordLengthMs(int recordLengthMs) {
		this.recordLengthMs = recordLengthMs;
	}

	public int getBufferSizeInBytes() {
		return bufferSizeInBytes;
	}

	public void setBufferSizeInBytes(int bufferSizeInBytes) {
		this.bufferSizeInBytes = bufferSizeInBytes;
	}
}
