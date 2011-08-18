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

public class ContigFactory {
	
	public static <T extends GenomeLetter> AbstractContig<T> createContig(ContigImplementationType implementationType, Coder<T> coder) {
		AbstractContig<T> contig = null;
		switch (implementationType) {
		case SIMPLE:
			contig = new SimpleContig<T>();
			break;
		case BYTE:
			contig = new ByteContig<T>(coder);
			break;
		case PACKED:
			contig = new PackedContig<T>(coder);
			break;
		default:
			throw new IllegalArgumentException("Unknown contig implementation type!");
		}
		return contig;
	}

}
