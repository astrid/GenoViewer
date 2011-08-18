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

import java.util.ArrayList;
import java.util.List;

/**
 * An instance of this class represents the alignment section of a mapping file.
 */
public class MappingBody {

	/**
	 * The list of alingment records.
	 */
	private List<AlignmentRecord> records = new ArrayList<AlignmentRecord>();

	/**
	 * Creates an empty body instance.
	 */
	public MappingBody() {
	}

	/**
	 * @return the list of alignment records
	 */
	public List<AlignmentRecord> getRecords() {
		return this.records;
	}

	/**
	 * Adds an alignment record to the body.
	 * 
	 * @param alignmentRecord
	 *            the alignment record to be added
	 */
	public void addRecord(AlignmentRecord alignmentRecord) {
		this.records.add(alignmentRecord);
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();

		for (AlignmentRecord samBRecord : this.records) {
			if (result.length() > 0) {
				result.append('\n');
			}
			result.append(samBRecord.toString());
		}

		return result.toString();
	}

}
