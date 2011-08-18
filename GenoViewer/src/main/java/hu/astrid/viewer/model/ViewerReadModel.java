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

package hu.astrid.viewer.model;

import hu.astrid.viewer.model.alignment.Interval;
import hu.astrid.viewer.model.alignment.LoadedIntervalList;
import hu.astrid.mapping.exception.IndexFileFormatException;
import hu.astrid.mapping.exception.MappingFileFormatException;
import hu.astrid.mapping.io.IndexedBamReader;
import hu.astrid.mapping.io.SamReader;
import hu.astrid.mapping.model.AlignmentRecord;
import hu.astrid.mapping.model.AlignmentRecordComparator;
import hu.astrid.mapping.model.HeaderRecord;
import hu.astrid.mapping.model.HeaderRecordType;
import hu.astrid.mapping.model.HeaderTagType;
import hu.astrid.mapping.model.MappingHeader;
import hu.astrid.mapping.model.SortOrder;
import hu.astrid.mvc.swing.AbstractModel;
import hu.astrid.viewer.Viewer;
import hu.astrid.viewer.gui.StatusBar.ProgressValue;
import hu.astrid.viewer.gui.mutation.MutationsDialog;
import hu.astrid.viewer.model.mutation.Mutation;
import hu.astrid.viewer.model.mutation.MutationFilter;
import hu.astrid.viewer.model.mutation.MutationScanner;
import hu.astrid.viewer.model.mutation.MutationTableFilter;
import hu.astrid.viewer.util.Alignment;
import hu.astrid.viewer.util.CSVReader;
import hu.astrid.viewer.util.CSVWriter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

/**
 * Viewer model for handling read files
 */
public class ViewerReadModel extends AbstractModel {

	/** Alignment load state {@link  } */
	public static final String ALIGNMENT_LOAD = "AlignmentLoaded";
	/** Indicates preload of reads completed {@link AlignmentRecordPreloader } */
	public static final String ALIGNMENT_PRELOAD = "AlignmentPreload";
	/** Actual reference index in read file {@link ViewerReadModel#setActAlignmentRefNameIndex(java.lang.Integer) } */
	public static final String ACT_ALIGNMENT_REF_INDEX = "ActAlignmentRefNameIndex";
	/** Mutations table load state {@link ViewerReadModel#setMutationsLoaded(java.lang.Boolean) } */
	public static final String MUTATIONS_LOAD = "MutationsLoaded";
	/** Indicates wether mutations loading in progress */
	public static final String MUTATIONS_LOADING_STATE = "MutationsLoadingState";
	/** Mutation table filter {@link ViewerReadModel#setMutationsFilter(hu.astrid.viewer.model.mutation.MutationTableFilter) } */
	public static final String MUTATIONS_FILTER = "MutationsFilter";
	/**Logger*/
	private static final Logger logger = Logger.getLogger(ViewerReadModel.class);
	private final AlignmentRecordComparator alignmentRecordComparator = new AlignmentRecordComparator();
	/**List of reads*/
	List<AlignmentRecord> readList = new ArrayList<AlignmentRecord>();
	/**Stores the loaded regions from alignment file*/
	private LoadedIntervalList loadedIntervals = null;
	/**Name of alignment file*/
	private String fileName;
	/**Absolute path of alignment*/
	private String filePath;
	private IndexedBamReader bamReader;
	/**Index of the actual reference in the BAM file*/
	private int actRefNameIndex = -1;
	/**Is every reads loaded from alignment file*/
	private boolean isWholeFileLoaded;
	/**Indicates that whole file need to be loaded*/
	private boolean needWholeFileLoaded;
	/**Load intervals with this length from end of reference to find the last covered position*/
	private final int readEndCheckBufferSize = 100;
	/**Are the records sorted in the file*/
	private boolean sorted = false;
	/**Length of reference where reads loaded from*/
	private int actReferenceLength = 0;
	/** variable for storing the position of the last read's last item */
	private int lastReadEndPos;
	/**ExecutorService to preload reads*/
	private ExecutorService preloaderService = Executors.newSingleThreadExecutor();
	/**ExecutorService to write files for generated data*/
	private ExecutorService writerService = Executors.newCachedThreadPool();
	/**List containing reference names in SAM file*/
	private List<String> samReferenceNames = new ArrayList<String>();
	private int lastIntervalStart;
	private int lastIntervalEnd;
	/** List of {@link Mutation}s discovered in alignment file */
	private List<Mutation> mutationsList = null;
//	/** Indicates that mutation table loading is in progress*/
//	private Boolean mutationLoadInProgress = false;
	/**Filter for reduce displayed model on {@link MutationsDialog} table */
	private MutationTableFilter mutationFilter = null;

