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

package hu.astrid.contig.reader;

import hu.astrid.contig.AbstractContig;
import hu.astrid.contig.Contig;
import hu.astrid.core.GenomeLetter;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Astrid Research
 * Author: Attila
 * Created: 2009.12.18.
 */
abstract class AbstractContigReader<T extends GenomeLetter> implements ContigReader<T> {

    protected BufferedReader fastaSource;

    protected String record = null;

	public AbstractContigReader(BufferedReader fastaSource) {
        this.fastaSource = fastaSource;
    }

    protected abstract AbstractContig<T> createContig();

    @Override
    public List<Contig<T>> load() throws IOException {
        Contig<T> contig = null;
        List<Contig<T>> result = new ArrayList<Contig<T>>();

		while ((contig = loadContig()) != null) {
            result.add( contig );
		}

        return result;
    }

}
