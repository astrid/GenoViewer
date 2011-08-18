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

import hu.astrid.core.GenomeLetter;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: bds
 * Date: Dec 16, 2009
 * Time: 12:42:37 AM
 */
public interface Contig<T extends GenomeLetter> {

    String getId();

    void setId(String id);

    T get(int pos);

    List<T> getSequence();

    List<T> getSequence(int pos, int seqLength);

    int size();

    boolean containsNonconcreteLetter(int start, int length);

    boolean containsNonconcreteLetter();

    void put(T letter);
}