	/**
	 * Load reads from SAM file. If the {@code SO} tag is {@code UNSORTED}, reads will be sorted
	 * @param file The SAM file.
	 * @throws IOException
	 * @throws MappingFileFormatException
	 */
	public void loadSamFile(File file) throws MappingFileFormatException, IOException {
		unloadReads();
		synchronized (this) {
			SamReader samReader = null;
			try {
				int samFileLimit = Viewer.getApplicationProperties().getSamFileLimit();
				if (file.length() > samFileLimit) {
					throw new SamSizeExceededException(file.getAbsolutePath(), samFileLimit);
				}
				samReader = new SamReader(new BufferedReader(new FileReader(file)));
			} catch (IOException ex) {
				logger.error(ex.getMessage());
				throw ex;
			}

			MappingHeader header = samReader.readHeader();
			if (header != null) {
				for (HeaderRecord record : header.getRecords()) {
					if (record.getType() == HeaderRecordType.HD) {
						if (record.getTagValue(HeaderTagType.SO) != null && !record.getTagValue(HeaderTagType.SO).equalsIgnoreCase(SortOrder.UNSORTED.toString())) {
							sorted = true;
						}
					} else if (record.getType() == HeaderRecordType.SQ) {
						actReferenceLength = new Integer(record.getTagValue(HeaderTagType.LN));
						String sequenceName = record.getTagValue(HeaderTagType.SN);
						samReferenceNames.add(sequenceName);
					}
				}
			}

			AlignmentRecord sam = null;
			try {
				sam = samReader.nextRecord();
				while (sam != null) {
					readList.add(sam);
					sam = samReader.nextRecord();
				}

				isWholeFileLoaded = true;
				samReader.close();
			} catch (MappingFileFormatException ex) {
				logger.error(ex.getMessage(), ex);
				throw ex;
			} catch (IOException ex) {
				logger.error(ex.getMessage(), ex);
				throw ex;
			}

			fileName = file.getName();
			filePath = file.getAbsolutePath();

			if (!sorted) {
				sortAlignmentRecords();
			}

			lastReadEndPos = readList.get(readList.size() - 1).getPosition() + readList.get(readList.size() - 1).getSequence().length();

			//If header didnt contained SO tag
			if (actReferenceLength == 0) {
				AlignmentRecord record = readList.get(readList.size() - 1);
				actReferenceLength = record.getPosition() - 1 + record.getReadLength();
			}

			loadedIntervals = new LoadedIntervalList(actReferenceLength);
			loadedIntervals.init(0, actReferenceLength);
		}

		firePropertyChange(ALIGNMENT_LOAD, null, fileName);
	}

	/**
	 * Open BAM file
	 * @param file BAM file
	 * @throws IOException
	 * @throws IndexFileFormatException
	 * @throws MappingFileFormatException
	 */
	public void loadBamFile(File file) throws IOException, IndexFileFormatException, MappingFileFormatException {
		unloadReads();
		synchronized (this) {
			try {
				bamReader = new IndexedBamReader(file.getPath());

				for (HeaderRecord record : bamReader.getHeader().getRecords()) {
					if (record.getType() == HeaderRecordType.HD) {
						if (record.getTagValue(HeaderTagType.SO) != null && !record.getTagValue(HeaderTagType.SO).toUpperCase().equalsIgnoreCase(SortOrder.UNSORTED.toString().toUpperCase())) {
							sorted = true;
						}
					}
				}
				fileName = file.getName();
				filePath = file.getAbsolutePath();
			} catch (IOException ex) {
				logger.error(ex.getMessage());
				throw ex;
			} catch (IndexFileFormatException ex) {
				logger.error(ex.getMessage(), ex);
				throw ex;
			} catch (MappingFileFormatException ex) {

				logger.error(ex.getMessage(), ex);
				throw ex;
			}

			//TODO memóriába betöltés feltételét finomítani
			logger.debug("BamFileLimit: " + Viewer.getApplicationProperties().getBamFileLimit());
			if (file.length() < Viewer.getApplicationProperties().getBamFileLimit()) {
				needWholeFileLoaded = true;
			}

		}

		logger.trace("bam loaded");

		firePropertyChange(ALIGNMENT_LOAD, null, fileName);
	}

	/**
	 * Sort alignment records stored in the model
	 */
	public synchronized void sortAlignmentRecords() {
		Collections.sort(readList, alignmentRecordComparator);
	}

