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
package org.peprframework.ide.propertychooser;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Listener;

/**
 * @author Volker Fritzsch
 * @version 1.0
 *
 */
public class BooleanPropertyChooserBuilder implements PropertyChooserBuilder {

	protected class PropertyChooserImpl implements PropertyChooser {

		private Button checkbox;
		
		public PropertyChooserImpl(Composite parent, int style) {
			checkbox = new Button(parent, style | SWT.CHECK);
			checkbox.setText("enabled");
		}

		/* (non-Javadoc)
		 * @see org.peprframework.ide.propertychooser.PropertyChooser#addListener(int, org.eclipse.swt.widgets.Listener)
		 */
		public void addListener(int eventType, Listener listener) {
			checkbox.addListener(eventType, listener);
		}

		/* (non-Javadoc)
		 * @see org.peprframework.ide.propertychooser.PropertyChooser#dispose()
		 */
		public void dispose() {
			checkbox.dispose();
		}

		/* (non-Javadoc)
		 * @see org.peprframework.ide.propertychooser.PropertyChooser#getValue()
		 */
		public Object getValue() {
			return checkbox.getSelection();
		}

		/* (non-Javadoc)
		 * @see org.peprframework.ide.propertychooser.PropertyChooser#setLayoutData(java.lang.Object)
		 */
		public void setLayoutData(Object layoutData) {
			checkbox.setLayoutData(layoutData);
		}

		/* (non-Javadoc)
		 * @see org.peprframework.ide.propertychooser.PropertyChooser#setValue(java.lang.Object)
		 */
		public void setValue(Object value) {
			checkbox.setSelection((Boolean) value);
		}
		
		/* (non-Javadoc)
		 * @see org.peprframework.ide.propertychooser.PropertyChooser#addModifyListener(org.eclipse.swt.events.ModifyListener)
		 */
		public void addPropertyListener(final PropertyListener listener) {
			checkbox.addSelectionListener(new SelectionAdapter() {
				
				/* (non-Javadoc)
				 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
				 */
				@Override
				public void widgetSelected(SelectionEvent e) {
					listener.propertyModified();
				}
				
			});
		}
		
	}
	
	/* (non-Javadoc)
	 * @see org.peprframework.ide.propertychooser.PropertyChooserBuilder#applyOn(org.eclipse.swt.widgets.Composite, java.lang.Object, java.lang.String)
	 */
	public PropertyChooser applyOn(Composite parent, int style) {
		return new PropertyChooserImpl(parent, style);
	}

}
