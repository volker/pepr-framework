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
package org.peprframework.core;

import java.lang.reflect.ParameterizedType;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;

import org.jetlang.core.Callback;

/**
 * @author Volker Fritzsch
 * @version 1.0
 *
 * @param <InputType>
 * @param <OutputType>
 * @param <ConfigurationType>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="Activity", namespace="schema.pepr.org")
public abstract class Activity<InputType,OutputType,ConfigurationType extends Configuration> extends PropertyChangePropagator implements Callback<Context>{
	
	protected transient Class<ConfigurationType> configurationTypeClass;
	
	protected transient Class<InputType> inputTypeClass;
	
	protected transient Class<OutputType> outputTypeClass;
	
	protected transient EngineActivityCallback callback;
	
	protected transient Process process;
	
	@XmlElementRef(type=Configuration.class)
	protected ConfigurationType configuration;
	
	@XmlElementRef(type=InputFilter.class)
	protected InputFilter<InputType> inputFilter = new GroovyInputFilter<InputType>();
	
	@XmlElementRef(type=Location.class)
	protected Location location = new Location();
	
	@XmlAttribute
	protected String description = "";
	
	@XmlAttribute(required=true)
	@XmlID
	protected String name = "";
	
	/**
	 * @return the id
	 */
	public abstract String getId();

	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Activity other = (Activity) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	public ConfigurationType getConfiguration() {
		return configuration;
	}

	@SuppressWarnings("unchecked")
	public Class<ConfigurationType> getConfigurationTypeClass() {
		if (configurationTypeClass == null)
			configurationTypeClass = (Class<ConfigurationType>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[2];
		return configurationTypeClass;
	}
	
	public Object getInputFilter() {
		return inputFilter;
	}

	@SuppressWarnings("unchecked")
	public Class<InputType> getInputTypeClass() {
		if (inputTypeClass == null)
			inputTypeClass = (Class<InputType>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		return inputTypeClass;
	}

	public Location getLocation() {
		return location;
	}

	public String getName() {
		return name;
	}

	@SuppressWarnings("unchecked")
	public Class<OutputType> getOutputTypeClass() {
		if (outputTypeClass == null)
			outputTypeClass = (Class<OutputType>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[1];
		return outputTypeClass;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	public void initialize() {
		if (inputFilter != null) {
			inputFilter.initialize();
		}
	}
	
	public void onMessage(Context ctx) {
		try {
			InputType input = createInput();
			input = inputFilter.filter(input, ctx);
			OutputType output = handleMessage(input);
			ctx.set(name, output);
			ctx.setLatestActivity(name);
			callback.publish(ctx);
		} catch (Exception ex) {
			System.err.println("Exception in Activity '" + name + "':");
			ex.printStackTrace();
		}
	}
	
	public abstract OutputType handleMessage(InputType msg);

	public void setConfiguration(ConfigurationType configuration) {
		Configuration oldConfiguration = this.configuration;
		this.configuration = configuration;
		firePropertyChanged("configuration", oldConfiguration, configuration);
	}
	
	public void setInputFilter(InputFilter<InputType> inputFilter) {
		Object oldInputFilter = this.inputFilter;
		this.inputFilter = inputFilter;
		firePropertyChanged("inputFilter", oldInputFilter, inputFilter);
	}
	
	public void setLocation(Location location) {
		Location oldLocation = this.location;
		this.location = location;
		firePropertyChanged("location", oldLocation, location);
	}

	public void setName(String name) {
		String oldName = this.name;
		this.name = name;
		firePropertyChanged("name", oldName, name);
	}
	
	public void terminate() {
		if (inputFilter != null) {
			inputFilter.terminate();
		}
	}

	public InputType createInput() {
		try {
			return getInputTypeClass().newInstance();
		} catch (InstantiationException ex) {
			ex.printStackTrace();
		} catch (IllegalAccessException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	/**
	 * @return the callback
	 */
	public EngineActivityCallback getCallback() {
		return callback;
	}

	/**
	 * @param callback the callback to set
	 */
	public void setCallback(EngineActivityCallback callback) {
		this.callback = callback;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the process
	 */
	public Process getProcess() {
		return process;
	}

	/**
	 * @param process the process to set
	 */
	public void setProcess(Process process) {
		this.process = process;
	}
}
