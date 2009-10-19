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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IRegistryEventListener;
import org.eclipse.core.runtime.Platform;

/**
 * @author Volker Fritzsch
 * @version 1.0
 *
 */
public class ProcessMarshaller implements IRegistryEventListener {

	private JAXBContext jaxbContext;

	/**
	 * @param process
	 * @param file
	 * @param progressMonitor
	 */
	public void marshallProcess(Process process, IFile file, IProgressMonitor progressMonitor) {
		synchronized(ProcessMarshaller.class) {
		try {
			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			marshaller.marshal(process, out);
			file.setContents(new ByteArrayInputStream(out.toByteArray()), IResource.KEEP_HISTORY, progressMonitor);
		} catch (JAXBException ex) {
			ex.printStackTrace();
		} catch (CoreException ex) {
			ex.printStackTrace();
		}
		}
	}

	/**
	 * @param xmlString
	 * @return
	 */
	public Process unmarshallProcess(String xmlString) {
		synchronized(ProcessMarshaller.class) {
			Process process = null;
			try {
				Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
				process = (Process) unmarshaller.unmarshal(new StringReader(xmlString));
			} catch (JAXBException ex) {
				if (ex.getLinkedException() != null && ex.getLinkedException().getMessage() != null && ex.getLinkedException().getMessage().equals("Premature end of file.")) {
					//ignore
				} else {
					ex.printStackTrace();
				}
			} 
			return process;
			}
	}
	
	/**
	 * @param file
	 * @return
	 */
	public Process unmarshallProcess(IFile file) {
		synchronized(ProcessMarshaller.class) {
		Process process = null;
		try {
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			process = (Process) unmarshaller.unmarshal(file.getContents());
		} catch (JAXBException ex) {
			if (ex.getLinkedException() != null && ex.getLinkedException().getMessage() != null && ex.getLinkedException().getMessage().equals("Premature end of file.")) {
				//ignore
			} else {
				ex.printStackTrace();
			}
		} catch (CoreException ex) {
			ex.printStackTrace();
		}

		return process;
		}
	}

	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	private void createJaxbContext() {
		synchronized(ProcessMarshaller.class) {
		Set<Class<?>> classes = new HashSet<Class<?>>();
		classes.add(Process.class);
		classes.add(Activity.class);
		classes.add(Starter.class);
		classes.add(Transition.class);
		classes.add(GroovyInputFilter.class);
		
		try {
			IExtension[] activityExtensions = Platform.getExtensionRegistry().getExtensionPoint("org.peprframework.core.activity").getExtensions();
			for (IExtension activityExtension : activityExtensions) {
				IConfigurationElement[] elements = activityExtension.getConfigurationElements();
				for (IConfigurationElement element : elements) {
					Activity activity = (Activity) element.createExecutableExtension("implementation");
					classes.add(activity.getClass());
				}
			}
			
			IExtension[] starterExtensions = Platform.getExtensionRegistry().getExtensionPoint("org.peprframework.core.starter").getExtensions();
			for (IExtension extension : starterExtensions) {
				IConfigurationElement[] elements = extension.getConfigurationElements();
				for (IConfigurationElement element : elements) {
					Starter starter = (Starter) element.createExecutableExtension("implementation");
					classes.add(starter.getClass());
				}
			}
			
			IExtension[] configurationExtensions = Platform.getExtensionRegistry().getExtensionPoint("org.peprframework.core.configuration").getExtensions();
			for (IExtension configurationExtension : configurationExtensions) {
				IConfigurationElement[] elements = configurationExtension.getConfigurationElements();
				for (IConfigurationElement element : elements) {
					Configuration configuration = (Configuration) element.createExecutableExtension("implementation");
					classes.add(configuration.getClass());
				}
			}
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			jaxbContext = JAXBContext.newInstance(classes.toArray(new Class[classes.size()]));
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IRegistryEventListener#added(org.eclipse.core.runtime.IExtension[])
	 */
	public void added(IExtension[] extensions) {
		createJaxbContext();
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
		createJaxbContext();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IRegistryEventListener#removed(org.eclipse.core.runtime.IExtensionPoint[])
	 */
	public void removed(IExtensionPoint[] extensionPoints) {
	}

}
