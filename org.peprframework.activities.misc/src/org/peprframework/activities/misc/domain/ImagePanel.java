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
package org.peprframework.activities.misc.domain;

import java.awt.CardLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;


/**
 * @author Stefan Scherer
 * @version 1.0
 *
 */
@SuppressWarnings("serial")
public class ImagePanel extends JPanel{

	/**
	 *
	 */
	private CardLayout imageManager;
	private ImageIcon falseImage;
	private ImageIcon trueImage;
	private JLabel falseLabel = null;
	private JLabel trueLabel = null;
	private String trueImageFile;
	private String falseImageFile;
	
	public ImagePanel(String trueImageFile, String falseImageFile){
		super();
		this.trueImageFile = trueImageFile;
		this.falseImageFile = falseImageFile;
		initialize();
	}

	private void initialize(){
        System.out.println(System.getProperty("user.dir"));
		setImageManager(new CardLayout());
		this.setLayout(getImageManager());
		falseImage = createImageIcon(this.falseImageFile, "false");
		falseLabel = new JLabel(falseImage, JLabel.CENTER);

		trueImage = createImageIcon(this.trueImageFile, "true");
		trueLabel = new JLabel(trueImage, JLabel.CENTER);

		this.add(falseLabel,"false");
		this.add(trueLabel,"true");


	}

	/** Returns an ImageIcon, or null if the path was invalid. */
	protected ImageIcon createImageIcon(String path,
	                                           String description) {
	    /*java.net.URL imgURL = getClass().getResource(path);
	    if (imgURL != null) {
               return new ImageIcon(imgURL, description);
	    } else {
	        System.err.println("Couldn't find file: " + path);
	        return null;
	    }*/
		return new ImageIcon(path, description);
	}

    /**
     * @return the imageManager
     */
    public CardLayout getImageManager() {
        return imageManager;
    }

    /**
     * @param imageManager the imageManager to set
     */
    public void setImageManager(CardLayout imageManager) {
        this.imageManager = imageManager;
    }

}
