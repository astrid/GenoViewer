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
package hu.astrid.viewer.gui.mutation;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

/**
 * Paging model for {@link JTable} that shows only specified number of record.
 * @author Szuni
 */
public class PagingModel extends AbstractTableModel {
	private static final long serialVersionUID = 1L;

	/** Maximum number of rows on a page */
	protected int pageSize;
	/** Actual page index*/
	protected int pageOffset;
	/** Table data */
	protected Object[][] data;
	/** Header names */
	protected Object[] columnHeader;

	/**
	 * //TODO
	 * @param columnHeader
	 * @param data
	 * @param size page size
	 */
	public PagingModel(Object[] columnHeader, Object[][] data, int size) {
		this.columnHeader = columnHeader;
		this.data = data;
		pageSize = size;
	}

	public void setColumnHeader(Object[] columnHeader) {
		this.columnHeader = columnHeader;
		fireTableDataChanged();
	}

	// Return values appropriate for the visible table part.
	@Override
	public int getRowCount() {
		return Math.min(pageSize, data.length-(pageSize*pageOffset));
	}

	@Override
	public int getColumnCount() {
		return columnHeader.length;
	}

	// Work only on the visible part of the table.
	@Override
	public Object getValueAt(int row, int col) {
		int realRow = row + (pageOffset * pageSize);
		return data[realRow][col];
	}

	@Override
	public String getColumnName(int col) {
		return columnHeader[col].toString();
	}

	// Use this method to figure out which page you are on.
	/**
	 *
	 * @return index of actual page
	 */
	public int getPageOffset() {
		return pageOffset;
	}

	/**
	 *
	 * @return number of pages
	 */
	public int getPageCount() {
		return (int) Math.ceil((double) data.length / pageSize);
	}


	// Use this method if you want to know how big the real table is . . . we
	// could also write "getRealValueAt()" if needed.
	/**
	 *
	 * @return number of rows contained in the model
	 */
	public int getRealRowCount() {
		return data.length;
	}

	/**
	 *
	 * @return number of row displayed on a page
	 */
	public int getPageSize() {
		return pageSize;
	}

	/**
	 * Change page size and fire a data changed
	 * @param s new page size
	 */
	public void setPageSize(int s) {
		if (s == pageSize) {
			return;
		}
		int oldPageSize = pageSize;
		pageSize = s;
		pageOffset = (oldPageSize * pageOffset) / pageSize;
		fireTableDataChanged();
		/*
		 * if (pageSize < oldPageSize) { fireTableRowsDeleted(pageSize,
		 * oldPageSize - 1); } else { fireTableRowsInserted(oldPageSize,
		 * pageSize - 1); }
		 */
	}

	/**
	 * Update the page offset and fire a data changed (all rows).
	 */
	public void pageNext() {
		if (pageOffset < getPageCount() - 1) {
			pageOffset++;
			fireTableDataChanged();
		}
	}

	/**
	 * Update the page offset and fire a data changed (all rows).
	 */
	public void pagePrevious() {
		if (pageOffset > 0) {
			pageOffset--;
			fireTableDataChanged();
		}
	}

	/**
	 * Jump to specified page (indexex from 0)
	 * @param pageIndex
	 * @return {@code true} - if jump was successful, a page exists with given index
	 */
	public boolean setPage(int pageIndex) {
		if (pageIndex >= 0 && pageIndex < getPageCount()) {
			pageOffset = pageIndex;
			fireTableDataChanged();
			return true;
		}
		else {
			return false;
		}
	}
}
