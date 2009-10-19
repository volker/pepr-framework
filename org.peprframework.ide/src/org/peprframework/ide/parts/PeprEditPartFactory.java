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

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;
import org.peprframework.core.Activity;
import org.peprframework.core.Process;
import org.peprframework.core.Transition;


/**
 * @author Volker Fritzsch
 * @version 1.0
 *
 */
public class PeprEditPartFactory implements EditPartFactory {

	public EditPart createEditPart(EditPart context, Object model) {
		EditPart part = getPartForModel(model);
		if (part != null)
			part.setModel(model);
		
		return part;
	}
	
	protected EditPart getPartForModel(Object model) {
		if (model instanceof Process)
			return new ProcessEditPart();
		
		if (model instanceof Activity)
			return new ActivityEditPart();
		
		if (model instanceof Transition)
			return new TransitionEditPart();
		
		return null;
	}

}
