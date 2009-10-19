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

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Volker Fritzsch
 * @version 1.0
 *
 * @param <InputType>
 */
@XmlRootElement(name="GroovyInputFilter")
public class GroovyInputFilter<InputType> extends InputFilter<InputType> {

	@XmlElement
	@ConfigurableProperty(label = "Groovy Script")
	private String script = "";
	
	private transient Script groovyScript;
	
	@SuppressWarnings("unchecked")
	public InputType filter(InputType input, Context ctx) {
		groovyScript.getBinding().setVariable("input", input);
		groovyScript.getBinding().setVariable("context", ctx);
		groovyScript.run();
		input = (InputType) groovyScript.getBinding().getVariable("input");
		return input;
	}
	
	public String getScript() {
		return script;
	}
	
	@Override
	public void initialize() {
		Binding binding = new Binding();
		GroovyShell shell = new GroovyShell(binding);
		groovyScript = shell.parse(script);
	}

	public void setScript(String script) {
		this.script = script;
	}

	@Override
	public void terminate() {
	}
	
}
