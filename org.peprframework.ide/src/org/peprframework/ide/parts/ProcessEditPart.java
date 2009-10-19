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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editpolicies.ContainerEditPolicy;
import org.eclipse.gef.editpolicies.RootComponentEditPolicy;
import org.eclipse.gef.editpolicies.XYLayoutEditPolicy;
import org.eclipse.gef.requests.CreateRequest;
import org.peprframework.core.Activity;
import org.peprframework.core.Location;
import org.peprframework.core.Process;
import org.peprframework.core.Starter;


/**
 * @author Volker Fritzsch
 * @version 1.0
 *
 */
public class ProcessEditPart extends AbstractGraphicalEditPart implements PropertyChangeListener {

	@Override
	protected IFigure createFigure() {
//		LayeredPane layer = new ScalableFreeformLayeredPane();
		FreeformLayer layer = new FreeformLayer();
		layer.setLayoutManager(new XYLayout());
		return layer;
	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new RootComponentEditPolicy());
		installEditPolicy(EditPolicy.CONTAINER_ROLE, new ContainerEditPolicy() {
		
			@Override
			protected Command getCreateCommand(final CreateRequest request) {
				return new Command("create") {
					
					@Override
					public void execute() {
						Activity child = (Activity) request.getNewObject();
						chooseUniqueName(child);
						if (child instanceof Starter)
							((Process) getModel()).addStarter((Starter) child);
						else
							((Process) getModel()).addActivity(child);
					}
					
				};
			}
		});
		
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new XYLayoutEditPolicy() {
		
			@Override
			protected Command getCreateCommand(final CreateRequest request) {
				return new Command("create") {
					
					@Override
					public void execute() {
						Activity child = (Activity) request.getNewObject();
						int x = request.getLocation().x;
						int y = request.getLocation().y;
						child.setLocation(new Location(x,y));
					}
					
				};
			}
		
			@Override
			protected Command createChangeConstraintCommand(final EditPart child, final Object constraint) {
				return new Command("changeConstraint") {
					
					@Override
					public void execute() {
//						if (!(child instanceof ActivityEditPart))
//							return;
						
						Rectangle rectangle = (Rectangle) constraint;
						Activity node = (Activity) child.getModel();
						int x = rectangle.getLocation().x;
						int y = rectangle.getLocation().y;
						node.setLocation(new Location(x,y));
					}
					
				};
			}
		});

	}

	
	@SuppressWarnings("unchecked")
	@Override
	protected List getModelChildren() {
		List children = new ArrayList(((Process) getModel()).getActivities());
		children.addAll(((Process) getModel()).getStarters());
		
		return children;
	}

	public void propertyChange(PropertyChangeEvent evt) {
		refresh();
		for (Iterator it = getChildren().iterator(); it.hasNext();) {
			EditPart child = (EditPart) it.next();
			child.refresh();
		}
		getParent().refresh();
	}

	@Override
	public void activate() {
		if (isActive())
			return;
		
		super.activate();
		((Process) getModel()).addPropertyChangeListener(this);
	}

	@Override
	public void deactivate() {
		if (!isActive())
			return;

		super.deactivate();
		((Process) getModel()).removePropertyChangeListener(this);
	}
	
	protected void chooseUniqueName(Activity newNode) {
		List children = getModelChildren();
		for (Object child : children) {
//			if (child instanceof Activity) {
				Activity node = (Activity) child;
				if (node.getName().equals(newNode.getName()))
					newNode.setName(newNode.getName() + "#" + System.currentTimeMillis());
//			}
		}
	}
}
