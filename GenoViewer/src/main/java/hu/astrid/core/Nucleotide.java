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
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Astrid Research
 * Author: balint
 * Created: Nov 18, 2009 10:30:59 AM
 * Enum representing concrete nucleotids (A, C, G, T) and 'N' (any base)
 * Added basic support for additional, non-concrete IUPAC codes. These symbols will be interpreted as {@link Nucleotide#N N}
 * @see <a href="http://www.dna.affrc.go.jp/misc/MPsrch/InfoIUPAC.html">IUPAC codes description</a>
 */
public enum Nucleotide implements GenomeLetter {

	/**
	 * Adenine
	 */
	A('A') {

		@Override
		public byte byteValue() {
			return 0;
		}

		@Override
		public boolean isConcrete() {
			return true;
		}

		@Override
		public Nucleotide getComplement() {
			return T;
		}
	},
	/**
	 * Cytosine
	 */
	C('C') {

		@Override
		public byte byteValue() {
			return 1;
		}

		@Override
		public boolean isConcrete() {
			return true;
		}

		@Override
		public Nucleotide getComplement() {
			return G;
		}
	},
	/**
	 * Guanine
	 */
	G('G') {

		@Override
		public byte byteValue() {
			return 2;
		}

		@Override
		public boolean isConcrete() {
			return true;
		}
	},
	/**
	 * Thymine
	 */
	T('T') {

		@Override
		public byte byteValue() {
			return 3;
		}

		@Override
		public boolean isConcrete() {
			return true;
		}
	},
	/**
	 * Any base (A, C, G, T)
	 */
	N('N') {

		@Override
		public byte byteValue() {
			return 4;
		}

		@Override
		public boolean isConcrete() {
			return false;
		}
	};
	private static final Set<Character> nonConcreteIupacCodes = new HashSet<Character>() {

		{
			add('R'); //Purine (A or G)
			add('Y'); //Pyrimidine (C, T, or U)
			add('M'); //C or A
			add('K'); //T, U, or G
			add('W'); //T, U, or A
			add('S'); //C or G
			add('B'); //C, T, U, or G (not A)
			add('D'); //A, T, U, or G (not C)
			add('H'); //A, T, U, or C (not G)
			add('V'); //A, C, or G (not T, not U)
			add('N'); //Any base (A, C, G, T, or U)
		}
	};
	private static final EnumSet<Nucleotide> concreteNucleotides = EnumSet.allOf(Nucleotide.class);
	private static final Map<Nucleotide, Map<Color, Nucleotide>> conversionMap = new EnumMap<Nucleotide, Map<Color, Nucleotide>>(Nucleotide.class);
	private static final Map<Nucleotide, Map<Nucleotide, Color>> nucleotideMap = new EnumMap<Nucleotide, Map<Nucleotide, Color>>(Nucleotide.class);
	private static final Map<Nucleotide, Nucleotide> inverseMap = new EnumMap<Nucleotide, Nucleotide>(Nucleotide.class);

	static {

		for (Nucleotide nucleotide : Nucleotide.values()) {
			if (!nucleotide.isConcrete()) {
				concreteNucleotides.remove(nucleotide);
			}
			conversionMap.put(nucleotide, new EnumMap<Color, Nucleotide>(Color.class));
			nucleotideMap.put(nucleotide, new EnumMap<Nucleotide, Color>(Nucleotide.class));
		}
		conversionMap.get(A).put(Color.C0, A);
		conversionMap.get(A).put(Color.C1, C);
		conversionMap.get(A).put(Color.C2, G);
		conversionMap.get(A).put(Color.C3, T);

		conversionMap.get(C).put(Color.C0, C);
		conversionMap.get(C).put(Color.C1, A);
		conversionMap.get(C).put(Color.C2, T);
		conversionMap.get(C).put(Color.C3, G);

		conversionMap.get(G).put(Color.C0, G);
		conversionMap.get(G).put(Color.C1, T);
		conversionMap.get(G).put(Color.C2, A);
		conversionMap.get(G).put(Color.C3, C);

		conversionMap.get(T).put(Color.C0, T);
		conversionMap.get(T).put(Color.C1, G);
		conversionMap.get(T).put(Color.C2, C);
		conversionMap.get(T).put(Color.C3, A);

		nucleotideMap.get(A).put(A, Color.C0);
		nucleotideMap.get(A).put(C, Color.C1);
		nucleotideMap.get(A).put(G, Color.C2);
		nucleotideMap.get(A).put(T, Color.C3);
		nucleotideMap.get(C).put(A, Color.C1);
		nucleotideMap.get(C).put(C, Color.C0);
		nucleotideMap.get(C).put(G, Color.C3);
		nucleotideMap.get(C).put(T, Color.C2);
		nucleotideMap.get(G).put(A, Color.C2);
		nucleotideMap.get(G).put(C, Color.C3);
		nucleotideMap.get(G).put(G, Color.C0);
		nucleotideMap.get(G).put(T, Color.C1);
		nucleotideMap.get(T).put(A, Color.C3);
		nucleotideMap.get(T).put(C, Color.C2);
		nucleotideMap.get(T).put(G, Color.C1);
		nucleotideMap.get(T).put(T, Color.C0);

		nucleotideMap.get(N).put(A, Color.C4);
		nucleotideMap.get(N).put(C, Color.C4);
		nucleotideMap.get(N).put(T, Color.C4);
		nucleotideMap.get(N).put(G, Color.C4);
		nucleotideMap.get(A).put(N, Color.C4);
		nucleotideMap.get(C).put(N, Color.C4);
		nucleotideMap.get(T).put(N, Color.C4);
		nucleotideMap.get(G).put(N, Color.C4);
		nucleotideMap.get(N).put(N, Color.C4);

		inverseMap.put(A, T);
		inverseMap.put(C, G);
		inverseMap.put(G, C);
		inverseMap.put(T, A);
	}

	/**
	 * @return set of defined, concrete nucleotids
	 */
	public static EnumSet<Nucleotide> getConcreteNucleotides() {
		return concreteNucleotides;
	}
	private char nucleotideChar;

	private Nucleotide(char nucleotideChar) {
		this.nucleotideChar = nucleotideChar;
	}

	/**
	 * @return complement {@link Nucleotide Nucleotide}, or null if there is no defined complement
	 */
	@Override
	public Nucleotide getComplement() {
		return inverseMap.get(this);
	}

	/**
	 * @return character representation of the {@link Nucleotide Nucleotide}
	 */
	@Override
	public char charValue() {
		return nucleotideChar;
	}

	/**
	 * @param c {@link hu.astrid.core.Color Color}, we try to find if it is associated with any {@link Nucleotide Nucleotide}
	 * @throws IllegalArgumentException if param c points to null
	 * @return the {@link Nucleotide Nucleotide} with color c, or null if there is no Nucleotide with associated Color c
	 */
	public Nucleotide translate(Color c) {
		if (c == null) {
			throw new IllegalArgumentException("Argument is null!");
		}
		return conversionMap.get(this).get(c);
	}

	/**
	 * @param n {@link Nucleotide Nucleotide}, we try to find out if has any associated {@link hu.astrid.core.Color Color}
	 * @throws IllegalArgumentException if param n points to null
	 * @return the color associated with n, or null if there is no associated color
	 */
	public Color getColor(Nucleotide n) {
		if (n == null) {
			throw new IllegalArgumentException("Argument is null!");
		}
		return nucleotideMap.get(this).get(n);
	}

	@Override
	public String toString() {
		return String.valueOf(nucleotideChar);
	}

	/**
	 * Returns true if a given character is convertible for a letter in this
	 * alphabet, i. e. is either A, C, G, T or can be interpreted as a non-concrete IUPAC code
	 * @param ch character representing a symbol
	 * @see <a href="http://www.dna.affrc.go.jp/misc/MPsrch/InfoIUPAC.html">IUPAC codes description</a>
	 * @return true if c is convertible for a letter, false otherwise
	 */
	public static boolean isValid(char ch) {
		char c = Character.toUpperCase(ch);
		return (c == 'A' || c == 'C' || c == 'G' || c == 'T' || c == 'N'
				|| Nucleotide.isNonConcreteIupacCode(c));
	}

	/**
	 * Get the corresponding {@link Nucleotide Nucleotide} based on ch, all non-concrete IUPAC code will be interpreted as {@link Nucleotide#N N}
	 * @param ch character value we try to interpret as nucleotide (concrete or non-concrete symbols allowed alike)
	 * @see <a href="http://www.dna.affrc.go.jp/misc/MPsrch/InfoIUPAC.html">IUPAC codes description</a>
	 * @throws IllegalArgumentException if we cannot interpret ch as IUPAC code
	 * @return the associated {@link Nucleotide Nucleotide}
	 */
	public static Nucleotide valueOf(char ch) {
		// TODO switch
		char c = Character.toUpperCase(ch);
		if (c == 'A') {
			return A;
		} else if (c == 'C') {
			return C;
		} else if (c == 'G') {
			return G;
		} else if (c == 'T') {
			return T;
		} else if (Nucleotide.isNonConcreteIupacCode(c)) {
			return N;
		}
		throw new IllegalArgumentException("Invalid nucleotide code: [" + c + "]");
	}

	public static Nucleotide valueOf(byte code) {
		Nucleotide result;
		switch (code) {
			case 0:
				result = A;
				break;
			case 1:
				result = C;
				break;
			case 2:
				result = G;
				break;
			case 3:
				result = T;
				break;
			case 4:
				result = N;
				break;
			default:
				throw new IllegalArgumentException("Invalid code: " + code);
		}
		return result;
	}

	/**
	 * Check whether ch is IUPAC code or not
	 * @param ch IUPAC code candidate
	 * @see <a href="http://www.dna.affrc.go.jp/misc/MPsrch/InfoIUPAC.html">IUPAC codes description</a>
	 * @return if can be interpreted as IUPAC, or not
	 */
	private static boolean isNonConcreteIupacCode(char ch) {
		return nonConcreteIupacCodes.contains(Character.toUpperCase(ch));
	}
}
