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
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.pde.ui.launcher.AbstractLaunchShortcut;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.part.FileEditorInput;
import org.peprframework.ide.editors.ProcessEditor;

/**
 * @author Volker Fritzsch
 * @version 1.0
 *
 */
public class LaunchShortcut extends AbstractLaunchShortcut {

	private static final String CONFIGURATION_TYPE = "org.peprframework.ide.launch.process";
	
	private String processLocation;
	
	private String projectName;
	
	/* (non-Javadoc)
	 * @see org.eclipse.pde.ui.launcher.AbstractLaunchShortcut#getLaunchConfigurationTypeName()
	 */
	@Override
	protected String getLaunchConfigurationTypeName() {
		return CONFIGURATION_TYPE;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.ui.launcher.AbstractLaunchShortcut#initializeConfiguration(org.eclipse.debug.core.ILaunchConfigurationWorkingCopy)
	 */
	@Override
	protected void initializeConfiguration(ILaunchConfigurationWorkingCopy wc) {
		wc.setAttribute(IPeprLaunchConfigurationConstants.ATTRIBUTE_PROJECT, this.projectName);
		wc.setAttribute(IPeprLaunchConfigurationConstants.ATTRIBUTE_PROCESS, this.processLocation);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.ui.launcher.AbstractLaunchShortcut#isGoodMatch(org.eclipse.debug.core.ILaunchConfiguration)
	 */
	@Override
	protected boolean isGoodMatch(ILaunchConfiguration configuration) {
		try {
			if (!configuration.getType().getIdentifier().equals(CONFIGURATION_TYPE))
				return false;
			if (!configuration.getAttribute(IPeprLaunchConfigurationConstants.ATTRIBUTE_PROJECT, "").equals(projectName))
				return false;
			if (!configuration.getAttribute(IPeprLaunchConfigurationConstants.ATTRIBUTE_PROCESS, "").equals(processLocation))
				return false;
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.debug.ui.ILaunchShortcut#launch(org.eclipse.jface.viewers
	 * .ISelection, java.lang.String)
	 */
	public void launch(ISelection selection, String mode) {
		this.projectName = null;
		this.processLocation = null;
		
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection structuredSelection = (IStructuredSelection) selection;
			IFile file = (IFile) structuredSelection.getFirstElement();
			
			launch(file, mode);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ILaunchShortcut#launch(org.eclipse.ui.IEditorPart, java.lang.String)
	 */
	public void launch(IEditorPart editor, String mode) {
		this.projectName = null;
		this.processLocation = null;
		
		if (editor instanceof ProcessEditor) {
			FileEditorInput input = (FileEditorInput) editor.getEditorInput();
			IFile file = input.getFile();
			
			launch(file, mode);
		}
	}

	/**
	 * @param file
	 * @param mode
	 */
	protected void launch(IFile file, String mode) {
		this.projectName = file.getProject().getName();
		this.processLocation = file.getProjectRelativePath().toString();

		launch(mode);
	}
}
