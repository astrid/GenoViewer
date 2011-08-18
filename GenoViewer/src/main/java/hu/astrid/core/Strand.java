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

import hu.astrid.mapping.exception.GffFileFormatException;
import hu.astrid.mapping.model.EmptyValue;
import java.util.HashMap;
import java.util.Map;

public enum Strand {

    FORWARD('+'),
    REVERSE('-'),
    EMPTY(EmptyValue.EMPTY.getValue());

    private char value;

    private static Map<Character,Strand> instance;

    static {
        instance = new HashMap<Character, Strand>();
        instance.put('+', Strand.FORWARD);
        instance.put('-', Strand.REVERSE);
        instance.put(EmptyValue.EMPTY.getValue(), Strand.EMPTY);
    }

    private Strand(char value) {
        this.value = value;
    }

    public char getValue() {

        return this.value;

    }

    public static Strand getInstance( char value ) throws GffFileFormatException {

        Strand result = instance.get( value );

        if( result == null )
            throw new GffFileFormatException("Illegal strand field value:[" + value + "]");

        return result;

    }

}
