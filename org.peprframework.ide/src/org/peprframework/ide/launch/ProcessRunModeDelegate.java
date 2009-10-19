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
package org.peprframework.ide.launch;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;
import org.peprframework.core.CorePlugin;
import org.peprframework.core.Engine;
import org.peprframework.core.ProcessMarshaller;

/**
 * @author Volker Fritzsch
 * @version 1.0
 *
 */
public class ProcessRunModeDelegate implements ILaunchConfigurationDelegate {

//	public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor)
//			throws CoreException {
//		if (!ILaunchManager.RUN_MODE.equals(mode))
//			return;
//		
//		String projectName = (String) configuration.getAttribute(IPeprLaunchConfigurationConstants.ATTRIBUTE_PROJECT, "");
//		String processPath = (String) configuration.getAttribute(IPeprLaunchConfigurationConstants.ATTRIBUTE_PROCESS, "");
//		
//		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
//		
//		String[] cp = null;
//		IPluginModelBase[] models = PluginRegistry.getAllModels();
//		for (IPluginModelBase model : models) {
//			if (model.getBundleDescription().getSymbolicName().equals("org.eclipse.equinox.launcher")) {
//				cp = new String[] { model.getInstallLocation()};
//				break;
//			}
//		}
//		
//		VMRunnerConfiguration runConfiguration = new VMRunnerConfiguration("org.eclipse.equinox.launcher.Main", cp);
//		runConfiguration.setVMArguments(new String[] {"-Xmx512M", "-d32"});
//		runConfiguration.setProgramArguments(new String[] {"-noSplash", "-consoleLog", "-data", root.getLocation().toOSString(), "-application", "org.pepr.headless.application", "-projectName", projectName, "-processLocation", processPath });
//		IVMRunner runner = JavaRuntime.getDefaultVMInstall().getVMRunner(ILaunchManager.RUN_MODE);
//		runner.run(runConfiguration, launch, monitor);
//	}

	private static Engine engine;
	
	public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor) throws CoreException {
		if (!ILaunchManager.RUN_MODE.equals(mode))
			return;
		
		String projectName = (String) configuration.getAttribute(IPeprLaunchConfigurationConstants.ATTRIBUTE_PROJECT, "");
		String processLocation = (String) configuration.getAttribute(IPeprLaunchConfigurationConstants.ATTRIBUTE_PROCESS, "");

		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject project = root.getProject(projectName);
		
		Path processPath = new Path(processLocation);
		IFile processFile = (IFile) project.findMember(processPath);
		processFile.refreshLocal(IResource.DEPTH_ZERO, null);
		
		ProcessMarshaller marshaller = CorePlugin.getDefault().getProcessMarshaller();
		org.peprframework.core.Process process = marshaller.unmarshallProcess(processFile);
		
		engine = new Engine();
		engine.startProcess(process);
		
//		while(engine.isRunning()) {
//			Thread.sleep(100);
//		}
//		return EXIT_OK;
	}
}
