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

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.gef.dnd.TemplateTransferDragSourceListener;
import org.eclipse.gef.palette.CombinedTemplateCreationEntry;
import org.eclipse.gef.palette.ConnectionCreationToolEntry;
import org.eclipse.gef.palette.MarqueeToolEntry;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.palette.PaletteGroup;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.palette.PaletteSeparator;
import org.eclipse.gef.palette.SelectionToolEntry;
import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.gef.ui.palette.PaletteViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;
import org.peprframework.ide.editors.ProcessEditor;
import org.peprframework.ide.model.ActivityFactory;
import org.peprframework.ide.model.TransitionFactory;

/**
 * @author Volker Fritzsch
 * @version 1.0
 *
 */
public class PaletteView extends ViewPart implements ISelectionListener {

	private PaletteViewer viewer;
	
	private PaletteGroup activityGroup;
	
	private PaletteGroup starterGroup;
	
	private PaletteRoot paletteRoot;
	
	@Override
	public void createPartControl(Composite parent) {
		viewer = new PaletteViewer();
		viewer.createControl(parent);
		viewer.addDragSourceListener(new TemplateTransferDragSourceListener(viewer));
		
		getSite().getPage().addSelectionListener(this);
		
		IEditorPart activeEditor = getSite().getPage().getActiveEditor();
		if (activeEditor != null && activeEditor instanceof ProcessEditor) {
			ProcessEditor activeProcessEditor = (ProcessEditor) activeEditor;
			activeProcessEditor.getEditDomain().setPaletteViewer(viewer);
			activeProcessEditor.getEditDomain().setPaletteRoot(createPaletteRoot());
		}
	}

