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
package org.peprframework.ide.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import org.peprframework.core.ConfigurableProperty;

/**
 * Currently, only Fields can be annotated.
 * 
 * @author Volker Fritzsch
 * @version 1.0
 *
 */
public class IntrospectionHelper {

	public static class Property {
		String label;
		Method getter;
		Method setter;
		@SuppressWarnings("unchecked")
		Class type;
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((label == null) ? 0 : label.hashCode());
			return result;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Property other = (Property) obj;
			if (label == null) {
				if (other.label != null)
					return false;
			} else if (!label.equals(other.label))
				return false;
			return true;
		}

		public String getLabel() {
			return label;
		}

		public Method getGetter() {
			return getter;
		}

		public Method getSetter() {
			return setter;
		}

		@SuppressWarnings("unchecked")
		public Class getType() {
			return type;
		}
		
	}
	
	@SuppressWarnings("unchecked")
	public static Set<Property> inspect(Class clazz) throws NoSuchMethodException {
		Set<Property> properties = new HashSet<Property>();
		
		// get all fields that this class declares. We do not support inheritance, yet.
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			inspectField(properties, clazz, field);
		}
		
		return properties;
	}

	@SuppressWarnings("unchecked")
	private static void inspectField(Set<Property> properties, Class clazz, Field field) throws NoSuchMethodException {
		
		// return if it is not annotated
		if (!field.isAnnotationPresent(ConfigurableProperty.class))
			return;
		
		// get annotation
		ConfigurableProperty propertyAnnotation = field.getAnnotation(ConfigurableProperty.class);
		
		// extract label
		String label = propertyAnnotation.label().equals("") ? field.getName() : propertyAnnotation.label();
		
		// extract getter name
		String getterMethodName = propertyAnnotation.getter();
		if (getterMethodName.equals("")) {
			if (field.getType().equals(Boolean.class) || field.getType().equals(boolean.class))
				getterMethodName = "is" + field.getName().toUpperCase().charAt(0) + field.getName().substring(1);
			else
				getterMethodName = "get" + field.getName().toUpperCase().charAt(0) + field.getName().substring(1);
		}
		
		// extract setter name
		String setterMethodName = propertyAnnotation.getter();
		if (setterMethodName.equals("")) {
			setterMethodName = "set" + field.getName().substring(0, 1).toUpperCase().concat(field.getName().substring(1));
		}
		
		// let's see if both getter and setter methods are available
		Method getter = clazz.getMethod(getterMethodName, new Class[0]);
		if (!getter.getReturnType().equals(field.getType())) {
			throw new NoSuchMethodException(field.getType().getName() + " " + getterMethodName + "()");
		}
		Method setter = clazz.getMethod(setterMethodName, new Class[] {field.getType()});

		Property prop = new Property();
		prop.label = label;
		prop.getter = getter;
		prop.setter = setter;
		prop.type = field.getType();
			
		if (properties.contains(prop)) {
			throw new RuntimeException(prop.label + " already indexed.");
		}
		
		properties.add(prop);
	}
	
}
