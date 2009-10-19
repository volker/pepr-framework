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
package org.peprframework.ide.views.pages.configuration;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.gef.commands.Command;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.peprframework.core.Activity;
import org.peprframework.core.Configuration;
import org.peprframework.ide.parts.ActivityEditPart;
import org.peprframework.ide.propertychooser.PropertyChooser;
import org.peprframework.ide.propertychooser.PropertyChooserBuilder;
import org.peprframework.ide.propertychooser.PropertyListener;
import org.peprframework.ide.views.ProcessInspectorPage;

/**
 * @author Volker Fritzsch
 * @version 1.0
 *
 */
public class ConfigurationPage implements ProcessInspectorPage {

	private class PropertyChooserContainer {
		
		String field;
		
		Label name;
		
		PropertyChooser propertyChooser;
		
		Label description;
		
		PropertyChooserContainer(Label name, String field, PropertyChooser propertyChooser, Label description) {
			this.name = name;
			this.field = field;
			this.propertyChooser = propertyChooser;
			this.description = description;
		}
	}
	
	private ModificationHook modificationHook;

	private static final String CONFIGURATION_EXTENSION_POINT_NAMESPACE = "org.peprframework.core";

	private static final String CONFIGURATION_EXTENSION_POINT_NAME = "configuration";

	private static final String PROPERTY_CHOOSER_NAMESPACE = "org.peprframework.ide";

	private static final String PROPERTY_CHOOSER_EXTENSION_POINT_NAME = "configurationPropertyChooser";

	private FormToolkit toolkit;
	
	private Composite panel;
	
	private ScrolledForm scrolledForm;
	
	private Map<String,PropertyChooserBuilder> propertyChooserBuilders;
	
	private List<PropertyChooserContainer> propertyChoosers = new ArrayList<PropertyChooserContainer>();

	private Configuration selectedConfiguration;
	
	public ConfigurationPage(FormToolkit toolkit, Composite parent) {
		this.toolkit = toolkit;
		this.scrolledForm = toolkit.createScrolledForm(parent);
		this.scrolledForm.getBody().setLayout(new FillLayout());
		this.panel = toolkit.createComposite(this.scrolledForm.getBody());
		
		
		TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 2;
		layout.makeColumnsEqualWidth = false;
		this.panel.setLayout(layout);
		
		initializeConfigurationPropertyTypeBindings();
	}
	
	private void initializeConfigurationPropertyTypeBindings() {
		propertyChooserBuilders = new HashMap<String, PropertyChooserBuilder>();
		IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(PROPERTY_CHOOSER_NAMESPACE, PROPERTY_CHOOSER_EXTENSION_POINT_NAME);
		IExtension[] extensions = extensionPoint.getExtensions();
		for (IExtension extension : extensions) {
			IConfigurationElement[] configurationElements = extension.getConfigurationElements();
			for (IConfigurationElement configurationElement : configurationElements) {
				try {
					String type = configurationElement.getAttribute("type");
					PropertyChooserBuilder implementation = (PropertyChooserBuilder) configurationElement.createExecutableExtension("implementation");
					propertyChooserBuilders.put(type, implementation);
				} catch (CoreException e) {
					e.printStackTrace();
				}
			}
		}
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
			@Override
			public void execute() {
				for (PropertyChooserContainer container : propertyChoosers) {
					String field = container.field;
					PropertyChooser chooser = container.propertyChooser;
					
					Field f;
					try {
						f = selectedConfiguration.getClass().getDeclaredField(field);
						f.setAccessible(true);
						f.set(selectedConfiguration, chooser.getValue());
						//System.out.println("Set " + field + " to " + chooser.getValue());
					} catch (SecurityException e) {
						e.printStackTrace();
					} catch (NoSuchFieldException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}
			}
			
		};
		
		return cmd;
	}

	/* (non-Javadoc)
	 * @see org.peprframework.ide.views.ProcessInspectorPage#getControl()
	 */
	public Control getControl() {
		return scrolledForm;
	}

