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

package hu.astrid.mapping.util;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class to perform BAM file specific operations.
 */
public class BamUtil {
	
	/**
     * Default ASCII charset to decode 1-byte characters.
     */
    private static final Charset ASCII_CHARSET = Charset.forName("US-ASCII");
	
	/**
     * Magic string of BAI files.
     */
    public static final String BAI_MAGIC_STRING = "BAI\u0001";
    
    /**
     * Byte representation of the BAI magic string.
     */
    public static final byte[] BAI_MAGIC_BYTES = BAI_MAGIC_STRING.getBytes(ASCII_CHARSET);
    
    /**
     * Magic string of BAM files.
     */
    public static final String BAM_MAGIC_STRING = "BAM\u0001";
    
    /**
     * Byte representation of the BAM magic string.
     */
    public static final byte[] BAM_MAGIC_BYTES = BAM_MAGIC_STRING.getBytes(ASCII_CHARSET);

    /**
     * 8-bit mask for performing operations byte by byte.
     */
    private static final int OP_PATTERN = 0xFF;
    
    private BamUtil() {
    	
    }

    /**
     * Calculate bin given an alignment in [beg,end[.
     *
     * @param beg
     *              begin of region
     * @param end
     *              end of region (exclusive)
     */
    public static int regToBin(int beg, int end) {
        if (beg >> 14 == end >> 14) {
            return ((1 << 15) - 1) / 7 + (beg >> 14);
        }

        if (beg >> 17 == end >> 17) {
            return ((1 << 12) - 1) / 7 + (beg >> 17);
        }

        if (beg >> 20 == end >> 20) {
            return ((1 << 9) - 1) / 7 + (beg >> 20);
        }

        if (beg >> 23 == end >> 23) {
            return ((1 << 6) - 1) / 7 + (beg >> 23);
        }

        if (beg >> 26 == end >> 26) {
            return ((1 << 3) - 1) / 7 + (beg >> 26);
        }

        return 0;
    }

    /**
     * Calculate list of bins that may overlap with region [startPos,endPos[.
     *
     * @param startPos
     *              start position of region
     * @param endPos
     *              end position of region (exclusive)
     */
    public static List<Integer> regionToBins(int startPos, int endPos) {
        List<Integer> result = new ArrayList<Integer>();
        result.add(0);

        int maxPos = 0x1fffffff;
        int start = (startPos > 0) ? (startPos - 1 & maxPos) : 0;
        int end = (endPos > 0) ? (endPos - 1 & maxPos) : maxPos;

        if (start > end) {
            return null;
        }

        for (int k = 1 + (start >> 26); k <= 1 + (end >> 26); k++) {
            result.add(k);
        }

        for (int k = 9 + (start >> 23); k <= 9 + (end >> 23); k++) {
            result.add(k);
        }

        for (int k = 73 + (start >> 20); k <= 73 + (end >> 20); k++) {
            result.add(k);
        }

        for (int k = 585 + (start >> 17); k <= 585 + (end >> 17); k++) {
            result.add(k);
        }

        for (int k = 4681 + (start >> 14); k <= 4681 + (end >> 14); k++) {
            result.add(k);
        }

        return result;
    }

    /**
     * This method one little-endian represented byte array converts to integer.
     *
     * @param bytes the bytes contain array
     */
    public static int toInt(byte[] bytes) {
    	return bytes[0] & OP_PATTERN | (bytes[1] & OP_PATTERN) << 8 | (bytes[2] & OP_PATTERN) << 16 | (bytes[3] & OP_PATTERN) << 24;
    }

    /**
     * This method one little-endian represented byte array converts to short.
     *
     * @param bytes bytes contain array
     */
    public static short toShort(byte[] bytes) {
    	return (short) (bytes[0] & OP_PATTERN | (bytes[1] & OP_PATTERN) << 8);
    }

    /**
     * This method converts one integer number to little-endian byte array.
     *
     * @param intr the number which type of integer
     */
    public static byte[] toByteArray(int intr) {
        byte[] result = new byte[4];
        
        for (int i = 0; i < 4; ++i) {
           result[i] = (byte) (intr & OP_PATTERN);
           intr = (intr >>> 8);
        }

        return result;
    }

    /**
     * This method converts one short number to little-endian byte array.
     *
     * @param shrt the number which type of short
     */
    public static byte[] toByteArray(short shrt) {
        byte[] result = new byte[2];
        int intr = shrt;
        
        for (int i = 0; i < 2; ++i) {
           result[i] = (byte) (intr & OP_PATTERN);
           intr = (intr >>> 8);
        }

        return result;
    }
    
    /**
     * Converts a long value to little-endian byte array.
     *
     * @param value the long value to be converted
     */
	public static byte[] toByteArray(long value) {
        byte[] result = new byte[8];
        
        for (int i = 0; i < 8; ++i) {
           result[i] = (byte) (value & OP_PATTERN);
           value = (value >>> 8);
        }

        return result;
    }

    /**
     * This method converts one character array to byte array.
     *
     * @param charArray characters contain array
     */
    public static byte[] toByteArray(char[] charArray) {
        byte[] result = new byte[charArray.length];

        for (int i = 0; i < charArray.length; i++) {
            result[i] = (byte) charArray[i];
        }

        return result;
    }

    /**
     * This method converts one bytes array to string.
     *
     * @param bytes array of byte
     */
    public static String toString(byte[] bytes) {
        char[] chars = new char[bytes.length];

        for( int i = 0; i < bytes.length; i++) {
            chars[i] = (char) bytes[i];
        }

        return new String( chars );
    }

    /**
     * This method elements of one character array to offset
     * by offset parameter value.
     *
     * @param chars characters contain array
     * @param offset offset rate
     */
    public static void offset( char[] chars, int offset ) {
        for (int i = 0; i < chars.length; i++) {
            chars[i] += offset;
        }
    }

	/**
	 * Tokenizes the given string with the given separator string.
	 * 
	 * @param string
	 *            the string to be tokenized
	 * @param tokenSeparator
	 *            the separator string
	 * @return the tokens
	 */
	public static String[] getTokens(String string, String tokenSeparator) {
		List<Integer> tokenEndIndices = new ArrayList<Integer>();
		int fromIndex = 0;
		while ((fromIndex = string.indexOf(tokenSeparator, fromIndex)) != -1) {
			tokenEndIndices.add(fromIndex++);
		}
		if ((tokenEndIndices.isEmpty())
				|| (string.length() > tokenEndIndices.get(tokenEndIndices
						.size() - 1) + 1))
			tokenEndIndices.add(string.length());

		fromIndex = -tokenSeparator.length();
		String array[] = new String[tokenEndIndices.size()];
		for (int i = 0; i < array.length; ++i) {
			fromIndex += tokenSeparator.length();
			array[i] = string.substring(fromIndex, tokenEndIndices.get(i));
			fromIndex = tokenEndIndices.get(i);
		}

		return array;
	}
    
}
