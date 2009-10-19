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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * @author Volker Fritzsch
 * @version 1.0
 *
 */
@SuppressWarnings("unchecked")
@XmlRootElement(name = "Transition")
@XmlAccessorType(XmlAccessType.FIELD)
public class Transition extends PropertyChangePropagator {

	@XmlIDREF
	private Activity source;
	
	@XmlIDREF
	private Activity target;
	
	@XmlTransient
	private Object condition;
	
	@XmlAttribute(required = true)
	@XmlID
	private String label = "";

	public Activity getSource() {
		return source;
	}
	
	public void setSource(Activity source) {
		Activity oldSource = this.source;
		this.source = source;
		firePropertyChanged("source", oldSource, this.source);
	}

	public Activity getTarget() {
		return target;
	}

	public void setTarget(Activity target) {
		Activity oldTarget = this.target;
		this.target = target;
		firePropertyChanged("target", oldTarget, this.target);
	}

	public Object getCondition() {
		return condition;
	}

	public void setCondition(Object condition) {
		Object oldCondition = this.condition;
		this.condition = condition;
		firePropertyChanged("condition", oldCondition, this.condition);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((source == null) ? 0 : source.hashCode());
		result = prime * result + ((target == null) ? 0 : target.hashCode());
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
		Transition other = (Transition) obj;
		if (source == null) {
			if (other.source != null)
				return false;
		} else if (!source.equals(other.source))
			return false;
		if (target == null) {
			if (other.target != null)
				return false;
		} else if (!target.equals(other.target))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "[Transition] (" + source + ") -> (" + target + ")";
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		String oldLabel = this.label;
		this.label = label;
		firePropertyChanged("label", oldLabel, this.label);
	}
}