	/**
	 * Load reads from read file, where reads start indicies are in an interval. Records and load interval are stored.
	 * If SAM file used, or every records loaded from BAM file, just returns the list, loading doesnt happen.
	 * @param start itnerval start index, (inculsive) in case of SAM doesnt matter
	 * @param end interval end index, (exclusive) in case of SAM doesnt matter
	 * @return list of loaded reads
	 * @throws IOException
	 * @throws MappingFileFormatException
	 */
	public List<AlignmentRecord> loadReads(int start, int end) throws IOException, MappingFileFormatException {
		if (isWholeFileLoaded) {
			synchronized (this) {
				return readList;
			}
		}
		try {
			stopPreload();
			final ArrayList<AlignmentRecord> list;
			synchronized (this) {
				readList = Collections.synchronizedList(bamReader.loadRecords(bamReader.getHeader().getReferenceNames().get(actRefNameIndex), start, end));

				loadedIntervals.init(start, Math.min(end, actReferenceLength));

				if (!sorted) {
					sortAlignmentRecords();
				}
				list = new ArrayList<AlignmentRecord>(readList);
			}
			logger.trace("loaded " + start + " " + Math.min(end, actReferenceLength));

			return list;
		} catch (IOException ex) {
			logger.error(ex.getMessage(), ex);
			throw ex;
		} catch (MappingFileFormatException ex) {
			logger.error(ex.getMessage(), ex);
			throw ex;
		}
	}

	/**
	 * Stop every preload thread
	 */
	private void stopPreload() {
		List<Runnable> unDoneList = preloaderService.shutdownNow();
		logger.trace("everything stopped, " + unDoneList.size() + " undone");
	}

	/**
	 * Load reads through currently active reader without storing
	 *
	 * @param start
	 * @param end
	 * @return
	 * @throws IOException
	 * @throws MappingFileFormatException 
	 */
	public List<AlignmentRecord> loadReadsWithoutStore(int start, int end) throws IOException, MappingFileFormatException {
		if (isWholeFileLoaded) {
			synchronized (this) {
				return new ArrayList<AlignmentRecord>(readList);
			}
		}
		try {
			List<AlignmentRecord> list = bamReader.loadRecords(bamReader.getHeader().getReferenceNames().get(actRefNameIndex), start, end);
			if (!sorted) {
				Collections.sort(list, alignmentRecordComparator);
			}

			logger.trace("load without store positions start " + start + " end " + end);

			return list;
		} catch (IOException ex) {
			logger.error(ex.getMessage(), ex);
			throw ex;
		} catch (MappingFileFormatException ex) {
			logger.error(ex.getMessage(), ex);
			throw ex;
		}
	}

	/**
	 * @param start start position in records collection
	 * @param end end position in records collection
	 * @return list of alignment records
	 */
	public synchronized List<AlignmentRecord> getPrelodedReads(int start, int end) {
		return new ArrayList<AlignmentRecord>(readList.subList(start, end));
	}

	/**
	 * Reference names contained in the BAM file
	 * @return
	 */
	public synchronized List<String> getRefNames() {
		if (bamReader != null) {
			return bamReader.getHeader().getReferenceNames();
		} else {
			return samReferenceNames;
		}
	}

	/**
	 * Reference name, for loading from BAM file
	 * @param actRefNameIndex
	 * @throws MappingFileFormatException
	 * @throws IOException
	 */
	public void setActAlignmentRefNameIndex(Integer actRefNameIndex) throws MappingFileFormatException, IOException {
		int oldValue = this.actRefNameIndex;

		//TODO SAM doesnt support multiple references yet

		if (bamReader != null) {
			if (this.actRefNameIndex != actRefNameIndex) {
				synchronized (this) {
					this.actRefNameIndex = actRefNameIndex;
					this.actReferenceLength = bamReader.getHeader().getReferenceLengths().get(actRefNameIndex);
					loadedIntervals = new LoadedIntervalList(actReferenceLength);
					//TODO memóriába betöltés feltételét finomítani
					if (needWholeFileLoaded) {
						//Set false to reload for new reference
						isWholeFileLoaded = false;
						loadReads(0, actReferenceLength);
						isWholeFileLoaded = true;
					}
					lastReadEndPos = calculateLastReadEndPosInBam();
				}
			}
		}
		firePropertyChange(ACT_ALIGNMENT_REF_INDEX, oldValue, actRefNameIndex);
	}

