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

package hu.astrid.alignmenter.util;

import hu.astrid.alignmenter.algorithm.Alignmenter;
import hu.astrid.alignmenter.core.Mutation;
import hu.astrid.io.FastaReader;
import hu.astrid.read.FastaRead;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class AlignmenterReader {
    private static final Logger logger = Logger.getLogger(AlignmenterReader.class);

    private CSVReader csvReader;
    private FastaReader fastaReader;

    private List<Mutation> readMutationsTable(String parent, String csvFileName) throws IOException {
        return readMutationsTable(parent + File.separator + csvFileName);
    }

    private List<Mutation> readMutationsTable(String csvFileName) throws IOException {
        List<Mutation> mutationsTable = new LinkedList<Mutation>();

        csvReader = new CSVReader<Mutation>(new BufferedReader(new FileReader(csvFileName)), Mutation.class);

        /*
            CSV file's structure (per line):
            FullCoverage;Length;MutationSequence;MutationType;Occurence;ReferenceSequence;StartPos;
        */
        mutationsTable = csvReader.readAll();

        return mutationsTable;
    }

    private FastaRead readSequence(String parent, String fileName) throws IOException {
        fastaReader = new FastaReader(new BufferedReader(new FileReader(parent + File.separator + fileName)));

        FastaRead sequence = fastaReader.readNext();

        return sequence;
    }

    public Alignmenter readAlignmenter(List<String> fileNameList) throws IOException {
        return readAlignmenter("", fileNameList);
    }

    public Alignmenter readAlignmenter(String parent, List<String> fileNameList) throws IOException {
		Alignmenter alignmenter;
        String referenceSequenceFileName = fileNameList.get(0);
        FastaRead referenceSequence = readSequence(parent, referenceSequenceFileName);
        List<FastaRead> sequences = new LinkedList<FastaRead>();
        Map<String, List<Mutation>> mutationsTable = new HashMap<String, List<Mutation>>();
        int i = 1;

        sequences.add(referenceSequence);
        while (i < fileNameList.size()) {
            String consensusFileName = fileNameList.get(i++);
            String csvFileName = fileNameList.get(i++);

            FastaRead consensus = readSequence(parent, consensusFileName);
            List<Mutation> mutations = readMutationsTable(parent, csvFileName);

            sequences.add(consensus);
            mutationsTable.put(consensus.getId(), mutations);
        }

        alignmenter = new Alignmenter(sequences, mutationsTable);

        return alignmenter;
    }

    public void close() throws IOException {
        if (csvReader != null) {
            csvReader.close();
        }

        if (fastaReader != null) {
            fastaReader.close();
        }
    }
}
