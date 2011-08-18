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

import hu.astrid.contig.Contig;
import hu.astrid.core.Nucleotide;
import hu.astrid.mapping.exception.GffFileFormatException;
import hu.astrid.mapping.exception.IndexFileFormatException;
import hu.astrid.mapping.exception.MappingFileFormatException;
import hu.astrid.mapping.model.AlignmentRecord;
import hu.astrid.mapping.model.GffRecord;
import hu.astrid.mvc.swing.AbstractController;
import hu.astrid.mvc.swing.AbstractModel;
import hu.astrid.mvc.swing.AbstractView;
import hu.astrid.utility.SequenceConverter;
import hu.astrid.viewer.model.alignment.ReadData;
import hu.astrid.viewer.model.alignment.ReadData.PositionMutation;
import hu.astrid.viewer.model.consensus.ConsensusData;
import hu.astrid.viewer.gui.content.consensus.ConsensusPanel;
import hu.astrid.viewer.gui.content.alignment.AlignmentPanel;
import hu.astrid.viewer.gui.content.fasta.FastaPanel;
import hu.astrid.viewer.gui.workspace.WorkspacePanel;
import hu.astrid.viewer.model.CoverageModel;
import hu.astrid.viewer.model.Project;
import hu.astrid.viewer.model.SelectionModel;
import hu.astrid.viewer.model.SelectionModel.SelectedPosition;
import hu.astrid.viewer.model.SelectionModel.SelectionType;
import hu.astrid.viewer.model.alignment.Interval;
import hu.astrid.viewer.model.ViewerConsensusModel;
import hu.astrid.viewer.model.ViewerFastaModel;
import hu.astrid.viewer.model.ViewerGffModel;
import hu.astrid.viewer.model.ViewerReadModel;
import hu.astrid.viewer.model.WorkspaceModel;
import hu.astrid.viewer.model.mutation.Mutation;
import hu.astrid.viewer.model.mutation.MutationType;
import hu.astrid.viewer.reader.FastaRandomReader.FastaRandomReaderException;
import hu.astrid.viewer.util.FileTypes;
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.SwingWorker;

import org.apache.log4j.Logger;

/**
 * Fasta viewer controller.
 * 
 * @author Szuni, Máté
 */
public class ViewerController extends AbstractController {

	/**Actual profile settings changed*/
	public static final String VIEWER_PROFILE_PROPERTY = "ViewerProfile";
	/**Consensus save property*/
	public static final String CONSENSUS_SAVE = "consensusSave";
	/**Clipboard boundary property*/
	private static final String CLIPBOARD_BOUNDARY = "ClipboardBoundary";
	/**Line width for consensus save*/
	private static final int LINE_WIDTH = 70;
	private static final Logger logger = Logger.getLogger(ViewerController.class);
	/** ReadDataList */
	private final List<ReadData> readsDataList = new ArrayList<ReadData>();
	/** Maximum length of a read */
	private int maxReadLength = 0;
	private boolean isReadDataLoaded;
	/** Number of file opnening(s) in progress*/
	private int openProgressCount = 0;
	/**Stores reference's selected positions*/
	protected SelectedPosition referenceSelectedPositions = new SelectedPosition();
	/**Stores consensus's selected positions*/
	protected SelectedPosition consensusSelectedPositions = new SelectedPosition();

	/*********
	 * FASTA *
	 *********/
	/**
	 * Open a fasta file. Notify recent file handler
	 * 
	 * @param file
	 *            Fasta file
	 * @throws FileNotFoundException 
	 * @throws IOException
	 * @throws FastaRandomReaderException
	 * @see ViewerFastaModel#setReferenceLoaded(java.io.File)
	 */
	public void openFastaFile(File file) throws FileNotFoundException,
			IOException, FastaRandomReaderException {
		for (AbstractModel modell : registeredModels) {
			if (modell instanceof ViewerFastaModel) {
				ViewerFastaModel fastaModell = (ViewerFastaModel) modell;
				fastaModell.setReferenceLoaded(file);
				Viewer.getApplicationProperties().notifyFileOpened(file, FileTypes.FASTA);
				return;
			}
		}
	}

	/**
	 * Return names of the contigs in fasta
	 * file.
	 * 
	 * @return
	 * @see ViewerFastaModel#getContigNames()
	 */
	public List<String> getContigNames() {
		for (AbstractModel modell : registeredModels) {
			if (modell instanceof ViewerFastaModel) {
				ViewerFastaModel fastaModell = (ViewerFastaModel) modell;
				return fastaModell.getContigNames();
			}
		}
		return null;
	}

	/**
	 * Return the actual contig length.
	 * 
	 * @return
	 * @see ViewerFastaModel#getContigLength()
	 */
	public int getContigLength() {
		for (AbstractModel modell : registeredModels) {
			if (modell instanceof ViewerFastaModel) {
				ViewerFastaModel fastaModell = (ViewerFastaModel) modell;
				return fastaModell.getContigLength();
			}
		}
		return -1;
	}

	/**
	 * Olvasás a fájlból, az aktuálisan kezelt kontig megadott nukleotid
	 * pozíciójáról és megjeleníti a nézetben. Ha komoly hiba miatt nem tud
	 * olvasni, lezárja a fájlt.
	 * 
	 * @param position
	 *            kezdőpozíció amelyról olvasni szeretnénk
	 * @throws IOException
	 * @throws hu.astrid.viewer.reader.FastaRandomReader.FastaRandomReaderException
	 * @see ViewerFastaModel#readFromPosition(int)
	 */
	public void readFromPosition(int position) throws IOException, FastaRandomReaderException {
		for (AbstractModel modell : registeredModels) {
			if (modell instanceof ViewerFastaModel) {
				ViewerFastaModel fastaModell = (ViewerFastaModel) modell;
				Contig<Nucleotide> contig = fastaModell.readFromPosition(position);
				for (AbstractView view : registeredViews) {
					if (view instanceof FastaPanel) {
						FastaPanel fastaPanel = (FastaPanel) view;
						fastaPanel.showReferenceContig(position, contig.toString(), SequenceConverter.convert(contig).toString());
					}
				}
				return;
			}
		}
	}

