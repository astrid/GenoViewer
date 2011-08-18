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

/**
 * 
 */
public class AlignmentPosition {

    public AlignmentPosition() {
    }

	/**
	 * The name of the query sequence.
	 */
	protected String queryName;

	/**
	 * The name of the reference sequence.
	 */
	protected String referenceName;

	/**
	 * 1-based leftmost position or coordinate of the clipped query sequence.
	 */
	protected int position;
	
	/**
	 * The length of the read.
	 */
	protected int readLength;

	/**
	 * @return the name of the query sequence
	 */
	public String getQueryName() {
		return queryName;
	}

	/**
	 * @param queryName
	 *            the name of the query sequence to be set
	 */
	public void setQueryName(String queryName) {
		this.queryName = queryName;
	}

	/**
	 * @return the name of the reference sequence
	 */
	public String getReferenceName() {
		return referenceName;
	}

	/**
	 *
	 * @param referenceName
	 *            the name of the reference sequence to be set
	 */
	public void setReferenceName(String referenceName) {
		this.referenceName = referenceName;
	}

	/**
	 * @return the position of the query
	 */
	public int getPosition() {
		return position;
	}

	/**
	 * @param position
	 *            the position of the query to be set
	 */
	public void setPosition(int position) {
		this.position = position;
	}

	/**
	 * @return the length of the read
	 */
    public int getReadLength() {
		return readLength;
	}

    /**
     * @param readLength the length of the read to be set
     */
	public void setReadLength(int readLength) {
		this.readLength = readLength;
	}

	@Override
    public String toString() {
        return queryName + "\t" + referenceName + "\t" + position + "\t" + readLength;
    }

}
