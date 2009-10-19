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

import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.ui.actions.ActionBarContributor;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.gef.ui.actions.RedoRetargetAction;
import org.eclipse.gef.ui.actions.UndoRetargetAction;
import org.eclipse.gef.ui.actions.ZoomComboContributionItem;
import org.eclipse.gef.ui.actions.ZoomInRetargetAction;
import org.eclipse.gef.ui.actions.ZoomOutRetargetAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.actions.ActionFactory;

/**
 * @author Volker Fritzsch
 * @version 1.0
 *
 */
public class ProcessEditorActionBarContributor extends ActionBarContributor {

	private String[] zoomStrings = new String[] { ZoomManager.FIT_ALL, ZoomManager.FIT_HEIGHT, ZoomManager.FIT_WIDTH };
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.ui.actions.ActionBarContributor#buildActions()
	 */
	@Override
	protected void buildActions() {
		addRetargetAction(new UndoRetargetAction());
		addRetargetAction(new RedoRetargetAction());
		addRetargetAction(new ZoomInRetargetAction());
		addRetargetAction(new ZoomOutRetargetAction());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.ui.actions.ActionBarContributor#declareGlobalActionKeys()
	 */
	@Override
	protected void declareGlobalActionKeys() {
		addGlobalActionKey(ActionFactory.UNDO.getId());
		addGlobalActionKey(ActionFactory.REDO.getId());
		addGlobalActionKey(ActionFactory.COPY.getId());
		addGlobalActionKey(ActionFactory.PASTE.getId());
		addGlobalActionKey(ActionFactory.DELETE.getId());
		addGlobalActionKey(ActionFactory.SELECT_ALL.getId());
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorActionBarContributor#contributeToToolBar(org.eclipse.jface.action.IToolBarManager)
	 */
	@Override
	public void contributeToToolBar(IToolBarManager toolBarManager) {
		super.contributeToToolBar(toolBarManager);
		
		toolBarManager.add(new Separator());
		toolBarManager.add(new ZoomComboContributionItem(getPage(), zoomStrings));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorActionBarContributor#contributeToMenu(org.eclipse.jface.action.IMenuManager)
	 */
	@Override
	public void contributeToMenu(IMenuManager menuManager) {
		super.contributeToMenu(menuManager);
		
		MenuManager viewMenu = new MenuManager("&View", IWorkbenchActionConstants.M_VIEW);
		viewMenu.add(getAction(GEFActionConstants.ZOOM_IN));
		viewMenu.add(getAction(GEFActionConstants.ZOOM_OUT));
		menuManager.insertAfter(IWorkbenchActionConstants.M_EDIT,viewMenu);
	}
	
}
