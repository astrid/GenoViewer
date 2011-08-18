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

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import javax.swing.CellRendererPane;
import javax.swing.JComponent;
import javax.swing.JTextArea;
import javax.swing.JToolTip;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicToolTipUI;

/**
 * Multi line tooltip support for every JComponent. Just need to extend component
 * by overrinding {@link JComponent#createToolTip() } to <br>
 * {@code public JToolTip createToolTip()
            {
                return new JMultiLineToolTip();
            }
 * }
 * @author Szuni
 */
public class JMultiLineToolTip extends JToolTip
{
	private static final String uiClassID = "ToolTipUI";
	private static final long serialVersionUID = 1L;

	String tipText;
	JComponent component;

	public JMultiLineToolTip() {
	    updateUI();
	}

	@Override
	public void updateUI() {
	    setUI(MultiLineToolTipUI.createUI(this));
	}

	public void setColumns(int columns)
	{
		this.columns = columns;
		this.fixedwidth = 0;
	}

	public int getColumns()
	{
		return columns;
	}

	public void setFixedWidth(int width)
	{
		this.fixedwidth = width;
		this.columns = 0;
	}

	public int getFixedWidth()
	{
		return fixedwidth;
	}

	protected int columns = 0;
	protected int fixedwidth = 0;
}



class MultiLineToolTipUI extends BasicToolTipUI {
	static MultiLineToolTipUI sharedInstance = new MultiLineToolTipUI();
	Font smallFont;
	static JToolTip tip;
	protected CellRendererPane rendererPane;

	private static JTextArea textArea ;

	public static ComponentUI createUI(JComponent c) {
	    return sharedInstance;
	}

	public MultiLineToolTipUI() {
	    super();
	}

	@Override
	public void installUI(JComponent c) {
	    super.installUI(c);
		tip = (JToolTip)c;
	    rendererPane = new CellRendererPane();
	    c.add(rendererPane);
	}

	@Override
	public void uninstallUI(JComponent c) {
		super.uninstallUI(c);

	    c.remove(rendererPane);
	    rendererPane = null;
	}

	@Override
	public void paint(Graphics g, JComponent c) {
	    Dimension size = c.getSize();
	    textArea.setBackground(c.getBackground());
		rendererPane.paintComponent(g, textArea, c, 1, 1,
					    size.width - 1, size.height - 1, true);
	}

	@Override
	public Dimension getPreferredSize(JComponent c) {
		String tipText = ((JToolTip)c).getTipText();
		if (tipText == null)
			return new Dimension(0,0);
		textArea = new JTextArea(tipText );
	    rendererPane.removeAll();
		rendererPane.add(textArea );
		textArea.setWrapStyleWord(true);
		int width = ((JMultiLineToolTip)c).getFixedWidth();
		int columns = ((JMultiLineToolTip)c).getColumns();

		if( columns > 0 )
		{
			textArea.setColumns(columns);
			textArea.setSize(0,0);
		textArea.setLineWrap(true);
			textArea.setSize( textArea.getPreferredSize() );
		}
		else if( width > 0 )
		{
		textArea.setLineWrap(true);
			Dimension d = textArea.getPreferredSize();
			d.width = width;
			d.height++;
			textArea.setSize(d);
		}
		else
			textArea.setLineWrap(false);


		Dimension dim = textArea.getPreferredSize();

		dim.height += 1;
		dim.width += 1;
		return dim;
	}

	@Override
	public Dimension getMinimumSize(JComponent c) {
	    return getPreferredSize(c);
	}

	@Override
	public Dimension getMaximumSize(JComponent c) {
	    return getPreferredSize(c);
	}
}


