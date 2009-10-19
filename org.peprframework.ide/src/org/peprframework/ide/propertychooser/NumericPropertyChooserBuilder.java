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

import java.math.BigDecimal;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Spinner;

/**
 * @author Volker Fritzsch
 * @version 1.0
 *
 */
public class NumericPropertyChooserBuilder implements PropertyChooserBuilder {

	protected class PropertyChooserImpl implements PropertyChooser {

		private Spinner spinner;
		
		public PropertyChooserImpl(Composite parent, int style) {
			spinner = new Spinner(parent, style);
		}

		/* (non-Javadoc)
		 * @see org.peprframework.ide.propertychooser.PropertyChooser#addListener(int, org.eclipse.swt.widgets.Listener)
		 */
		public void addListener(int eventType, Listener listener) {
			spinner.addListener(eventType, listener);
		}

		/* (non-Javadoc)
		 * @see org.peprframework.ide.propertychooser.PropertyChooser#dispose()
		 */
		public void dispose() {
			spinner.dispose();
		}

		/* (non-Javadoc)
		 * @see org.peprframework.ide.propertychooser.PropertyChooser#getValue()
		 */
		public Object getValue() {
			BigDecimal decimal = new BigDecimal(spinner.getSelection());
			decimal = decimal.setScale(spinner.getDigits());
			return decimal.doubleValue();
		}

		/* (non-Javadoc)
		 * @see org.peprframework.ide.propertychooser.PropertyChooser#setLayoutData(java.lang.Object)
		 */
		public void setLayoutData(Object layoutData) {
			spinner.setLayoutData(layoutData);
		}

		/* (non-Javadoc)
		 * @see org.peprframework.ide.propertychooser.PropertyChooser#setValue(java.lang.Object)
		 */
		public void setValue(Object value) {
			BigDecimal decimal = null;
			if (value instanceof Integer)
				decimal = new BigDecimal((Integer) value);
			if (value instanceof Float)
				decimal = new BigDecimal((Float) value);
			if (value instanceof Double)
				decimal = new BigDecimal((Double) value);
			if (value instanceof Long)
				decimal = new BigDecimal((Long) value);
			
			spinner.setDigits(decimal.scale());
			spinner.setSelection(decimal.unscaledValue().intValue());
		}
		
		/* (non-Javadoc)
		 * @see org.peprframework.ide.propertychooser.PropertyChooser#addModifyListener(org.eclipse.swt.events.ModifyListener)
		 */
		public void addPropertyListener(final PropertyListener listener) {
			spinner.addModifyListener(new ModifyListener() {
				
				public void modifyText(ModifyEvent e) {
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
