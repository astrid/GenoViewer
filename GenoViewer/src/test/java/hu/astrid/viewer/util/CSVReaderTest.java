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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package hu.astrid.viewer.util;

import hu.astrid.viewer.model.mutation.Mutation;
import hu.astrid.viewer.model.mutation.MutationRegion;
import hu.astrid.viewer.model.mutation.MutationType;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Szuni
 */
public class CSVReaderTest {

	@Test
	public void testRead() throws IOException {
		String input = "MutationSequence;MutationType;ReferenceSequence;StartPos;Occurence;FullCoverage;Length;\n"+
			"NACGTN;INSERTION;****;0;3;10;4;\n"+
			"*****;DELETION;CCAGT;5;1;9;5;\n"+
			"NTN;SNP;C;2;1;2;1;\n"+
			"NATN;MNP;CT;2;1;2;2;\n";
		CSVReader<Mutation> reader = new CSVReader<Mutation>(new StringReader(input), Mutation.class);
		Assert.assertEquals(new Mutation(MutationType.INSERTION, 0, 4, 3, 10, "****", "NACGTN"), reader.read());
		Assert.assertEquals(new Mutation(MutationType.DELETION, 5, 5, 1, 9, "CCAGT", "*****"), reader.read());
		Assert.assertEquals(new Mutation(MutationType.SNP, 2, 1, 1, 2, "C", "NTN"), reader.read());
		Assert.assertEquals(new Mutation(MutationType.MNP, 2, 2, 1, 2, "CT", "NATN"), reader.read());
		reader.close();
	}

	@Test
	public void testReadAll() throws IOException {
		String input = "MutationSequence;MutationType;ReferenceSequence;StartPos;Occurence;FullCoverage;Length;\n"+
			"NACGTN;INSERTION;****;0;3;10;4;\n"+
			"*****;DELETION;CCAGT;5;1;9;5;\n"+
			"NTN;SNP;C;2;1;2;1;\n"+
			"NATN;MNP;CT;2;1;2;2;\n";
		List<Mutation> list = new ArrayList<Mutation>();
		list.add(new Mutation(new MutationRegion(0, 4, MutationType.INSERTION, "NACGTN"), 1, 10, "****"));
		list.add(new Mutation(new MutationRegion(5, 5, MutationType.DELETION, "*****"), 1, 9, "CCAGT"));
		list.add(new Mutation(new MutationRegion(2, 1, MutationType.SNP, "NTN"), 1, 2, "C"));
		//This mutation uses different adaptors from input, it has to pass
		list.add(new Mutation(new MutationRegion(2, 2, MutationType.MNP, "AATT"), 1, 2, "CT"));

		CSVReader<Mutation> reader = new CSVReader<Mutation>(new StringReader(input), Mutation.class);

		Assert.assertEquals(list, reader.readAll());
		reader.close();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testTooManyFieldsInInputHeader() throws IOException {
		String input = "MutationSequence;MutationType;ReferenceSequence;StartPos;Occurence;FullCoverage;Length;Unexpedected header;\n";
		CSVReader<Mutation> reader = new CSVReader<Mutation>(new StringReader(input), Mutation.class);
	}

	@Test(expected = IllegalStateException.class)
	public void testTooFewFieldsInInputHeader() throws IOException {
		String input = "MutationSequence;MutationType;ReferenceSequence;StartPos;Occurence;FullCoverage;\n";
		CSVReader<Mutation> reader = new CSVReader<Mutation>(new StringReader(input), Mutation.class);
	}

	@Test(expected = IllegalStateException.class)
	public void testTooFewFieldsInInput() throws IOException {
		String input = "MutationSequence;MutationType;ReferenceSequence;StartPos;Occurence;FullCoverage;Length\n"+
			"NACGTN;INSERTION;null;0;3;10;4;\n"+
			"NACGTN;INSERTION;null;0;3;10;4;2\n";
		CSVReader<Mutation> reader = new CSVReader<Mutation>(new StringReader(input), Mutation.class);
		Assert.assertEquals(new Mutation(MutationType.INSERTION, 0, 4, 3, 10, null, "ACGT"), reader.read());
		reader.read();
	}

	@Test()
	public void testEmptyInput() throws IOException {
		String input = "MutationSequence;MutationType;ReferenceSequence;StartPos;Occurence;FullCoverage;Length\n";
		CSVReader<Mutation> reader = new CSVReader<Mutation>(new StringReader(input), Mutation.class);
		Assert.assertEquals(new ArrayList<Mutation>(), reader.readAll());
	}
}
