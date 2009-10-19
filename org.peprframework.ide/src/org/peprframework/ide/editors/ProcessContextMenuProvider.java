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
package org.peprframework.ide.editors;

import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.ui.actions.ActionFactory;

/**
 * @author Volker Fritzsch
 * @version 1.0
 */
public class ProcessContextMenuProvider extends ContextMenuProvider {

	private ActionRegistry actionRegistry;
	
	/**
	 * @param viewer
	 * @param actionRegistry
	 */
	ProcessContextMenuProvider(EditPartViewer viewer, ActionRegistry actionRegistry) {
		super(viewer);
		this.actionRegistry = actionRegistry;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.gef.ContextMenuProvider#buildContextMenu(org.eclipse.jface.action.IMenuManager)
	 */
	@Override
	public void buildContextMenu(IMenuManager menu) {
		GEFActionConstants.addStandardActionGroups(menu);
		
		IAction action;
		action = actionRegistry.getAction(ActionFactory.UNDO.getId());
		menu.appendToGroup(GEFActionConstants.GROUP_UNDO, action);

		action = actionRegistry.getAction(ActionFactory.REDO.getId());
		menu.appendToGroup(GEFActionConstants.GROUP_UNDO, action);
		
		action = actionRegistry.getAction(ActionFactory.DELETE.getId());
		if (action.isEnabled())
			menu.appendToGroup(GEFActionConstants.GROUP_EDIT, action);

	}

}
