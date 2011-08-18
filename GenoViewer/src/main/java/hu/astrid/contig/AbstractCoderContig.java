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

import hu.astrid.core.Coder;
import hu.astrid.core.GenomeLetter;

public abstract class AbstractCoderContig<T extends GenomeLetter> extends AbstractContig<T> {
	
	protected Coder<T> coder;
	
	public AbstractCoderContig(Coder<T> coder) {
		this.coder = coder;
	}
	
	protected byte encode(T letter) {
		return coder.encode(letter);
	}
	
	protected T decode(byte code) {
		return coder.decode(code);
	}

}
