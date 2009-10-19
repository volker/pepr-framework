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

import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import javax.xml.bind.annotation.XmlRootElement;

import org.peprframework.core.Starter;

/**
 * @author Volker Fritzsch
 * @version 1.0
 *
 */
@XmlRootElement(name = "MicrophoneStarter")
public class MicrophoneStarter extends Starter<MicrophoneOutput, MicrophoneStarterConfiguration> {

	private transient AudioFormat audioFormat;

	private transient TargetDataLine targetDataLine;

	private transient AudioInputStream audioInputStream;

	private transient AudioInputStream convertedAudioInputStream;
	
	private transient int bytesPerRecording;
	
	private transient int bytesPerOverlap;
	
	private transient double[] overlap;
	
	@Override
	public void initialize() {
		super.initialize();

		bytesPerRecording = (int) (getConfiguration().getSampleRate() / 1000 * getConfiguration().getRecordLengthMs() * getConfiguration().getSampleSizeInBits() / 8);
		bytesPerOverlap = (int) (getConfiguration().getSampleRate() / 1000 * getConfiguration().getOverlapLengthMs() * getConfiguration().getSampleSizeInBits() / 8);
		overlap = new double[bytesPerOverlap / 2];
		
		try {
			audioFormat = getAudioFormat();
			DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
			targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
			targetDataLine.open(audioFormat);
			targetDataLine.start();
			audioInputStream = new AudioInputStream(targetDataLine);
			convertedAudioInputStream = AudioSystem.getAudioInputStream(AudioFormat.Encoding.PCM_SIGNED, audioInputStream);
		} catch (LineUnavailableException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public void starterLoop() {
		while (true) {
			byte[] buf = new byte[getConfiguration().getBufferSizeInBytes()];
			int bytesRecorded = -1;
			try {
				if ((bytesRecorded = convertedAudioInputStream.read(buf, 0, buf.length)) != -1) {
					int numberOfRecordedSamples = bytesRecorded / bytesPerRecording;
					for (int i = 0; i < numberOfRecordedSamples; i++) {
						byte[] currentSample = new byte[bytesPerRecording];
						System.arraycopy(buf, i * currentSample.length, currentSample, 0, currentSample.length);

						// create output
						MicrophoneOutput output = new MicrophoneOutput();
						output.data = new double[(bytesPerOverlap + bytesPerRecording) / 2];
						
						// copy overlap to output
						System.arraycopy(overlap, 0, output.data, 0, overlap.length);
						
						// copy current recording to output
						int k = 0;
						for (int j = overlap.length; j < output.data.length; j++) {
							output.data[j] = ((currentSample[k] & 0xff) ^ currentSample[k + 1] << 8) / 32768d;
							k += 2;
						}

						// backup new overlap
						System.arraycopy(output.data, output.data.length - overlap.length, overlap, 0, overlap.length);
						
						// fork process
						startProcess(output);
					}
					System.arraycopy(buf, bytesRecorded, buf, 0, buf.length - bytesRecorded);
				}
			} catch (IOException ex) {
				throw new RuntimeException(ex);
			}
		}
	}

	@Override
	public void terminate() {
		super.terminate();
	}

	private AudioFormat getAudioFormat() {
		return new AudioFormat(getConfiguration().getSampleRate(), getConfiguration().getSampleSizeInBits(),
				getConfiguration().getChannels(), getConfiguration().isSigned(), getConfiguration().isBigEndian());
	}

	/* (non-Javadoc)
	 * @see org.peprframework.core.Activity#getId()
	 */
	@Override
	public String getId() {
		return "org.peprframework.starters.misc.file";
	}
}
