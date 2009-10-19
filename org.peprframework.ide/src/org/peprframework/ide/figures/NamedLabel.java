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
package org.peprframework.ide.figures;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Image;

public class NamedLabel extends Label {

	private String name;
	
	private Dimension nameTextSize;
	
	private Point nameTextLocation;
	
	public NamedLabel(Image image, String name) {
		this(image, name, null);
	}
	
	public NamedLabel(Image image, String name, String text) {
		super(text, image);
		this.name = name;
	}
	
	private void calculateNameTextLocation() {
		nameTextLocation = new Point();
		
		Dimension position = getIconSize();
		Dimension offset = getSize().getDifference(getPreferredSize());
		position.height += offset.height;
		position.width += offset.width;
		position.width -= getNameTextSize().width;
		position.height -= getNameTextSize().height;
		
		position.scale(0.5f);
		
		nameTextLocation.translate(position);
	}
	
	private Dimension calculateNameTextSize() {
		return getTextUtilities().getTextExtents(getName(), getFont());
	}
	
	public String getName() {
		return name;
	}

	protected Point getNameTextLocation() {
		if (nameTextLocation != null) 
			return nameTextLocation;
		calculateNameTextLocation();
		return nameTextLocation;
	}
	
	protected Dimension getNameTextSize() {
		if (nameTextSize == null)
			nameTextSize = calculateNameTextSize();
		return nameTextSize;
	}
	
	@Override
	protected void paintFigure(Graphics graphics) {
		graphics.pushState();
		super.paintFigure(graphics);
		graphics.popState();
		graphics.pushState();
		Rectangle bounds = getBounds();
		graphics.translate(bounds.x, bounds.y);
		graphics.drawText(getName(), getNameTextLocation());
		graphics.translate(-bounds.x, -bounds.y);
		graphics.popState();
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	
}
