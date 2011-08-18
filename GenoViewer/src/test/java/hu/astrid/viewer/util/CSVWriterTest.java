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
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Szuni
 */
public class CSVWriterTest {

	Writer writer;

	@Before
	public void init(){
		writer = new StringWriter();
	}

	@Test
	public void testWriteCollection() throws IOException {
		CSVWriter<Mutation> csv = new CSVWriter<Mutation>(writer, Mutation.class);
		List<Mutation> list = new ArrayList<Mutation>();
		list.add(new Mutation(new MutationRegion(0, 4, MutationType.INSERTION, "CACGTT"), 1, 10, "****"));
		list.add(new Mutation(new MutationRegion(5, 5, MutationType.DELETION,  "*****"), 1, 9, "CCAGT"));
		list.add(new Mutation(new MutationRegion(2, 1, MutationType.SNP, "ATA"), 1, 2, "C"));
		list.add(new Mutation(new MutationRegion(2, 2, MutationType.MNP, "NATA"), 1, 2, "CT"));


		csv.write(list);
		csv.close();

		Assert.assertEquals(
			"FullCoverage;Length;MutationSequence;MutationType;Occurence;ReferenceSequence;StartPos;\n"+
			"10;4;CACGTT;INSERTION;1;****;0;\n"+
			"9;5;*****;DELETION;1;CCAGT;5;\n"+
			"2;1;ATA;SNP;1;C;2;\n"+
			"2;2;NATA;MNP;1;CT;2;\n",
			writer.toString());
	}

	@Test
	public void testWriteRecord() throws IOException {
		CSVWriter<Mutation> csv = new CSVWriter<Mutation>(writer, Mutation.class);
		csv.write(new Mutation(new MutationRegion(0, 4, MutationType.INSERTION, "NACGTN"), 3, 10, "****"));
		csv.write(new Mutation(new MutationRegion(5, 5, MutationType.DELETION, "*****"), 1, 9, "CCAGT"));
		csv.close();

		Assert.assertEquals(
			"FullCoverage;Length;MutationSequence;MutationType;Occurence;ReferenceSequence;StartPos;\n"+
			"10;4;NACGTN;INSERTION;3;****;0;\n"+
			"9;5;*****;DELETION;1;CCAGT;5;\n",
			writer.toString());
	}
}
