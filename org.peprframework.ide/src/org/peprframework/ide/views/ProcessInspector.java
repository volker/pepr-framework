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
package org.peprframework.ide.views;

import org.eclipse.gef.commands.Command;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.part.ViewPart;
import org.peprframework.ide.parts.ActivityEditPart;
import org.peprframework.ide.views.ProcessInspectorPage.ModificationHook;
import org.peprframework.ide.views.pages.configuration.ConfigurationPage;
import org.peprframework.ide.views.pages.input.InputPage;
import org.peprframework.ide.views.pages.overview.OverviewPage;

/**
 * @author Volker Fritzsch
 * @version 1.0
 */
public class ProcessInspector extends ViewPart implements ISelectionListener {

	private FormToolkit toolkit;
	
	private Form scrolledForm;
	
	private Action applyAction;
	
	private ActivityEditPart currentSelection;
	
	private CTabFolder tabFolder;
	
	private ProcessInspectorPage overviewPage;
	
	private ProcessInspectorPage configurationPage;
	
	private ProcessInspectorPage inputPage;

	private ModificationHook modificationHook = new ModificationHook() {
	
		public void modified() {
			applyAction.setEnabled(true);
		}
	};
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.ISelectionListener#selectionChanged(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		Object selectedElement = null;
		if (selection != null && selection instanceof IStructuredSelection)
			selectedElement = ((IStructuredSelection) selection).getFirstElement();
		
		this.currentSelection = null;
		if (selectedElement != null && selectedElement instanceof ActivityEditPart) {
			this.currentSelection = (ActivityEditPart) selectedElement;
			scrolledForm.setText(this.currentSelection.getActivity().getName());
		} else {
			scrolledForm.setText("Process Inspector");			
		}

		this.overviewPage.selectionChanged(part, selection);
		this.configurationPage.selectionChanged(part, selection);
		this.inputPage.selectionChanged(part, selection);
		
		//TODO push this into all subpages
//		this.scrolledForm.reflow(true);
		for (CTabItem tab : tabFolder.getItems()) {
			((Composite) tab.getControl()).layout(true, true);
		}
		this.applyAction.setEnabled(false);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		toolkit = new FormToolkit(parent.getDisplay());
		scrolledForm = toolkit.createForm(parent);
		scrolledForm.getBody().setLayout(new FillLayout());
		
		createActions();
		fillFormToolBar();
		fillPages();
		
		getSite().getPage().addPostSelectionListener(this);
		selectionChanged(getSite().getPart(), getSite().getPage().getSelection());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
		scrolledForm.getBody().setFocus();
	}
	
	protected void fillFormToolBar() {
		toolkit.decorateFormHeading(scrolledForm);
		IToolBarManager manager = scrolledForm.getToolBarManager();
		manager.add(applyAction);
		manager.update(true);
	}
	
	protected void createActions() {
		applyAction = new Action() {
			@Override
			public void run() {
				// chain all page's commands
				Command cmd = overviewPage.createApplyCommand();
				cmd = cmd.chain(configurationPage.createApplyCommand());
				cmd = cmd.chain(inputPage.createApplyCommand());
				
				// execute compound command
				currentSelection.getViewer().getEditDomain().getCommandStack().execute(cmd);
				
				applyAction.setEnabled(false);
			}
		};
		applyAction.setText("Apply");
		applyAction.setEnabled(false);
	}
	
	protected void fillPages() {
		tabFolder = new CTabFolder(scrolledForm.getBody(), SWT.BOTTOM);
		
		// overview tab
		Composite overviewComposite = toolkit.createComposite(tabFolder);
		overviewComposite.setLayout(new FillLayout());
		CTabItem overviewTab = new CTabItem(tabFolder, SWT.FLAT);
		overviewTab.setControl(overviewComposite);
		overviewTab.setText("Overview");
		overviewPage = new OverviewPage(toolkit, overviewComposite);
		overviewPage.setModificationHook(modificationHook);
		
		
		// configuration tab
		Composite configurationComposite = toolkit.createComposite(tabFolder);
		configurationComposite.setLayout(new FillLayout());
		CTabItem configurationTab = new CTabItem(tabFolder, SWT.FLAT);
		configurationTab.setControl(configurationComposite);
		configurationTab.setText("Configuration");
		configurationPage = new ConfigurationPage(toolkit, configurationComposite);
		configurationPage.setModificationHook(modificationHook);
		
		// input tab
		Composite inputComposite = toolkit.createComposite(tabFolder);
		inputComposite.setLayout(new FillLayout());
		CTabItem inputTab = new CTabItem(tabFolder, SWT.FLAT);
		inputTab.setControl(inputComposite);
		inputTab.setText("Input");
		inputPage = new InputPage(toolkit, inputComposite);
		inputPage.setModificationHook(modificationHook);
		
		tabFolder.setSelection(overviewTab);
	}
}
