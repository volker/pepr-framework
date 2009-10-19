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
package org.peprframework.ide.views.pages.overview;

import org.eclipse.gef.commands.Command;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.peprframework.core.Activity;
import org.peprframework.ide.parts.ActivityEditPart;
import org.peprframework.ide.views.ProcessInspectorPage;

/**
 * @author Volker Fritzsch
 * @version 1.0
 *
 */
public class OverviewPage implements ProcessInspectorPage {

	private Composite panel;
	
	private Text nameText;
	
	private Text descriptionText;
	
	@SuppressWarnings("unchecked")
	private Activity selectedActivity;
	
	public OverviewPage(FormToolkit toolkit, Composite parent) {
		this.panel = toolkit.createComposite(parent);
		
		panel.setLayout(new GridLayout(2, false));
		
		Label nameLabel = toolkit.createLabel(panel, "Name:");
		nameLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		
		nameText = toolkit.createText(panel, "", SWT.BORDER);
		nameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		
		Label descriptionLabel = toolkit.createLabel(panel, "Description:");
		descriptionLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		
		descriptionText = toolkit.createText(panel, "", SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
		descriptionText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
	}
	
	/* (non-Javadoc)
	 * @see org.peprframework.ide.views.ProcessInspectorPage#addModifyListener(org.eclipse.swt.events.ModifyListener)
	 */
	public void setModificationHook(final ModificationHook hook) {
		ModifyListener listener = new ModifyListener() {
		
			public void modifyText(ModifyEvent e) {
				hook.modified();
			}
		};
		
		nameText.addModifyListener(listener);
		descriptionText.addModifyListener(listener);
	}

	/* (non-Javadoc)
	 * @see org.peprframework.ide.views.ProcessInspectorPage#createApplyCommand()
	 */
	public Command createApplyCommand() {
		Command cmd = new Command("Apply") {
			
			@SuppressWarnings("unchecked")
			private Activity activity = selectedActivity;
			
			private String oldName;
			
			private String newName = nameText.getText().trim();
			
			private String oldDescription;
			
			private String newDescription = descriptionText.getText();
			
			/* (non-Javadoc)
			 * @see org.eclipse.gef.commands.Command#execute()
			 */
			@Override
			public void execute() {
				oldName = activity.getName();
				activity.setName(newName);
				
				oldDescription = activity.getDescription();
				activity.setDescription(newDescription);
			}
			
			/* (non-Javadoc)
			 * @see org.eclipse.gef.commands.Command#undo()
			 */
			@Override
			public void undo() {
				activity.setName(oldName);
				activity.setDescription(oldDescription);
			}
			
		};
		
		return cmd;
	}

	/* (non-Javadoc)
	 * @see org.peprframework.ide.views.ProcessInspectorPage#getControl()
	 */
	public Control getControl() {
		return panel;
	}

	/* (non-Javadoc)
	 * @see org.peprframework.ide.views.ProcessInspectorPage#selectionChanged(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		Object selectedElement = null;
		if (selection != null && selection instanceof IStructuredSelection)
			selectedElement = ((IStructuredSelection) selection).getFirstElement();

		selectedActivity = null;
		if (selectedElement != null && selectedElement instanceof ActivityEditPart)
			selectedActivity = ((ActivityEditPart) selectedElement).getActivity();
		
		if (selectedActivity != null) {
			nameText.setText(selectedActivity.getName().trim());
			descriptionText.setText(selectedActivity.getDescription());
		} else {
			nameText.setText("");
			descriptionText.setText("");
			descriptionText.pack();
		}
	}
}
