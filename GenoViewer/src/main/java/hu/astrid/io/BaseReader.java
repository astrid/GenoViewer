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

import java.io.BufferedReader;
import java.io.IOException;

class BaseReader {
	
    static class Pair {
        private String id;
        private String value;

        public Pair(String id, String value) {
            this.id = id;
            this.value = value;
        }

        public String getId() {
            return id;
        }

        public String getValue() {
            return value;
        }
    }
    
    protected final BufferedReader seqReader;
    private String bufLine;
    private boolean hasBufferedLine = false;

    public BaseReader(BufferedReader seqReader) {
    	if (seqReader == null) {
    		throw new NullPointerException();
    	}
        this.seqReader = seqReader;
    }

    protected String findNextIdLine() throws IOException {
        String line;
        while ((line = nextLine()) != null) {
            if (getLineType(line) == LineType.ID) {
                break;
            }
        }
        return line;
    }

    public void close() throws IOException {
        seqReader.close();
    }

    protected Pair getNextPair() throws IOException {
        while (true) {
            String id = findNextIdLine();
            if (id == null) {
                return null;
            }
            String sequence = getSequence();
            if (sequence.contains(".")) {
                continue;
            }
            if (sequence.isEmpty()) {
                return null;
            }
            return new Pair(id, sequence);
        }
    }

    protected String nextLine() throws IOException {
        if(hasBufferedLine) {
            hasBufferedLine = false;
            return bufLine;
        }
        return seqReader.readLine();
    }

    public void pushBack(String line) {
        bufLine = line;
        hasBufferedLine = true;
    }

    public String getSequence() throws IOException {
        StringBuilder result = new StringBuilder();
        String line = nextLine();
        while (line!=null && getLineType(line) != LineType.ID) {
            if (getLineType(line) == LineType.SEQUENCE) {
                result.append(line);
            }
            line = nextLine();
        }
        if (line != null) {
            pushBack(line);
        }
        return result.toString();
    }

    private LineType getLineType(String line) {
        LineType result;

        if (line.isEmpty()) {
            result = LineType.EMPTY;
        } else if (line.charAt(0) == '>') {
            result = LineType.ID;
        } else if (line.charAt(0) == '#') {
            result = LineType.COMMENT;
        } else {
            result = LineType.SEQUENCE;
        }

        return result;
    }

    protected enum LineType {
        ID,
        SEQUENCE,
        COMMENT,
        START,
        EMPTY
        }

    protected enum Status {
        FOUND,
        MISSING
    }
}