	/* (non-Javadoc)
	 * @see org.peprframework.ide.views.ProcessInspectorPage#selectionChanged(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
	 */
	@SuppressWarnings("unchecked")
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		Object selectedElement = null;
		if (selection != null && selection instanceof IStructuredSelection)
			selectedElement = ((IStructuredSelection) selection).getFirstElement();

		Activity selectedActivity = null;
		if (selectedElement != null && selectedElement instanceof ActivityEditPart)
			selectedActivity = ((ActivityEditPart) selectedElement).getActivity();
		
		selectedConfiguration = null;
		if (selectedActivity != null) {
			selectedConfiguration = selectedActivity.getConfiguration();
		
			if (selectedConfiguration == null)
				try {
					selectedConfiguration = (Configuration) selectedActivity.getConfigurationTypeClass().newInstance();
					selectedActivity.setConfiguration(selectedConfiguration);
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
		}
		
		// cleanup
		for (PropertyChooserContainer propertyChooserContainer : propertyChoosers) {
			propertyChooserContainer.name.dispose();
			propertyChooserContainer.propertyChooser.dispose();
			propertyChooserContainer.description.dispose();
		}
		this.propertyChoosers.clear();
		
		// adjust input controls to new selection
		if (selectedConfiguration != null)
			adjustToSelectedConfiguration();
		
		this.panel.redraw();
		this.scrolledForm.reflow(true);
	}

	@SuppressWarnings("unchecked")
	private void adjustToSelectedConfiguration() {
		Class currentConfigurationClass = selectedConfiguration.getClass();
		IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(CONFIGURATION_EXTENSION_POINT_NAMESPACE, CONFIGURATION_EXTENSION_POINT_NAME);
		IExtension[] extensions = extensionPoint.getExtensions();
		for (IExtension extension : extensions) {
			IConfigurationElement[] configurationElements = extension.getConfigurationElements();
			for (IConfigurationElement configurationElement : configurationElements) {
				String implementation = configurationElement.getAttribute("implementation");
				if (currentConfigurationClass.getName().equals(implementation)) {
					// we have found the extension that belongs to the selected activity's configuration
					
					IConfigurationElement[] properties = configurationElement.getChildren("property");
					for (IConfigurationElement property : properties) {
						String name = property.getAttribute("name");
						String field = property.getAttribute("field");
						String type = property.getAttribute("type");
						String description = property.getAttribute("description");
						
						addPropertyChooser(name, field, type, description);
					}
				}
			}
		}
	}
	
	/**
	 * @param name
	 * @param field
	 * @param type
	 * @param description
	 */
	private void addPropertyChooser(String name, String field, String type, String description) {
		Label nameLabel = toolkit.createLabel(panel, name);
		TableWrapData data = new TableWrapData(TableWrapData.LEFT, TableWrapData.MIDDLE);
		nameLabel.setLayoutData(data);
		
		PropertyChooserBuilder builder = propertyChooserBuilders.get(type);
		
		// fallback
		if (builder == null)
			builder = propertyChooserBuilders.get("text");
		
		PropertyChooser propertyChooser = builder.applyOn(panel, SWT.BORDER);
		try {
			Field declaredField = selectedConfiguration.getClass().getDeclaredField(field);
			declaredField.setAccessible(true);
			Object value = declaredField.get(selectedConfiguration);
			propertyChooser.setValue(value);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		data = new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.MIDDLE);
		propertyChooser.setLayoutData(data);
		propertyChooser.addPropertyListener(new PropertyListener() {
		
			public void propertyModified() {
				modificationHook.modified();
			}
		});
		
		Label descriptionLabel = toolkit.createLabel(panel, description);
		data = new TableWrapData(TableWrapData.RIGHT, TableWrapData.TOP);
		data.colspan = 2;
		descriptionLabel.setLayoutData(data);
		FontData fontData = new FontData("ARIAL", 10, SWT.ITALIC);
		Font descriptionFont = new Font(panel.getDisplay(), fontData);
		descriptionLabel.setFont(descriptionFont);
		
		propertyChoosers.add(new PropertyChooserContainer(nameLabel, field, propertyChooser, descriptionLabel));
		
//		panel.redraw();
	}

}
