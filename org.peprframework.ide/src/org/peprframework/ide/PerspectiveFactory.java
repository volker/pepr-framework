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
package org.peprframework.ide;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

/**
 * @author Volker Fritzsch
 * @version 1.0
 *
 */
public class PerspectiveFactory implements IPerspectiveFactory {

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IPerspectiveFactory#createInitialLayout(org.eclipse.ui.IPageLayout)
	 */
	public void createInitialLayout(IPageLayout layout) {
		layout.addView("org.eclipse.ui.navigator.ProjectExplorer", IPageLayout.LEFT, .25f, layout.getEditorArea());
		layout.addView("org.peprframework.ide.views.ProcessInspector", IPageLayout.BOTTOM, .6f, layout.getEditorArea());
		layout.addView("org.peprframework.ide.views.PaletteView", IPageLayout.RIGHT, .8f, layout.getEditorArea());
	}

}