	/**
	 * Return true if Fasta file is opened.
	 * 
	 * @return
	 * @see ViewerFastaModel#isFastaFileOpened()
	 */
	public boolean isFastaFileOpened() {
		for (AbstractModel modell : registeredModels) {
			if (modell instanceof ViewerFastaModel) {
				ViewerFastaModel fastaModell = (ViewerFastaModel) modell;
				return fastaModell.isFastaFileOpened();
			}
		}
		return false;
	}

	/**
	 * @return
	 * @see ViewerFastaModel#getFileName()
	 */
	public String getFastaFileName() {
		for (AbstractModel modell : registeredModels) {
			if (modell instanceof ViewerFastaModel) {
				ViewerFastaModel fastaModell = (ViewerFastaModel) modell;
				return fastaModell.getFileName();
			}
		}
		return null;
	}

	/********
	 * READ *
	 ********/
	/**
	 * Load a SAM file.
	 * 
	 * @param file
	 *            SAM file
	 * @throws IOException
	 * @throws MappingFileFormatException
	 * @see ViewerReadModel#loadSamFile(java.io.File)
	 */
	public void loadSamFile(File file) throws IOException,
			MappingFileFormatException {
		synchronized (readsDataList) {
			readsDataList.clear();
		}
		maxReadLength = 0;
		isReadDataLoaded = false;
		for (AbstractModel modell : registeredModels) {
			if (modell instanceof ViewerReadModel) {
				ViewerReadModel readModell = (ViewerReadModel) modell;
				readModell.loadSamFile(file);
				Viewer.getApplicationProperties().notifyFileOpened(file, FileTypes.SAM);
				return;
			}
		}
	}

	/**
	 * Returns true, if there are reads loaded from SAM file or BAM file opened
	 * 
	 * @return
	 * @see ViewerReadModel#isReadsLoaded()
	 */
	public boolean isReadsLoaded() {
		for (AbstractModel modell : registeredModels) {
			if (modell instanceof ViewerReadModel) {
				ViewerReadModel readModell = (ViewerReadModel) modell;
				return readModell.isReadsLoaded();
			}
		}
		return false;
	}

	/**
	 * Load a BAM file.
	 * 
	 * @param file
	 *            BAM file
	 * @throws IOException
	 * @throws IndexFileFormatException
	 * @throws MappingFileFormatException
	 * @see ViewerReadModel#loadBamFile(java.io.File)
	 */
	public void loadBamFile(File file) throws IOException,
			IndexFileFormatException, MappingFileFormatException {
		synchronized (readsDataList) {
			readsDataList.clear();
		}
		maxReadLength = 0;
		isReadDataLoaded = false;
		for (AbstractModel modell : registeredModels) {
			if (modell instanceof ViewerReadModel) {
				ViewerReadModel readModell = (ViewerReadModel) modell;
				readModell.loadBamFile(file);
				readModell.isSorted();
				Viewer.getApplicationProperties().notifyFileOpened(file, FileTypes.BAM);
				return;
			}
		}
	}

	/**
	 * Displays reads from read file, where reads start indicies are in an
	 * interval. Records are stored. If SAM file used, or every records loaded
	 * from BAM file, just displays the list, loading doesnt happen.
	 * 
	 * @param start
	 *            interval start index, in case of SAM doesnt matter
	 * @param end
	 *            interval end index, in case of SAM doesnt matter
	 * @throws IOException
	 * @throws MappingFileFormatException
	 * @see ViewerReadModel#loadReads(int, int)
	 */
	public void loadReads(int start, int end) throws IOException,
			MappingFileFormatException {
		synchronized (readsDataList) {
			if (readsDataList.isEmpty() || !isReadDataLoaded) {
				for (AbstractModel modell : registeredModels) {
					if (modell instanceof ViewerReadModel) {
						ViewerReadModel readModell = (ViewerReadModel) modell;
						if (readModell.isWholeFileLoaded()) {
							isReadDataLoaded = true;
							end = readModell.getActReferenceLength();
						}
						readsDataList.clear();
						List<AlignmentRecord> readList = readModell.loadReads(start, end);
						for (AlignmentRecord read : readList) {
							if (maxReadLength < read.getSequence().length()) {
								maxReadLength = read.getSequence().length();
							}
							ReadData rdm = new ReadData(read);
							readsDataList.add(rdm);
						}
						break;
					}
				}
			}
			for (AbstractView view : registeredViews) {
				if (view instanceof AlignmentPanel) {
					AlignmentPanel alignmentPanel = (AlignmentPanel) view;
					alignmentPanel.showReads(readsDataList, new Interval(
							start, end));
				}
			}
			logger.trace("read load done");
		}
	}

	/**
	 * //TODO
	 * @param start
	 *            start position in records collection
	 * @param end
	 *            end position in records collection
	 * @return list of read datas
	 * @see ViewerReadModel#getPrelodedReads(int, int)
	 */
	public List<ReadData> getPreloadedReads(int start, int end) {
		List<ReadData> preloadedReadsDataList = new ArrayList<ReadData>();
		for (AbstractModel modell : registeredModels) {
			if (modell instanceof ViewerReadModel) {
				ViewerReadModel readModell = (ViewerReadModel) modell;
				logger.trace("preloaded interval " + start + " " + end);
				List<AlignmentRecord> readList = readModell.getPrelodedReads(
						start, end);
				for (AlignmentRecord read : readList) {
					if (maxReadLength < read.getSequence().length()) {
						maxReadLength = read.getSequence().length();
					}
					ReadData rdm = new ReadData(read);
					preloadedReadsDataList.add(rdm);
				}
				break;
			}
		}
		synchronized (readsDataList) {
			readsDataList.addAll(preloadedReadsDataList);
//			Collections.sort(readsDataList, new Comparator<ReadData>() {
//
//				@Override
//				public int compare(ReadData o1, ReadData o2) {
//					if (o1.getPosition() == o2.getPosition()) {
//						return 0;
//					} else if (o1.getPosition() < o2.getPosition()) {
//						return -1;
//					} else {
//						return 1;
//					}
//				}
//			});
		}
		logger.trace("preloaded interval " + start + " " + end + " stored");
		return preloadedReadsDataList;
	}

