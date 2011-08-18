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

import java.util.EnumMap;
import java.util.Map;

/**
 * Astrid Research Author: balint Created: Nov 18, 2009 10:30:06 AM
 */
public enum Color implements GenomeLetter {
	C0('0') {
		@Override
		public byte byteValue() {
			return 0;
		}

		@Override
		public boolean isConcrete() {
			return true;
		}
	},
	C1('1') {
		@Override
		public byte byteValue() {
			return 1;
		}

		@Override
		public boolean isConcrete() {
			return true;
		}
	},
	C2('2') {
		@Override
		public byte byteValue() {
			return 2;
		}

		@Override
		public boolean isConcrete() {
			return true;
		}
	},
	C3('3') {
		@Override
		public byte byteValue() {
			return 3;
		}

		@Override
		public boolean isConcrete() {
			return true;
		}
	},
	C4('4') {
		@Override
		public byte byteValue() {
			return 4;
		}

		@Override
		public boolean isConcrete() {
			return false;
		}
	};

	private static final Map<Color, Map<Color, Color>> colorMap = new EnumMap<Color, Map<Color, Color>>(Color.class);

	static {
		for (Color c : Color.values()) {
			colorMap.put(c, new EnumMap<Color, Color>(Color.class));
		}
		colorMap.get(C0).put(C0, C0);
		colorMap.get(C0).put(C1, C1);
		colorMap.get(C0).put(C2, C2);
		colorMap.get(C0).put(C3, C3);

		colorMap.get(C1).put(C0, C1);
		colorMap.get(C1).put(C1, C0);
		colorMap.get(C1).put(C2, C3);
		colorMap.get(C1).put(C3, C2);

		colorMap.get(C2).put(C0, C2);
		colorMap.get(C2).put(C1, C3);
		colorMap.get(C2).put(C2, C0);
		colorMap.get(C2).put(C3, C1);

		colorMap.get(C3).put(C0, C3);
		colorMap.get(C3).put(C1, C2);
		colorMap.get(C3).put(C2, C1);
		colorMap.get(C3).put(C3, C0);

		colorMap.get(C4).put(C0, C4);
		colorMap.get(C4).put(C1, C4);
		colorMap.get(C4).put(C2, C4);
		colorMap.get(C4).put(C3, C4);
		colorMap.get(C4).put(C4, C4);
		
		colorMap.get(C0).put(C4, C4);
		colorMap.get(C1).put(C4, C4);
		colorMap.get(C2).put(C4, C4);
		colorMap.get(C3).put(C4, C4);
	}
	
	private char colorChar;

	private Color(char colorChar) {
		this.colorChar = colorChar;
	}
	
	public Color add(Color c2) {
		if (c2 == null) {
			throw new IllegalArgumentException("Argument is null!");
		}
		return colorMap.get(this).get(c2);
	}

	@Override
	public char charValue() {
		return colorChar;
	}

	@Override
	public Color getComplement() {
		return this;
	}

	@Override
	public String toString() {
		return String.valueOf(colorChar);
	}
	
	public static Color valueOf(char c) {
		// TODO switch
		if (c == '0') {
			return C0;
		} else if (c == '1') {
			return C1;
		} else if (c == '2') {
			return C2;
		} else if (c == '3') {
			return C3;
		} else if (c == '4') {
			return C4;
		}
		throw new IllegalArgumentException("Invalid color code: [" + c + "]");
	}

	public static Color valueOf(byte code) {
		Color result;
		switch (code) {
		case 0:
			result = C0;
			break;
		case 1:
			result = C1;
			break;
		case 2:
			result = C2;
			break;
		case 3:
			result = C3;
			break;
		case 4:
			result = C4;
			break;
		default:
			throw new IllegalArgumentException("Invalid code: " + code);
		}
		return result;
	}

}
