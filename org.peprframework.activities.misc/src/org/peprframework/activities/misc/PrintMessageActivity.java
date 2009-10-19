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

import javax.xml.bind.annotation.XmlRootElement;

import org.peprframework.core.Activity;
import org.peprframework.core.Configuration;

/**
 * @author Volker Fritzsch
 * @version 1.0
 *
 */
@XmlRootElement(name="PrintMessage")
public class PrintMessageActivity extends Activity<String, Void, Configuration> {

	private int i = 0;
	
	private long start = 0;
	
	@Override
	public void initialize() {
		super.initialize();
//		inputFilter = new InputFilter<Object>() {
//		
//			@Override
//			public void terminate() {
//			}
//		
//			@Override
//			public void initialize() {
//			}
//		
//			@Override
//			public void filter(Object input, Context ctx) {
//			}
//		};
//		start = System.currentTimeMillis();
	}

	@Override
	public Void handleMessage(String msg) {
		if (start == 0)
			start = System.currentTimeMillis();
		System.out.println(msg);
		if (i++ % 100 == 0) {
			System.out.println("* Processed " + i + " packages in " + (System.currentTimeMillis() - start) + "ms.");
		}
		return null;
	}
	
	@Override
	public void terminate() {
	}

	/* (non-Javadoc)
	 * @see org.peprframework.core.Activity#getId()
	 */
	@Override
	public String getId() {
		return "org.peprframework.activities.misc.print";
	}

}
