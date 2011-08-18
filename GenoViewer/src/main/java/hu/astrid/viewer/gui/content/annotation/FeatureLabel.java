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

package hu.astrid.viewer.gui.content.annotation;

import hu.astrid.mapping.model.GffRecord;
import hu.astrid.viewer.gui.DashBoard;
import java.awt.Color;
import java.awt.Graphics;

/**
 * Label for show a feature on {@link GffPanel}.
 * @author Szuni
 */
public class FeatureLabel extends AbstractAnnotationLabel{
	private static final long serialVersionUID = 1L;

	private static final Color FEATURE_BORDER_COLOR = Color.blue;

	/**
	 * Constructs a feature label width positions
	 * @param record record of annotation
	 */
	public FeatureLabel (GffRecord record) {
		super(record);
	}

	@Override
	public void paint(Graphics g) {
		int charWidth = DashBoard.fontWidth;
		int width = (endPosition - startPosition)*charWidth;
		int height = DashBoard.fontHeight/3;
		g.setClip(0, 0, width, DashBoard.fontHeight);
		g.setColor(color);
		g.fillRect(0, height, width, height);

		g.setColor(FEATURE_BORDER_COLOR);
		g.drawLine(0, height-2, 0, height*2+1);
		g.drawLine(0, height*2, charWidth/2, height*2);
		g.drawLine(0, height-1, charWidth/2, height-1);

		g.drawLine(width-1, height-2, width-1, height*2+1);
		g.drawLine(width-1, height*2, width-1-charWidth/2, height*2);
		g.drawLine(width-1, height-1, width-1-charWidth/2, height-1);
	}

}