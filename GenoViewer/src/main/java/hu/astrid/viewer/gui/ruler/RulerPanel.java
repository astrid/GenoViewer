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

package hu.astrid.viewer.gui.ruler;

import hu.astrid.viewer.gui.DashBoard;
import hu.astrid.viewer.gui.ResizeableScrollPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.SwingUtilities;
import org.apache.log4j.Logger;

/**
 * Scrollpane that contains a ruler as header
 * @author Szuni
 */
public class RulerPanel extends ResizeableScrollPanel {

	/**
	 *
	 */
	private static final long serialVersionUID = 3L;
	/**Position header*/
	private PositionHeader columnView;
	/**Logger*/
	private static final Logger logger = Logger.getLogger(RulerPanel.class);

	/** Creates new form RulerPanel */
	public RulerPanel() {
		super();
		columnView = new PositionHeader(1) {
			private static final long serialVersionUID = 1L;

			@Override
			public void paint(Graphics g) {
				super.paint(g);
				if (!selectionEnabled) {
					if (scrollPointerPosition != null) {
						g.setColor(cyan);
						g.drawLine(scrollPointerPosition * DashBoard.fontWidth + DashBoard.fontWidth / 2, 0, scrollPointerPosition * DashBoard.fontWidth + DashBoard.fontWidth / 2, this.getHeight());
					}
					if (selectionPosition != null) {
						g.setColor(Color.black);
						g.drawLine(selectionPosition * DashBoard.fontWidth + DashBoard.fontWidth / 2, 0, selectionPosition * DashBoard.fontWidth + DashBoard.fontWidth / 2, this.getHeight());
					}
				}

			}
		};
		columnView.setPreferredWidth(102);
		contentPanel.setPreferredSize(new Dimension(0, 0));
		this.setColumnHeaderView(columnView);
		setMaximumSize(new Dimension(32767, PositionHeader.SIZE));
		setMinimumSize(new Dimension(19, PositionHeader.SIZE));
	}

	/**
	 * @param increment rulers increment
	 */
	public void setIncrement(int increment) {
		columnView.setIncrement(increment);
	}

	@Override
	public void setPreferredWidth(final int width) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				contentPanel.setPreferredSize(new Dimension(width * DashBoard.fontWidth, 0));
				columnView.setPreferredWidth(width);
				RulerPanel.this.validate();
			}
		});
	}

	@Override
	public void repaintContent() {
		throw new UnsupportedOperationException("Not supported.");
	}
//	public void addSelectedPosition(Integer marker) {
//		if (selectedPositions.size() >= 2) {
//			selectedPositions.remove(0);
//			selectedPositions.add(marker);
//		} else {
//			this.selectedPositions.add(marker);
//		}
//		logger.debug("SelectionList: " + selectedPositions);
//	}
}
