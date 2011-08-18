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

package hu.astrid.contig;

import hu.astrid.core.GenomeLetter;

/**
 * Astrid Research Author: Attila Created: 2009.12.18.
 */
public abstract class AbstractContig<T extends GenomeLetter> implements Contig<T> {

	private String id = "";
	
	/**Default constructor*/
	public AbstractContig() {
		//Empty
	}
	
	public AbstractContig(String id) {
		this.id = id;
	}

	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}
	
	@Override
	public boolean containsNonconcreteLetter(int start, int length) {
		for (T letter : this.getSequence(start, length)) {
			if (!letter.isConcrete()) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean containsNonconcreteLetter() {
		return this.containsNonconcreteLetter(0, this.size());
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();

		for (T element : getSequence()) {
			result.append(element.toString());
		}

		return result.toString();
	}

}
