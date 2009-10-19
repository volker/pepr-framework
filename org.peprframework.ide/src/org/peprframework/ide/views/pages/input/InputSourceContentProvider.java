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
package org.peprframework.ide.views.pages.input;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.gef.EditPart;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.peprframework.core.Activity;
import org.peprframework.core.Process;
import org.peprframework.core.Transition;
import org.peprframework.ide.parts.ActivityEditPart;

/**
 * @author Volker Fritzsch
 * @version 1.0
 *
 */
public class InputSourceContentProvider implements ITreeContentProvider {
	
	private Set<Activity> previousActivities = new HashSet<Activity>();

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
	 */
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof EditPart)
			return previousActivities.toArray();
		
		if (parentElement instanceof Activity) {
			Activity activity = (Activity) parentElement;
			Field[] fields = activity.getOutputTypeClass().getDeclaredFields();
			OutputProperty[] result = new OutputProperty[fields.length];
			for (int i = 0; i < fields.length; i++) {
				result[i] = new OutputProperty();
				result[i].setSource(activity.getName());
				result[i].setName(fields[i].getName());
				result[i].setType(fields[i].getType());
			}
			
			return result;
		}
		
		return new Object[0];
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
	 */
	public Object getParent(Object element) {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
	 */
	public boolean hasChildren(Object element) {
		if (element instanceof EditPart)
			return !previousActivities.isEmpty();
		
		if (element instanceof Activity) {
			Activity activity = (Activity) element;
			Class<?> outputClass = activity.getOutputTypeClass();
			return Void.class.equals(outputClass) ? false : outputClass.getDeclaredFields().length > 0;
		}
			
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose() {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (!(newInput instanceof ActivityEditPart)) {
			((TreeViewer) viewer).refresh(null, true);
			return;
		}
		
		rebuildContextModel((ActivityEditPart) newInput);
		((TreeViewer) viewer).refresh(newInput, true);
	}
	
	/**
	 * @param editPart
	 */
	protected void rebuildContextModel(ActivityEditPart editPart) {
		Process process = editPart.getProcess();
		Activity activity = editPart.getActivity();
		
		Set<Transition> transitions = process.getTransitions();
		previousActivities.clear();
		findPreviousActivities(activity, transitions, previousActivities);
		previousActivities.remove(activity);
	}

	/**
	 * @param activity
	 * @param transitions
	 * @param previousActivities
	 */
	private void findPreviousActivities(Activity activity, Set<Transition> transitions, Set<Activity> previousActivities) {
		// have we already visited this activity?
		if (previousActivities.contains(activity))
			return;
		
		// now we visit this activity
		previousActivities.add(activity);
		
		// lets visit all previous activities for activity
		for (Transition transition : transitions) {
			if (transition.getTarget().equals(activity))
				findPreviousActivities(transition.getSource(), transitions, previousActivities);
		}
	}

}
