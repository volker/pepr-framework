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

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.pde.ui.launcher.OSGiLaunchConfigurationInitializer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.model.WorkbenchLabelProvider;

/**
 * @author Volker Fritzsch
 * @version 1.0
 *
 */
public class MainTab extends AbstractLaunchConfigurationTab {

	public static final String ID = "org.peprframework.ide.launch.tabs.main";
	
	private class MainTabListener extends SelectionAdapter implements ModifyListener {
	
		/* (non-Javadoc)
		 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
		 */
		public void modifyText(ModifyEvent e) {
			updateLaunchConfigurationDialog();
		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
		 */
		@Override
		public void widgetSelected(SelectionEvent e) {
			if (e.getSource() == projectBrowseButton) {
				IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
				IProject[] projects = root.getProjects();
				
				ElementListSelectionDialog dialog = new ElementListSelectionDialog(getShell(), new WorkbenchLabelProvider());
				dialog.setTitle("Project Selection");
				dialog.setMessage("Select a project to constrain your search.");
				dialog.setMultipleSelection(false);
				dialog.setBlockOnOpen(true);
				dialog.setElements(projects);
				int result = dialog.open();
				if (result == ElementListSelectionDialog.OK) {
					IProject project = (IProject) dialog.getFirstResult();
					projectText.setText(project.getName()); 
				}
			} else {
				if (e.getSource() == processBrowseButton) {
					IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
					IProject[] projects = null;
					try {
						projects = new IProject[] { root.getProject(projectText.getText()) };
					} catch (Exception ex) {
						projects = root.getProjects();
					}
					
					final List<IResource> processes = new LinkedList<IResource>();
					for (IProject project : projects) {
						try {
							project.accept(new IResourceVisitor() {
							
								public boolean visit(IResource resource) throws CoreException {
									if (resource != null && resource.getFileExtension() != null && resource.getFileExtension().equals("process"))
										processes.add(resource);
									return true;
								}
							});
						} catch (CoreException ex) {
						}
					}
					
					ElementListSelectionDialog dialog = new ElementListSelectionDialog(getShell(), new WorkbenchLabelProvider());
					dialog.setTitle("Select Process");
					dialog.setMessage("Select type (? = any character, * = any String, TZ = TimeZone):");
					dialog.setMultipleSelection(false);
					dialog.setBlockOnOpen(true);
					dialog.setElements(processes.toArray());
					int result = dialog.open();
					if (result == ElementListSelectionDialog.OK) {
						IResource resource = (IResource) dialog.getFirstResult();
						processText.setText(resource.getProjectRelativePath().toString());
					}
				}
			}
		}
		
	}
	
	private MainTabListener listener = new MainTabListener();
	
	private Text projectText;
	
	private Button projectBrowseButton;
	
	private Text processText;
	
	private Button processBrowseButton;
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		composite.setLayout(new GridLayout(1, false));
		setControl(composite);
		
		Group projectGroup = new Group(composite, SWT.NONE);
		projectGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		projectGroup.setLayout(new GridLayout(2, false));
		projectGroup.setText("Project:");
		
		projectText = new Text(projectGroup, SWT.BORDER);
		projectText.setLayoutData(new GridData(SWT.FILL, SWT.DEFAULT, true, false));
		projectText.addModifyListener(listener);
		projectBrowseButton = new Button(projectGroup, SWT.PUSH);
		projectBrowseButton.setLayoutData(new GridData(SWT.DEFAULT, SWT.DEFAULT, false, false));
		projectBrowseButton.setText("Browse...");
		projectBrowseButton.addSelectionListener(listener);
		
		Group processGroup = new Group(composite, SWT.NONE);
		processGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		processGroup.setLayout(new GridLayout(2, false));
		processGroup.setText("Process:");
		
		processText = new Text(processGroup, SWT.BORDER);
		processText.setLayoutData(new GridData(SWT.FILL, SWT.DEFAULT, true, false));
		processText.addModifyListener(listener);
		processBrowseButton = new Button(processGroup, SWT.PUSH);
		processBrowseButton.setLayoutData(new GridData(SWT.DEFAULT, SWT.DEFAULT, false, false));
		processBrowseButton.setText("Search...");
		processBrowseButton.addSelectionListener(listener);
		
		Dialog.applyDialogFont(composite);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#getName()
	 */
	public String getName() {
		return "Main";
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#initializeFrom(org.eclipse.debug.core.ILaunchConfiguration)
	 */
	public void initializeFrom(ILaunchConfiguration configuration) {
		try {
			projectText.setText(configuration.getAttribute(IPeprLaunchConfigurationConstants.ATTRIBUTE_PROJECT, ""));
		} catch (CoreException ex) {
			projectText.setText("");
		}
		try {
			processText.setText(configuration.getAttribute(IPeprLaunchConfigurationConstants.ATTRIBUTE_PROCESS, ""));
		} catch (CoreException ex) {
			processText.setText("");
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#performApply(org.eclipse.debug.core.ILaunchConfigurationWorkingCopy)
	 */
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(IPeprLaunchConfigurationConstants.ATTRIBUTE_PROJECT, projectText.getText());
		configuration.setAttribute(IPeprLaunchConfigurationConstants.ATTRIBUTE_PROCESS, processText.getText());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#setDefaults(org.eclipse.debug.core.ILaunchConfigurationWorkingCopy)
	 */
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		OSGiLaunchConfigurationInitializer initializer = new OSGiLaunchConfigurationInitializer();
		initializer.initialize(configuration);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.AbstractLaunchConfigurationTab#getId()
	 */
	@Override
	public String getId() {
		return ID;
	}
}
