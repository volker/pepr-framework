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
package org.peprframework.ide;

import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.peprframework.ide.model.ActivityRegistry;

/**
 * @author Volker Fritzsch
 * @version 1.0
 *
 */
public class Activator extends AbstractUIPlugin {
	
//	private JAXBContext jaxbContext;

	// The plug-in ID
	public static final String PLUGIN_ID = "org.peprframework.ide";

	// The shared instance
	private static Activator plugin;
	
	private ActivityRegistry activityRegistry;
	
	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
//		Platform.getAdapterManager().registerAdapters(new ProcessInspectorSourceAdapterFactory(), Activity.class);
//		Platform.getAdapterManager().registerAdapters(new ProcessInspectorSourceAdapterFactory(), Transition.class);
//		Platform.getAdapterManager().registerAdapters(new ProcessInspectorSourceAdapterFactory(), Process.class);
		
		//register activity extensions
		activityRegistry = new ActivityRegistry();
		
		Platform.getExtensionRegistry().addListener(activityRegistry, "org.peprframework.core.activity");
		Platform.getExtensionRegistry().addListener(activityRegistry, "org.peprframework.core.starter");
		IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint("org.peprframework.core.activity");
		activityRegistry.added(extensionPoint.getExtensions());
		extensionPoint = Platform.getExtensionRegistry().getExtensionPoint("org.peprframework.core.starter");
		activityRegistry.added(extensionPoint.getExtensions());
		
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		
		Platform.getExtensionRegistry().removeListener(activityRegistry);
		activityRegistry = null;
		
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}
	
	public ActivityRegistry getActivityRegistry() {
		return activityRegistry;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
	
//	public void marshallProcess(Process process, IFile file, IProgressMonitor monitor) {
//		try {
//			if (jaxbContext == null) {
//				createJaxbContext();
//			}
//			
//			Marshaller marshaller = jaxbContext.createMarshaller();
//			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
//			
//			ByteArrayOutputStream out = new ByteArrayOutputStream();
//			marshaller.marshal(process, out);
//			file.setContents(new ByteArrayInputStream(out.toByteArray()), IResource.KEEP_HISTORY, monitor);
//		} catch (JAXBException ex) {
//			ex.printStackTrace();
//		} catch (CoreException ex) {
//			ex.printStackTrace();
//		}
//		
//	}
//	
//	public Process unmarshallProcess(IFile file) {
//		Process process = null;
//		try {
//			if (jaxbContext == null) {
//				createJaxbContext();
//			}
//			
//			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
//			process = (Process) unmarshaller.unmarshal(file.getContents());
//		} catch (JAXBException ex) {
//			ex.printStackTrace();
//		} catch (CoreException ex) {
//			ex.printStackTrace();
//		}
//		
//		return process;
//	}
//
//	private void createJaxbContext() throws JAXBException {
////		jaxbContext = JAXBContext.newInstance(Join.class, Plotter.class, FileStarter.class, FileStarterConfiguration.class, MicrophoneStarter.class, MicrophoneStarterConfiguration.class, MatlabAdapter.class, MatlabConfiguration.class, InputFilter.class, GroovyInputFilter.class, Process.class, Activity.class, SupportVectorMachine.class, SupportVectorMachineConfiguration.class, EchoStateNetwork.class, EchoStateNetworkConfiguration.class, Plotter.class, PrintMessageActivity.class, MovingAverage.class, MovingAverageConfiguration.class);
//		jaxbContext = JAXBContext.newInstance();
//	}
}
