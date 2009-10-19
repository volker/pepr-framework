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
package org.peprframework.ide.adapters;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.peprframework.core.Process;


/**
 * @author Volker Fritzsch
 * @version 1.0
 *
 */
public class ProcessPropertySource implements IPropertySource {

	private Process process;
	
	public ProcessPropertySource(Process process) {
		this.process = process;
	}

	public Object getEditableValue() {
		return null;
	}

	public IPropertyDescriptor[] getPropertyDescriptors() {
		IPropertyDescriptor asdf = new TextPropertyDescriptor("process.name", "Name");
		return new IPropertyDescriptor[] { asdf };
	}

	public Object getPropertyValue(Object id) {
		if (id.equals("process.name"))
			return process.getName();
		
		return null;
	}

	public boolean isPropertySet(Object id) {
		// TODO Auto-generated method stub
		return false;
	}

	public void resetPropertyValue(Object id) {
		// TODO Auto-generated method stub

	}

	public void setPropertyValue(Object id, Object value) {
		if (id.equals("process.name"))
			process.setName((String) value);
	}

}
