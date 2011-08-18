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

import hu.astrid.contig.Contig;
import hu.astrid.core.GenomeLetter;

import java.io.IOException;
import java.util.List;

/**
 * Astrid Research
 * Author: Attila
 * Created: 2009.12.18.
 */
public interface ContigReader<T extends GenomeLetter> {

    List<Contig<T>> load() throws IOException;

    Contig<T> loadContig() throws IOException;

}