	/**
	 * Start preloading reads. If a load or occured before, boundaries set by {@code interval},
	 * this load will be extended.
	 * If view was scrolled, new boundaries set by {@code interval}, and extension starts if needed.
	 * In null is passed as parameter, just extension occurs if needed.
	 * @param interval the displayed interval after load or scroll, or null if extension needed
	 */
	public synchronized void notifyPreloader(Interval interval) {
		if (interval != null) {
			lastIntervalStart = interval.start;
			lastIntervalEnd = interval.end;
		}
		if (preloaderService.isShutdown()) {
			preloaderService = Executors.newSingleThreadExecutor();

		}

		preloaderService.execute(new AlignmentRecordPreloader());
	}

	/**
	 * Reference length, from reads are loaded from in BAM file
	 * @return
	 */
	public Integer getActReferenceLength() {
		return actReferenceLength;
	}

	/**
	 * Returns true, if there are reads loaded from SAM file or BAM file opened
	 * @return
	 */
	public synchronized boolean isReadsLoaded() {
		return !readList.isEmpty() || bamReader != null;
	}

	/**
	 * Unload the reads, close BAM reader, notify views
	 */
	public void unloadReads() {
		if (!readList.isEmpty()) {
			Viewer.getController().unloadConsesnsus();
			synchronized (this) {
				if (fileName == null) {
					return;
				}
				stopPreload();
				readList = new ArrayList<AlignmentRecord>();
				samReferenceNames = new ArrayList<String>();
				fileName = null;
				filePath = null;
				loadedIntervals.clear();
				if (bamReader != null) {
					try {
						bamReader.close();
					} catch (IOException ex) {
						logger.error(ex.getMessage(), ex);
					}
				}
				bamReader = null;
				mutationsList = null;
				actRefNameIndex = -1;
				actReferenceLength = 0;
				isWholeFileLoaded = false;
				needWholeFileLoaded = false;
				sorted = false;
				lastIntervalStart = 0;
				lastIntervalEnd = 0;
			}
			setMutationsLoaded(false);
			firePropertyChange(ALIGNMENT_LOAD, fileName, null);
		}
	}

	/**
	 * Determine that an interval were previously loaded
	 * @param start interval start
	 * @param end interval end
	 * @return {@code true} - if every readfrom given interval is loaded
	 */
	public synchronized boolean isIntervalLoaded(int start, int end) {
		lastIntervalStart = start;
		lastIntervalEnd = end;
		boolean containment = loadedIntervals.contains(start, end);
		if (containment) {
			notifyPreloader(new Interval(start, end));
		}
		return containment;
	}

	/**
	 * Name of file where reads loaded from
	 * @return
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @param id index in collection
	 * @return specified element from collection of alignment records
	 */
	public synchronized AlignmentRecord getReadById(int id) {
		try {
			return readList.get(id);
		} catch (IndexOutOfBoundsException ex) {
			return null;
		}
	}

	/**
	 * Is every reads loaded, or indexed load needed
	 * @return
	 */
	public boolean isWholeFileLoaded() {
		return isWholeFileLoaded;
	}

	/**
	 * @return {@code true} - if alignment files {@code SO} tag is not {@code  UNSORTED} <br>
	 * {@code false} - else
	 */
	public boolean isSorted() {
		return sorted;
	}

	/**
	 * @return position of last reads last nucleotide
	 */
	public synchronized int getLastReadEndPos() {
		
		return this.lastReadEndPos;
	}

	/**
	 * Search for last covered position
	 * @return position of last reads last nucleotide
	 * @throws IOException
	 * @throws MappingFileFormatException
	 */
	private int calculateLastReadEndPosInBam() throws IOException, MappingFileFormatException {
		int i=0;
		int start = Math.max(actReferenceLength - readEndCheckBufferSize, 0);

		int end = actReferenceLength;
		List<AlignmentRecord> tempList;
		//TODO nagy meretu fileok eseten belathatatlan ideig fut
		while (true) {
			tempList = loadReadsWithoutStore(start, end);
			logger.debug("while | tempList: "+tempList+" "+start+" "+end);
			if (!tempList.isEmpty()) {
				//TODO indelek esetén ez eltolódik
				return tempList.get(tempList.size() - 1).getPosition() + tempList.get(tempList.size() - 1).getReadLength();
			}

			start = Math.max(start - readEndCheckBufferSize, 0);
			end -= readEndCheckBufferSize;
		}
	}

	/**
	 * Preload an interval, refresh loaded, store alignment records in model intervals and notify views.
	 */
	private class AlignmentRecordPreloader implements Runnable {

