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
package org.peprframework.ide.commands;

import org.eclipse.gef.commands.Command;
import org.peprframework.core.Activity;
import org.peprframework.core.Process;
import org.peprframework.core.Transition;


/**
 * @author Volker Fritzsch
 * @version 1.0
 *
 */
@SuppressWarnings("unchecked")
public class CreateTransitionCommand extends Command {

	private Transition transition;
	
	private Activity source;
	
	private Activity target;
	
	private Process process;
	
	@Override
	public void execute() {
		transition.setSource(source);
		transition.setTarget(target);
		process.addTransition(transition);
	}
	
	@Override
	public void undo() {
		process.removeTransition(transition);
	}
	
	public Activity getSource() {
		return source;
	}

	public void setSource(Activity source) {
		this.source = source;
	}

	public Transition getTransition() {
		return transition;
	}

	public Activity getTarget() {
		return target;
	}

	public void setTarget(Activity target) {
		this.target = target;
	}

	public void setTransition(Transition transition) {
		this.transition = transition;
	}

	public Process getProcess() {
		return process;
	}

	public void setProcess(Process process) {
		this.process = process;
	}

}
