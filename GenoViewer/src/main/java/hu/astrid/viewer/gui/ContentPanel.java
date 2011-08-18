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

package hu.astrid.viewer.gui;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JComponent;

/**
 * //TODO
 * @author Szuni
 */
public interface ContentPanel {

	/**
	 * Display the whole content again. Used to accomodate to changed showing settings.
	 */
	public abstract void repaintContent();

	/**
	 * Clear the panel
	 */
	public abstract void clear();

	/**
	 * Adds the specified mouse listener to receive mouse events from this component. If listener l is null, no exception is thrown and no action is performed.
	 * Refer to AWT Threading Issues for details on AWT's threading model.
	 *
	 * @param listener the mouse listener
	 * @see JComponent#addMouseListener(java.awt.event.MouseListener)
	 * @see MouseEvent
	 * @see MouseListener
	 * @see JComponent#removeMouseListener(java.awt.event.MouseListener)
	 * @see JComponent#getMouseListeners()
	 */
	public abstract void addMouseListener(MouseListener listener);
}
