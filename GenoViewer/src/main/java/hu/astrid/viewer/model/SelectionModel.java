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
package hu.astrid.viewer.model;

import hu.astrid.mvc.swing.AbstractModel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author OTTO
 */
public class SelectionModel extends AbstractModel {

	public static final String CLIPBOARD_BOUNDARY = "ClipboardBoundary";

	public static class SelectedPosition {

		private List<Integer> selectedPositions = new ArrayList<Integer>();
		private static final Logger logger = Logger.getLogger(SelectionModel.class);

		public void addSelectedPosition(Integer marker) {
			logger.debug("addedPosition: " + marker);

			if (marker == null) {
				if (selectedPositions.size() >= 2) {
					marker = selectedPositions.get(1);
					selectedPositions.clear();
					selectedPositions.add(marker);
				}
			} else {
				if (selectedPositions.size() >= 2) {
					selectedPositions.remove(0);
					selectedPositions.add(marker);

				} else {
					this.selectedPositions.add(marker);
				}
			}
//		logger.debug("SelectionList: " + selectedPositions);
		}

		public List<Integer> getSelectedPositions() {
			List<Integer> positions = new ArrayList<Integer>();
			positions.addAll(selectedPositions);
			Collections.sort(positions);
			return positions;
		}

		public int numberOfMarkers() {
			return selectedPositions.size();
		}

		public int getEndPosition() {
			return selectedPositions.get(1) > selectedPositions.get(0) ? selectedPositions.get(1) : selectedPositions.get(0);
		}

		public int getStartPosition() {
			if (selectedPositions.size() < 2) {
				return selectedPositions.get(0);
			}
			return selectedPositions.get(0) < selectedPositions.get(1) ? selectedPositions.get(0) : selectedPositions.get(1);
		}

		public void clearPositions() {
			selectedPositions.clear();
		}
	}

	/** Enum type to differentiate a selection type */
	public static enum SelectionType {

		REFERENCE, CONSENSUS, BOTH;
	}

	public void setClipboardBoundary(SelectionType selectionType) {
		firePropertyChange(CLIPBOARD_BOUNDARY, null, selectionType);
	}
}
