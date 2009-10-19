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
import java.util.List;
import java.util.Set;

import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.ManhattanConnectionRouter;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.PolylineDecoration;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.XYAnchor;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy;
import org.eclipse.gef.requests.CreateConnectionRequest;
import org.eclipse.gef.requests.GroupRequest;
import org.eclipse.gef.requests.LocationRequest;
import org.eclipse.gef.requests.ReconnectRequest;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.peprframework.core.Activity;
import org.peprframework.core.Process;
import org.peprframework.core.Transition;
import org.peprframework.ide.Activator;
import org.peprframework.ide.commands.CreateTransitionCommand;
import org.peprframework.ide.commands.DeleteActivityCommand;
import org.peprframework.ide.figures.NamedLabel;
import org.peprframework.ide.model.ActivityRegistry;

/**
 * @author Volker Fritzsch
 * @version 1.0
 *
 */
public class ActivityEditPart extends AbstractGraphicalEditPart implements NodeEditPart, PropertyChangeListener {

	@Override
	protected IFigure createFigure() {
		ActivityRegistry registry = Activator.getDefault().getActivityRegistry();
		ImageDescriptor descr = registry.getProcessIconImageDescriptor(((Activity) getModel()).getId());
		if (descr == null) {
			descr = ImageDescriptor.getMissingImageDescriptor();
		}
		
		Image image = descr.createImage();
		String name = registry.getName(((Activity) getModel()).getId());
		String text = ((Activity) getModel()).getName();
		NamedLabel figure = new NamedLabel(image, name, text);
		figure.setIconAlignment(PositionConstants.CENTER);
		figure.setTextPlacement(PositionConstants.SOUTH);
		figure.setTextAlignment(PositionConstants.CENTER);
		
		Point location = new Point(((Activity) getModel()).getLocation().getX(), ((Activity) getModel()).getLocation().getY());
		figure.setBounds(new Rectangle(location, new Dimension(image.getBounds().width, image.getBounds().height + 50)));
		figure.setLocation(location);
		return figure;
	}
	
	public Activity getActivity() {
		return (Activity) getModel();
	}
	
	public Process getProcess() {
		return (Process) getParent().getModel();
	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new ComponentEditPolicy() {
			
			@SuppressWarnings("unchecked")
			@Override
			protected Command createDeleteCommand(GroupRequest deleteRequest) {
				DeleteActivityCommand cmd = new DeleteActivityCommand();
				cmd.setProcess((Process) getParent().getModel());
				cmd.setActivity((Activity) getModel());
				
				return cmd;
			}
			
		});
		
		
		installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE, new GraphicalNodeEditPolicy() {
		
			@Override
			protected Command getReconnectTargetCommand(ReconnectRequest request) {
				System.out.println("ReconnectTargetCommand!");
				return null;
			}
		
			@Override
			protected Command getReconnectSourceCommand(ReconnectRequest request) {
				System.out.println("ReconnectSourceCommand!");
				return null;
			}
		
			@Override
			protected Command getConnectionCreateCommand(CreateConnectionRequest request) {
				CreateTransitionCommand cmd = new CreateTransitionCommand();
				cmd.setTransition((Transition) request.getNewObject());
				cmd.setSource((Activity) getHost().getModel());
				request.setStartCommand(cmd);
				
				return cmd;
			}
		
			@Override
			protected Command getConnectionCompleteCommand(CreateConnectionRequest request) {
				CreateTransitionCommand cmd = (CreateTransitionCommand) request.getStartCommand();
				cmd.setTarget((Activity) getHost().getModel());
				cmd.setProcess((Process) getHost().getParent().getModel());
				return cmd;
			}
			
			@Override
			protected Connection createDummyConnection(Request req) {
				PolylineConnection conn = new PolylineConnection();
				conn.setSourceAnchor(getSourceConnectionAnchor((CreateConnectionRequest) req));
				conn.setTargetDecoration(new PolylineDecoration());
				conn.setConnectionRouter(new ManhattanConnectionRouter());
				
				return conn;
			}
		});
	}

	public void propertyChange(PropertyChangeEvent evt) {
		figure.setLocation(new Point(((Activity) getModel()).getLocation().getX(), ((Activity) getModel()).getLocation().getY()));
		((Label) figure).setText(((Activity) getModel()).getName());
		refresh();
	}

	@Override
	public void activate() {
		if (isActive())
			return;
		
		super.activate();
		((Activity) getModel()).addPropertyChangeListener(this);
	}

	@Override
	public void deactivate() {
		if (!isActive())
			return;

		super.deactivate();
		((Activity) getModel()).removePropertyChangeListener(this);
	}

	public ConnectionAnchor getSourceConnectionAnchor(ConnectionEditPart connection) {
		return new ChopboxAnchor(getFigure());
	}

	public ConnectionAnchor getSourceConnectionAnchor(Request request) {
		if (request instanceof LocationRequest) {
			LocationRequest req = (LocationRequest) request;
			return new XYAnchor(req.getLocation());
		}
		
		return new ChopboxAnchor(getFigure());
	}

	public ConnectionAnchor getTargetConnectionAnchor(ConnectionEditPart connection) {
		return new ChopboxAnchor(getFigure());
	}

	public ConnectionAnchor getTargetConnectionAnchor(Request request) {
		if (request instanceof LocationRequest) {
			LocationRequest req = (LocationRequest) request;
			return new XYAnchor(req.getLocation());
		}
		
		return new ChopboxAnchor(getFigure());
	}

	@SuppressWarnings("unchecked")
	@Override
	protected List getModelSourceConnections() {
		Process process = (Process) getParent().getModel();
		Set<Transition> transitions = process.getTransitions();
		List<Transition> sourceTransitions = new ArrayList<Transition>();
		for (Transition transition : transitions) {
			Activity source = transition.getSource();
			if (source != null && source.equals(getModel()))
				sourceTransitions.add(transition);
		}
		
		return sourceTransitions;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected List getModelTargetConnections() {
		Process process = (Process) getParent().getModel();
		Set<Transition> transitions = process.getTransitions();
		List<Transition> targetTransitions = new ArrayList<Transition>();
		for (Transition transition : transitions) {
			Activity target = transition.getTarget();
			if (target != null && target.equals(getModel()))
				targetTransitions.add(transition);
		}
		
		return targetTransitions;
	}

}
