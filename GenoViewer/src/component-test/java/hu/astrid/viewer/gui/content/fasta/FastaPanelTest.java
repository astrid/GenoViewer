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

package hu.astrid.viewer.gui.content.fasta;

import hu.astrid.viewer.gui.content.fasta.FastaPanel;
import hu.astrid.io.FastaReader;
import hu.astrid.read.FastaRead;
import hu.astrid.componenttest.ParameterizedTestSuite;
import hu.astrid.viewer.Viewer;
import hu.astrid.viewer.ViewerController;
import hu.astrid.viewer.gui.DashBoard;
import hu.astrid.viewer.gui.MainWindow;
import hu.astrid.viewer.model.ViewerFastaModel;
import hu.astrid.viewer.model.ViewerReadModel;
import hu.astrid.viewer.properties.ProfileProperties;
import hu.astrid.viewer.properties.ProfileProperties.SequenceDisplayMode;
import hu.astrid.viewer.reader.FastaRandomReader.ContigPositionOutOfBoundsException;
import hu.astrid.viewer.reader.FastaRandomReader.FastaRandomReaderException;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import java.util.regex.Pattern;

import javax.swing.JScrollPane;

import org.apache.log4j.Logger;

import junit.extensions.abbot.ComponentTestFixture;
import junit.framework.Test;
import abbot.finder.ComponentNotFoundException;
import abbot.finder.MultipleComponentsFoundException;
import abbot.tester.ComponentTester;
import abbot.util.Condition;
import hu.astrid.mvc.swing.AbstractView;

/**
 *
 * @author Szuni
 */
public class FastaPanelTest extends ComponentTestFixture {

	private static final String RESOURCES_DIR = System.getProperty("viewer.testfiles.dir");
	private static final int STRING_MATCH_LENGTH = 10;
	private ViewerFastaModel modell;
	private ViewerController controller;
	private MainWindow mainWindow;
	private ComponentTester tester;
	private Frame frame;
	private File file;
	private FastaReader fastaReader;
	private FastaRead read;
	private FastaPanel fastaPanel;
	private static final int SCROLL_CHECK_REPEATS = 220;
	private static final int OPEN_TIMEOUT = 500;
	private int position;
	private int skipped;
	/** Default logger */
	private static final Logger logger = Logger.getLogger(FastaPanelTest.class);

	public FastaPanelTest(String fileName) {
		// file = new File(System.getProperty("java.io.tmpdir")+"/c1215.fasta");
		file = new File(fileName);
		Viewer.init();
	}

