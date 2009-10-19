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
package org.peprframework.ide.wizards;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;

/**
 * @author Volker Fritzsch
 * @version 1.0
 *
 */
public class NewProcessWizard extends Wizard implements INewWizard {

	private IWorkbench workbench;
	
	private IStructuredSelection selection;
	
	private WizardNewFileCreationPage mainPage;
	
	@Override
	public boolean performFinish() {
		IFile file = mainPage.createNewFile();
		BasicNewResourceWizard.selectAndReveal(file, workbench.getActiveWorkbenchWindow());
		
		try {
			IDE.openEditor(workbench.getActiveWorkbenchWindow().getActivePage(), file);
		} catch (PartInitException ex) {
			ex.printStackTrace();
		}
		
		return true;
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.workbench = workbench;
		this.selection = selection;
		
		setWindowTitle("New Process");
		setDefaultPageImageDescriptor(ImageDescriptor.getMissingImageDescriptor());
		setNeedsProgressMonitor(true);
	}

	@Override
	public void addPages() {
		mainPage = new WizardNewFileCreationPage("basicNewProcessPage", selection);
		mainPage.setTitle("Process");
		mainPage.setDescription("Create a new process resource.");
		mainPage.setFileExtension("process");
		addPage(mainPage);
	}
	
}
