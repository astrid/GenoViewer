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

package hu.astrid.viewer.model;

import hu.astrid.mapping.model.AlignmentRecord;
import hu.astrid.mvc.swing.AbstractModel;
import hu.astrid.viewer.model.alignment.ReadData;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author onagy
 */
public class CoverageModel extends AbstractModel {

	/** Coverage data generation event*/
	public static final String COVERAGE_DATA_GENERATED = "CoverageData";
	/** Default logger */
	private static final Logger logger = Logger.getLogger(CoverageModel.class);

	/**
	 * Generate coverage informations for the demanded region of alignment records.
	 * @param fromPosition
	 * @param toPosition
	 * @param readList list of alignment records
	 */
	public void generateCoverageData(int fromPosition, int toPosition, List<ReadData> readList) {

		long start, end;

		start = System.nanoTime();

		logger.trace("Generating new coverage data:[" + fromPosition + "-" + toPosition + "]");
		List<Coverage> coverageList = new ArrayList<Coverage>(toPosition - fromPosition);

		for (int i = fromPosition; i <= toPosition; i++) {

			coverageList.add(new Coverage(i));
		}

		Iterator<ReadData> subsequenceIterator = null;

		for (int i = 0; i < readList.size(); i++) {

			if (readList.get(i).getPosition() + readList.get(i).getCoveredLength() - 1 >= fromPosition) {

				subsequenceIterator = readList.listIterator(i);
				break;
			}
		}

		if (subsequenceIterator != null) {
			while (subsequenceIterator.hasNext()) {

				ReadData read = subsequenceIterator.next();

				if (read.getPosition() - 1 <= toPosition) {
					for (int i = read.getPosition() - 1; i < read.getPosition() - 1 + read.getCoveredLength(); i++) {

						if (i >= fromPosition && i <= toPosition) {

							coverageList.get(i - fromPosition).increaseCoverageByOne();
						}
					}
				}
			}
		}

		end = System.nanoTime();

		logger.trace("Coverage data generation takes " + ((end - (double) start) / 1000000000) + " sec(s) to complete!");

		firePropertyChange(COVERAGE_DATA_GENERATED, null, coverageList);
	}
}