	/**
	 * Determine that the displayed interval is full loaded in the model
	 * 
	 * @see ViewerReadModel
	 * @param displayPosition
	 *            start position on display
	 * @param displayWidth
	 *            width of interval
	 * @return {@code true} - if the interval is loaded in the model and can be
	 *         queried<br> {@code false} - if there are missing reads from interval
	 *         in the model
	 * @see ViewerReadModel#isIntervalLoaded(int, int)
	 */
	public boolean isIntervalLoaded(int displayPosition, int displayWidth) {
		logger.trace("check loaded interval from " + displayPosition + " with length " + displayWidth);
		for (AbstractModel modell : registeredModels) {
			if (modell instanceof ViewerReadModel) {
				ViewerReadModel viewerReadModel = (ViewerReadModel) modell;
				int startPos = Math.max(0, displayPosition - maxReadLength);
				int endPos = Math.min(displayPosition + displayWidth,
						viewerReadModel.getActReferenceLength());
				if (startPos > endPos) {
					return false;
				}
				return viewerReadModel.isIntervalLoaded(startPos, endPos);
			}
		}
		return false;
	}

	/**
	 * Notify preloader to do preload if its necessary.
	 * @param interval last displayed interval or {@code null} if no change happened
	 */
	public void notifyPreloader(Interval interval) {
		for (AbstractModel modell : registeredModels) {
			if (modell instanceof ViewerReadModel) {
				ViewerReadModel viewerReadModel = (ViewerReadModel) modell;
				viewerReadModel.notifyPreloader(interval);
			}
		}
	}

	/**
	 * //TODO
	 * @param start
	 * @param end
	 * @return
	 * @throws IOException
	 * @throws MappingFileFormatException
	 * @see ViewerReadModel#loadReadsWithoutStore(int, int)
	 */
	public List<AlignmentRecord> loadReadsForInDel(int start, int end)
			throws IOException, MappingFileFormatException {
		for (AbstractModel modell : registeredModels) {
			if (modell instanceof ViewerReadModel) {
				ViewerReadModel readModell = (ViewerReadModel) modell;
				return (readModell.loadReadsWithoutStore(start - maxReadLength,
						end));
			}
		}
		return null;
	}

	/**
	 * @return
	 * @see ViewerReadModel#getLastReadEndPos()
	 */
	public int lastReadEndPos() {
		for (AbstractModel modell : registeredModels) {
			if (modell instanceof ViewerReadModel) {
				ViewerReadModel viewerReadModel = (ViewerReadModel) modell;
				return viewerReadModel.getLastReadEndPos();
			}
		}
		return 0;
	}

	/**
	 * //TODO
	 * @param start
	 * @param end
	 * @return
	 * @throws MappingFileFormatException
	 * @throws IOException
	 */
	public int getNumberOfReadsByPosition(int start, int end) throws MappingFileFormatException, IOException {
		for (AbstractModel modell : registeredModels) {
			if (modell instanceof ViewerReadModel) {
				ViewerReadModel viewerReadModel = (ViewerReadModel) modell;
				return viewerReadModel.getNumberOfReadsByPosition(start, end);
			}
		}
		return 0;
	}

	/**
	 * //TODO
	 * @param start
	 * @param length
	 * @return
	 * @throws MappingFileFormatException
	 * @throws IOException
	 */
	public String getSubSequenceByTransformationPosition(int start, int length) throws MappingFileFormatException, IOException {
		for (AbstractModel modell : registeredModels) {
			if (modell instanceof ViewerReadModel) {
				ViewerReadModel viewerReadModel = (ViewerReadModel) modell;
				return viewerReadModel.getSubSequenceByTransformationPosition(start, length);
			}
		}
		return "";
	}

	/**
	 * @return Reference names contained in the BAM file
	 * @see ViewerReadModel
	 */
	public List<String> getRefNames() {
		for (AbstractModel modell : registeredModels) {
			if (modell instanceof ViewerReadModel) {
				ViewerReadModel fastaModell = (ViewerReadModel) modell;
				return fastaModell.getRefNames();
			}
		}
		return null;
	}

	/**
	 * Reference length, from reads are loaded from in BAM file
	 * 
	 * @return
	 * @see ViewerReadModel
	 */
	public Integer getAlignmentReferenceLength() {
		for (AbstractModel modell : registeredModels) {
			if (modell instanceof ViewerReadModel) {
				ViewerReadModel readModell = (ViewerReadModel) modell;
				return readModell.getActReferenceLength();
			}
		}
		return null;
	}

	/**
	 * @return maximum length of a read
	 */
	public int getMaxReadLength() {
		return maxReadLength;
	}

	/**
	 * //TODO
	 * @param start
	 * @param end
	 * @return
	 * @throws IOException
	 * @throws MappingFileFormatException
	 */
	public Map<Integer, Integer> loadSnpCoverage(int start, int end)
			throws IOException, MappingFileFormatException {
		// Map<Integer, Integer> coverageMap=new LinkedHashMap<Integer,
		// Integer>();
		if (isWholeFileLoaded()) {
			ArrayList<ReadData> list = new ArrayList<ReadData>();
			synchronized (readsDataList) {
				for (ReadData read : readsDataList) {
					if (read.getPosition() >= start && read.getPosition() < end) {
						list.add(read);
					}
				}
			}
			return loadSnpCoverageMap(list);
		}

		if (start < 0) {
			start = 0;
		}
		if (end < 0) {
			end = 0;
		}
		List<ReadData> readsList = new ArrayList<ReadData>();
		for (AbstractModel modell : registeredModels) {
			if (modell instanceof ViewerReadModel) {
				ViewerReadModel readModell = (ViewerReadModel) modell;
				for (Iterator<AlignmentRecord> it = readModell.loadReadsWithoutStore(start, end).iterator(); it.hasNext();) {
					AlignmentRecord read = it.next();
					ReadData rdm = new ReadData(read);
					readsList.add(rdm);
				}
			}
		}
		logger.debug(readsList.size());
		return loadSnpCoverageMap(readsList);
	}

