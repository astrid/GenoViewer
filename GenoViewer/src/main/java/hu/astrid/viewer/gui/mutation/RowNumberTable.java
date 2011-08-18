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

package hu.astrid.viewer.gui.mutation;

/**
 * Use a JTable as a renderer for row numbers of a given main table.
 * This table must be added to the row header of the scrollpane that
 * contains the main table.
 * @author Szuni
 */
import java.awt.*;
import java.beans.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

public class RowNumberTable extends JTable implements ChangeListener, PropertyChangeListener {

	private static final long serialVersionUID = 1L;
	private JTable main;

	/**
	 *
	 * @param table
	 */
	public RowNumberTable(JTable table) {
		main = table;
		main.addPropertyChangeListener(this);

		setFocusable(false);
		setAutoCreateColumnsFromModel(false);
		setModel(main.getModel());
		setSelectionModel(main.getSelectionModel());

		main.getModel().addTableModelListener(new TableModelListener() {

			@Override
			public void tableChanged(TableModelEvent e) {
				resizeHeader();
			}
		});

		TableColumn column = new TableColumn();
		column.setHeaderValue(" ");
		addColumn(column);
		column.setCellRenderer(new RowNumberRenderer());
		resizeHeader();

		setPreferredScrollableViewportSize(getPreferredSize());
	}

	@Override
	public void addNotify() {
		super.addNotify();

		Component c = getParent();

		//  Keep scrolling of the row table in sync with the main table.

		if (c instanceof JViewport) {
			JViewport viewport = (JViewport) c;
			viewport.addChangeListener(this);
		}
	}

	private void resizeHeader() {
		int value = getValueAt(getRowCount() - 1, 0).toString().length();
		int width = value * 10 + 10;
		getColumnModel().getColumn(0).setPreferredWidth(width);
		doLayout();
		setPreferredScrollableViewportSize(getPreferredSize());
	}

	/*
	 *  Delegate method to main table
	 */
	@Override
	public int getRowCount() {
		return main.getRowCount();
	}

	@Override
	public int getRowHeight(int row) {
		return main.getRowHeight(row);
	}

	/*
	 *  This table does not use any data from the main TableModel, except size
	 *  values, so just return a value based on the row parameter.
	 */
	@Override
	public Object getValueAt(int row, int column) {
		TableModel model = this.getModel();
		int pagedRows = 0;
		if (model instanceof PagingModel) {
			PagingModel pm = (PagingModel) model;
			pagedRows = pm.getPageOffset() * pm.getPageSize();
		}

		final int value = row + 1 + pagedRows;

		return Integer.toString(value);
	}

	/*
	 *  Don't edit data in the main TableModel by mistake
	 */
	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}
//
//  Implement the ChangeListener
//

	@Override
	public void stateChanged(ChangeEvent e) {
		//  Keep the scrolling of the row table in sync with main table

		JViewport viewport = (JViewport) e.getSource();
		JScrollPane scrollPane = (JScrollPane) viewport.getParent();
		scrollPane.getVerticalScrollBar().setValue(viewport.getViewPosition().y);
	}
//
//  Implement the PropertyChangeListener
//

	@Override
	public void propertyChange(PropertyChangeEvent e) {
		//  Keep the row table in sync with the main table
		if ("selectionModel".equals(e.getPropertyName())) {
			setSelectionModel(main.getSelectionModel());
		}

		if ("model".equals(e.getPropertyName())) {
			setModel(main.getModel());
			resizeHeader();
		}
	}

	/*
	 *  Borrow the renderer from JDK1.4.2 table header
	 */
	private static class RowNumberRenderer extends DefaultTableCellRenderer {

		private static final long serialVersionUID = 1L;

		@Override
		public Component getTableCellRendererComponent(
				JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			Component renderer = table.getTableHeader().getDefaultRenderer().getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			JLabel header = (JLabel) renderer;

			header.setHorizontalAlignment(JLabel.CENTER);

			if (isSelected) {
				header.setFont(header.getFont().deriveFont(Font.BOLD));
			}

			return header;
		}
	}
}
