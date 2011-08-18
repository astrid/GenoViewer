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

package hu.astrid.viewer.gui.content.alignment;

import hu.astrid.viewer.gui.content.alignment.AlignmentPanel;
import hu.astrid.viewer.gui.content.alignment.ReadLabel;
import hu.astrid.componenttest.ParameterizedTestSuite;
import hu.astrid.mapping.exception.IndexFileFormatException;
import hu.astrid.mapping.exception.MappingFileFormatException;
import hu.astrid.mapping.io.BamReader;
import hu.astrid.mapping.io.MappingFileReader;
import hu.astrid.mapping.io.SamReader;
import hu.astrid.mapping.model.AlignmentRecord;
import hu.astrid.viewer.Viewer;
import hu.astrid.viewer.ViewerController;
import hu.astrid.viewer.gui.DashBoard;
import hu.astrid.viewer.gui.MainWindow;
import hu.astrid.viewer.model.ViewerFastaModel;
import hu.astrid.viewer.model.ViewerReadModel;
import hu.astrid.viewer.properties.ProfileProperties;
import hu.astrid.viewer.properties.ProfileProperties.ReadDisplayType;
import hu.astrid.viewer.properties.ProfileProperties.SequenceDisplayMode;
import hu.astrid.viewer.util.Alignment;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;
import java.util.zip.DataFormatException;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import junit.extensions.abbot.ComponentTestFixture;

import org.apache.log4j.Logger;
import org.junit.Test;

import abbot.finder.ComponentNotFoundException;
import abbot.finder.ComponentSearchException;
import abbot.finder.MultipleComponentsFoundException;
import abbot.tester.ComponentTester;
import abbot.tester.DialogTester;
import abbot.util.Condition;
import hu.astrid.mvc.swing.AbstractView;

/**
 *
 * @author Szuni
 */
public class AlignmentPanelTest extends ComponentTestFixture {

	private static final String RESOURCES_DIR = System.getProperty("viewer.testfiles.dir");
	private ViewerFastaModel fastaModell;
	private ViewerReadModel readModel;
	private ViewerController controller;
	private MainWindow mainWindow;
	private ComponentTester tester;
	private DashBoard fastaView;
	private AlignmentPanel alignmentPanel;
	private final File file;
	private Frame frame;
	private MappingFileReader reader;
	private boolean isColorCodeAvailable = false;
	private static final int SCROLL_CHECK_REPEATS = 5;
	/** Default logger */
	private static final Logger logger = Logger.getLogger(AlignmentPanelTest.class);

	public AlignmentPanelTest(String fileName) {
		file = new File(fileName);
	}

