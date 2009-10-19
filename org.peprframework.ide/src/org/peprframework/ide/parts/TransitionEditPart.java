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
package org.peprframework.ide.parts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.PolylineDecoration;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.AbstractConnectionEditPart;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.requests.GroupRequest;
import org.peprframework.core.Process;
import org.peprframework.core.Transition;
import org.peprframework.ide.commands.DeleteTransitionCommand;


/**
 * @author Volker Fritzsch
 * @version 1.0
 *
 */
public class TransitionEditPart extends AbstractConnectionEditPart implements
		PropertyChangeListener {
	
	@Override
	protected IFigure createFigure() {
		PolylineConnection figure = (PolylineConnection) super.createFigure();
		figure.setTargetDecoration(new PolylineDecoration());
		return figure;
	};

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new ComponentEditPolicy() {
			
			@Override
			protected Command getDeleteCommand(GroupRequest request) {
				DeleteTransitionCommand cmd = new DeleteTransitionCommand();
				cmd.setProcess((Process) getSource().getParent().getModel());
				cmd.setTransition((Transition) getModel());

				return cmd;
			}
			
		});
	}

	public void propertyChange(PropertyChangeEvent evt) {
		refresh();
	}

	@Override
	public void activate() {
		if (isActive())
			return;
		
		super.activate();
		((Transition) getModel()).addPropertyChangeListener(this);
	}

	@Override
	public void deactivate() {
		if (!isActive())
			return;

		super.deactivate();
		((Transition) getModel()).removePropertyChangeListener(this);
	}
	
	@Override
	public void setSelected(int value) {
		super.setSelected(value);
		if (value != EditPart.SELECTED_NONE)
			((PolylineConnection)getFigure()).setLineStyle(Graphics.LINE_DOT);
		else
			((PolylineConnection)getFigure()).setLineStyle(Graphics.LINE_SOLID);
	}

}
