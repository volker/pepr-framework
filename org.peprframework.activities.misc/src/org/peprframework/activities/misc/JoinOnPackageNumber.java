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

import org.peprframework.core.Configuration;
import org.peprframework.core.Context;

/**
 * @author Volker Fritzsch
 * @version 1.0
 *
 */
@XmlRootElement(name="JoinOnPackageNumber")
public class JoinOnPackageNumber extends AbstractJoin<Void,Void,Configuration> {

	/* (non-Javadoc)
	 * @see org.peprframework.activities.misc.AbstractJoin#canMergeContexts(java.util.List)
	 */
	@Override
	protected boolean canMergeContexts(List<Context> candidates) {
		Iterator<Context> it = candidates.iterator();
		short no = it.next().getContextNumber();
		
		while (it.hasNext())
			if (no != it.next().getContextNumber())
				return false;
		
		return true;
	}

	/* (non-Javadoc)
	 * @see org.peprframework.activities.misc.AbstractJoin#createContextComparator()
	 */
	@Override
	protected Comparator<Context> createContextSortOrderComparator() {
		return new Comparator<Context>() {
			
			public int compare(Context o1, Context o2) {
				int result = (Integer.valueOf(o1.getContextNumber()).compareTo(Integer.valueOf(o2.getContextNumber())));
//				System.out.println("Comparing values " + o1.getProcessNumber() + " and " + o2.getProcessNumber() + ": " + result);
				return result;
			}
		};
	}

	
	/* (non-Javadoc)
	 * @see org.peprframework.activities.misc.AbstractJoin#getId()
	 */
	@Override
	public String getId() {
		return "org.peprframework.activities.misc.joinOnPackageNumber";
	}

}
