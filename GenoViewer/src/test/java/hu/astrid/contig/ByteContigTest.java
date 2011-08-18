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

import static org.junit.Assert.assertEquals;
import hu.astrid.core.Coder;
import hu.astrid.core.Nucleotide;

import java.io.IOException;

import org.junit.Test;

/**
 * Astrid Research
 * Author: balint
 * Created: Jan 6, 2010 2:16:18 PM
 */
public class ByteContigTest {

    @Test
    public void testPut() throws IOException {

        ByteContig<Nucleotide> contig2 = new ByteContig<Nucleotide>(new Coder<Nucleotide>(
        		new Nucleotide[] { Nucleotide.A, Nucleotide.C, Nucleotide.G, Nucleotide.T }));
        contig2.put( Nucleotide.C );
        assertEquals( "C", contig2.toString() );
        
    }
    
    
}