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

package hu.astrid.io;

import hu.astrid.read.CsRead;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: bds
 * Date: Dec 11, 2009
 * Time: 8:05:05 PM
 */
public class CsFastaQualReader extends CsFastaReader {

    private BufferedReader qualReader;

    private CsFastaQualReader(BufferedReader seqReader, BufferedReader qualReader) {
        super(seqReader);
        this.qualReader = qualReader;
    }

    public static CsFastaQualReader getInstance(BufferedReader seqReader, BufferedReader qualReader) {
        return new CsFastaQualReader(seqReader, qualReader);
    }

    @Override
    public CsRead readNext() throws IOException {
        CsRead read = null;
        Pair pair = getNextPair();
        if (pair != null) {
            int[] qualArray = qualArrayById(pair.getId().substring(1));
            read = new CsRead(pair.getId().substring(1), pair.getValue(), qualArray);
        }
        return read;
    }

    protected Status findNextWithId(String currentId) throws IOException {
        String curr;
        while ((curr = qualReader.readLine()) != null) {
            if (curr.equals(">" + currentId)) {
                return Status.FOUND;
            }
        }
        return Status.MISSING;
    }

    private int[] qualArrayById(String currentId) throws IOException {
        int[] result = null;
        if (findNextWithId(currentId) != Status.FOUND) {
            return result;
        }
        String qual = qualReader.readLine();
        String[] arr = qual.split(" ");
        result = new int[arr.length];
        for (int i = 0; i < arr.length; i++) {
            result[i] = Integer.valueOf(arr[i]);
        }
        return result;
    }
}
