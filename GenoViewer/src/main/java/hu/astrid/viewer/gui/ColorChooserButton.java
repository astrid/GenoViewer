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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;

public class ColorChooserButton extends JButton {

	private Icon icon = new ImageIcon(getClass().getResource("/hu/astrid/viewer/gui/colorchoosericons/ColorChooserIcon.png"));
	private Color color = null;
	private Color tmpColor = null;
	private boolean colorChanged = false;

	public ColorChooserButton() {
//		super();
		setText(null);
//		setForeground(Color.red);
		setToolTipText(Viewer.getLabelResources().getString("chooseColorButtonToolTip"));
		setIcon(icon);
		setMargin(new Insets(0, 0, 0, 0));
		setIconTextGap(0);
		setBorderPainted(false);
		setBorder(null);
		setPreferredSize(new Dimension(20, 20));
		this.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseExited(MouseEvent e) {
				if (colorChanged==false) {
					color = tmpColor;
//					repaint();
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				colorChanged=false;
				tmpColor = color;
				color = new Color(tmpColor.getRed(), tmpColor.getGreen(), tmpColor.getBlue(), 100);
//				color=new Color(10,10,10);
//				repaint();
			}
		});
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		if (color != null) {
			g.setColor(new Color(184, 188, 176));
			g.drawRect(0, 0, 20, 20);
			g.setColor(color);
			g.fillRect(1, 1, 18, 18);
		}
	}

	public void setColor(Color color) {
		colorChanged = true;
		this.color = color;
		repaint();
	}

	public Color getColor() {
		return color;
	}
}
