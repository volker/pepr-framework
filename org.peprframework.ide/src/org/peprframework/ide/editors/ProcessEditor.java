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

import java.util.ArrayList;
import java.util.EventObject;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.KeyHandler;
import org.eclipse.gef.KeyStroke;
import org.eclipse.gef.MouseWheelHandler;
import org.eclipse.gef.MouseWheelZoomHandler;
import org.eclipse.gef.dnd.TemplateTransferDropTargetListener;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.DeleteAction;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.gef.ui.actions.ZoomInAction;
import org.eclipse.gef.ui.actions.ZoomOutAction;
import org.eclipse.gef.ui.parts.GraphicalEditor;
import org.eclipse.gef.ui.parts.GraphicalViewerKeyHandler;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertySheetPageContributor;
import org.peprframework.core.Process;
import org.peprframework.ide.parts.PeprEditPartFactory;

/**
 * @author Volker Fritzsch
 * @version 1.0
 *
 */
public class ProcessEditor extends GraphicalEditor implements ITabbedPropertySheetPageContributor {

	private Process process;
	private KeyHandler sharedKeyHandler;
	
	/**
	 * 
	 */
	public ProcessEditor() {
		setEditDomain(new DefaultEditDomain(this));
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.ui.parts.GraphicalEditor#configureGraphicalViewer()
	 */
	@Override
	protected void configureGraphicalViewer() {
		ScrollingGraphicalViewer viewer = (ScrollingGraphicalViewer) getGraphicalViewer();
		viewer.setRootEditPart(new ScalableFreeformRootEditPart());
		viewer.getControl().setBackground(ColorConstants.listBackground);
		
		ZoomManager zoomManager = (ZoomManager) getAdapter(ZoomManager.class);
		ActionRegistry registry = getActionRegistry();
		IAction action;
		
		action = new ZoomOutAction(zoomManager);
		registry.registerAction(action);
//		getSite().getKeyBindingService().registerAction(action);
		
		action = new ZoomInAction(zoomManager);
		registry.registerAction(action);
//		getSite().getKeyBindingService().registerAction(action);
		
		action = new DeleteAction(getSite().getPart());
		registry.registerAction(action);
//		getSite().getKeyBindingService().registerAction(action);
		
		ArrayList<String> zoomLevels = new ArrayList<String>(3);
		zoomLevels.add(ZoomManager.FIT_ALL);
		zoomLevels.add(ZoomManager.FIT_WIDTH);
		zoomLevels.add(ZoomManager.FIT_HEIGHT);
		zoomManager.setZoomLevelContributions(zoomLevels);
		
		getGraphicalViewer().setKeyHandler(new GraphicalViewerKeyHandler(getGraphicalViewer())
		.setParent(getCommonKeyHandler()));
		
		ContextMenuProvider provider =
			new ProcessContextMenuProvider(getGraphicalViewer(), getActionRegistry());
		getGraphicalViewer().setContextMenu(provider);
		getSite().registerContextMenu(
			"org.peprframework.ide.editor.contextmenu", //$NON-NLS-1$
			provider,
			getGraphicalViewer());
		
		viewer.setProperty(MouseWheelHandler.KeyGenerator.getKey(SWT.MOD1), MouseWheelZoomHandler.SINGLETON);
	}
	
	/**
	 * @return
	 */
	protected KeyHandler getCommonKeyHandler() {
		if (sharedKeyHandler == null) {
			sharedKeyHandler = new KeyHandler();
			sharedKeyHandler.put(
				KeyStroke.getPressed(SWT.DEL, 127, 0),
				getActionRegistry().getAction(ActionFactory.DELETE.getId()));
			sharedKeyHandler.put(
				KeyStroke.getPressed(SWT.F2, 0),
				getActionRegistry().getAction(GEFActionConstants.DIRECT_EDIT));
		}
		return sharedKeyHandler;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.ui.parts.GraphicalEditor#createActions()
	 */
	@Override
	protected void createActions() {
		super.createActions();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void doSave(IProgressMonitor monitor) {
		IFile file = ((FileEditorInput) getEditorInput()).getFile();
		org.peprframework.core.CorePlugin.getDefault().getProcessMarshaller().marshallProcess(process, file, monitor);
		getEditDomain().getCommandStack().markSaveLocation();
		firePropertyChange(PROP_DIRTY);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.tabbed.ITabbedPropertySheetPageContributor#getContributorId()
	 */
	public String getContributorId() {
		return "org.peprframework.ide.editors.ProcessEditor";
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.ui.parts.GraphicalEditor#getEditDomain()
	 */
	@Override
	public DefaultEditDomain getEditDomain() {
		return super.getEditDomain();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.ui.parts.GraphicalEditor#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
	 */
	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		super.init(site, input);
		
		setPartName(input.getName());
		setTitleToolTip(input.getToolTipText());
		setTitleImage(input.getImageDescriptor().createImage());
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.ui.parts.GraphicalEditor#initializeGraphicalViewer()
	 */
	@Override
	protected void initializeGraphicalViewer() {
		IFile file = ((FileEditorInput) getEditorInput()).getFile();
		process = org.peprframework.core.CorePlugin.getDefault().getProcessMarshaller().unmarshallProcess(file);
		
		if (process == null) {
			process = new Process();
			process.setName(getEditorInput().getName());
		}
		
		getGraphicalViewer().setEditDomain(getEditDomain());
		getGraphicalViewer().setEditPartFactory(new PeprEditPartFactory());
		
		getGraphicalViewer().setContents(process);
		getGraphicalViewer().addDropTargetListener(new TemplateTransferDropTargetListener(getGraphicalViewer()));
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Object getAdapter(Class type) {
		if (type == ZoomManager.class)
			return ((ScalableFreeformRootEditPart) getGraphicalViewer().getRootEditPart()).getZoomManager();
		
		return super.getAdapter(type);
	}
	
	@Override
	public void commandStackChanged(EventObject event) {
		super.commandStackChanged(event);
		if (getEditDomain().getCommandStack().isDirty())
			firePropertyChange(PROP_DIRTY);
	}

}
