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

package hu.astrid.viewer.util;

import hu.astrid.viewer.Viewer;
import hu.astrid.viewer.gui.DashBoard;
import javax.swing.SwingWorker;
import org.apache.log4j.Logger;

/**
 * This task loads coverage data for an interval with given start position
 * @author Szuni
 */
public class CoverageLoadingTask extends SwingWorker<Object, Void> {

	private Throwable throwable = null;
	private int position;

	/**
	 * set coverage loads start value
	 * @param position intervals start position
	 */
	public void setPosition(int position) {
		this.position = position;
	}

	@Override
	protected Object doInBackground() throws Exception {
		try {
			Viewer.getController().generateCoverageData(position / DashBoard.fontWidth, (position / DashBoard.fontWidth) + Viewer.getMainWindow().getDisplayWidth());
		} catch (Throwable ex) {
			throwable = ex;
		}
		return null;
	}

	@Override
	protected void done() {
		if (throwable != null) {
			Logger.getLogger(CoverageLoadingTask.class).error(throwable.getMessage(), throwable);
		}
	}
}
