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
package org.peprframework.ide.views.pages.input;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.content.IContentTypeManager;
import org.eclipse.gef.commands.Command;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextListener;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.TextEvent;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.util.DelegatingDragAdapter;
import org.eclipse.jface.util.DelegatingDropAdapter;
import org.eclipse.jface.util.TransferDragSourceListener;
import org.eclipse.jface.util.TransferDropTargetListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.texteditor.AbstractDocumentProvider;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.peprframework.core.Activity;
import org.peprframework.core.GroovyInputFilter;
import org.peprframework.core.InputFilter;
import org.peprframework.ide.parts.ActivityEditPart;
import org.peprframework.ide.views.ProcessInspectorPage;
import org.peprframework.ide.views.pages.input.InputTargetContentProvider.InputProperty;

/**
 * @author Volker Fritzsch
 * @version 1.0
 *
 */
public class InputPage implements ProcessInspectorPage {

	private FormToolkit toolkit;
	
	private Composite panel;
	
	private TreeViewer sourceViewer;
	
//	private ListViewer targetViewer;
	private TableViewer targetViewer;
	
	private InputSourceContentProvider sourceViewerContentProvider;
	
	private InputSourceLabelProvider sourceViewerLabelProvider;
	
	private InputTargetContentProvider targetViewerContentProvider;
	
	private InputTargetLabelProvider targetViewerLabelProvider;
	
	private IDocumentProvider textViewerDocumentProvider;

	private ITextViewer textViewer;

	private ActivityEditPart selectedActivityEditPart;
	
	protected ModificationHook modificationHook;
	
	public InputPage(FormToolkit toolkit, Composite parent) {
		this.toolkit = toolkit;
		this.panel = toolkit.createComposite(parent);
		GridLayout layout = new GridLayout(2, false);
		this.panel.setLayout(layout);
		
		// create viewers
		createInputTargetViewer();
		createMappingTextViewer();
		createInputSourceViewer();
	}
	
	/* (non-Javadoc)
	 * @see org.peprframework.ide.views.ProcessInspectorPage#addModifyListener(org.eclipse.swt.events.ModifyListener)
	 */
	public void setModificationHook(ModificationHook hook) {
		this.modificationHook = hook;
	}

