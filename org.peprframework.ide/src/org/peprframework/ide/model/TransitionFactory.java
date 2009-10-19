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
package org.peprframework.ide.model;

import org.eclipse.gef.requests.CreationFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.peprframework.core.Transition;

/**
 * @author Volker Fritzsch
 * @version 1.0
 *
 */
public class TransitionFactory implements CreationFactory {

	public Object getNewObject() {
		return new Transition();
	}

	public Object getObjectType() {
		return TransitionFactory.class.getCanonicalName();
	}

	public ImageDescriptor getSmallIcon() {
		return ImageDescriptor.getMissingImageDescriptor();
	}
	
	public ImageDescriptor getLargeIcon() {
		return ImageDescriptor.getMissingImageDescriptor();
	}
	
	public String getShortDescription() {
		return "A transition between two activities.";
	}
	
	public String getLabel() {
		return "Transition";
	}
	
}
