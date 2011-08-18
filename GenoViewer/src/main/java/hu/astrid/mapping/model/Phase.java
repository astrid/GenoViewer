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

import hu.astrid.mapping.exception.GffFileFormatException;
import java.util.HashMap;
import java.util.Map;

/**
 * This enumeration represents a field in GFF file, it name is phase.
 * It possible values are one of the integers 0, 1, 2, or '.'.
 */
public enum Phase {

    FIRST_BASE('0'),
    SECOND_BASE('1'),
    THIRD_BASE('2'),
    EMPTY(EmptyValue.EMPTY.getValue());

    private char value;

    private static Map<Character,Phase> instance;

    static {
        instance = new HashMap<Character, Phase>();
        instance.put('0', Phase.FIRST_BASE);
        instance.put('1', Phase.SECOND_BASE);
        instance.put('2', Phase.THIRD_BASE);
        instance.put(EmptyValue.EMPTY.getValue(), Phase.EMPTY);
    }

    private Phase(char value) {
        this.value = value;
    }

    public char getValue() {

        return this.value;

    }

    /**
     * This returns an instance of Phase.
     *
     * @param value value of field
     */
    public static Phase getInstance( char value ) throws GffFileFormatException {

        Phase result = instance.get( value );

        if( result == null )
            throw new GffFileFormatException("Illegal phase field value:[" + value + "]");

        return result;

    }

}
