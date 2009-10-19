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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IRegistryEventListener;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.osgi.framework.Bundle;
import org.peprframework.ide.Activator;

/**
 * @author Volker Fritzsch
 * @version 1.0
 */
public class ActivityRegistry implements IRegistryEventListener {

	private Set<String> registeredIds = new HashSet<String>();
	
	/**
	 * 16x16 Icons for the palette view 
	 */
	private Map<String,ImageDescriptor> smallIcons = new HashMap<String, ImageDescriptor>();
	
	/**
	 * 24x24 Icons for the palette view 
	 */
	private Map<String,ImageDescriptor> largeIcons = new HashMap<String, ImageDescriptor>();
	
	/**
	 * Larger Icons for the Process Editor 
	 */
	private Map<String,ImageDescriptor> processIcons = new HashMap<String, ImageDescriptor>();
	
	/**
	 * Descriptions
	 */
	private Map<String,String> names = new HashMap<String, String>();
	
	/**
	 * Names
	 */
	private Map<String,String> descriptions = new HashMap<String, String>();
	
	public ImageDescriptor getSmallIconImageDescriptor(String id) {
		return smallIcons.get(id);
	}
	
	public ImageDescriptor getLargeIconImageDescriptor(String id) {
		return largeIcons.get(id);
	}

	public ImageDescriptor getProcessIconImageDescriptor(String id) {
		return processIcons.get(id);
	}
	
	public String getName(String id) {
		return names.get(id);
	}
	
	public String getDescription(String id) {
		return descriptions.get(id);
	}
	
	public String[] getRegisteredIds() {
		return registeredIds.toArray(new String[registeredIds.size()]);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IRegistryEventListener#added(org.eclipse.core.runtime.IExtension[])
	 */
	public void added(IExtension[] extensions) {
		for (IExtension extension : extensions) {
			Bundle contributor = Platform.getBundle(extension.getContributor().getName());
			String uid = extension.getExtensionPointUniqueIdentifier();
			IConfigurationElement[] configurationElements = extension.getConfigurationElements();
			for (IConfigurationElement configurationElement : configurationElements) {
				String id = configurationElement.getAttribute("id");
				
				names.put(id, configurationElement.getAttribute("name"));
				descriptions.put(id, configurationElement.getAttribute("name"));
				if (configurationElement.getAttribute("smallIcon") != null) {
					smallIcons.put(id, ImageDescriptor.createFromURL(contributor.getResource(configurationElement.getAttribute("smallIcon"))));
				} else {
					if (uid.equals("org.peprframework.core.activity")) {
						smallIcons.put(id, Activator.getImageDescriptor("/icons/default_activity_small.png"));
					}
					if (uid.equals("org.peprframework.core.starter")) {
						smallIcons.put(id, Activator.getImageDescriptor("/icons/default_starter_small.png"));
					}
				}
				if (configurationElement.getAttribute("largeIcon") != null) {
					largeIcons.put(id, ImageDescriptor.createFromURL(contributor.getResource(configurationElement.getAttribute("largeIcon"))));
				} else {
					if (uid.equals("org.peprframework.core.activity")) {
						largeIcons.put(id, Activator.getImageDescriptor("/icons/default_activity_large.png"));
					}
					if (uid.equals("org.peprframework.core.starter")) {
						largeIcons.put(id, Activator.getImageDescriptor("/icons/default_starter_large.png"));
					}
				}
				if (configurationElement.getAttribute("processIcon") != null) {
					processIcons.put(id, ImageDescriptor.createFromURL(contributor.getResource(configurationElement.getAttribute("processIcon"))));
				} else {
					if (uid.equals("org.peprframework.core.activity")) {
						processIcons.put(id, Activator.getImageDescriptor("/icons/default_activity_large.png"));
					}
					if (uid.equals("org.peprframework.core.starter")) {
						processIcons.put(id, Activator.getImageDescriptor("/icons/default_starter_large.png"));
					}
				}
				registeredIds.add(id);
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IRegistryEventListener#added(org.eclipse.core.runtime.IExtensionPoint[])
	 */
	public void added(IExtensionPoint[] extensionPoints) {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IRegistryEventListener#removed(org.eclipse.core.runtime.IExtension[])
	 */
	public void removed(IExtension[] extensions) {
		for (IExtension extension : extensions) {
			IConfigurationElement[] configurationElements = extension.getConfigurationElements();
			for (IConfigurationElement configurationElement : configurationElements) {
				String id = configurationElement.getAttribute("id");
				
				registeredIds.remove(id);
				names.remove(id);
				descriptions.remove(id);
				smallIcons.remove(id);
				largeIcons.remove(id);
				processIcons.remove(id);
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IRegistryEventListener#removed(org.eclipse.core.runtime.IExtensionPoint[])
	 */
	public void removed(IExtensionPoint[] extensionPoints) {
	}

}
