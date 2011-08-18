/*
 * This file is part of GenoViewer.
 *
 * GenoViewer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GenoViewer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GenoViewer.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.astrid.viewer.gui;

import hu.astrid.viewer.gui.DashBoard.PanelResizeListener;
import hu.astrid.viewer.util.SwingComponentQueryTask;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ExecutionException;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import org.apache.log4j.Logger;

/**
 * //TODO
 * @author Szuni
 */
public abstract class ResizeableScrollPanel extends JScrollPane implements ResizeListenerProvider, ContentPanel {

	private static final long serialVersionUID = 1L;
	protected GridBagLayout layout = new GridBagLayout();
	public static Integer selectionPosition = null;
	public static Integer scrollPointerPosition = null;
	public static boolean selectionEnabled = false;
	public static final Color cyan = Color.cyan;
	public static final Color selectionColor = new Color(0, 0, 1, 0.2f);
	protected JPanel contentPanel = new JPanel() {
		private static final long serialVersionUID = 1L;

		@Override
		public void paint(Graphics g) {
			super.paint(g);
			if (!selectionEnabled) {
				if (scrollPointerPosition != null) {
					g.setColor(cyan);
					g.drawLine(scrollPointerPosition * DashBoard.fontWidth, 0, scrollPointerPosition * DashBoard.fontWidth, this.getHeight());
					g.drawLine((scrollPointerPosition + 1) * DashBoard.fontWidth, 0, (scrollPointerPosition + 1) * DashBoard.fontWidth, this.getHeight());
				}
				if (selectionPosition != null) {
					g.setColor(Color.black);
					g.drawLine(selectionPosition * DashBoard.fontWidth, 0, selectionPosition * DashBoard.fontWidth, this.getHeight());
					g.drawLine((selectionPosition + 1) * DashBoard.fontWidth, 0, (selectionPosition + 1) * DashBoard.fontWidth, this.getHeight());
				}
			}
		}
	};

	/**
	 * //TODO
	 */
	public ResizeableScrollPanel() {
		contentPanel.setLayout(layout);
		contentPanel.setDoubleBuffered(true);
		this.setViewportView(contentPanel);
		this.setDoubleBuffered(true);
	}

	/**
	 * Set the width of the panel
	 * @param width width in number of characters
	 */
	public void setPreferredWidth(final int width) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				layout.columnWidths = new int[]{width * DashBoard.fontWidth};
				contentPanel.invalidate();
				ResizeableScrollPanel.this.validate();
//				Logger.getLogger(ResizeableScrollPanel.this.getClass()).trace("width set to "+width);
			}
		});
	}

	/**
	 * Invoke resize listeners registered for {@see ResizeListenerProvider}
	 */
	protected final void invokePanelResizeListeners() {
		for (PanelResizeListener listener : panelResizeListeners) {
			listener.resize();
		}
	}

	/**
	 * @return number of characters of reference contig shown on the screen
	 */
	public final Integer getDisplayableCharacters() {
		if (SwingUtilities.isEventDispatchThread()) {
			return this.getViewport().getWidth() / DashBoard.fontWidth;
		} else {
			SwingComponentQueryTask<Integer> task = new SwingComponentQueryTask<Integer>() {

				@Override
				protected Integer query() {
					//TODO fuggoleges scrollbar erteket nem vonja le :( readek betoltesenel ennyivel tobbet kell szamolni
					return ResizeableScrollPanel.this.getViewport().getWidth() / DashBoard.fontWidth;
				}
			};

			SwingUtilities.invokeLater(task);

			try {
				return task.get();
			} catch (InterruptedException ex) {
				Logger.getLogger(this.getClass()).trace("display width query interrupted");
				return null;
			} catch (ExecutionException ex) {
				Logger.getLogger(this.getClass()).error(ex.getMessage(), ex);
				return null;
			}
		}
	}

	/**
	 * @return nucleotide position of the scrollbar, queried in event dispatch thread
	 */
	protected Integer getHorizontalScrollBarValue() {
		if (SwingUtilities.isEventDispatchThread()) {
			return this.getHorizontalScrollBar().getValue() / DashBoard.fontWidth;
		} else {
			SwingComponentQueryTask<Integer> task = new SwingComponentQueryTask<Integer>() {

				@Override
				protected Integer query() {
					return ResizeableScrollPanel.this.getHorizontalScrollBar().getValue() / DashBoard.fontWidth;
				}
			};

			SwingUtilities.invokeLater(task);

			try {
				return task.get();
			} catch (InterruptedException ex) {
				Logger.getLogger(this.getClass()).trace("scrollbar value query interrupted");
				return null;
			} catch (ExecutionException ex) {
				Logger.getLogger(this.getClass()).error(ex.getMessage(), ex);
				return null;
			}
		}
	}

	/**
	 * {@inheritDoc }<br>Implementation is EventDispatchThread safe
	 */
	@Override
	public void clear() {
		Runnable clearTask = new Runnable() {
			@Override
			public void run() {
				contentPanel.removeAll();
				contentPanel.repaint();
				ResizeableScrollPanel.this.validate();
				Logger.getLogger(this.getClass()).trace("panel cleared");
			}
		};
		if (!SwingUtilities.isEventDispatchThread()) {
			try {
				SwingUtilities.invokeAndWait(clearTask);
			} catch (InterruptedException ex) {
				Logger.getLogger(this.getClass()).trace("panel clear interrupted");
			} catch (InvocationTargetException ex) {
				Logger.getLogger(this.getClass()).error(ex.getMessage(), ex);
			}
		} else {
			clearTask.run();
		}

	}
}