	/**
	 * //TODO
	 * @param readsList
	 * @return
	 */
	private Map<Integer, Integer> loadSnpCoverageMap(List<ReadData> readsList) {
		Map<Integer, Integer> coverageMap = new LinkedHashMap<Integer, Integer>();
		Map<Integer, Integer> sortedCoverageMap = new LinkedHashMap<Integer, Integer>();
		for (ReadData readData : readsList) {
			for (PositionMutation snp : readData.getSnpList()) {
				int key = readData.getPosition() + snp.getPosition();
				if (coverageMap.containsKey(key)) {
					int count = coverageMap.get(key);
					coverageMap.put(key, ++count);
				} else {
					coverageMap.put(key, 1);
				}
			}
		}
		// Sort Map
		// System.out.println(coverageMap);
		ArrayList<Integer> keys = new ArrayList<Integer>();
		for (Integer key : coverageMap.keySet()) {
			keys.add(key);
		}
		Collections.sort(keys);
		for (Integer key : keys) {
			// snpCoverageMap.put(key, coverageMap.get(key));
			sortedCoverageMap.put(key, coverageMap.get(key));
		}
		// System.out.println("cover: " + coverageMap);
		return sortedCoverageMap;
	}

	/**
	 * @return
	 * @see ViewerReadModel#getFileName() 
	 */
	public String getReadFileName() {
		for (AbstractModel modell : registeredModels) {
			if (modell instanceof ViewerReadModel) {
				ViewerReadModel readModell = (ViewerReadModel) modell;
				return readModell.getFileName();
			}
		}
		return null;
	}

	/**
	 *@param id index in collection
	 * @return read from model in specified index
	 * @see ViewerReadModel#getReadById(int)
	 */
	public AlignmentRecord getReadById(int id) {
		for (AbstractModel modell : registeredModels) {
			if (modell instanceof ViewerReadModel) {
				ViewerReadModel readModell = (ViewerReadModel) modell;
				return readModell.getReadById(id);
			}
		}
		return null;
	}

	/**
	 * Is every reads loaded, or indexed load needed
	 * 
	 * @return
	 * @see ViewerReadModel#isWholeFileLoaded()
	 */
	public boolean isWholeFileLoaded() {
		for (AbstractModel modell : registeredModels) {
			if (modell instanceof ViewerReadModel) {
				ViewerReadModel readModell = (ViewerReadModel) modell;
				return readModell.isWholeFileLoaded();
			}
		}
		return false;
	}

	/**
	 * Get a part of reference contig, used for {@see hu.astrid.viewer.util.Alignment}
	 * @param position
	 * @param refLength
	 * @return
	 * @see ViewerFastaModel#getRefrenceAlignmentString(int, int)
	 */
	public String getRefrenceAlignmentString(int position, int refLength) {
		for (AbstractModel modell : registeredModels) {
			if (modell instanceof ViewerFastaModel) {
				ViewerFastaModel fastaModell = (ViewerFastaModel) modell;
				return fastaModell.getRefrenceAlignmentString(position,
						refLength);
			}
		}
		return null;
	}

	/**
	 * Get evaluated reads datas. For consistency before return, the read datas and alignment record in model are sorted.
	 * @return list az read datas
	 */
	public List<ReadData> getReadsDataList() {
		ArrayList<ReadData> copy;
		ViewerReadModel readModell = null;
		for (AbstractModel modell : registeredModels) {
			if (modell instanceof ViewerReadModel) {
				readModell = (ViewerReadModel) modell;
			}
		}
		synchronized (readsDataList) {
			readModell.sortAlignmentRecords();
			Collections.sort(readsDataList, new Comparator<ReadData>() {

				@Override
				public int compare(ReadData o1, ReadData o2) {
					if (o1.getPosition() == o2.getPosition()) {
						return 0;
					} else if (o1.getPosition() < o2.getPosition()) {
						return -1;
					} else {
						return 1;
					}
				}
			});
			copy = new ArrayList<ReadData>(readsDataList);
		}
		return copy;
	}

	/*******
	 * GFF *
	 *******/
	/**
	 * Load a GFF annotation file and build annotation hierarchy, notify recent file handler
	 * 
	 * @param file
	 *            GFF file
	 * @throws IOException
	 * @throws GffFileFormatException
	 */
	public void loadAnnotations(File file) throws IOException,
			GffFileFormatException {
		for (AbstractModel modell : registeredModels) {
			if (modell instanceof ViewerGffModel) {
				ViewerGffModel gffModell = (ViewerGffModel) modell;
				gffModell.setAnnotationsLoaded(file);
				Viewer.getApplicationProperties().notifyFileOpened(file, FileTypes.GFF);
			}
		}
	}

	/**
	 * @return Annotation hierarchy. In the map every key represents a feature type,
	 * the values are sets of representative records of specific type.
	 * @see ViewerGffModel#getAnnotations()
	 */
	public Map<String, Set<GffRecord>> getAnnotations() {
		for (AbstractModel modell : registeredModels) {
			if (modell instanceof ViewerGffModel) {
				ViewerGffModel gffModell = (ViewerGffModel) modell;
				return gffModell.getAnnotations();
			}
		}
		return null;
	}

	/**
	 * @return name of GFF file where annotations loaded from
	 * @see ViewerGffModel#getFileName()
	 */
	public String getGffFileName() {
		for (AbstractModel modell : registeredModels) {
			if (modell instanceof ViewerGffModel) {
				ViewerGffModel gffModell = (ViewerGffModel) modell;
				return gffModell.getFileName();
			}
		}
		return null;
	}

