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

import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

/**
 * @author Volker Fritzsch
 * @version 1.0
 */
public class CorePlugin extends Plugin {

	private ProcessMarshaller processMarshaller;
	
	// The plug-in ID
	public static final String PLUGIN_ID = "org.peprframework.core";

	// The shared instance
	private static CorePlugin plugin;
	
	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static CorePlugin getDefault() {
		return plugin;
	}
	
	public ProcessMarshaller getProcessMarshaller() {
		return processMarshaller;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugin#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		
		processMarshaller = new ProcessMarshaller();
		
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		registry.addListener(processMarshaller, "org.peprframework.core.activity");
		registry.addListener(processMarshaller, "org.peprframework.core.starter");
		registry.addListener(processMarshaller, "org.peprframework.core.configuration");
		processMarshaller.added(new IExtension[0]);
		
		plugin = this;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		registry.removeListener(processMarshaller);
		processMarshaller = null;
		
		super.stop(context);
	}
}