		@Override
		public void run() {
			try {
				int prevSize, actSize;
				synchronized (ViewerReadModel.this) {
					final int intervalLength = lastIntervalEnd - lastIntervalStart;
					final int intervalCenter = lastIntervalStart + (intervalLength) / 2;
					//TODO néha nullpointer
					final Interval actualInterval = loadedIntervals.getContainigInterval(intervalCenter);
					if ((actualInterval.start == 0 || intervalCenter - actualInterval.start > 1000) && (actualInterval.end == actReferenceLength || actualInterval.end - intervalCenter > 1000)) {
						return;
					}
					final int start, end;
					if ((intervalCenter - actualInterval.start < actualInterval.end - intervalCenter && actualInterval.start != 0) || actualInterval.end == actReferenceLength) {
						end = actualInterval.start;
						start = Math.max(0, end - Viewer.getApplicationProperties().getReadDistance());
					} else if ((intervalCenter - actualInterval.start >= actualInterval.end - intervalCenter && actualInterval.end != actReferenceLength) || actualInterval.start == 0) {
						start = actualInterval.end;
						end = Math.min(actReferenceLength, start + Viewer.getApplicationProperties().getReadDistance());
					} else {
						start = end = 0;
						throw new AssertionError("Incorrect state, how did it get here???");
					}
					logger.trace("preloading interval " + start + " " + end);
					List<AlignmentRecord> list = bamReader.loadRecords(bamReader.getHeader().getReferenceNames().get(actRefNameIndex), start, end);
					loadedIntervals.add(start, end);
					prevSize = readList.size();
					if (!sorted) {
						Collections.sort(list, alignmentRecordComparator);
					}
					readList.addAll(list);
					actSize = readList.size();
				}
				firePropertyChange(ALIGNMENT_PRELOAD, prevSize, actSize);
			} catch (IOException ex) {
				logger.error(ex.getMessage(), ex);
			} catch (MappingFileFormatException ex) {
				logger.error(ex.getMessage(), ex);
			} catch (Exception ex) {
				logger.error(ex.getMessage(), ex);
			}
		}
	}

	/*************
	 * Consensus *
	 *************/
	/**
	 * //TODO
	 * @param start
	 * @param end
	 * @return
	 * @throws MappingFileFormatException
	 * @throws IOException
	 */
	public int getNumberOfReadsByPosition(int start, int end) throws MappingFileFormatException, IOException {
		int numberOfReadsByPosition = 0;
		if (isWholeFileLoaded) {
			for (AlignmentRecord record : readList) {
				if (record.getPosition() <= start && record.getPosition() + record.getReadLength() >= end) {
					numberOfReadsByPosition++;
				} else if (record.getPosition() > start) {
					break;
				}
			}
		} else {
			for (AlignmentRecord record : bamReader.loadRecords(bamReader.getHeader().getReferenceNames().get(actRefNameIndex), start - Viewer.getController().getMaxReadLength(), end)) {
				if (record.getPosition() <= start && record.getPosition() + record.getReadLength() >= end) {
					numberOfReadsByPosition++;
				}
			}
		}
		return numberOfReadsByPosition;
	}

	//TODO miez?
	public String getSubSequenceByTransformationPosition(int start, int end) throws MappingFileFormatException, IOException {
		String subSequence = null;
		if (isWholeFileLoaded) {
			for (AlignmentRecord record : readList) {
				if (record.getPosition() <= start && record.getPosition() + record.getReadLength() >= end) {
					subSequence = record.getSequence().substring(start - record.getPosition(), end - record.getPosition());
				}
			}
		} else {
			for (AlignmentRecord record : bamReader.loadRecords(bamReader.getHeader().getReferenceNames().get(actRefNameIndex), start - Viewer.getController().getMaxReadLength(), end)) {
				if (record.getPosition() <= start && (record.getPosition() + record.getReadLength()) >= (end)) {
					subSequence = record.getSequence().substring(start - record.getPosition(), end - record.getPosition());
					break;
				}
			}
		}
		return subSequence;
	}

