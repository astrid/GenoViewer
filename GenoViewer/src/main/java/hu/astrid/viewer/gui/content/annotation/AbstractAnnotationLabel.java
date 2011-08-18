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
package hu.astrid.viewer.gui.content.annotation;

import hu.astrid.mapping.model.GffRecord;
import hu.astrid.viewer.Viewer;
import hu.astrid.viewer.gui.DashBoard;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JComponent;

/**
 * Abstract label for annotations. Store start and end positions, {@link JComponent#paint(java.awt.Graphics)}
 * need to be overriden.
 * @author Szuni
 */
public abstract class AbstractAnnotationLabel extends JComponent {
	private static final long serialVersionUID = 1L;

	/**Start position of annotation (inclusive, started from 0)*/
	protected final int startPosition;
	/**End position of annotation (inclusive, started from 0)*/
	protected final int endPosition;
	protected final Color color;

	/**
	 * Constructs an annotation label width positions
	 * @param record record of annotation
	 */
	public AbstractAnnotationLabel(GffRecord record) {
		startPosition = record.getStart() - 1;
		endPosition = record.getEnd();
		color = Viewer.getController().getAnnottationColor(record.getType());
	}

	/**
	 * @return start position of annotation
	 */
	public int getStartPosition() {
		return startPosition;
	}

	/**
	 *
	 * @return length of annotation
	 */
	public int getLength() {
		return endPosition - startPosition;
	}

	//TODO változtatott fontnál erre oda kell figyelni!!!
	@Override
	public Dimension getPreferredSize() {
		return new Dimension((endPosition - startPosition) * DashBoard.fontWidth, DashBoard.fontHeight);
	}
}
