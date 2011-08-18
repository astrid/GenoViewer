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

package hu.astrid.mapping.model;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Compares alignment records by their reference name, mapping position and query name.
 * @author Szuni
 */
public class AlignmentRecordComparatorExtended implements Comparator<AlignmentRecord>, Serializable {
	
	private static final long serialVersionUID = 1L;

	@Override
	public int compare(AlignmentRecord record1, AlignmentRecord record2) {
		int ret = record1.getReferenceName().compareTo(record2.getReferenceName());
		if ( ret != 0) {
			return ret;
		}
		if (record1.getPosition() < record2.getPosition()) {
			return -1;
		} else if (record1.getPosition() > record2.getPosition()) {
			return 1;
		}
		return record1.getQueryName().compareTo(record2.getQueryName());
	}

}
