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

import java.net.URL;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.gef.requests.CreationFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.peprframework.core.Activity;

/**
 * @author Volker Fritzsch
 * @version 1.0
 *
 */
public class ActivityFactory implements CreationFactory {

	private IConfigurationElement configurationElement;
	
	public ActivityFactory(IConfigurationElement configurationElement) {
		this.configurationElement = configurationElement;
	}

	@SuppressWarnings("unchecked")
	public Object getNewObject() {
		Activity activity = null;
		
		try {
			activity = (Activity) configurationElement.createExecutableExtension("implementation");
			activity.setName(getLabel());
		} catch (CoreException e) {
			e.printStackTrace();
		}
		
		return activity;
	}

	public Object getObjectType() {
		return configurationElement.getAttribute("id");
	}

	public ImageDescriptor getSmallIcon() {
		if (configurationElement.getAttribute("smallIcon") != null) {
			String contributor = configurationElement.getContributor().getName();
			URL resource = Platform.getBundle(contributor).getResource(configurationElement.getAttribute("smallIcon"));
			return ImageDescriptor.createFromURL(resource);
		}
		return null;
	}

	public ImageDescriptor getLargeIcon() {
		if (configurationElement.getAttribute("largeIcon") != null) {
			String contributor = configurationElement.getContributor().getName();
			URL resource = Platform.getBundle(contributor).getResource(configurationElement.getAttribute("largeIcon"));
			return ImageDescriptor.createFromURL(resource);
		}
		return null;
	}

	public String getShortDescription() {
		return configurationElement.getAttribute("description");
	}

	public String getLabel() {
		return configurationElement.getAttribute("name");
	}

}