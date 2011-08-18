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

import hu.astrid.viewer.Viewer;
import hu.astrid.viewer.properties.ProfileProperties;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.JComponent;

/**
 * Draws a sequence in nucleotide or in color code mode. Uses applications {@code labelFont}
 * @author Mat
 */
public class SequenceLabel extends JComponent {

	private static final long serialVersionUID = 2L;
	/**Showing mode if component*/
	protected boolean colorMode = false;
	/**Temporary properties for previewing profile settings*/
	protected ProfileProperties tempProperties = null;
	/**Displayed text of component*/
	protected String text;

	/**
	 * Create empty label in nucleotide mode
	 */
	public SequenceLabel() {
		this("", false);
	}

	/**
	 * Create empty label in specified mode
	 * @param mode label shoud be displayed in color mode or nucleotide
	 */
	public SequenceLabel(boolean mode) {
		this("", mode);
	}

	/**
	 * Create label with specified text in nucleotide mode
	 * @param string label text
	 */
	public SequenceLabel(String string) {
		this(string, false);
	}

	/**
	 * Create label with specified text in specified mode
	 * @param string label text
	 * @param mode label shoud be displayed in color mode or nucleotide
	 */
	public SequenceLabel(String string, boolean mode) {
		super();
		setDoubleBuffered(true);
		setFont(DashBoard.labelFont);
		this.text = string;
		this.colorMode = mode;
	}

	/**	 *
	 * @return wether the sequence shown in color mode or nucleotides
	 */
	public boolean isColorMode() {
		return colorMode;
	}

	/**
	 * @param colorMode wether the sequence shown in color mode or nucleotides
	 */
	public void setColorMode(boolean colorMode) {
		this.colorMode = colorMode;
	}

	/**
	 *
	 * @return displayed text of component
	 */
	public String getText() {
		return text;
	}

	/**
	 *
	 * @param text displayed text of component
	 */
	public void setText(String text) {
		this.text = text;
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		ProfileProperties profile = tempProperties == null ? Viewer.getActiveProfile() : tempProperties;
		final Rectangle visibleRect = this.getVisibleRect();
		char c = ' ';
		int locX = 0;
		int locY = g.getFontMetrics().getAscent();
		final int width = g.getFontMetrics().charWidth(c);
		final int height = g.getFontMetrics().getHeight();
		final boolean isZoomedOut = getFont().getSize() < 17;
		if (isZoomedOut) {
			g.setColor(profile.getZoomedTextColor());
			g.fillRect(locX, 1, width * text.length(), height - 1);
		}
		final int leftLimit = visibleRect.x-width, rightLimit = visibleRect.x+visibleRect.width;

		for (int i = 0; i < text.length(); i++, locX += width) {
			if(locX<leftLimit || locX>rightLimit)
				continue;
			c = text.charAt(i);
			if (c == '-' /*&& getFont().getSize() < 17*/) {
				g.setColor(profile.getDelitionColor());
				g.fillRect(locX, 0, width, height);
			}
			if (isZoomedOut) {
				continue;
			}
			if (colorMode) {
				switch (c) {
					case '0': {
						g.setColor(profile.get0Color());
						break;
					}
					case '1': {
						g.setColor(profile.get1Color());
						break;
					}
					case '2': {
						g.setColor(profile.get2Color());
						break;
					}
					case '3': {
						g.setColor(profile.get3Color());
						break;
					}
					default: {
//						paintChars(locX, locY, g, c);
						continue;
					}
				}
				g.fillOval(locX, 0, width, height);
			}
//				paintChars(locX, locY, g, c);
		}
		if (!isZoomedOut) {
			paintChars(0, locY, g, this.text);
		}
	}

	/**
	 * Paint characters to a position
	 * @param posX
	 * @param posY
	 * @param g
	 * @param characters
	 */
	protected void paintChars(int posX, int posY, Graphics g, String characters) {
		g.setColor(tempProperties == null ? Viewer.getActiveProfile().getTextColor() : tempProperties.getTextColor());
		g.drawString(characters, posX, posY);
	}

	//TODO változtatott fontnál erre oda kell figyelni!!!
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(text.length() * DashBoard.fontWidth, DashBoard.fontHeight);
	}

	public int getNumOfDrawnChars() {
		return this.getText().length();
	}
}
