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

package hu.astrid.viewer;

import hu.astrid.viewer.model.SelectionModel.SelectionType;
import hu.astrid.mapping.exception.MappingFileFormatException;
import hu.astrid.viewer.Viewer;
import hu.astrid.viewer.ViewerController;
import hu.astrid.viewer.model.ViewerConsensusModel;
import hu.astrid.viewer.model.ViewerFastaModel;
import hu.astrid.viewer.model.consensus.ConsensusData;
import hu.astrid.viewer.model.mutation.Mutation;
import hu.astrid.viewer.reader.FastaRandomReader.FastaRandomReaderException;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class CopyToClipboardTest {

	private ViewerController controller = new ViewerController();
	private Logger logger = Logger.getLogger(CopyToClipboardTest.class);

	@Before
	public void before() throws FileNotFoundException, IOException, FastaRandomReaderException, MappingFileFormatException {
		File fasta = new File(System.getProperty("viewer.testfiles.dir") + System.getProperty("file.separator") + "c1215.fasta");
		ViewerFastaModel fastaModel = new ViewerFastaModel();
		ViewerConsensusModel consensusModel = new ViewerConsensusModel();

		controller.addModel(fastaModel);
		controller.addModel(consensusModel);

		controller.openFastaFile(fasta);
		fastaModel.setActReferenceContigIndex(0);
		consensusModel.setConsensusData(new ConsensusData(new ArrayList<Mutation>(), "TATATATATATATA", "101010110101011"));
	}

	public void after() {
		Viewer.getController().closeAllFiles();
	}

	@Test
	public void testClipboard() {
		//1
		controller.referenceSelectedPositions.addSelectedPosition(0);
		controller.referenceSelectedPositions.addSelectedPosition(9);
		controller.copyToClipboard(SelectionType.REFERENCE);
		assertEquals("TATATATAAT", paste());
//		2
		controller.consensusSelectedPositions.addSelectedPosition(0);
		controller.consensusSelectedPositions.addSelectedPosition(9);
		controller.copyToClipboard(SelectionType.CONSENSUS);
		assertEquals("TATATATATA", paste());

	}

	private String paste() {
		try {
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			return (String) clipboard.getData(DataFlavor.stringFlavor);
		} catch (UnsupportedFlavorException ex) {
			java.util.logging.Logger.getLogger(CopyToClipboardTest.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IOException ex) {
			java.util.logging.Logger.getLogger(CopyToClipboardTest.class.getName()).log(Level.SEVERE, null, ex);
		}
		return new String();
	}
}