	/**
	 * Concatenate a sequence uppon alignments. I case of mutations the match or mutations
	 * occurence in generated sequence isn't determined, every time the first available
	 * alignment gets concatenated, and the next examined record is that have start position after
	 * the generated sequences end. If there are unset nucleotide places between the acual
	 * examined alignment and the end of the generated sequence, the last alignment
	 * before actual gets examined, after that the actual.
	 * @return generated sequence uppon alignments
	 * @throws IOException
	 * @throws MappingFileFormatException
	 */
	public String getSequenceByReads() throws MappingFileFormatException, IOException {
		String consensusPath = filePath.substring(0, filePath.lastIndexOf('.')) + "_consensus.fasta";
		File generatedSequenceFile = new File(consensusPath);
		if (generatedSequenceFile.exists()) {
			Viewer.startStatusbarJob(Viewer.getLabelResources().getString("statusbarMessageGeneratedSequenceLoading"));
			String seq = loadGeneratedSequence(generatedSequenceFile);
			Viewer.stopStatusbarJob(Viewer.getLabelResources().getString("statusbarMessageGeneratedSequenceLoading"));
			if (seq != null) {
				return seq;
			}
		}
		long startTime = System.currentTimeMillis();
		StringBuilder result = new StringBuilder(actReferenceLength);

		if (!isWholeFileLoaded || !isSorted()) {
			logger.warn("Consensus generation doesn't work well on unsorted alignment files!!!");
		}

		int progressValue = 0;
		Viewer.setStatusbarProgresValue(new ProgressValue("ConsensusGeneration", progressValue));
		int sequencePosition = 0;
		int start = 0, end = isWholeFileLoaded ? actReferenceLength : Viewer.getApplicationProperties().getBufferSize();
		while (start <= actReferenceLength) {
			List<AlignmentRecord> recordsList = loadReadsWithoutStore(start, end);
			for (int i = 0; i < recordsList.size(); ++i) {
				AlignmentRecord record = recordsList.get(i);
				if (record.getPosition() < sequencePosition) {
					continue;
				}

				//If the start position is not match to the end of generated sequence
				boolean prevAppendOccured = false;
				if (record.getPosition() - 1 > result.length()) {
					prevAppendOccured = appendPreviousRecords(i - 1, recordsList, sequencePosition, result);
				}
				final String sequenceForConsensus = Alignment.getSequenceForConsensus(record);
				final int firstUnknownPosition = appendSequence(result, record.getPosition() - 1, sequenceForConsensus);
				if (firstUnknownPosition > -1 && !prevAppendOccured) {
					appendPreviousRecords(i - 1, recordsList, firstUnknownPosition, result);
				}
				if (result.indexOf("N", record.getPosition() - 1) == -1) {
					sequencePosition = result.length();
				}

				int genearationValue = (int) ((double) record.getPosition() / actReferenceLength * 100);
				if (genearationValue > progressValue) {
					progressValue = genearationValue;
					Viewer.setStatusbarProgresValue(new ProgressValue("ConsensusGeneration", progressValue));
				}
			}

			//If last record is not appended yet
			if (appendPreviousRecords(recordsList.size() - 1, recordsList, sequencePosition, result)) {
				sequencePosition = result.length();
			}

			end += end - start;
			start += (end - start) / 2;
		}

		for (int j = result.length(); j < getActReferenceLength(); j++) {
			result.append("N");
		}

		Viewer.setStatusbarProgresValue(new ProgressValue("ConsensusGeneration", 101));
		logger.info("Generation time : " + (System.currentTimeMillis() - startTime));
		writeSequenceToFasta(result.toString(), "consensus", generatedSequenceFile);
		return result.toString();
	}

	/**
	 * Append new seqence for the consensus at given position. If new position is
	 * larger then constructed consensus length, positions between marked with 'N'.
	 * @param consensus constructed consensus
	 * @param position start position of new sequence
	 * @param sequence sequence to append
	 * @return -1 - if no 'N'-s are appeneded thi time<br>
	 *			first position of newly appended 'N'
	 */
	private int appendSequence(StringBuilder consensus, int position, String sequence) {
		int ret = -1;
		if (position > consensus.length()) {
			ret = consensus.length();
			int numberOfUnknownNucleotide = position - consensus.length();
			//If there are empty places yet before actual read start and generated sequence
			if (numberOfUnknownNucleotide > 0) {
				char[] emptyPlaces = new char[numberOfUnknownNucleotide];
				Arrays.fill(emptyPlaces, 'N');
				consensus.append(emptyPlaces);
			}
		}

		int i = 0;
		for (i = 0; position + i < consensus.length() && i < sequence.length(); ++i) {
			if (consensus.charAt(position + i) == 'N' && sequence.charAt(i) != 'N') {
				consensus.setCharAt(position + i, sequence.charAt(i));
			} else if (ret == -1 && consensus.charAt(position + i) == 'N') {
				ret = position + i;
			}
		}
		if (sequence.length() > i) {
			int unknownPosition = sequence.substring(i).indexOf('N');
			if (unknownPosition > 0 && ret == -1) {
				ret = position + i + unknownPosition;
			}
			consensus.append(sequence.substring(i));
		}
		return ret;
	}