	/* (non-Javadoc)
	 * @see org.peprframework.ide.views.ProcessInspectorPage#createApplyCommand()
	 */
	public Command createApplyCommand() {
		Command cmd = new Command("Apply") {
			
			/* (non-Javadoc)
			 * @see org.eclipse.gef.commands.Command#execute()
			 */
			@SuppressWarnings("unchecked")
			@Override
			public void execute() {
				Object inputFilter = selectedActivityEditPart.getActivity().getInputFilter();
				if (inputFilter == null) {
					inputFilter = new GroovyInputFilter();
					selectedActivityEditPart.getActivity().setInputFilter((InputFilter) inputFilter);
				}
					
				if (inputFilter instanceof GroovyInputFilter) {
					((GroovyInputFilter) inputFilter).setScript(textViewer.getTextWidget().getText());
				}
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

		ActivityEditPart selectedActivityEditPart = null;
		if (selectedElement instanceof ActivityEditPart)
			selectedActivityEditPart = (ActivityEditPart) selectedElement;
		
		sourceViewer.setInput(selectedActivityEditPart);
		targetViewer.setInput(selectedActivityEditPart);
			
		if (selectedActivityEditPart != null)
			textViewerDocumentProvider.disconnect(this.selectedActivityEditPart);
			
		this.selectedActivityEditPart = selectedActivityEditPart;
		try {
			textViewerDocumentProvider.connect(selectedActivityEditPart);
		} catch (CoreException ex) {
			ex.printStackTrace();
		}
		IDocument document = textViewerDocumentProvider.getDocument(selectedActivityEditPart);
		textViewer.setDocument(document);
	}

	private void createMappingTextViewer() {
		DelegatingDropAdapter dropAdapter = new DelegatingDropAdapter();
		dropAdapter.addDropTargetListener(new TransferDropTargetListener() {
		
			public void dropAccept(DropTargetEvent event) {
			}
		
			public void drop(DropTargetEvent event) {
				StyledText textWidget = textViewer.getTextWidget();
				String text = (String) event.data;
				int caretOffset = textWidget.getCaretOffset();
				textWidget.insert(text);
				textWidget.setCaretOffset(caretOffset + text.length());
				textWidget.setFocus();
				
			}
		
			public void dragOver(DropTargetEvent event) {
			}
		
			public void dragOperationChanged(DropTargetEvent event) {
			}
		
			public void dragLeave(DropTargetEvent event) {
			}
		
			public void dragEnter(DropTargetEvent event) {
			}
		
			public boolean isEnabled(DropTargetEvent event) {
				return true;
			}
		
			public Transfer getTransfer() {
				return TextTransfer.getInstance();
			}
		});
		
		textViewerDocumentProvider = new AbstractDocumentProvider() {
		
			@Override
			protected IRunnableContext getOperationRunner(IProgressMonitor monitor) {
				// TODO Auto-generated method stub
				return null;
			}
		
			@Override
			protected void doSaveDocument(IProgressMonitor monitor, Object element,
					IDocument document, boolean overwrite) throws CoreException {
			}
		
			@SuppressWarnings("unchecked")
			@Override
			protected IDocument createDocument(Object element) throws CoreException {
				if (!(element instanceof ActivityEditPart))
					return null;
				
				Activity activity = ((ActivityEditPart) element).getActivity();
				InputFilter inputFilter = (InputFilter) activity.getInputFilter();

				if (inputFilter == null)
					inputFilter = new GroovyInputFilter();

				if (!(inputFilter instanceof GroovyInputFilter))
					return null;

				GroovyInputFilter groovyInputFilter = (GroovyInputFilter) inputFilter;
				String script = groovyInputFilter.getScript();
				IDocument document = null;
				
				if (script == null || script.trim().length() == 0) {
					document = new Document("");
				} else {
					document = new Document(script);
				}
				
				return document;
			}
			
			@Override
			public IContentType getContentType(Object element)
					throws CoreException {
				return Platform.getContentTypeManager().getContentType(IContentTypeManager.CT_TEXT);
			}
		
			@Override
			protected IAnnotationModel createAnnotationModel(Object element)
					throws CoreException {
				return null;
			}
		};
		
		Section section = toolkit.createSection(panel, Section.SHORT_TITLE_BAR | Section.EXPANDED | Section.CLIENT_INDENT);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 2);
		section.setLayoutData(data);
		section.setText("Mapping");
		
		textViewer = new TextViewer(section, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		section.setClient(textViewer.getTextWidget());
		
		DropTarget textViewerDropTarget = new DropTarget(textViewer.getTextWidget(), DND.DROP_COPY | DND.DROP_MOVE);
		textViewerDropTarget.setTransfer(dropAdapter.getTransfers());
		textViewerDropTarget.addDropListener(dropAdapter);
		
		textViewer.addTextListener(new ITextListener() {
		
			public void textChanged(TextEvent event) {
				if (event.getDocumentEvent() == null) {
					return;
				}
				
				modificationHook.modified();
			}
		});
	}

	private void createInputTargetViewer() {
		Section section = toolkit.createSection(panel, Section.SHORT_TITLE_BAR | Section.EXPANDED | Section.CLIENT_INDENT | Section.DESCRIPTION);
		GridData data = new GridData(SWT.FILL, SWT.FILL, false, false);
		data.widthHint = 400;
		section.setLayoutData(data);
		section.setText("Input");
		section.setDescription("The input parameters of the selected Activity.");
		
		targetViewerContentProvider = new InputTargetContentProvider();
		targetViewerLabelProvider = new InputTargetLabelProvider();
		targetViewer = new TableViewer(section, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.SINGLE);
//		targetViewer = new ListViewer(section, SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		TableViewerColumn typeColumn = new TableViewerColumn(targetViewer, SWT.NONE);
		typeColumn.getColumn().setWidth(80);
		TableViewerColumn nameColumn = new TableViewerColumn(targetViewer, SWT.NONE);
		nameColumn.getColumn().setWidth(320);
		targetViewer.getTable().setHeaderVisible(false);
		targetViewer.setSorter(new ViewerSorter() {
			
			/* (non-Javadoc)
			 * @see org.eclipse.jface.viewers.ViewerComparator#compare(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
			 */
			@Override
			public int compare(Viewer viewer, Object e1, Object e2) {
				InputProperty p1 = (InputProperty) e1;
				InputProperty p2 = (InputProperty) e2;
				
				return p1.getName().compareTo(p2.getName());
			}
			
		});
		section.setClient(targetViewer.getControl());

		targetViewer.setContentProvider(targetViewerContentProvider);
		targetViewer.setLabelProvider(targetViewerLabelProvider);
		
		// DnD
		DelegatingDragAdapter dragAdapter = new DelegatingDragAdapter();
		dragAdapter.addDragSourceListener(new TransferDragSourceListener() {
			
			private IStructuredSelection selection;
		
			public void dragStart(DragSourceEvent event) {
				IStructuredSelection selection = (IStructuredSelection) targetViewer.getSelection();
				
				if(!selection.isEmpty() && selection.getFirstElement() instanceof InputProperty) {
					this.selection = selection;
					event.doit = true;
				} else {
					this.selection = null;
					event.doit = false;
				}
			}
		
			public void dragSetData(DragSourceEvent event) {
				InputProperty inputProperty = (InputProperty) this.selection.getFirstElement();
				event.data = "input." + inputProperty.getName() + " = ";
			}
		
			public void dragFinished(DragSourceEvent event) {
			}
		
			public Transfer getTransfer() {
				return TextTransfer.getInstance();
			}
		});
		targetViewer.addDragSupport(DND.DROP_COPY | DND.DROP_MOVE, dragAdapter.getTransfers(), dragAdapter);
	}

	private void createInputSourceViewer() {
		Section section = toolkit.createSection(panel, Section.SHORT_TITLE_BAR | Section.EXPANDED | Section.CLIENT_INDENT | Section.DESCRIPTION);
		GridData data = new GridData(SWT.FILL, SWT.FILL, false, true);
		section.setLayoutData(data);
		section.setText("Context");
		section.setDescription("The available output of previous Activities in the current Context.");
		
		sourceViewerContentProvider = new InputSourceContentProvider();
		sourceViewerLabelProvider = new InputSourceLabelProvider();
		sourceViewer = new TreeViewer(section);
		section.setClient(sourceViewer.getControl());
		
		sourceViewer.setContentProvider(sourceViewerContentProvider);
		sourceViewer.setLabelProvider(sourceViewerLabelProvider);
		
		sourceViewer.setSorter(new ViewerSorter() {
			
			/* (non-Javadoc)
			 * @see org.eclipse.jface.viewers.ViewerComparator#compare(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
			 */
			@Override
			public int compare(Viewer viewer, Object e1, Object e2) {
				if (e1 instanceof Activity && e2 instanceof Activity) {
					return ((Activity) e1).getName().compareTo(((Activity) e2).getName());
				}
				
				return ((OutputProperty) e1).getName().compareTo(((OutputProperty) e2).getName());
			}
			
		});
		
		// DnD
		DelegatingDragAdapter dragAdapter = new DelegatingDragAdapter();
		dragAdapter.addDragSourceListener(new TransferDragSourceListener() {
			
			private ITreeSelection selection;
		
			public void dragStart(DragSourceEvent event) {
				ITreeSelection selection = (ITreeSelection) sourceViewer.getSelection();
				
				if(!selection.isEmpty() && selection.getFirstElement() instanceof OutputProperty) {
					this.selection = selection;
					event.doit = true;
				} else {
					this.selection = null;
					event.doit = false;
				}
			}
		
			public void dragSetData(DragSourceEvent event) {
				OutputProperty outputProperty = (OutputProperty) this.selection.getFirstElement();
				event.data = "context.get(\"" + outputProperty.getSource() + "\")." + outputProperty.getName();
			}
		
			public void dragFinished(DragSourceEvent event) {
			}
		
			public Transfer getTransfer() {
				return TextTransfer.getInstance();
			}
		});
		sourceViewer.addDragSupport(DND.DROP_COPY | DND.DROP_MOVE, dragAdapter.getTransfers(), dragAdapter);
	}
}