	public static Collection<Object[]> parameters() {
		File resourcesDir = new File(RESOURCES_DIR);
		String[] fastaFiles = resourcesDir.list(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".fasta");
			}
		});
		ArrayList<Object[]> list = new ArrayList<Object[]>();
		for (String fileName : fastaFiles) {
			list.add(new String[] { RESOURCES_DIR + "/" + fileName });
		}
		return list;
	}

	@Override
	public void setUp() throws ComponentNotFoundException, MultipleComponentsFoundException, FileNotFoundException, IOException {
		Viewer.initForTest(null);
		modell = Viewer.getFastaModel();
		final ViewerReadModel readModel = Viewer.getReadModel();
		controller = Viewer.getController();
		mainWindow = Viewer.getMainWindow();
		tester = new ComponentTester();
		// tester.setAutoDelay(200);
		for(AbstractView view : mainWindow.getAdditionalViews()) {
			if(view instanceof FastaPanel)
				fastaPanel = (FastaPanel)view;
		}
		fastaPanel.setPreferredSize(new Dimension(fastaPanel.getPreferredSize().width, 80));
		fastaPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		//
		// frame = showFrame(fastaPanel, new Dimension(800, 200));
		fastaReader = new FastaReader(new BufferedReader(new FileReader(file)));
		read = fastaReader.readNext();
		skipped = 0;
	}

	@Override
	protected void tearDown() throws Exception {
		controller.closeAllFiles();
		frame.dispose();
		fastaReader.close();
		modell = null;
		controller = null;
		mainWindow = null;
	}

	public void testScroll() throws MultipleComponentsFoundException, ComponentNotFoundException, IOException, ContigPositionOutOfBoundsException, FileNotFoundException, FastaRandomReaderException {
		logger.info("Test scrolling file " + file.getPath());
		openFile(file);
		fastaReader = new FastaReader(new BufferedReader(new FileReader(file)));
		read = fastaReader.readNext();
		Random random = new Random(System.currentTimeMillis());
		int contigLength = fastaPanel.getReferenceContigLength();
		if (fastaPanel.getReferenceContigLength() - fastaPanel.getDisplayableCharacters() < 0) {
			logger.info("OK. Scroll doesn't need");
			return;
		}
		logger.info("\tTest scrolling");
		// Scrolls along the contig or picks random positions
		for (int i = 0; i < SCROLL_CHECK_REPEATS; ++i) {
			if (i == contigLength)
				break;
			if (contigLength < SCROLL_CHECK_REPEATS) {
				position = i;
			} else {
				position = random.nextInt(contigLength);
			}
			fastaPanel.getViewport().setViewPosition(new Point(position * DashBoard.fontWidth, 0));
			tester.wait(new Condition() {

				public boolean test() {
					return !Viewer.getStatusBar().isBusy();
				}
			});
			if (fastaPanel.getHorizontalScrollBar().getValue() == 0 && position != 0) {
				logger.info("skip " + ++skipped);
				continue;
			}
			if (position < fastaPanel.getReferenceContigLength() - fastaPanel.getDisplayableCharacters())
				assertEquals("Scrollbar position invalid in " + file.getPath() + " ", position, fastaPanel.getHorizontalScrollBar().getValue() / DashBoard.fontWidth);
			matchStrings(position, read.toString(), fastaPanel.getDisplayedReferenceContig());
		}
		logger.info("OK");
	}

	public void testViewModes() throws ComponentNotFoundException, MultipleComponentsFoundException, IOException, FileNotFoundException, FastaRandomReaderException {
		logger.info("Test selecting view modes for " + file.getPath());
		ProfileProperties prof = Viewer.getPropertyHandler().getActiveProfile().makeCopy("prof");
		prof.setSequenceDisplayMode(SequenceDisplayMode.NUCLEOTIDE);
		Viewer.getPropertyHandler().addNewProfile(prof);
		// Viewer.getPropertyHandler().saveProfile( "prof" );
		Viewer.getPropertyHandler().setActiveProfile("prof");
		openFile(file);
		assertTrue(Pattern.matches("[ACGTN]+", fastaPanel.referenceContigNucleotideLabel.getText()));
		assertTrue(Pattern.matches("[01234]+", fastaPanel.referenceContigColorLabel.getText()));
		assertTrue("Nucleotide reference isnt shown", fastaPanel.referenceContigNucleotideLabel.isVisible());
		assertFalse("Color code reference shown", fastaPanel.referenceContigColorLabel.isVisible());
		prof.setSequenceDisplayMode(SequenceDisplayMode.COLOR);
		// Viewer.getPropertyHandler().saveProfile( "prof" );
		Viewer.getPropertyHandler().setActiveProfile("prof");
		tester.wait(new Condition() {

			@Override
			public boolean test() {
				return !mainWindow.getStatusBar().isBusy();
			}
		}, OPEN_TIMEOUT);
		assertTrue(Pattern.matches("[ACGTN]+", fastaPanel.referenceContigNucleotideLabel.getText()));
		assertTrue(Pattern.matches("[01234]+", fastaPanel.referenceContigColorLabel.getText()));
		assertFalse("Nucleotide reference shown", fastaPanel.referenceContigNucleotideLabel.isVisible());
		assertTrue("Color code reference isnt shown", fastaPanel.referenceContigColorLabel.isVisible());
		prof.setSequenceDisplayMode(SequenceDisplayMode.BOTH);
		// Viewer.getPropertyHandler().saveProfile( "prof" );
		Viewer.getPropertyHandler().setActiveProfile("prof");
		tester.wait(new Condition() {

			@Override
			public boolean test() {
				return !mainWindow.getStatusBar().isBusy();
			}
		}, OPEN_TIMEOUT);
		assertTrue(Pattern.matches("[ACGTN]+", fastaPanel.referenceContigNucleotideLabel.getText()));
		assertTrue(Pattern.matches("[01234]+", fastaPanel.referenceContigColorLabel.getText()));
		assertTrue("Nucleotide reference isnt shown", fastaPanel.referenceContigNucleotideLabel.isVisible());
		assertTrue("Color code reference isnt shown", fastaPanel.referenceContigColorLabel.isVisible());
		logger.info("OK");
	}

	// TODO scrollbar issue on teamcity
	private void matchStrings(int position, String expected, String actual) {
		if (position < fastaPanel.getReferenceContigLength() - fastaPanel.getDisplayableCharacters())
			assertEquals("Scrollbar position invalid in " + file.getPath() + " ", position, fastaPanel.getHorizontalScrollBar().getValue() / DashBoard.fontWidth);
		int j = position + fastaPanel.getDisplayableCharacters() - fastaPanel.getReferenceContigLength() + 1;
		expected = expected.substring(position);
		actual = actual.substring(j > 0 ? j : 0);
		int checkLength = Math.min(STRING_MATCH_LENGTH, Math.min(expected.length(), actual.length()));
		assertEquals("Displayed contig 0 from position " + (position + 1) + " doesn't match with the file " + file.getPath(), expected.substring(0, checkLength), actual.substring(0, checkLength));
	}

	public static Test suite() throws InvocationTargetException {
		return new ParameterizedTestSuite(FastaPanelTest.class, parameters());
	}

	private void openFile(File file) throws FileNotFoundException, IOException, FastaRandomReaderException {
		Viewer.startStatusbarJob(Viewer.getLabelResources().getString("statusbarMessageOpen"));
		modell.setReferenceLoaded(file);
		tester.wait(new Condition() {

			@Override
			public boolean test() {
				return !mainWindow.getStatusBar().isBusy();
			}
		});
		frame = showFrame(fastaPanel, new Dimension(800, 200));
	}
}