	/**
	 * @return last annotated position
	 * @see ViewerGffModel#getMaxLengthOfAnnotations()
	 */
	public int getMaxLengthOfAnnotation() {
		for (AbstractModel modell : registeredModels) {
			if (modell instanceof ViewerGffModel) {
				ViewerGffModel gffModell = (ViewerGffModel) modell;
				return gffModell.getMaxLengthOfAnnotations();
			}
		}
		return 0;
	}

	/**
	 * @return true if there are annotations loaded from GFF file
	 * @see ViewerGffModel#isAnnotationsLoaded()
	 */
	public boolean isAnnotationsLoaded() {
		for (AbstractModel modell : registeredModels) {
			if (modell instanceof ViewerGffModel) {
				ViewerGffModel gffModell = (ViewerGffModel) modell;
				return gffModell.isAnnotationsLoaded();
			}
		}
		return false;
	}

	/**
	 * @return annotations types contained in current file
	 * @see ViewerGffModel#getAnnotationTypes()
	 */
	public Set<String> getAnnotationTypes() {
		for (AbstractModel modell : registeredModels) {
			if (modell instanceof ViewerGffModel) {
				ViewerGffModel gffModell = (ViewerGffModel) modell;
				return gffModell.getAnnotationTypes();
			}
		}
		return null;
	}
	private final Set<String> defaultGroups = new HashSet<String>(Arrays.asList(new String[]{"gene", "ORF", "tRNA", "snRNA", "ncRNA", "snoRNA", "rRNA", "pseudogene"}));
	private Set<String> groupsWithoutProject = defaultGroups;

	/**
	 * Get annotation types represented as group stored in actual project, or a new
	 * set with previosly recognized groups if no project open.
	 * @return
	 * @see WorkspaceModel#getGroups() 
	 */
	public Set<String> getAnnotationGroups() {
		for (AbstractModel model : registeredModels) {
			if (model instanceof WorkspaceModel) {
				WorkspaceModel workspaceModel = ((WorkspaceModel) model);
				if (workspaceModel.getActProject() > -1) {
					return ((WorkspaceModel) model).getGroups();
				}
			}
		}
		return groupsWithoutProject;
	}
	private Map<String, Boolean> visibilityWithoutProject = null;

	/**
	 * Get visibility data for annotations, or last known visibility settings if no opened project specified
	 * @return
	 * @see WorkspaceModel#getAnnotationsVisibility() 
	 */
	public Map<String, Boolean> getAnnotationsVisibility() {
		for (AbstractModel model : registeredModels) {
			if (model instanceof WorkspaceModel) {
				WorkspaceModel workspaceModel = ((WorkspaceModel) model);
				if (workspaceModel.getActProject() > -1) {
					return workspaceModel.getAnnotationsVisibility();
				}
			}
		}
		return visibilityWithoutProject;
	}

	/**
	 * Set set of annotations to be group
	 * @param groups annotations to be interpreted as group
	 * @see WorkspaceModel#setGroups(java.util.Set) 
	 */
	public void setAnnotationGroups(Set<String> groups) {
		for (AbstractModel model : registeredModels) {
			if (model instanceof WorkspaceModel) {
				WorkspaceModel workspaceModel = ((WorkspaceModel) model);
				if (workspaceModel.getActProject() > -1) {
					((WorkspaceModel) model).setGroups(groups);
					return;
				}
			}
		}
		groupsWithoutProject = groups;
	}

	/**
	 * Set annotation's associtated visibility
	 * @param annotationsVisibility key as annotation type, value if annotation is visible or not
	 * @see  WorkspaceModel#setAnnotationsVisibility(java.util.Map) 
	 */
	public void setAnnotationsVisibility(Map<String, Boolean> annotationsVisibility) {
		for (AbstractModel model : registeredModels) {
			if (model instanceof WorkspaceModel) {
				WorkspaceModel workspaceModel = ((WorkspaceModel) model);
				if (workspaceModel.getActProject() > -1) {
					((WorkspaceModel) model).setAnnotationsVisibility(annotationsVisibility);
					return;
				}
			}
		}
		visibilityWithoutProject = annotationsVisibility;
	}

	/**
	 * Get {@link Color Color} of the specified annotation, or null if there is no active workspace
	 * @param annotation annotation type
	 * @return the color associated to annotation type
	 * @see WorkspaceModel#getAnnotationColor(java.lang.String) 
	 */
	public Color getAnnottationColor(String annotation) {
		for (AbstractModel model : registeredModels) {
			if (model instanceof WorkspaceModel) {
				WorkspaceModel workspaceModel = ((WorkspaceModel) model);
				if (workspaceModel.getActProject() > -1) {
					return ((WorkspaceModel) model).getAnnotationColor(annotation);
				}
			}
		}
		return colorMap.containsKey(annotation) ? colorMap.get(annotation) : null;
	}

	/**
	 * Set the annotation's color
	 * @param key the annotation which color to be changed
	 * @param color the new {@link Color Color}
	 * @see WorkspaceModel#setAnnotationColor(java.lang.String, java.awt.Color) 
	 */
	public void setAnnotationColor(String key, Color color) {
		for (AbstractModel model : registeredModels) {
			if (model instanceof WorkspaceModel) {
				WorkspaceModel workspaceModel = ((WorkspaceModel) model);
				if (workspaceModel.getActProject() > -1) {
					((WorkspaceModel) model).setAnnotationColor(key, color);
					return ;
				}
			}
		}
		colorMap.put(key, color);
	}
	
	private Map<String, Color> colorMap = new HashMap<String, Color>();

