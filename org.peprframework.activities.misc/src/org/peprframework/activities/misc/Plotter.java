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
package org.peprframework.activities.misc;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.peprframework.core.Activity;

/**
 * @author Volker Fritzsch
 * @version 1.0
 *
 */
@XmlRootElement(name = "Plotter")
@XmlAccessorType(XmlAccessType.FIELD)
public class Plotter extends Activity<PlotterInput, Void, PlotterConfiguration> {
	
	@SuppressWarnings("serial")
	private class TiledPlotterPanel extends JPanel {
		
		private List<BufferedImage> tiles = new ArrayList<BufferedImage>(10);
		
		private int currentTileX;
		
		private int tileWidth = 400;
		
		private int tileHeight = 200;
		
		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			
			// draw tiles
			int x = getWidth() - currentTileX;
			List<BufferedImage> tiles = new ArrayList<BufferedImage>(this.tiles);
			for (BufferedImage tile : tiles) {
				g.drawImage(tile, x, 0, null);
				x -= tileWidth;
			}

		}

		protected void increment() {
			currentTileX++;
		}
		protected void paintDatapoint(double datapoint) {
			// if the current tile is fully used, we create another one
			BufferedImage currentTile = null;
			Graphics2D g = null;
			if (currentTileX >= tileWidth || tiles.isEmpty()) {
				currentTile = new BufferedImage(tileWidth, tileHeight, BufferedImage.TYPE_4BYTE_ABGR);
				currentTileX = 0;
				
				// draw scale
				g = currentTile.createGraphics();
				g.setColor(Color.LIGHT_GRAY);
				g.drawLine(0, scaleY(0), tileWidth, scaleY(0));
				g.drawLine(0, scaleY(1), tileWidth, scaleY(1));
				g.drawLine(0, scaleY(-1), tileWidth, scaleY(-1));
				
				// add tile to collection
				tiles.add(0, currentTile);
			} else {
				currentTile = tiles.get(0);
				g = currentTile.createGraphics();
			}
			
			// paint current datapoint
			int y = scaleY(datapoint);
			if (datapoint < getConfiguration().getLowerBarrier()) {
				g.setColor(Color.BLACK);
//				int y = (int) (datapoint * tileHeight / -4) + tileHeight / 2;
				g.drawOval(currentTileX, y, 3, 3);
			} else if (datapoint > getConfiguration().getUpperBarrier()) {
					g.setColor(Color.RED);
//					int y = (int) (datapoint * tileHeight / -4) + tileHeight / 2;
					g.fillOval(currentTileX, y, 3, 3);
				} else {
					g.setColor(Color.BLUE);
					g.fillOval(currentTileX, y, 3, 3);
				}
			g.dispose();
			
			// remove old tiles
			int numberOfNecessaryTiles = (getWidth() / tileWidth) + 2;
			while (tiles.size() > numberOfNecessaryTiles) {
				tiles.remove(tiles.size() - 1);
			}
		}
		
	}

	private transient JFrame frame;

	private transient TiledPlotterPanel canvas;
	
	protected int scaleY(double y) {
		int result = (int) (y * canvas.tileHeight / -4) + canvas.tileHeight / 2;
		result = Math.min(canvas.tileHeight - 3, result);
		result = Math.max(result, 0);
		
		return result;
	}

	@Override
	public void initialize() {
		super.initialize();
		try {
			SwingUtilities.invokeAndWait(new Runnable() {

				public void run() {
					canvas = new TiledPlotterPanel();
					canvas.addComponentListener(new ComponentAdapter() {
						
						@Override
						public void componentResized(ComponentEvent e) {
							int oldTileHeight = canvas.tileHeight;
							int newTileHeight = canvas.getHeight();
							
							List<BufferedImage> oldTiles = new ArrayList<BufferedImage>(canvas.tiles);
							List<BufferedImage> newTiles = new ArrayList<BufferedImage>(oldTiles.size());
							for (BufferedImage oldTile : oldTiles) {
								BufferedImage newTile = new BufferedImage(canvas.tileWidth, newTileHeight, BufferedImage.TYPE_4BYTE_ABGR);
								newTiles.add(newTile);
								
								Graphics2D g = newTile.createGraphics();
								g.scale(1d, (double) newTileHeight / (double) oldTileHeight);
								g.drawImage(oldTile, 0, 0, null);
								g.dispose();
							}
							
							canvas.tileHeight = canvas.getHeight();
							canvas.tiles = newTiles;
						}
						
					});
					
					frame = new JFrame("pepr - '" + getName() + "'");
					frame.getContentPane().setLayout(new BorderLayout());
					frame.getContentPane().add(canvas, BorderLayout.CENTER);

					frame.setSize(600, 150);
					frame.setVisible(true);
					frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				}
			});
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		} catch (InvocationTargetException ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void terminate() {
		super.terminate();
		frame.setVisible(false);
		frame.dispose();
	}

	@Override
	public Void handleMessage(PlotterInput input) {
//		System.out.println("Duration in nanos: " + (System.nanoTime() - input.startTimeNanos));
//		System.out.println(name + "[" + input.packagenumber + "] : " + input.datapoints[0]);
		for (double d : input.datapoints) {
			try {
				canvas.paintDatapoint(d);
			} catch (NullPointerException ex) {
				ex.printStackTrace();
				System.err.println(canvas);
				System.err.println(d);
			}
		}
		canvas.increment();
		
		SwingUtilities.invokeLater(new Runnable() {

			public void run() {
				canvas.repaint();
			}
		});

		return null;
	}

	/* (non-Javadoc)
	 * @see org.peprframework.core.Activity#getId()
	 */
	@Override
	public String getId() {
		return "org.peprframework.activities.misc.plotter";
	}
	
}
