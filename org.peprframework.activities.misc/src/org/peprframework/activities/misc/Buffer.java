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
import org.peprframework.core.Context;


/**
 * @author Stefan Scherer
 * @version 1.0
 *
 */
@XmlRootElement(name = "Buffer")
public class Buffer extends Activity<BufferInput, BufferOutput, BufferConfiguration> {

	
	final static String BUFFER_ID = "org.peprframework.activities.misc.buffer";
	private transient List<Object> buffer;
	
	@Override
	public void initialize() {
		super.initialize();
		if (getConfiguration() == null)
			setConfiguration(new BufferConfiguration());
		
		buffer = new LinkedList<Object>();
		
		System.out.println("Buffer: initialized.");
	}
	
	@Override
	public String getId() {
		return BUFFER_ID;
	}

	@Override
	public void onMessage(Context ctx) {
		try {
			BufferInput input = createInput();
			input = inputFilter.filter(input, ctx);
			BufferOutput output = handleMessage(input);
			ctx.set(name, output);
			ctx.setLatestActivity(name);
			if(output.output != null)
				callback.publish(ctx);
		} catch (Exception ex) {
			System.err.println("Exception in Buffer '" + name + "':");
			ex.printStackTrace();
		}
	}
	
	@Override
	public BufferOutput handleMessage(BufferInput msg) {
		BufferOutput output = new BufferOutput();
		if(buffer.size() < getConfiguration().getBufferSize()) {
			buffer.add(msg.input);
			output.output = null;
		}
		else {
			buffer.add(msg.input);
			output.output = buffer.remove(0);
		}
		
		return output;
	}

}