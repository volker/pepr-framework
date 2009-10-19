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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import org.peprframework.core.Activity;
import org.peprframework.core.Configuration;
import org.peprframework.core.Context;

/**
 * @author Volker Fritzsch
 * @version 1.0
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class AbstractJoin<Input,Output,C extends Configuration> extends Activity<Input, Output, C>{

	private transient int bufferFillLimit = 50;
	
	private transient Map<String,List<Context>> buffers;
	
	private transient Comparator<Context> contextSortOrderComparator;
	
	/**
	 * @param candidates
	 * @return
	 */
	protected abstract boolean canMergeContexts(List<Context> candidates);
	
	/**
	 * @return
	 */
	protected abstract Comparator<Context> createContextSortOrderComparator();
	
	/**
	 * @param candidates
	 * @param potentialCandidateIterator
	 * @param bufferIterator
	 * @return
	 */
	private boolean findCandidates(List<Context> candidates, Iterator<Context> potentialCandidateIterator, Iterator<Iterator<Context>> bufferIterator) {
		while (potentialCandidateIterator.hasNext()) {
			Context candidate = potentialCandidateIterator.next();
			candidates.add(candidate);
			if (canMergeContexts(candidates)) {
				if (bufferIterator.hasNext()) {
					Iterator<Context> tmp = bufferIterator.next();
					if (findCandidates(candidates, tmp, bufferIterator))
						return true;
				} else {
					return true;
				}
			}
			candidates.remove(candidate);
		}
		
		return false;
	}

	/* (non-Javadoc)
	 * @see org.peprframework.core.Activity#getId()
	 */
	@Override
	public abstract String getId();
	
	/* (non-Javadoc)
	 * @see org.peprframework.core.Activity#handleMessage(java.lang.Object)
	 */
	@Override
	public Output handleMessage(Input msg) {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.peprframework.core.Activity#initialize()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void initialize() {
		super.initialize();
		
		// fetch context comparator from concrete join implementation
		this.contextSortOrderComparator = createContextSortOrderComparator();
		this.buffers = new HashMap<String,List<Context>>();
		
		// inspect preceding activities and store their names
		Set<Activity> precedingActivities = getProcess().getDirectPreviousActivities(this);
		for (Activity activity : precedingActivities) {
			this.buffers.put(activity.getName(), new ArrayList<Context>(this.bufferFillLimit));
		}
	}

	/**
	 * @param candidates
	 * @return
	 */
	private Context mergeContexts(List<Context> candidates) {
		Iterator<Context> it = candidates.iterator();
		Context mergedContext = it.next();
		while(it.hasNext())
			mergedContext = mergedContext.merge(it.next());
		
		return mergedContext;
	}

	/* (non-Javadoc)
	 * @see org.peprframework.core.Activity#onMessage(org.peprframework.core.Context)
	 */
	@Override
	public synchronized void onMessage(Context ctx) {
		// if we do not know the latest activity the current context had to pass, then there is something
		// really wrong with our initialisation code
		if (!buffers.containsKey(ctx.getLatestActivity()))
			throw new RuntimeException("Encountered unknown activity '" + ctx.getLatestActivity() + "'.");
		
		List<Context> candidates = new ArrayList<Context>(buffers.keySet().size());
		candidates.add(ctx);
		
		List<Iterator<Context>> bufferIterators = new ArrayList<Iterator<Context>>(buffers.keySet().size() - 1);
		for (String precedingActivityName : buffers.keySet()) {
			if (precedingActivityName.equals(ctx.getLatestActivity()))
				continue;
			
			bufferIterators.add(buffers.get(precedingActivityName).iterator());
		}
		
		Iterator<Iterator<Context>> bufferIterator = bufferIterators.iterator();
		Iterator<Context> potentialCandidateIterator = bufferIterator.next();
		if(findCandidates(candidates, potentialCandidateIterator, bufferIterator)) {
			for (Context context : candidates) {
				if (context.equals(ctx))
					continue;
				
				buffers.get(context.getLatestActivity()).remove(context);
			}
			
			Context mergedContext = mergeContexts(candidates);
			mergedContext.setLatestActivity(name);
			callback.publish(mergedContext);
			
			return;
		}
		
		// is enough space left to buffer the current context?
		if (buffers.get(ctx.getLatestActivity()).size() >= bufferFillLimit) {
			System.err.println("Maximum buffer fill reached for activity '" + ctx.getLatestActivity() + "'. Removing oldest entry.");
			buffers.get(ctx.getLatestActivity()).remove(0);
			return;
		}
		
		buffers.get(ctx.getLatestActivity()).add(ctx);
		Collections.sort(buffers.get(ctx.getLatestActivity()), contextSortOrderComparator);
	}
	
}
