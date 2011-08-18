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
import hu.astrid.core.Color;

public class AdaptorContigFactory {	
	
	public static AdaptorContig createContig(ContigImplementationType implementationType, Coder<Color> coder) {
		Contig<Color> contig = null;
		switch (implementationType) {
		case SIMPLE:
			contig = new SimpleContig<Color>();
			break;
		case BYTE:
			contig = new ByteContig<Color>(coder);
			break;
		case PACKED:
			contig = new PackedContig<Color>(coder);
			break;
		default:
			throw new IllegalArgumentException("Unknown contig implementation type!");
		}
		
		AdaptorContig adaptorContig = new AdaptorContigImpl();
		adaptorContig.setContig(contig);
		return adaptorContig;
	}
	
// TODO correct
//	private static int arraySize = 1000;
	
//	public AdaptorContigImpl createContig(ContigImplementationType implementationType, Contig<Nucleotide> contig) {
//		throw new UnsupportedOperationException("not supported yet");
//		Nucleotide tmpNuc = contig.get(0);
//		ByteColorContig result = new ColorContigFactory().createContig(implementationType);
//		result.setId(contig.getId());
//		
//		int size = contig.size();
//		int tmpIndex = 0;
//		byte[] tmpArray = new byte[arraySize];
//
//		for (int i = 1; i < size; i++) {
//			Nucleotide n = contig.get(i);
//			byte b = tmpNuc.getColor(n).byteValue();
//			tmpNuc = n;
//
//			if (tmpIndex < arraySize) {
//				tmpArray[tmpIndex++] = b;
//			} else {
//				result.putByteArray(tmpArray, tmpIndex);
//				Arrays.fill(tmpArray, 0, tmpArray.length, (byte) 0);
//				tmpIndex = 1;
//				tmpArray[tmpIndex] = b;
//			}
//		}
//
//		result.putByteArray(tmpArray, tmpIndex);
//		result.concatArray(tmpIndex);
//
//		AdaptorContigImpl adaptorContig = new AdaptorContigImpl();
//		adaptorContig.setContig(result);
//		adaptorContig.setAdaptor(contig.get(0));
//		return adaptorContig;
//
//	}

}