	private PaletteRoot createPaletteRoot() {
		if (paletteRoot == null) {
			paletteRoot = new PaletteRoot();
			
			PaletteGroup controls = new PaletteGroup("Controls");
			
			//tools
			ToolEntry selectionToolEntry = new SelectionToolEntry();
			controls.add(selectionToolEntry);
			paletteRoot.setDefaultEntry(selectionToolEntry);
			
			ToolEntry marqueeToolEntry = new MarqueeToolEntry();
			controls.add(marqueeToolEntry);
			
			//transitions
			TransitionFactory transitionFactory = new TransitionFactory();
			ToolEntry connectionCreationTool = new ConnectionCreationToolEntry(
					transitionFactory.getLabel(),
					transitionFactory.getShortDescription(),
					transitionFactory,
					transitionFactory.getSmallIcon(),
					transitionFactory.getLargeIcon());
			controls.add(connectionCreationTool);
			
			paletteRoot.add(controls);
			PaletteSeparator separator = new PaletteSeparator("control-separator");
			separator.setUserModificationPermission(PaletteEntry.PERMISSION_NO_MODIFICATION);
			paletteRoot.add(separator);
			
			IExtensionRegistry registry = Platform.getExtensionRegistry();
			//starters
			PaletteGroup starters = new PaletteGroup("Starters");
			IExtension[] starterExtensions = registry.getExtensionPoint("org.peprframework.core.starter").getExtensions();
			for (IExtension extension : starterExtensions) {
				IConfigurationElement[] configurationElements = extension.getConfigurationElements();
				for (IConfigurationElement configurationElement : configurationElements) {
					ActivityFactory factory = new ActivityFactory(configurationElement);
					CombinedTemplateCreationEntry creationToolEntry = new CombinedTemplateCreationEntry(
							factory.getLabel(),
							factory.getShortDescription(),
							factory,
							factory.getSmallIcon(),
							factory.getLargeIcon());
					starters.add(creationToolEntry);
				}
			}
			
			paletteRoot.add(starters);
			separator = new PaletteSeparator("starter-separator");
			separator.setUserModificationPermission(PaletteEntry.PERMISSION_NO_MODIFICATION);
			paletteRoot.add(separator);
			
			//activities
			PaletteGroup activities = new PaletteGroup("Activities");
//			ActivityRegistry activityRegistry = Activator.getDefault().getActivityRegistry();
//			String[] ids = activityRegistry.getRegisteredIds();
//			for (String id : ids) {
//				ActivityFactory factory = new ActivityFactory(id);
//				CombinedTemplateCreationEntry creationToolEntry = new CombinedTemplateCreationEntry(
//						factory.getLabel(),
//						factory.getShortDescription(),
//						factory,
//						factory.getSmallIcon(),
//						factory.getLargeIcon());
//				activities.add(creationToolEntry);
//			}
			IExtension[] activityExtensions = registry.getExtensionPoint("org.peprframework.core.activity").getExtensions();
			for (IExtension extension : activityExtensions) {
				IConfigurationElement[] configurationElements = extension.getConfigurationElements();
				for (IConfigurationElement configurationElement : configurationElements) {
					ActivityFactory factory = new ActivityFactory(configurationElement);
					CombinedTemplateCreationEntry creationToolEntry = new CombinedTemplateCreationEntry(
							factory.getLabel(),
							factory.getShortDescription(),
							factory,
							factory.getSmallIcon(),
							factory.getLargeIcon());
					activities.add(creationToolEntry);
				}
			}
			
			paletteRoot.add(activities);
			
//			//nodes
//			SupportVectorMachineFactory svmFactory = new SupportVectorMachineFactory();
//			CombinedTemplateCreationEntry svmCreationToolEntry = new CombinedTemplateCreationEntry(
//					svmFactory.getLabel(),
//					svmFactory.getShortDescription(),
//					svmFactory,
//					svmFactory.getSmallIcon(),
//					svmFactory.getLargeIcon());
//			controls.add(svmCreationToolEntry);
//			
//			EchoStateNetworkFactory esnFactory = new EchoStateNetworkFactory();
//			CombinedTemplateCreationEntry esnCreationToolEntry = new CombinedTemplateCreationEntry(
//					esnFactory.getLabel(),
//					esnFactory.getShortDescription(),
//					esnFactory,
//					esnFactory.getSmallIcon(),
//					esnFactory.getLargeIcon());
//			controls.add(esnCreationToolEntry);
//			
//			PrintMessageActivityFactory printMessageFactory = new PrintMessageActivityFactory();
//			CombinedTemplateCreationEntry printMessageCreationToolEntry = new CombinedTemplateCreationEntry(
//					printMessageFactory.getLabel(),
//					printMessageFactory.getShortDescription(),
//					printMessageFactory,
//					printMessageFactory.getSmallIcon(),
//					printMessageFactory.getLargeIcon());
//			controls.add(printMessageCreationToolEntry);
//			
//			MatlabAdapterFactory matlabAdapterFactory = new MatlabAdapterFactory();
//			CombinedTemplateCreationEntry matlabAdapterCreationToolEntry = new CombinedTemplateCreationEntry(
//					matlabAdapterFactory.getLabel(),
//					matlabAdapterFactory.getShortDescription(),
//					matlabAdapterFactory,
//					matlabAdapterFactory.getSmallIcon(),
//					matlabAdapterFactory.getLargeIcon());
//			controls.add(matlabAdapterCreationToolEntry);
//			
//			MicrophoneStarterFactory microphoneStarterFactory = new MicrophoneStarterFactory();
//			CombinedTemplateCreationEntry microphoneStarterCreationToolEntry = new CombinedTemplateCreationEntry(
//					microphoneStarterFactory.getLabel(),
//					microphoneStarterFactory.getShortDescription(),
//					microphoneStarterFactory,
//					microphoneStarterFactory.getSmallIcon(),
//					microphoneStarterFactory.getLargeIcon());
//			controls.add(microphoneStarterCreationToolEntry);
//			
//			FileStarterFactory fileStarterFactory = new FileStarterFactory();
//			CombinedTemplateCreationEntry fileStarterCreationToolEntry = new CombinedTemplateCreationEntry(
//					fileStarterFactory.getLabel(),
//					fileStarterFactory.getShortDescription(),
//					fileStarterFactory,
//					fileStarterFactory.getSmallIcon(),
//					fileStarterFactory.getLargeIcon());
//			controls.add(fileStarterCreationToolEntry);
//			
//			JoinFactory joinFactory = new JoinFactory();
//			CombinedTemplateCreationEntry joinCreationToolEntry = new CombinedTemplateCreationEntry(
//					joinFactory.getLabel(),
//					joinFactory.getShortDescription(),
//					joinFactory,
//					joinFactory.getSmallIcon(),
//					joinFactory.getLargeIcon());
//			controls.add(joinCreationToolEntry);
//			
//			PlotterFactory plotterFactory = new PlotterFactory();
//			CombinedTemplateCreationEntry plotterCreationToolEntry = new CombinedTemplateCreationEntry(
//					plotterFactory.getLabel(),
//					plotterFactory.getShortDescription(),
//					plotterFactory,
//					plotterFactory.getSmallIcon(),
//					plotterFactory.getLargeIcon());
//			controls.add(plotterCreationToolEntry);
//			
//			MovingAverageFactory movingAverageFactory = new MovingAverageFactory();
//			CombinedTemplateCreationEntry movingAverageCreationToolEntry = new CombinedTemplateCreationEntry(
//					movingAverageFactory.getLabel(),
//					movingAverageFactory.getShortDescription(),
//					movingAverageFactory,
//					movingAverageFactory.getSmallIcon(),
//					movingAverageFactory.getLargeIcon());
//			controls.add(movingAverageCreationToolEntry);
			
		}
		
		return paletteRoot;
	}

	@Override
	public void setFocus() {
		viewer.setFocus(viewer.getRootEditPart());
	}

	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		if (part instanceof ProcessEditor) {
			ProcessEditor activeProcessEditor = (ProcessEditor) part;
			activeProcessEditor.getEditDomain().setPaletteViewer(viewer);
			activeProcessEditor.getEditDomain().setPaletteRoot(createPaletteRoot());
		}
		
	}

}
