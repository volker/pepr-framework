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
package org.peprframework.activities.video.domain;

import java.awt.Rectangle;

/**
 * @author Stefan Scherer
 * @version 1.0
 *
 */
public class FaceContainer {
	
	public FaceContainer(Rectangle face, int id)
	{
		this.id = id;
		this.face = face;
	}
	
	private Rectangle face;
	private int id;
	
	
	public void setId(int id) {
		this.id = id;
	}
	public int getId() {
		return id;
	}
	public void setFace(Rectangle face) {
		this.face = face;
	}
	public Rectangle getFace() {
		return face;
	}
	
}