	/**
	 * Set all specified annotation's color to the assigned {@link Color Color}
	 * @param colorMap the map containing information about annotations and associated colors
	 * @see WorkspaceModel#setAnnotationColors(java.util.Map) 
	 */
	public void setAnnotationColors(Map<String, Color> colorMap) {
		for (AbstractModel model : registeredModels) {
			if (model instanceof WorkspaceModel) {
				WorkspaceModel workspaceModel = ((WorkspaceModel) model);
				if (workspaceModel.getActProject() > -1) {
					((WorkspaceModel) model).setAnnotationColors(colorMap);
					return;
				}
			}
		}
		this.colorMap = colorMap;
	}

	/**
	 * Get annotations' color information
	 * @return {@link Map Map<String, Color>>} of annotations and associated colors, or null if there is no active workspace
	 * @see WorkspaceModel#getAnnotationColor(java.lang.String)
	 */
	public Map<String, Color> getAnnotationColors() {
		for (AbstractModel model : registeredModels) {
			if (model instanceof WorkspaceModel) {
				WorkspaceModel workspaceModel = ((WorkspaceModel) model);
				if (workspaceModel.getActProject() > -1) {
					return ((WorkspaceModel) model).getAnnotationColors();
				}
			}
		}
		return colorMap;
	}

	/** Call this method after a successfully load of any GFF file, so based on the loaded annotation file this method generates
	 * visibility and color data of the loaded annotations.
	 * @throws IllegalStateException if there is no loaded GFF file
	 * @see WorkspaceModel#loadAnnotationData()
	 */
	public void loadAnnotationData() {

		if (!Viewer.getController().isAnnotationsLoaded()) {
			throw new IllegalStateException("Cannot generate annotation visibility and color data without loaded annotation (GFF) file!");
		}
		for (AbstractModel model : registeredModels) {
			if (model instanceof WorkspaceModel) {
				WorkspaceModel workspaceModel = ((WorkspaceModel) model);
				if (workspaceModel.getActProject() > -1) {
					((WorkspaceModel) model).loadAnnotationData();
				}
			}
		}
	}

	/**
	 *
	 * @return
	 * @see WorkspaceModel#isAnnotationsReady() 
	 */
	public boolean isAnnotationsReady() {
		for (AbstractModel model : registeredModels) {
			if (model instanceof WorkspaceModel) {
				WorkspaceModel workspaceModel = ((WorkspaceModel) model);
				return workspaceModel.isAnnotationsReady();
			}
		}
		return false;
	}

	/**********
	 * Common *
	 **********/
	/**
	 *
	 * @return {@code true} - if there is at least one file opening in proceed
	 */
	public boolean isFileOpenInProgress() {
		return openProgressCount < 0;
	}

	/**
	 * Indicate an open progress start or end and modify counter
	 * @param runnigState {@code true} - if progress started<br>{@code false} - if progress ended
	 */
	public void setFileOpenInProgress(boolean runnigState) {
		if (runnigState) {
			openProgressCount--;
		} else {
			if (openProgressCount == 0) {
				throw new AssertionError("Too many open progresses are ended");
			}
			openProgressCount++;
		}
	}

	/**
	 * Close the Fasta and Alignment and Annotation files.
	 * 
	 * @see ViewerFastaModel#setReferenceLoaded(java.io.File) 
	 * @see ViewerReadModel#unloadReads()
	 * @see ViewerGffModel#setAnnotationsLoaded(java.io.File)
	 */
	public void closeAllFiles() {
		for (AbstractModel modell : registeredModels) {
			if (modell instanceof ViewerFastaModel) {
				ViewerFastaModel fastaModell = (ViewerFastaModel) modell;
				try {
					fastaModell.setReferenceLoaded(null);
				} catch (FileNotFoundException ex) {
					//Signed in model
				} catch (IOException ex) {
					//Signed in model
				} catch (FastaRandomReaderException ex) {
					//Signed in model
				}
			} else if (modell instanceof ViewerReadModel) {
				ViewerReadModel readModell = (ViewerReadModel) modell;
				readModell.unloadReads();
				synchronized (readsDataList) {
					readsDataList.clear();
				}
			} else if (modell instanceof ViewerGffModel) {
				ViewerGffModel gffModell = (ViewerGffModel) modell;
				try {
					gffModell.setAnnotationsLoaded(null);
				} catch (IOException ex) {
					//Signed in model
				} catch (GffFileFormatException ex) {
					//Signed in model
				}
			}
		}
	}

	public void closeFile(FileTypes type) {
		switch (type) {
			case FASTA: {
				closeFasta();
				break;
			}
			case SAM:
			case BAM: {
				closeAlignment();
				break;
			}
			case GFF: {
				closeGff();
				break;
			}
			default: {
				throw new AssertionError();
			}
		}
	}

	private void closeFasta() {
		for (AbstractModel modell : registeredModels) {
			if (modell instanceof ViewerFastaModel) {
				ViewerFastaModel fastaModell = (ViewerFastaModel) modell;
				try {
					fastaModell.setReferenceLoaded(null);
				} catch (FileNotFoundException ex) {
					//Signed in model
				} catch (IOException ex) {
					//Signed in model
				} catch (FastaRandomReaderException ex) {
					//Signed in model
				}
			}
		}
	}

	public void closeAlignment() {
		for (AbstractModel modell : registeredModels) {
			if (modell instanceof ViewerReadModel) {
				synchronized (readsDataList) {
					ViewerReadModel readModell = (ViewerReadModel) modell;
					readModell.unloadReads();
					readsDataList.clear();
				}
			}
		}
	}

	private void closeGff() {
		for (AbstractModel modell : registeredModels) {
			if (modell instanceof ViewerGffModel) {
				ViewerGffModel gffModell = (ViewerGffModel) modell;
				try {
					gffModell.setAnnotationsLoaded(null);
				} catch (IOException ex) {
					//Signed in model
				} catch (GffFileFormatException ex) {
					//Signed in model
				}
			}
		}
		groupsWithoutProject = defaultGroups;
		visibilityWithoutProject = null;
		colorMap.clear();
	}

