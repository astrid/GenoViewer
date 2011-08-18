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
import java.awt.*;
import javax.swing.*;

/**
 * Vonalzó. Minden 10. számot meg is jelenít.
 * @author Szuni
 */
public class PositionHeader extends JComponent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**Vonalzó magassága*/
	public static final int SIZE = 20;
	/**Vonalzó feliratainak betűtípusa*/
	private static final Font headerFont = new Font("SansSerif", Font.PLAIN, 10);
	/**Számozás kezdőszáma*/
	private int startIndex;
	/**Számozott beosztás elemek*/
	private int increment = 10;

	/**
	 *
	 * @param startIndex számozás kezdőszáma
	 */
	public PositionHeader(int startIndex) {
		this.startIndex = startIndex;
	}

	/**
	 * Set the increments, default is 10.
	 * @param increment
	 */
	public void setIncrement(int increment) {
		this.increment = increment;
	}

	/**
	 * Vonalzót a kívánt szélességének beállítása. Eddig rajzolja meg a vonalzót.
	 * @param pw szélesség a kívánt karakterek számában megadva
	 */
	public void setPreferredWidth(int pw) {
		this.setPreferredSize(new Dimension(pw * DashBoard.fontWidth, SIZE));
		this.revalidate();
	}

	@Override
	protected void paintComponent(Graphics g) {

		Rectangle drawHere = g.getClipBounds();
		if (drawHere != null) {
			int units = DashBoard.fontWidth;

			g.setColor(new Color(230, 130, 0));
			g.fillRect(drawHere.x, drawHere.y, drawHere.width, drawHere.height);

			g.setFont(headerFont);
			g.setColor(Color.black);

			FontMetrics rulerFontMetrics = g.getFontMetrics();

			int end = 0;
			int start = 0;
			int tickLength = 0;
			String text = null;

			start = (drawHere.x / units) * units;
			end = (((drawHere.x + drawHere.width) / units) + 1) * units;

			for (int i = start; i < end; i += units) {
				// Számozott elemeknél nagy vonal számmal, egyébként kis vonal
				if (i == 0 || (i + startIndex * units) % (units * increment) == 0) {
					tickLength = 5;
					text = Integer.toString(i / units + startIndex);
				} else {
					tickLength = 2;
					text = null;
				}

				if (tickLength != 0) {
					g.drawLine(i + units / 2, SIZE - 1, i + units / 2, SIZE - tickLength - 1);
					if (text != null) {
						g.drawString(text, i + units / 2 - rulerFontMetrics.stringWidth(text) / 2, SIZE - tickLength - 2);
					}
				}
			}
		}
	}
}
