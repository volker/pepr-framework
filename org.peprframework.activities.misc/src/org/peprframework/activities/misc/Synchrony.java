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
package org.peprframework.activities.misc;

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
@XmlRootElement(name = "Synchrony")
public class Synchrony extends Activity<SynchronyInput, SynchronyOutput, SynchronyConfiguration> {
	
	public static final String SYNCHRONY_ID = "org.peprframework.activities.misc.synchrony";
	
	private transient List<float[]> master;
	
	private transient List<float[]> slave;
	
	private int compareRangeStart;
	
	private int compareRangeEnd;
	
	private int compareRangeSize;
	
	@Override
	public void initialize() {
		super.initialize();
		
		int maxLag = getConfiguration().getMaxLag();
		int compareRange = getConfiguration().getCompareRange();
		int masterSize = maxLag + compareRange + maxLag;
		int slaveSize = masterSize;
		this.compareRangeStart = maxLag;
		this.compareRangeSize = compareRange;
		this.compareRangeEnd = maxLag + compareRange;
		
		this.master = new LinkedList<float[]>();
		for (int i = 0; i < masterSize; i++) {
			this.master.add(new float[] { 0.0f });
		}
		this.slave = new LinkedList<float[]>();
		for (int i = 0; i < slaveSize; i++) {
			this.slave.add(new float[] { 0.0f });
		}
		
		System.out.println("Synchrony: initialized");
	}
	
	@Override
	public String getId() {
		return SYNCHRONY_ID;
	}

	@Override
	public SynchronyOutput handleMessage(SynchronyInput msg) {
		// append new value to master list
		master.remove(0);
		master.add(msg.master);
		
		// append new value to slave list
		slave.remove(0);
		slave.add(msg.slave);

		int lag = 0;
		float[] maximum = new float[Math.min(master.get(0).length, slave.get(0).length)];
		float[] corr = new float[maximum.length];
		float[] corr_coeff = new float[maximum.length];
		float[] s_xx = new float[maximum.length];
		float[] s_yy = new float[maximum.length];
		float[] meanMaster = new float[maximum.length];
		float[] meanSlave = new float[maximum.length];
		for (int i = 0; i < maximum.length; i++) {
			maximum[i] = Float.MIN_VALUE;
		}
		
		for (int masterIdx = 0; masterIdx < master.size() - compareRangeSize; masterIdx++) {
			for (int compareRangeIdx = compareRangeStart; compareRangeIdx < compareRangeEnd; compareRangeIdx++) {
				for (int dim = 0; dim < corr.length; dim++) {
					try {
						meanMaster[dim] += master.get(masterIdx + (compareRangeIdx - compareRangeStart))[dim];
						meanSlave[dim] += slave.get(compareRangeIdx)[dim];
					} catch (ArrayIndexOutOfBoundsException ex) {
						System.out.println("Lost dimension in Synchrony Activity.");
					}
				}
			}
			
			for (int dim = 0; dim < corr.length; dim++) {
				meanMaster[dim] /= compareRangeSize;
				meanSlave[dim] /= compareRangeSize;
			}
			
//			System.out.println("Means: ");
//			for (int i = 0; i < meanMaster.length; i++) {
//				System.out.println("mean of master in dimension " + i + ": " +  meanMaster[i]);
//				System.out.println("mean of slave in dimension " + i + ": " +  meanSlave[i]);
//			}
			try {
			for (int compareRangeIdx = compareRangeStart; compareRangeIdx < compareRangeEnd; compareRangeIdx++) {
				for (int dim = 0; dim < corr.length; dim++) {
					corr[dim] += (master.get(masterIdx + (compareRangeIdx - compareRangeStart))[dim] - meanMaster[dim])
								* (slave.get(compareRangeIdx)[dim] - meanSlave[dim]);
					s_xx[dim] += (master.get(masterIdx + (compareRangeIdx - compareRangeStart))[dim] - meanMaster[dim])
								* (master.get(masterIdx + (compareRangeIdx - compareRangeStart))[dim] - meanMaster[dim]);
					s_yy[dim] += (slave.get(compareRangeIdx)[dim] - meanSlave[dim]) * (slave.get(compareRangeIdx)[dim] - meanSlave[dim]);   
				}
			}
			} catch (ArrayIndexOutOfBoundsException ex) {
				System.out.println("Lost dimension in Synchrony Activity.");
			}
			
			try {
			for (int dim = 0; dim < corr.length; dim++) {
				//TODO: currently unused
				corr_coeff[dim] = (corr[dim]*corr[dim])/(s_xx[dim]* s_yy[dim]);
				
				if (corr[dim] > maximum[dim]) {
					maximum[dim] = corr[dim];
					lag = masterIdx - compareRangeStart;
				}
				corr[dim] = 0.0f;
				s_xx[dim] = 0.0f;
				s_yy[dim] = 0.0f;
				meanMaster[dim] = 0.0f;
				meanSlave[dim] = 0.0f;
			}
			} catch (ArrayIndexOutOfBoundsException ex) {
				System.out.println("Lost dimension in Synchrony Activity.");
			}
		}

//		System.out.println("Synchrony: ");
//		for (int i = 0; i < maximum.length; i++) {
//			System.out.println("maximum synchrony with lag: " + lag + " in dimension " + i + ": " +  maximum[i]);
//		}
		
		// prepare output
		SynchronyOutput output = new SynchronyOutput();
		output.lag = lag;
		output.synchrony = maximum;
		
		return output;
	}

}