	/**
	 * Try to expand known sequence for consensus by examining previous records.
	 * Check the read sequentially backward, until the read read start position is
	 * less than first unknown position.
	 * @param index index of last appended record
	 * @param recordsList
	 * @param firstUnknownPosition first unknown position in sequence
	 * @param consensus previously built sequence
	 * @return {@code true} - if sequence expansion was sucessfull<br>
	 * {@code false} - if coudn't determine every unknown nucleotide
	 */
	private boolean appendPreviousRecords(int index, List<AlignmentRecord> recordsList, int firstUnknownPosition, StringBuilder consensus) {
		//Try to append the read before last
		boolean ret = false;
		for (int j = index; j > 0; --j) {
			AlignmentRecord previousRecord = recordsList.get(j);
			if (previousRecord.getPosition() < firstUnknownPosition - 50) {
				break;
			}
			String alignmentSequence = Alignment.getSequenceForConsensus(previousRecord);
			if (previousRecord.getPosition() + alignmentSequence.length() > firstUnknownPosition) {
				appendSequence(consensus, previousRecord.getPosition() - 1, alignmentSequence);
				ret = consensus.indexOf("N", firstUnknownPosition) == -1;
				if (ret) {
					break;
				}
			}
		}
		return ret;
	}

	/**
	 * Load generated sequence for consensus from a file
	 * @param file
	 * @return generated sequence if load was successful, {@code null} else
	 */
	private String loadGeneratedSequence(File file) {
		StringBuilder result = new StringBuilder(actReferenceLength);
		FileChannel channel = null;
		boolean success = false;
		try {
			channel = new RandomAccessFile(file, "r").getChannel();
			ByteBuffer buffer = ByteBuffer.allocate(Viewer.getApplicationProperties().getBufferSize());
			while (channel.read((ByteBuffer) buffer.clear()) > -1) {
				buffer.flip();
				result.append(getChars(buffer.array(), buffer.limit()));
			}
			result.delete(0, result.indexOf("\n") + 1);
			success = true;
		} catch (IOException ex) {
			logger.error(ex.getMessage(), ex);
		} finally {
			if (channel != null) {
				try {
					channel.close();
				} catch (IOException ex) {
					logger.error(ex.getMessage());
				}
			}
		}
		return success ? result.toString() : null;
	}

	/**
	 * Write gereated sequence for consensus to a fasta file
	 * @param sequence sequence to persist
	 * @param contigName name of exported contig
	 * @param file destination file
	 */
	private void writeSequenceToFasta(final String sequence, final String contigName, final File file) {
		writerService.submit(new Runnable() {

			@Override
			public void run() {
				FileChannel channel = null;
				try {
					channel = new RandomAccessFile(file, "rw").getChannel();
					ByteBuffer buffer = ByteBuffer.wrap(getBytes('>' + contigName + '\n'));
					channel.write(buffer);
					buffer = ByteBuffer.wrap(getBytes(sequence));
					channel.write(buffer);
					channel.close();
				} catch (Exception ex) {
					logger.error(ex.getMessage(), ex);
				} finally {
					if (channel != null) {
						if (channel != null) {
							try {
								channel.close();
							} catch (IOException ex) {
								logger.error(ex.getMessage());
							}
						}
					}
				}
			}
		});
	}

	/**
	 * This exception indicates, that a SAM file is larger than a specified size
	 * and cannot be opened.
	 */
	public static class SamSizeExceededException extends IOException {

		private static final long serialVersionUID = 1L;
		private String filePath;
		private int sizeLimit;

		/**
		 *
		 * @param filePath
		 * @param sizeLimit
		 */
		public SamSizeExceededException(String filePath, int sizeLimit) {
			super("File " + filePath + " exceeded " + sizeLimit + " bytes SAM file limit. Convert to Bam and try again.");
			this.filePath = filePath;
			this.sizeLimit = sizeLimit;
		}

		/**
		 * @return path of file exceeding open size limit
		 */
		public String getFilePath() {
			return filePath;
		}

		/**
		 * @return maximum size for accepting a SAM file to open
		 */
		public int getSizeLimit() {
			return sizeLimit;
		}
	}

	/*********************
	 * Mutation handling *
	 *********************/
	/**
	 * Load mutations of current alignment file and notify views. If mutations previously
	 * loaded, just notifications happens. Else if previously generated cvs file exists with
	 * corresponding format, it is loaded. Else new search starts and results saved
	 * after the notification. {@link ViewerReadModel#getMutationList() }
	 *
	 * @param loadState if {@code true} - load occurs<br> else - unload
	 */
	public void setMutationsLoaded(Boolean loadState) {
		List<Mutation> list = null;

		if (loadState) {
			list = getMutationList();
		} else {
			mutationsList = null;
		}
		firePropertyChange(MUTATIONS_LOAD, null, list);
		firePropertyChange(MUTATIONS_LOADING_STATE, null, false);
	}

