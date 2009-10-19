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
package org.peprframework.core;

import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Volker Fritzsch
 * @version 1.0
 *
 */
public class Context {

	private transient Map<String,Object> state = new HashMap<String,Object>();
	
	private transient long timestamp;
	
	private transient short number;
	
	private transient String latestActivity;
	
	private transient ConcurrentMap<String,Object> sharedState;
	
	public Context() {
		this(Short.MIN_VALUE, System.currentTimeMillis(), null);
	}
	
	public Context(short number) {
		this(number, System.currentTimeMillis(), null);
	}
	
	public Context(short number, ConcurrentMap<String,Object> sharedState) {
		this(number, System.currentTimeMillis(), sharedState);
	}
	
	public Context(short number, long timestamp, ConcurrentMap<String,Object> sharedState) {
		this.number = number;
		this.sharedState = sharedState;
		this.timestamp = timestamp;
	}
	
	/**
	 * Copy Constructor
	 * 
	 * @param ctx
	 */
	protected Context(Context ctx) {
		this.number = ctx.number;
		this.timestamp = ctx.timestamp;
		this.latestActivity = ctx.latestActivity;
		this.sharedState = ctx.sharedState;
		
		boolean failed;
		do {
		try {
			this.state = new HashMap<String, Object>(ctx.state);
			failed = false;
		} catch (ConcurrentModificationException ex) {
			failed = true;
		}
		} while (failed);
	}
	
	/**
	 * Merges the called and the given context into a new context object.
	 * 
	 * @param ctx
	 * @return
	 */
	public Context merge(Context ctx) {
		if (this.sharedState != ctx.sharedState) {
			throw new IllegalArgumentException("Merge must not be called with contexts from different processes.");
		}
		
		Context merged = new Context(this);
		merged.number = (short) Math.min(this.number, ctx.number);
		merged.timestamp = Math.min(this.timestamp, ctx.timestamp);
		merged.state.putAll(ctx.state);
		
		return merged;
	}
	
	public Object get(String name) {
		return state.get(name);
	}
	
	public void set(String name, Object value) {
		state.put(name, value);
	}
	
	public Object getShared(String name) {
		return sharedState.get(name);
	}
	
	public void setShared(String name, Object value) {
		sharedState.put(name, value);
	}
	
	public short getContextNumber() {
		return number;
	}
	
	public long getTimestamp() {
		return timestamp;
	}
	
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * @return the latestActivity
	 */
	public String getLatestActivity() {
		return latestActivity;
	}

	/**
	 * @param latestActivity the latestActivity to set
	 */
	public void setLatestActivity(String latestActivity) {
		this.latestActivity = latestActivity;
	}
	
}
