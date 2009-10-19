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

import java.lang.reflect.Field;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.peprframework.core.Activity;
import org.peprframework.ide.parts.ActivityEditPart;

/**
 * @author Volker Fritzsch
 * @version 1.0
 *
 */
public class InputTargetContentProvider implements IStructuredContentProvider {

	class InputProperty {
		
		private String source;
		
		private String name;
		
		private String value;
		
		private Class<?> type;
		
		public InputProperty(String source, String name) {
			this.source = source;
			this.name = name;
			this.value = "";
		}
		
		/**
		 * @return the source
		 */
		public String getSource() {
			return source;
		}
		
		/**
		 * @param source the source to set
		 */
		public void setSource(String source) {
			this.source = source;
		}

		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}

		/**
		 * @param name the name to set
		 */
		public void setName(String name) {
			this.name = name;
		}

		/**
		 * @return the value
		 */
		public String getValue() {
			return value;
		}

		/**
		 * @param value the value to set
		 */
		public void setValue(String value) {
			this.value = value;
		}

		/**
		 * @return the type
		 */
		public Class<?> getType() {
			return type;
		}

		/**
		 * @param type the type to set
		 */
		public void setType(Class<?> type) {
			this.type = type;
		}
		
	}
	
	private InputProperty[] inputProperties;
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements(Object inputElement) {
		return inputProperties;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose() {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (!(newInput instanceof ActivityEditPart))
			return;

		ActivityEditPart activityEditPart = (ActivityEditPart) newInput;
		this.inputProperties = introspectActivityInput(activityEditPart.getActivity());
		
//		((TableViewer) viewer).refresh();
	}

	private InputProperty[] introspectActivityInput(Activity activity) {
		Class<?> inputClass = activity.getInputTypeClass();
		if (inputClass == null || Void.class.equals(inputClass)) {
			return new InputProperty[0];
		}
		
		Field[] declaredFields = inputClass.getDeclaredFields();
		InputProperty[] inputProperties = new InputProperty[declaredFields.length];
		for (int i = 0; i < declaredFields.length; i++) {
			String name = declaredFields[i].getName();
			inputProperties[i] = new InputProperty(activity.getName(), name);
			inputProperties[i].setType(declaredFields[i].getType());
		}
		
		return inputProperties;
	}

}