	/**
	 * If mutations aren't previously loaded, try to load from csv file.
	 * If its unsuccessful, start to search.<br>
	 * Returned data is filtered with given {@link MutationTableFilter} if its present.
	 * If {@link Mutation#getCoverage()} {@code <=} {@link MutationTableFilter#maxCoverage}
	 * {@code &&} {@link Mutation#getCoverage()} {@code >=} {@link MutationTableFilter#minCoverage}
	 * the mutation gets into filtered data.
	 * @return new list of mutations in current alignment file
	 */
	public List<Mutation> getMutationList() {
		firePropertyChange(MUTATIONS_LOADING_STATE, null, true);
		if (mutationsList == null) {
			File csvFile = new File(filePath.substring(0, filePath.lastIndexOf('.') + 1) + "csv");
			if (csvFile.exists()) {
				Viewer.startStatusbarJob(Viewer.getLabelResources().getString("statusbarMessageMutationLoading"));
				List<Mutation> list = loadMutations(csvFile);
				mutationsList = list;
				Viewer.stopStatusbarJob(Viewer.getLabelResources().getString("statusbarMessageMutationLoading"));
			}
			//If load from file failed :
			if (mutationsList == null) {
				searchForMutations();
				synchronized (mutationsList) {
					writeMutationsToCSV(new ArrayList<Mutation>(mutationsList), csvFile);
				}
			}
		}
		List<Mutation> list;
		synchronized (mutationsList) {
			if (mutationFilter == null) {
				list = new ArrayList<Mutation>(mutationsList);
			} else {
				list = new ArrayList<Mutation>();
				for (Mutation mutation : mutationsList) {
					if (mutationFilter.allowedTypes.contains(mutation.getMutationType())
						&& mutationFilter.minCoverage <= mutation.getCoverage()
						&& mutationFilter.maxCoverage >= mutation.getCoverage()) {
						list.add(mutation);
					}
				}
			}
		}
		return list;
	}

	/**
	 * Apply a new filter for mutations and display filtered data in views
	 * @param filter
	 */
	public void setMutationsFilter(MutationTableFilter filter) {
		if (mutationFilter == null || !mutationFilter.equals(filter)) {
			mutationFilter = filter;
			setMutationsLoaded(true);
		}
	}

	/**
	 * Load mutations from a csv file
	 * @param file csv file
	 * @return list of mutations
	 */
	private List<Mutation> loadMutations(File file) {
		CSVReader<Mutation> reader = null;
		List<Mutation> mutations = null;
		try {
			reader = new CSVReader<Mutation>(new FileReader(file), Mutation.class);
			mutations = reader.readAll();
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException ex) {
					logger.error(ex.getMessage());
				}
			}
		}
		return mutations;
	}

	/**
	 * Write discovered mutations to a CSV file named after current alignment file
	 * @param mutations list of mutations to persist
	 * @param file destination file
	 */
	private void writeMutationsToCSV(final List<Mutation> mutations, final File file) {
		writerService.submit(new Runnable() {

			@Override
			public void run() {
				CSVWriter<Mutation> writer = null;
				try {
					writer = new CSVWriter<Mutation>(new FileWriter(file), Mutation.class);
					writer.write(mutations);
				} catch (IOException ex) {
					logger.error(ex.getMessage(), ex);
				} finally {
					if (writer != null) {
						try {
							writer.close();
						} catch (IOException ex) {
							logger.error(ex.getMessage(), ex);
						}
					}
				}

			}
		});
	}

	/**
	 * Search mutations in current alignment file and persist them to a CSV file
	 * named after the alignment file.
	 */
	private void searchForMutations() {
		mutationsList = MutationScanner.scanForMutationsInRegion(0, Viewer.getController().lastReadEndPos(), MutationFilter.instanceAllAllowed());
	}

	/**
	 * Convert a {@link String} to array of {@code byte}s. Uses only size narrowing
	 * from character[] to byte[]
	 * @param string string to convert
	 * @return characters of string in array of bytes
	 */
	private static byte[] getBytes(String string) {
		byte array[] = new byte[string.length()];
		for (int i = 0; i < string.length(); ++i) {
			array[i] = (byte) string.charAt(i);
		}
		return array;
	}

	/**
	 * Convert byte array to char array. Uses only type expansion without charset
	 * conversion. The returning array contains only representative positions.
	 * @param bytes array of bytes
	 * @param limit representative indexes in array
	 * @return array of chars
	 */
	private static char[] getChars(byte bytes[], int limit) {
		char array[] = new char[limit];
		for (int i = 0; i < limit; ++i) {
			array[i] = (char) bytes[i];
		}
		return array;
	}
}
