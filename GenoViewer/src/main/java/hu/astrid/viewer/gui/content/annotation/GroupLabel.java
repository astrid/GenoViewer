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

import hu.astrid.mapping.exception.GffFileFormatException;
import hu.astrid.mapping.model.GffRecord;
import hu.astrid.viewer.gui.DashBoard;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import org.apache.log4j.Logger;

/**
 * Label for show a group on {@see GffPanel}.
 * @author Szuni
 */
public class GroupLabel extends AbstractAnnotationLabel {

	private static final long serialVersionUID = 1167652056943336018L;
	/**Font of group name*/
	private Font groupNameFont = new Font("SansSerif", Font.PLAIN, 10);
	/**Name of group*/
	private String name;
	/**Width of sign in the ends of group*/
	private static int groupEndSignWidth = 5;
	private static final Logger logger = Logger.getLogger(GroupLabel.class);

	/**
	 * Constructs a group label width positions and name
	 * @param record record of annotation
	 */
	public GroupLabel(GffRecord record) {
		super(record);
		try {
			name = record.getAttributeValue("Name", "ID", "locus_tag");
		} catch (GffFileFormatException exc) {
//			logger.error("Cannot specify any identification field in GFF file, mark name as 'unknown'", exc);
			name = "unknown";
		}
	}

	@Override
	public void paint(Graphics g) {
		g.setColor(color);
		int width = (endPosition - startPosition) * DashBoard.fontWidth;
		int height = DashBoard.fontHeight;
		int verticalCenter = height / 2;
		g.setClip(0, 0, width, height);
		g.drawLine(0, verticalCenter, width, verticalCenter);
		g.fillArc(-groupEndSignWidth - 1, verticalCenter - groupEndSignWidth, groupEndSignWidth * 2, groupEndSignWidth * 2, 270, 180);
		g.fillArc(width - groupEndSignWidth, verticalCenter - groupEndSignWidth, groupEndSignWidth * 2, groupEndSignWidth * 2, 90, 180);
		if (DashBoard.labelFont.getSize() == 17) {
			g.setFont(groupNameFont);
			g.drawString(name, groupEndSignWidth, g.getFontMetrics(groupNameFont).getAscent());
		}
	}
}