	/**
	 * @see ViewerConsensusModel#unloadConsensus() 
	 */
	public void unloadConsesnsus() {
		for (AbstractModel model : registeredModels) {
			if (model instanceof ViewerConsensusModel) {
				ViewerConsensusModel consensusModell = (ViewerConsensusModel) model;
				consensusModell.unloadConsensus();
			}
		}
	}

	/**
	 *
	 * @param type
	 * @return
	 * @see WorkspaceModel#getFileName(hu.astrid.viewer.util.FileTypes) 
	 */
	public String getFileNameInActProject(FileTypes type) {
		for (AbstractModel model : registeredModels) {
			if (model instanceof WorkspaceModel) {
				return ((WorkspaceModel)model).getFileName(type);
			}
		}
		return null;
	}

	/*************
	 * WorkSpace *
	 *************/
	/**
	 *
	 * @param workspacePath
	 * @throws IllegalStateException
	 * @see WorkspaceModel#create(java.lang.String)
	 */
	public void createWorkspace(String workspacePath) throws IllegalStateException {
		for (AbstractModel modell : registeredModels) {
			if (modell instanceof WorkspaceModel) {
				WorkspaceModel workspaceModell = (WorkspaceModel) modell;
				workspaceModell.create(workspacePath);
			}
		}
	}

	/**
	 *
	 * @return name of actual workspace
	 * @see WorkspaceModel#getName()
	 */
	public String getWorkspaceName() {
		for (AbstractModel modell : registeredModels) {
			if (modell instanceof WorkspaceModel) {
				WorkspaceModel workspaceModell = (WorkspaceModel) modell;
				return workspaceModell.getName();
			}
		}
		return null;
	}

	/**
	 *
	 * @return contained projects in actual workspace
	 * @see WorkspaceModel#getProjects()
	 */
	public List<Project> getProjects() {
		for (AbstractModel modell : registeredModels) {
			if (modell instanceof WorkspaceModel) {
				WorkspaceModel workspaceModell = (WorkspaceModel) modell;
				return workspaceModell.getProjects();
			}
		}
		return null;
	}

	/**
	 * Add file to a specified project
	 * @param file
	 * @param fileType
	 * @param projectIndex
	 * @see WorkspaceModel#setNewFile(java.io.File, hu.astrid.viewer.util.FileTypes, java.lang.Integer) 
	 */
	public void addFileToProject(File file, FileTypes fileType, int projectIndex) {
		for (AbstractModel modell : registeredModels) {
			if (modell instanceof WorkspaceModel) {
				WorkspaceModel workspaceModell = (WorkspaceModel) modell;
				workspaceModell.setNewFile(file, fileType, projectIndex);
			}
		}
	}

	/************
	 * Coverage *
	 ************/
	/**
	 * Generate coverage informations for the demanded region. Loads alignment records
	 * covering the interval and counts coverage of tha positions.
	 * @param fromPosition
	 * @param toPosition
	 * @throws IOException
	 * @throws MappingFileFormatException
	 * @see CoverageModel#generateCoverageData(int, int, java.util.List)
	 */
	public void generateCoverageData(int fromPosition, int toPosition) throws IOException, MappingFileFormatException {
		for (AbstractModel modell : registeredModels) {
			if (modell instanceof CoverageModel) {
				CoverageModel coverageModel = (CoverageModel) modell;
				coverageModel.generateCoverageData(fromPosition, toPosition, Viewer.getController().getReadsDataList());
				return;
			}
		}
	}

	/*************
	 * Consensus *
	 *************/
	/**
	 * //TODO
	 * @param position
	 * @see ViewerConsensusModel#readFromPosition(int) 
	 */
	public void readConsensusFromPosition(int position) {
		for (AbstractModel model : registeredModels) {
			if (model instanceof ViewerConsensusModel) {
				ViewerConsensusModel consensusModell = (ViewerConsensusModel) model;
				ConsensusData consensus = consensusModell.readFromPosition(position);
				for (AbstractView view : registeredViews) {
					if (view instanceof ConsensusPanel) {
						ConsensusPanel consensusPanel = (ConsensusPanel) view;
						consensusPanel.showConsensusSequence(position, consensus);
					}
				}
				return;
			}
		}
	}

	/**
	 * 
	 * @return
	 * @see ViewerConsensusModel#getConsensusLength() 
	 */
	public int getConsensusLength() {
		for (AbstractModel modell : registeredModels) {
			if (modell instanceof ViewerConsensusModel) {
				ViewerConsensusModel consensusModel = (ViewerConsensusModel) modell;
				return consensusModel.getConsensusLength();
			}
		}
		return -1;
	}

	/**
	 * 
	 * @return {@code true} - consensus data is already generated
	 * @see ViewerConsensusModel#isConsensusAvailable() 
	 */
	public boolean isConsensusAvailable() {
		for (AbstractModel modell : registeredModels) {
			if (modell instanceof ViewerConsensusModel) {
				ViewerConsensusModel consensusModel = (ViewerConsensusModel) modell;
				return consensusModel.isConsensusAvailable();
			}
		}
		return false;
	}

	public void generateConsensus() {
		for (AbstractModel modell : registeredModels) {
			if (modell instanceof ViewerConsensusModel) {
				ViewerConsensusModel consensusModel = (ViewerConsensusModel) modell;
				consensusModel.generateConsesus();
				return;
			}
		}
	}

	/**
	 * Calculate and save the consensus
	 * @param contigName
	 * @param file
	 */
	public void saveConsensus(final String contigName, final File file) {

		new SwingWorker<Object, Object>() {

			Throwable throwable = null;

			@Override
			protected Object doInBackground() throws Exception {
				Viewer.startStatusbarJob(Viewer.getLabelResources().getString("consensusSaveMethod"));
				firePropertyChange(CONSENSUS_SAVE, null, false);
				for (AbstractModel modell : registeredModels) {
					if (modell instanceof ViewerConsensusModel) {
						writeToFile(getConsensusSequence(0, 0), contigName, file);
					}
				}
				return null;
			}

			@Override
			protected void done() {
				Viewer.stopStatusbarJob(Viewer.getLabelResources().getString("consensusSaveMethod"));
				firePropertyChange(CONSENSUS_SAVE, null, true);
				if (throwable != null) {
					logger.error(throwable.getMessage(), throwable);
				}
			}
		}.execute();
	}

