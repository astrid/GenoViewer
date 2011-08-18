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

import hu.astrid.alignmenter.core.Mutation;
import hu.astrid.alignmenter.core.MutationType;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;

public class CSVReaderTest {

    private Logger logger = Logger.getLogger(CSVReaderTest.class);

    @Test
    public void testReadAllMethod() {
        List<Mutation> actualMutations = null;
        CSVReader<Mutation> csvReader = null;


        List<Mutation> exceptedMutations = Arrays.asList(
                                            new Mutation(MutationType.MNP, 34, 3, 1, 50, "GGCAA", "ATG"),
                                            new Mutation(MutationType.MNP, 42, 5, 1, 70, "TCATAAG", "GCGCC"),
                                            new Mutation(MutationType.SNP, 43, 1, 1, 70, "GGG", "C")
        );

        StringReader stringReader = new StringReader("FullCoverage;Length;MutationSequence;MutationType;Occurence;ReferenceSequence;StartPos;\n" +
                                                     "50;3;GGCAA;MNP;1;ATG;34;\n" +
                                                     "70;5;TCATAAG;MNP;1;GCGCC;42;\n" +
                                                     "70;1;GGG;SNP;1;C;43;"
        );

        try {
            csvReader = new CSVReader<Mutation>(stringReader, Mutation.class);
            actualMutations = csvReader.readAll();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } finally {
            try {
                if(csvReader != null) {
                    csvReader.close();
                }
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }

        assertEquals(exceptedMutations, actualMutations);

        for(Mutation mutation : exceptedMutations) {
            logger.info(mutation.toString());
        }
    }
}

