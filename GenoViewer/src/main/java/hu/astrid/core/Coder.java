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

package hu.astrid.core;

import java.util.HashMap;
import java.util.Map;


public class Coder<T extends GenomeLetter> {
	
	private Map<T, Byte> codeTable;
	
	private Map<T, Character> charCodeTable;
	
	private Map<Byte, T> inverseCodeTable;
	
	private Map<Character, T> inverseCharCodeTable;
	
	public Coder(T[] elements) {
		this.codeTable = new HashMap<T, Byte>();
		this.charCodeTable = new HashMap<T, Character>();
		this.inverseCodeTable = new HashMap<Byte, T>();
		this.inverseCharCodeTable = new HashMap<Character, T>();
		for (T element : elements) {
			this.codeTable.put(element, element.byteValue());
			this.charCodeTable.put(element, element.charValue());
			this.inverseCodeTable.put(element.byteValue(), element);
			this.inverseCharCodeTable.put(element.charValue(), element);
		}
	}
	
	public byte encode(T letter) {
		return this.codeTable.get(letter);
	}
	
	public char encodeChar(T letter) {
		return this.charCodeTable.get(letter);
	}
	
	public T decode(byte b) {
		return this.inverseCodeTable.get(b);
	}
	
	public T decode(char c) {
		return this.inverseCharCodeTable.get(c);
	}
	

}
