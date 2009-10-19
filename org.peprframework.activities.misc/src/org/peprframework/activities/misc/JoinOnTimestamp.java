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

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.peprframework.core.Context;

/**
 * @author Volker Fritzsch
 * @version 1.0
 *
 */
@XmlRootElement(name="JoinOnTimestamp")
public class JoinOnTimestamp extends AbstractJoin<Void,Void,JoinOnTimestampConfiguration> {

	/* (non-Javadoc)
	 * @see org.peprframework.activities.misc.AbstractJoin#canMergeContexts(java.util.List)
	 */
	@Override
	protected boolean canMergeContexts(List<Context> candidates) {
		Iterator<Context> it = candidates.iterator();
		Context ctx = it.next();
		long min = ctx.getTimestamp();
		long max = ctx.getTimestamp();
		
		while (it.hasNext()) {
			ctx = it.next();
			min = Math.min(min, ctx.getTimestamp());
			max = Math.max(max, ctx.getTimestamp());
		}
		
		return (Math.abs(max - min) <= getConfiguration().getMaximumDistanceMillis());
	}

	/* (non-Javadoc)
	 * @see org.peprframework.activities.misc.AbstractJoin#createContextComparator()
	 */
	@Override
	protected Comparator<Context> createContextSortOrderComparator() {
		return new Comparator<Context>() {
			
			public int compare(Context o1, Context o2) {
				int result = Long.valueOf(o1.getTimestamp()).compareTo(Long.valueOf(o2.getTimestamp()));
//				System.out.println("Comparing values " + o1.getStartTimeMillis() + " and " + o2.getStartTimeMillis() + ": " + result);
				return result;
			}
		};
	}
	
	/* (non-Javadoc)
	 * @see org.peprframework.activities.misc.AbstractJoin#getId()
	 */
	@Override
	public String getId() {
		return "org.peprframework.activities.misc.joinOnTimestamp";
	}

}
