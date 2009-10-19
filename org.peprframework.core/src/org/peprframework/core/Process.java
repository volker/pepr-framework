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

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Volker Fritzsch
 * @version 1.0
 *
 */
@SuppressWarnings("unchecked")
@XmlRootElement(name="Process", namespace="schema.pepr.org")
@XmlAccessorType(XmlAccessType.FIELD)
public class Process extends PropertyChangePropagator {
	
	private transient ConcurrentMap<String,Object> sharedState = new ConcurrentHashMap<String, Object>();

	@XmlElementWrapper(name="Starters")
	@XmlElementRef(type=Activity.class)
	private Set<Starter> starters = new HashSet<Starter>();
	
	@XmlElementWrapper(name="Activities")
	@XmlElementRef(type=Activity.class)
	private Set<Activity> activities = new HashSet<Activity>();
	
	@XmlElementWrapper(name="Transitions")
	@XmlElementRef(type=Transition.class)
	private Set<Transition> transitions = new HashSet<Transition>();
	
	@XmlAttribute(required = true)
	private String name = "";

	public void addActivity(Activity activity) {
		activities.add(activity);
		firePropertyChanged("activities", null, activity);
	}
	
	public void addStarter(Starter starter) {
		starters.add(starter);
		firePropertyChanged("starters", null, starter);
	}
	
	public void addTransition(Transition transition) {
		transitions.add(transition);
		firePropertyChanged("transition", null, transition);
		transition.getSource().firePropertyChanged("transition", null, transition);
		transition.getTarget().firePropertyChanged("transition", null, transition);
	}

	public Set<Activity> getActivities() {
		return activities;
	}

	public String getName() {
		return name;
	}

	public Set<Starter> getStarters() {
		return starters;
	}

	public Set<Transition> getTransitions() {
		return transitions;
	}
	
	public void removeActivity(Activity activity) {
		activities.remove(activity);
		firePropertyChanged("activities", activity, null);
	}
	
	public void removeStarter(Starter starter) {
		starters.remove(starter);
		firePropertyChanged("starters", starter, null);
	}
	
	public void removeTransition(Transition transition) {
		transitions.remove(transition);
		firePropertyChanged("transitions", transition, null);
	}
	
	public void setActivities(Set<Activity> nodes) {
		this.activities = nodes;
	}
	
	public void setName(String name) {
		String oldName = this.name;
		this.name = name;
		firePropertyChanged("name", oldName, this.name);
	}

	public void setStarters(Set<Starter> starters) {
		this.starters = starters;
	}

	public void setTransitions(Set<Transition> transitions) {
		this.transitions = transitions;
	}

	/**
	 * @return the sharedState
	 */
	public ConcurrentMap<String, Object> getSharedState() {
		return sharedState;
	}

	/**
	 * @param sharedState the sharedState to set
	 */
	public void setSharedState(ConcurrentMap<String, Object> sharedState) {
		this.sharedState = sharedState;
	}
	
	/**
	 * Returns the direct preceding activities in the process or an empty set in case the given activity has no precedings.
	 *  
	 * @param activity
	 * @return
	 */
	public Set<Activity> getDirectPreviousActivities(Activity activity) {
		Set<Activity> preceding = new HashSet<Activity>();
		for (Transition transition : transitions) {
			if (transition.getTarget().equals(activity)) {
				preceding.add(transition.getSource());
			}
		}
		
		return preceding;
	}
	
}