	public static Collection<Object[]> parameters() {
		File resourcesDir = new File(RESOURCES_DIR);
		String[] fastaFiles = resourcesDir.list(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".sam") || name.endsWith(".bam");
			}
		});
		ArrayList<Object[]> list = new ArrayList<Object[]>();
		for (String fileName : fastaFiles) {
			list.add(new String[]{RESOURCES_DIR + "/" + fileName});
		}
		return list;
	}

	@Override
	public void setUp() throws ComponentNotFoundException, MultipleComponentsFoundException {
		Viewer.initForTest(null);
		fastaModell = Viewer.getFastaModel();
		readModel = Viewer.getReadModel();
		controller = Viewer.getController();
		mainWindow = Viewer.getMainWindow();
		tester = new ComponentTester();
		for (AbstractView view : mainWindow.getAdditionalViews()) {
			if (view instanceof AlignmentPanel) {
				alignmentPanel = (AlignmentPanel) view;
			}
		}
		alignmentPanel.setPreferredSize(new Dimension(alignmentPanel.getPreferredSize().width, 600));
		alignmentPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		frame = showFrame(alignmentPanel);
		new File("./Workspace/Profiles/").mkdirs();
	}

	@Override
	public void tearDown() {
		controller.closeAllFiles();
		frame.dispose();
		frame = null;
		tester = null;
		fastaView = null;
		alignmentPanel = null;
		fastaModell = null;
		readModel = null;
		controller = null;
		mainWindow = null;
	}

	// TODO hátteres betöltésnél tesztelni
	@Test
	public void testReadsLongView() throws ComponentNotFoundException, MultipleComponentsFoundException, IOException, MappingFileFormatException, IndexFileFormatException {
		logger.info("test long view mode " + file.getPath());
		ProfileProperties prof = Viewer.getPropertyHandler().getActiveProfile().makeCopy("prof");
		prof.setSequenceDisplayMode(SequenceDisplayMode.NUCLEOTIDE);
		prof.setReadDisplayType(ReadDisplayType.LONG);
		Viewer.getPropertyHandler().addNewProfile(prof);
		Viewer.getPropertyHandler().saveProfile("prof");
		Viewer.getPropertyHandler().setActiveProfile("prof");
		openFile(file);
		JPanel reads = alignmentPanel.getContentPanel();
		int i = 0;
		int oldY = 0;
		for (Component read : reads.getComponents()) {
			if (!(read instanceof JLabel)) {
				continue;
			}
			JLabel readLabel = (JLabel) read;
			assertTrue(i + ". component " + readLabel.getText() + " y: " + readLabel.getY() + " prev. y: " + oldY, readLabel.getY() >= oldY);
			i++;
			oldY = readLabel.getY();
		}
		logger.info("OK");
	}

	public void testReadsNucColView() throws ComponentNotFoundException, MultipleComponentsFoundException, IOException, MappingFileFormatException, IndexFileFormatException, ComponentSearchException {
		logger.info("test nucleotide and color view modes " + file.getPath());
		ProfileProperties prof = Viewer.getPropertyHandler().getActiveProfile().makeCopy("prof");
		prof.setSequenceDisplayMode(SequenceDisplayMode.NUCLEOTIDE);
		Viewer.getPropertyHandler().addNewProfile(prof);
		// Viewer.getPropertyHandler().saveProfile("prof");
		Viewer.getPropertyHandler().setActiveProfile("prof");
		openFile(file);
		DialogTester dialogTester = new DialogTester();
		JPanel reads = alignmentPanel.getContentPanel();
		// for (Component read : reads.getComponents()) {
		// if (!(read instanceof JLabel)) {
		// continue;
		// }
		// JLabel readLabel = (JLabel) read;
		// Pattern pattern = Pattern.compile("[ACGT]+");
		// java.util.regex.Matcher matcher =
		// pattern.matcher(readLabel.getText());
		// assertTrue(matcher.find());
		// }
		//
		// DashBoard.sequenceShowMode = DashBoard.COLOR_MODE;
		// Viewer.startStatusbarJob(null);
		// alignmentPanel.repaintContent();
		prof.setSequenceDisplayMode(SequenceDisplayMode.COLOR);
		// Viewer.getPropertyHandler().saveProfile("prof");

		Viewer.getPropertyHandler().setActiveProfile("prof");
		isColorCodeAvailable = reader.nextRecord().getOptionalTag("CS") != null;
		waitForIdle();
		if (isColorCodeAvailable) {
			for (Component read : reads.getComponents()) {
				if (!(read instanceof JLabel)) {
					continue;
				}
				JLabel readLabel = (JLabel) read;
				assertTrue(readLabel.getText() + " isnt colorcode", Pattern.matches("[0123-]+", readLabel.getText()));
			}
		} else {
//			tester.waitForFrameShowing(getHierarchy(), Viewer.getLabelResources().getString("dialogTitleWarning"));
		}

		prof.setSequenceDisplayMode(SequenceDisplayMode.BOTH);
		Viewer.getPropertyHandler().setActiveProfile("prof");
		// Viewer.getPropertyHandler().saveProfile("prof");
		boolean color = false;
		if (!isColorCodeAvailable) {
//			tester.waitForFrameShowing(getHierarchy(), Viewer.getLabelResources().getString("dialogTitleWarning"));
		}
		waitForIdle();

		for (Component read : reads.getComponents()) {
			if (!(read instanceof JLabel)) {
				continue;
			}
			JLabel readLabel = (JLabel) read;
			String regex;
			if (isColorCodeAvailable && color) {
				regex = "[0123-]+";
				color = false;
			} else {
				regex = "[ACGT-]+";
				color = true;
			}
			assertTrue(readLabel.getText() + " isnt " + (isColorCodeAvailable && color ? "colorcode" : "nucleotide"), Pattern.matches(regex, readLabel.getText()));
		}
		logger.info("OK");
	}

	// TODO hátteres betöltést belevenni és engedélyezni
	public void estScroll() throws MultipleComponentsFoundException, ComponentNotFoundException, IOException, DataFormatException, MappingFileFormatException, IndexFileFormatException {
		logger.info("test scroll " + file.getPath());
		ProfileProperties prof = Viewer.getPropertyHandler().getActiveProfile().makeCopy("prof");
		prof.setSequenceDisplayMode(SequenceDisplayMode.NUCLEOTIDE);
		Viewer.getPropertyHandler().addNewProfile(prof);
		Viewer.getPropertyHandler().saveProfile("prof");
		Viewer.getPropertyHandler().setActiveProfile("prof");
		if (!file.getName().endsWith(".bam")) {
			logger.info("OK scroll doesnt need");
			return;
		}
		openFile(file);
		if (readModel.isWholeFileLoaded()) {
			return;
		}
		Random random = new Random(System.currentTimeMillis());
		for (int i = 0; i < SCROLL_CHECK_REPEATS; ++i) {
			ArrayList<Component> list = new ArrayList<Component>();
			list.addAll(Arrays.asList(alignmentPanel.getContentPanel().getComponents()));
			list.remove(list.size() - 1);
			// Test for every read to be loaded, are they realy put on the panel
			// and no others
			for (AlignmentRecord read : readModel.loadReads(getLoadIntervalStart(), getLoadIntervalStart() + Viewer.getApplicationProperties().getBufferSize())) {
				assertTrue("Read doesnt displayed " + read.getQueryName() + " " + read.getSequence(), searchRead(read, list));
			}
			assertTrue("Unnecessary read displayed " + list.toArray(), list.isEmpty());
			fastaView.scrollToPosition(random.nextInt(Viewer.getController().getAlignmentReferenceLength()));
		}
		logger.info("OK");
	}

	public static junit.framework.Test suite() throws InvocationTargetException {
		return new ParameterizedTestSuite(AlignmentPanelTest.class, parameters());
	}

	private void openFile(File file) throws IOException, MappingFileFormatException, IndexFileFormatException {
		Viewer.startStatusbarJob(Viewer.getLabelResources().getString("statusbarMessageOpen"));
		if (file.getName().toLowerCase().endsWith(".sam")) {
			readModel.loadSamFile(file);
			reader = new SamReader(file);
		} else {
			readModel.loadBamFile(file);
			reader = new BamReader(new FileInputStream(file));
		}
		waitForIdle();
	}

	private void waitForIdle() {
		tester.wait(new Condition() {

			@Override
			public boolean test() {
				return !mainWindow.getStatusBar().isBusy();
			}
		});
	}

	private int getScreenPosition() {
		return alignmentPanel.getHorizontalScrollBar().getValue() / DashBoard.fontWidth;
	}

	private int getLoadIntervalStart() {
		return getScreenPosition() - Viewer.getApplicationProperties().getBufferSize() / 2 > -1 ? getScreenPosition() - Viewer.getApplicationProperties().getBufferSize() / 2 : 0;
	}

	private boolean searchRead(AlignmentRecord read, List<Component> list) {
		Iterator<Component> it = list.iterator();
		while (it.hasNext()) {
			Component c = it.next();
			ReadLabel r = (ReadLabel) c;
			String displaySeq = r.getText().replaceAll("-*", "");
			String recordSeq = read.getSequence();
			List<Character> operators = Alignment.parseOperators(read.getCigar());
			List<Integer> sizes = Alignment.parseSizes(read.getCigar());
			int j = 0, size = 0;
			int index = 0;
			for (int i = 0; i < operators.size(); ++i) {
				if (operators.get(i) == 'I' || operators.get(i) == 'S') {
					recordSeq = recordSeq.substring(0, index - size) + recordSeq.substring(index - size + sizes.get(i));
					size += sizes.get(i);
				}
				index += sizes.get(i);
			}
			if (recordSeq.equals(displaySeq)) {
				it.remove();
				return true;
			}
		}
		return false;
	}
}
