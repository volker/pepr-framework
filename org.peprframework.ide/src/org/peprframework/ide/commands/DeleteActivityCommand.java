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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.gef.commands.Command;
import org.peprframework.core.Activity;
import org.peprframework.core.Process;
import org.peprframework.core.Starter;
import org.peprframework.core.Transition;

/**
 * @author Volker Fritzsch
 * @version 1.0
 */
@SuppressWarnings("unchecked")
public class DeleteActivityCommand extends Command {

	private Activity activity;
	
	private Set transitions = new HashSet();

	private Process process;

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	@Override
	public void execute() {
		if (activity instanceof Starter) {
			process.removeStarter((Starter) activity);
		} else {
			process.removeActivity(activity);
		}
		for (Iterator it = process.getTransitions().iterator(); it.hasNext();) {
			Transition transition = (Transition) it.next();
			if (transition.getSource().equals(activity) || transition.getTarget().equals(activity)) {
				transitions.add(transition);
			}
		}
		
		for (Iterator it = transitions.iterator(); it.hasNext();) {
			Transition transition = (Transition) it.next();
			process.removeTransition(transition);
		}
	}

	/**
	 * @return the activity
	 */
	public Activity getActivity() {
		return activity;
	}

	/**
	 * @return the process
	 */
	public Process getProcess() {
		return process;
	}
	
	/**
	 * @param activity the activity to set
	 */
	public void setActivity(Activity activity) {
		this.activity = activity;
	}
	
	/**
	 * @param process the process to set
	 */
	public void setProcess(Process process) {
		this.process = process;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	@Override
	public void undo() {
		if (activity instanceof Starter) {
			process.addStarter((Starter) activity);
		} else {
			process.addActivity(activity);
		}
		for (Iterator it = transitions.iterator(); it.hasNext();) {
			Transition transition = (Transition) it.next();
			process.addTransition(transition);	
		}
	}

}