	/**
	 * Write consensus sequence into the given file. Put new line char after every 70th character.
	 * @param text
	 * @param contigName
	 * @param file
	 */
	private void writeToFile(String text, String contigName, File file) {
		FileWriter fileWriter = null;
		int textLength = text.length();
		int offset = 0;
		try {
			fileWriter = new FileWriter(file);
			fileWriter.write(">" + contigName + System.getProperty("line.separator"));
			if (textLength > 70) {
				while (textLength > 70) {
					fileWriter.write(text, offset, LINE_WIDTH);
					fileWriter.write(System.getProperty("line.separator"));
					textLength -= LINE_WIDTH;
					offset += LINE_WIDTH;
				}
				fileWriter.write(text, offset, textLength);
				fileWriter.close();
			} else {
				fileWriter.write(text);
				fileWriter.close();
			}
		} catch (IOException ex) {
			System.out.println(ex.getMessage());
		} finally {
			if (fileWriter != null) {
				try {
					fileWriter.close();
				} catch (IOException e) {
					logger.error(e.getMessage());
				}
			}
		}
	}

	/**
	 * Calculate the consensus sequence for save
	 * @param start
	 * @param end
	 * @return
	 */
	public String getConsensusSequence(int start, int end) {

		for (AbstractModel modell : registeredModels) {
			if (modell instanceof ViewerConsensusModel) {
				ViewerConsensusModel consensusModel = (ViewerConsensusModel) modell;
                return consensusModel.getConsensusSequence(start, end);
			}
		}
        return null;
	}

	/**
	 * Add selection marker to a selection list
	 * @param marker
	 * @param selectionType
	 */
	public void addSelectedPosition(Integer marker, SelectionType selectionType) {
		int megabazisPairLimit = 10000000;
		if (selectionType == SelectionType.CONSENSUS) {
			int boundary = 0;
			if (consensusSelectedPositions.getSelectedPositions().size() > 0) {
				boundary = Math.abs(marker - consensusSelectedPositions.getSelectedPositions().get(0));
			}
			if (!(boundary > megabazisPairLimit)) {
				consensusSelectedPositions.addSelectedPosition(marker);
			} else {
				setModelProperty(SelectionModel.CLIPBOARD_BOUNDARY, selectionType);
			}
		} else if (selectionType == SelectionType.REFERENCE) {
			int boundary = 0;
			if (referenceSelectedPositions.getSelectedPositions().size() > 0) {
				boundary = Math.abs(marker - referenceSelectedPositions.getSelectedPositions().get(0));
			}
			if (!(boundary > megabazisPairLimit)) {
				referenceSelectedPositions.addSelectedPosition(marker);
			} else {
				setModelProperty(SelectionModel.CLIPBOARD_BOUNDARY, selectionType);
			}
		}
	}

	/**
	 * Clear the list of selections (consensus & reference)
	 */
	public void clearSelectedPositions() {
		referenceSelectedPositions.clearPositions();
		consensusSelectedPositions.clearPositions();
	}

	public SelectedPosition getSelectedPosition(SelectionType selectionType) {
		if (selectionType == SelectionType.REFERENCE) {
			return referenceSelectedPositions;
		} else {
			return consensusSelectedPositions;
		}
	}

	/**
	 * Calculate the sequence for copy
	 * @param selectionType
	 * @return the string to copy
	 */
	protected String getSequenceToCopy(SelectionType selectionType) {
		int buffer = 16000;
		if (selectionType == SelectionType.REFERENCE) {
			if (referenceSelectedPositions.numberOfMarkers() == 2) {
				int start = referenceSelectedPositions.getStartPosition();
				int length = referenceSelectedPositions.getEndPosition() - referenceSelectedPositions.getStartPosition();
				StringBuilder sb = new StringBuilder();

				if (length > buffer) {
					while (length > buffer) {
						sb.append(getRefrenceAlignmentString(start + 1, buffer));
						start += buffer;
						length -= buffer;
					}
//					start += buffer;
					logger.debug("start: " + start + " " + length);
					sb.append(getRefrenceAlignmentString(start + 1, length + 1));
					return sb.toString();
				} else {
					return getRefrenceAlignmentString(start + 1, length + 1);
				}

			} else if (referenceSelectedPositions.numberOfMarkers() == 1) {
				return getRefrenceAlignmentString(referenceSelectedPositions.getStartPosition() + 1, 1);
			} else {
				return new String();
			}
		} else if (selectionType == SelectionType.CONSENSUS) {
			if (consensusSelectedPositions.numberOfMarkers() == 2) {
				return getConsensusSequence(consensusSelectedPositions.getStartPosition(), consensusSelectedPositions.getEndPosition() + 1);
			} else if (consensusSelectedPositions.numberOfMarkers() == 1) {
				return getConsensusSequence(consensusSelectedPositions.getStartPosition(), consensusSelectedPositions.getStartPosition() + 1);
			} else {
				return new String();
			}
		}
		return new String();
	}

	/**
	 * Manage the system clipboard
	 * @param selectionType
	 */
	public void copyToClipboard(SelectionType selectionType) {
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		StringSelection stringSelection = new StringSelection("");
		if (selectionType == SelectionType.BOTH) {
			StringBuilder sb = new StringBuilder(getSequenceToCopy(SelectionType.REFERENCE));
			sb.append(System.getProperty("line.separator"));
			sb.append(getSequenceToCopy(SelectionType.CONSENSUS));
			if (!sb.toString().equals(System.getProperty("line.separator"))) {
				stringSelection = new StringSelection(sb.toString());
			}
		} else {
			stringSelection = new StringSelection(getSequenceToCopy(selectionType));
		}
		clipboard.setContents(stringSelection, stringSelection);
	}
}
