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

package hu.astrid.viewer.model.mutation;

import hu.astrid.mapping.exception.MappingFileFormatException;
import hu.astrid.mapping.model.AlignmentRecord;
import hu.astrid.viewer.Viewer;
import hu.astrid.viewer.gui.StatusBar.ProgressValue;
import hu.astrid.viewer.model.Coverage;
import hu.astrid.viewer.util.Alignment;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;

/**
 * Utility class for different search methods made on mutations in alignment records.
 * @author onagy
 */
public class MutationScanner {

	private static final Logger logger = Logger.getLogger(MutationScanner.class);
	private BlockingQueue<AlignmentLoadingResult> workQueue;
	private final ExecutorService executorService = Executors.newFixedThreadPool(2);
	private final AlignmentLoadingResult SPECIAL_END_MARKER_RESULT = new AlignmentLoadingResult(new ArrayList<AlignmentRecord>(), -1, -1);

	private static List<MutationRegion> scanForMutationsInRecord(AlignmentRecord alignmentRecord, MutationFilter filter) {

		List<MutationRegion> resultList = new ArrayList<MutationRegion>();

		int deletionsLength = 0;
		int insertionsLength = 0;
		List<Character> operators = Alignment.parseOperators(alignmentRecord.getCigar());
		List<Integer> sizes = Alignment.parseSizes(alignmentRecord.getCigar());
		List<Integer> lengthModificators = new LinkedList<Integer>();
		StringBuffer strBuffer = null;
		int index = 0;
		boolean deletionOccured = false;
		int clipSize = 0;
		for (int i = 0; i < operators.size(); ++i) {
			int size = sizes.get(i);
			if (deletionOccured) {
				size--;
				deletionOccured = false;
			}
			switch (operators.get(i)) {
				case 'I': {
					int diff = deletionsLength - insertionsLength;
					insertionsLength += sizes.get(i);
					//we don't need insertion's length because they feature in read data
					lengthModificators.add(-sizes.get(i));
					//only for snp finding, they require previous indel data
//					lengthModificators.add(0);

					String seq = alignmentRecord.getSequence().substring(index - 1, index + size + 1);

					if (filter.isIsInterestedInInsertion()) {
						resultList.add(new MutationRegion((alignmentRecord.getPosition() + index + diff - clipSize), sizes.get(i),
								MutationType.INSERTION, seq));
					}
					index += size;
					break;
				}
				case 'D': {
					int diff = deletionsLength - insertionsLength;
					deletionsLength += sizes.get(i);
					lengthModificators.add(new Integer(sizes.get(i)));

					strBuffer = new StringBuffer();
					for (int j = 0; j < sizes.get(i); j++) {
						strBuffer.append("*");
					}

					if (filter.isIsInterestedInDeletion()) {
						resultList.add(new MutationRegion((alignmentRecord.getPosition() + index + diff - clipSize), sizes.get(i),
								MutationType.DELETION, strBuffer.toString()));
					}

					break;
				}
				case 'M':
				case 'N': {
					index += size;
					break;
				}
				case 'S': {
					clipSize += size;
					index += size;
					break;
				}
				default: {
					throw new IllegalArgumentException("Invalid operator:" + operators.get(i));
				}
			}
		}

		resultList.addAll(findSnps(alignmentRecord, lengthModificators, filter));

		return resultList;
	}

	/**
	 *
	 * @param startPos global position, where the search should be start
	 * @param endPos end of the search region
	 * @param mutationFilter a filter object for filtering mutations. See@{@link MutationFilter MutationFilter}
	 * @return list of collected mutations in a given region, unwanted instances filtered out
	 */
	@SuppressWarnings("empty-statement")
	public static List<Mutation> scanForMutationsInRegion(int startPos, int endPos, MutationFilter mutationFilter) {

		List<Mutation> mutations = null;
		List<AlignmentRecord> reads = null;
		Map<Integer, Coverage> coverageData = null;
		Map<MutationRegion, OccurenceData> mutationsWithInfo = new HashMap<MutationRegion, OccurenceData>();
		OccurenceData occurenceData = null;
		long fullRun = 0;
		long startTime = System.nanoTime();
		long endTime;


		int intervalLength = endPos - startPos, progressValue = 0;
		Viewer.setStatusbarProgresValue(new ProgressValue("MutationSearch", progressValue));

		try {

			int bufferSize = Viewer.getPropertyHandler().getApplicationProperties().getBufferSize();

			for (int i = startPos; i < endPos; i += bufferSize) {

				reads = Viewer.getController().loadReadsForInDel(Math.max(i - Viewer.getController().getMaxReadLength(), 0),
						Math.min(endPos, i + bufferSize + Viewer.getController().getMaxReadLength()));

				coverageData = MutationScanner.generateCoverageData(reads);

				for (AlignmentRecord record : reads) {

					if (record.getPosition() >= i && record.getPosition() < i + bufferSize) {

						for (MutationRegion mutationRegion : MutationScanner.scanForMutationsInRecord(record, mutationFilter)) {

							occurenceData = mutationsWithInfo.get(mutationRegion);

							if (occurenceData == null) {
								mutationsWithInfo.put(mutationRegion, new OccurenceData(1, coverageData.get(mutationRegion.getStartPos()).getCoverage()));
							} else {
								occurenceData.increaseOccurenceByOne();
							}
						}
					} else {
						;
					}
					int genearationValue = (int) (((double) record.getPosition() / intervalLength * 100) * 0.95);
					if (genearationValue > progressValue) {
						progressValue = genearationValue;
						Viewer.setStatusbarProgresValue(new ProgressValue("MutationSearch", progressValue));
					}
				}
			}
		} catch (Exception exc) {
			exc.printStackTrace();
		}

		mutations = new ArrayList<Mutation>(mutationsWithInfo.size());
		String refSeqPart = null;
		StringBuffer strBuff;
		List<MutationRegion> sortedSet = new ArrayList<MutationRegion>(mutationsWithInfo.keySet());
		//USE NO TreeSet here, because the comparing of the mutations based on position and length, we have no any other ordering between mutations
		//so TreeSet drop some items when it gets 0 @ comparing (what means mutations @ same position with the same length)
		//TreeSet<MutationRegion> sortedSet = new TreeSet<MutationRegion>(mutationsWithOccurence.keySet());
		//Collections.sort(sortedSet);
		//TODO adaptor nucleotid kell
		Collections.sort(sortedSet);
		int index = 0;
		for (MutationRegion item : sortedSet) {

			Mutation newMutation = null;
			if (item.getMutationType().equals(MutationType.MNP) || item.getMutationType().equals(MutationType.SNP)
					|| item.getMutationType().equals(MutationType.DELETION)) {

				refSeqPart = checkAndGetRefSeqPart(item.getStartPos(), item.getLength());
				if (refSeqPart == null || refSeqPart.isEmpty()) {
					refSeqPart = "N/A";
				}
			} else if (item.getMutationType().equals(MutationType.INSERTION)) {

				strBuff = new StringBuffer();

				for (int i = 0; i < item.getLength(); i++) {
					strBuff.append("*");
				}

				refSeqPart = strBuff.toString();
			}

			occurenceData = mutationsWithInfo.get(item);

			newMutation = new Mutation(item, occurenceData.occurence, occurenceData.coverage, refSeqPart);

			mutations.add(newMutation);
			int genearationValue = (int) (0.95 + index / sortedSet.size() * 0.1);
			if (genearationValue > progressValue) {
				progressValue = genearationValue;
				Viewer.setStatusbarProgresValue(new ProgressValue("MutationSearch", progressValue));
			}
			++index;
		}

		Collections.sort(mutations, new Comparator<Mutation>() {

			public int compare(Mutation m1, Mutation m2) {

				if (m1.getStartPos() < m2.getStartPos()) {
					return -1;
				} else if (m1.getStartPos() > m2.getStartPos()) {
					return 1;
				}

				if (m1.getOccurence() < m2.getOccurence()) {
					return -1;
				}
				else if (m1.getOccurence() > m2.getOccurence()) {
					return 1;
				}

				if (m1.getLength() < m2.getLength()) {
					return -1;
				}
				else if (m1.getLength() > m2.getLength()) {
					return 1;
				}

				int compareToResult = m1.getMutationSequence().compareTo(m2.getMutationSequence());

				if (compareToResult != 0) {
					return compareToResult;
				}

				compareToResult = m1.getReferenceSequence().compareTo(m2.getReferenceSequence());
				if (compareToResult != 0) {
					return compareToResult;
				}

				return 0;
			}
		});

		endTime = System.nanoTime();

		fullRun += endTime - startTime;

		//System.out.println("Fulltime!:" + (fullRun / 1000000000.0));

		Viewer.setStatusbarProgresValue(new ProgressValue("MutationSearch", 101));

		return mutations;
	}

	/**
	 * Not yet implemented...
	 * @param fromPos
	 * @return
	 */
	public static List<MutationRegion> scanForMutationsForwardToEnd(int fromPos) {
		throw new UnsupportedOperationException("Not yet implemented...");
	}

	/**
	 * Not yet implemented...
	 * @param toPos
	 * @return
	 */
	public static List<MutationRegion> scanForMutationsBackwardToStart(int toPos) {
		throw new UnsupportedOperationException("Not yet implemented...");
	}

	private static List<MutationRegion> findSnps(AlignmentRecord read, List<Integer> lengthModificators, MutationFilter filter) {

		List<MutationRegion> SNPs = new ArrayList<MutationRegion>();
		int positionInRead = 0;
		int indelsFoundSoFar = 0;
		int adaptorPosition = -1;
		String MDTag = null;
		try {
			MDTag = read.getOptionalTag("MD").getValue();
		} catch (NullPointerException ex) {
			logger.error("File " + Viewer.getReadModel().getFileName() + " doesn't have MDTag, so there will be no SNPs in generated consensus data.", ex);
			return new ArrayList<MutationRegion>();
		}

		for (String MDTagToken : getMDTagTokens(MDTag)) {
			//token starts with digit, so simply increment indicies
			if (Character.isDigit(MDTagToken.charAt(0))) {
				positionInRead += Integer.parseInt(MDTagToken);
				adaptorPosition += Integer.parseInt(MDTagToken);
			} //insertion or deletion, so add/substract value from lengthModifications
			else if (MDTagToken.charAt(0) == '^') {
				int indelLength = lengthModificators.get(indelsFoundSoFar++).intValue();
				//Insertion doesnt modifies position, beacause it doesnt appear
				positionInRead += Math.max(indelLength, 0);
				//Because insertion doesn't appear on screen but still in the read, deletion appears on screen but not present in read
				//Just insertion modifies adaptor position
				adaptorPosition += Math.max(-indelLength, 0);
			} else {
				int mutationLength = MDTagToken.length();
				char adaptor = adaptorPosition == -1 ? 'N' : read.getSequence().charAt(adaptorPosition);
				char endAdaptor = adaptorPosition + mutationLength + 1 >= read.getSequence().length() ? 'N' : read.getSequence().charAt(adaptorPosition + mutationLength + 1);
				String mutationSequence = read.getSequence().substring(adaptorPosition+1, adaptorPosition+1+mutationLength);
				if (MDTagToken.length() == 1 && filter.isIsInterestedInSNP()) {
					SNPs.add(new MutationRegion(read.getPosition() + positionInRead, mutationLength, MutationType.SNP, adaptor + mutationSequence + endAdaptor));
				} else if (filter.isIsInterestedInMNP()) {
					SNPs.add(new MutationRegion(read.getPosition() + positionInRead, mutationLength, MutationType.MNP, adaptor + mutationSequence + endAdaptor));
				}
				positionInRead += MDTagToken.length();
				adaptorPosition += MDTagToken.length();
			}
		}

		return SNPs;
	}

	private static List<String> getMDTagTokens(String MDTag) {

		List<String> resultList = new LinkedList<String>();
		Pattern pattern = Pattern.compile("(\\d+)|([\\^ACGT]+)");

		Matcher match = pattern.matcher(MDTag);
		match.reset();
		while (match.find()) {
			resultList.add(match.group());
		}

		return resultList;
	}

	private static Map<MutationRegion, Integer> compress(List<MutationRegion> mutationRegionImpls) {

		Integer value = null;
		Map<MutationRegion, Integer> result = new HashMap<MutationRegion, Integer>();
		for (MutationRegion mutationRegion : mutationRegionImpls) {

			value = result.get(mutationRegion);

			if (value == null) {
				result.put(mutationRegion, 1);
			} else {
				result.put(mutationRegion, new Integer(value + 1));
			}

		}

		return result;
	}

	private static String checkAndGetRefSeqPart(int startPos, int length) {

//		if (LoadedRefSeq.startPos > startPos || LoadedRefSeq.endPos <= startPos + length) {
		if (!(LoadedRefSeq.startPos <= startPos && LoadedRefSeq.endPos > startPos + length)) {

			LoadedRefSeq.startPos = startPos;
			LoadedRefSeq.loadedSequence = Viewer.getController().getRefrenceAlignmentString(startPos, Viewer.getApplicationProperties().getBufferSize());
			LoadedRefSeq.endPos = startPos + LoadedRefSeq.loadedSequence.length();
		}

		if (LoadedRefSeq.loadedSequence == null || LoadedRefSeq.loadedSequence.isEmpty()) {
			return null;
		}
//		//TODO: Megnézni mivan, ha nem teljes hosszú a kinyert referencia szekvencia
//		return LoadedRefSeq.loadedSequence.substring(startPos - LoadedRefSeq.startPos, Math.min(startPos - LoadedRefSeq.startPos + length, LoadedRefSeq.loadedSequence.length()));
		return LoadedRefSeq.loadedSequence.substring(startPos - LoadedRefSeq.startPos, startPos - LoadedRefSeq.startPos + length);
	}

	private static class LoadedRefSeq {

		static int startPos = -1;
		static int endPos = -1;
		static String loadedSequence = "";
	}

	private static Map<Integer, Coverage> generateCoverageData(List<AlignmentRecord> readList) {

		if (readList == null || readList.isEmpty()) {

			return new HashMap<Integer, Coverage>();
		}

		int max = -1;

		for (AlignmentRecord record : readList) {

			if (record.getPosition() + record.getSequence().length() > max) {

				max = record.getPosition() + record.getSequence().length();
			}
		}

		int coveredInterval = max - readList.get(0).getPosition();
		int absStartPos = readList.get(0).getPosition();

		Map<Integer, Coverage> coverageMap = new HashMap<Integer, Coverage>(coveredInterval);
		List<Coverage> coverageList = new ArrayList<Coverage>(coveredInterval);

		for (int i = 0; i < coveredInterval; i++) {

			coverageList.add(new Coverage(absStartPos + i, 0));
		}

		for (AlignmentRecord record : readList) {

			int startPos = record.getPosition();

			for (int i = 0; i < record.getSequence().length(); i++) {

				coverageList.get(startPos + i - absStartPos).increaseCoverageByOne();
			}
		}

		for (Coverage coverage : coverageList) {

			coverageMap.put(coverage.getAbsPosition(), coverage);
		}

		return coverageMap;
	}

	/**
	 *
	 * @param startPos
	 * @param endPos
	 * @return
	 */
	public List<Mutation> doFullScan(int startPos, int endPos) {

		workQueue = new LinkedBlockingQueue<AlignmentLoadingResult>(10);

		System.out.println("max read length:" + Viewer.getController().getMaxReadLength());

		new ReadProducerThread(startPos, endPos, Viewer.getApplicationProperties().getBufferSize(), Viewer.getController().getMaxReadLength()).start();
		new ReadConsumerThread().start();

		return new ArrayList<Mutation>();
	}

	private class ReadProducerThread extends Thread {

		private final int from;
		private final int to;
		private final int bufferFrameSize;
		private boolean allReadLoaded;
		private final int longestRead;

		public ReadProducerThread(int from, int to, int frameSize, int longestRead) {

			if (from >= to) {

				throw new IllegalArgumentException("Start cannot be bigger or equals to end! [" + from + "-" + to + "]");
			}
			this.from = from;
			this.to = to;
			this.bufferFrameSize = frameSize;
			this.longestRead = longestRead;
		}

		@Override
		public void run() {

			allReadLoaded = false;
			int startPos = from;
			int endPos = Math.min(startPos + bufferFrameSize, to);
			final AtomicInteger jobCounter = new AtomicInteger();

			for (; startPos != endPos;) {

				final int tempStart = startPos;
				final int tempEnd = endPos;

				jobCounter.incrementAndGet();

				executorService.submit(new Runnable() {

					public void run() {
						try {
							List<AlignmentRecord> loadedReads = Viewer.getController().loadReadsForInDel(tempStart - longestRead, tempEnd + longestRead);
							workQueue.put(new AlignmentLoadingResult(loadedReads, tempStart, tempEnd));
							System.out.println("Job generated.." + (tempStart - longestRead) + "-" + (tempEnd + longestRead));
						} catch (InterruptedException ex) {
							java.util.logging.Logger.getLogger(MutationScanner.class.getName()).log(Level.SEVERE, null, ex);
						} catch (IOException ex) {
							java.util.logging.Logger.getLogger(MutationScanner.class.getName()).log(Level.SEVERE, null, ex);
						} catch (MappingFileFormatException ex) {
							java.util.logging.Logger.getLogger(MutationScanner.class.getName()).log(Level.SEVERE, null, ex);
						} finally {
							jobCounter.decrementAndGet();
						}
					}
				});

				startPos = endPos;
				endPos = Math.min(startPos + bufferFrameSize, to);
			}

			while (jobCounter.get() != 0) {
				//System.out.println("Waiting....");
			}

			try {
				workQueue.put(SPECIAL_END_MARKER_RESULT);
			} catch (InterruptedException ex) {
				java.util.logging.Logger.getLogger(MutationScanner.class.getName()).log(Level.SEVERE, null, ex);
			}

			allReadLoaded = true;
		}

		public boolean isAllReadLoaded() {

			return this.allReadLoaded;
		}
	}

	private class AlignmentLoadingResult {

		final List<AlignmentRecord> reads;
		final int start;
		final int end;

		public AlignmentLoadingResult(List<AlignmentRecord> reads, int start, int end) {
			this.reads = reads;
			this.start = start;
			this.end = end;
		}

		@Override
		public String toString() {

			if (reads == null || reads.isEmpty()) {

				return ("No reads found in region [" + start + "-" + end + "]");
			}

			return ("[" + start + "-" + end + "] 1.read's start:" + reads.get(0).getPosition() + " last read's start:"
					+ reads.get(reads.size() - 1).getPosition() + " and last read's end:" + (reads.get(reads.size() - 1).getPosition()
					+ reads.get(reads.size() - 1).getSequence().length()));
		}

		@Override
		public boolean equals(Object obj) {

			if (!(obj instanceof AlignmentLoadingResult)) {
				return false;
			}

			AlignmentLoadingResult readLoadingResult = (AlignmentLoadingResult) obj;

			if (readLoadingResult.start != this.start || readLoadingResult.end != this.end) {
				return false;
			}

			return true;
		}

		@Override
		public int hashCode() {
			int hash = 13;
			hash = 111 * hash + this.start;
			hash = 193 * hash + this.end;
			return hash;
		}
	}

	private class ReadConsumerThread extends Thread {

		public ReadConsumerThread() {
		}

		@Override
		public void run() {

			Map<Integer, List<Mutation>> mutationData = new HashMap<Integer, List<Mutation>>(1);
			final AtomicInteger counter = new AtomicInteger(0);

			//List<Future<MutationSearchResult>> futureResults = new ArrayList<Future<MutationSearchResult>>(1000);

			CompletionService<MutationSearchResult> completionService = new ExecutorCompletionService<MutationSearchResult>(
					Executors.newFixedThreadPool(2));

			long startTime = System.nanoTime();

			while (true) {
				final AlignmentLoadingResult readLoadingResult = workQueue.poll();
				if (readLoadingResult == null) {
					continue;
				}
				//special marker object..
				if (readLoadingResult.equals(SPECIAL_END_MARKER_RESULT)) {
					break;
				}

				completionService.submit(new Callable<MutationSearchResult>() {

					public MutationSearchResult call() throws Exception {
						counter.incrementAndGet();
						return new MutationSearchResult(new ArrayList<Mutation>(1), 0, 1);
					}
				});



				Future<MutationSearchResult> future = completionService.poll();

				if (future != null) {

					System.out.println(counter.get() + ". job done!" + future);
				}

				/*
				Future<MutationSearchResult> future = execService.submit(new Callable<MutationSearchResult>() {

				public MutationSearchResult call() throws Exception {


				System.out.println("ReadLoadingResult's bounds:" + readLoadingResult.start + " - " + readLoadingResult.end);

				return new MutationSearchResult(new ArrayList<Mutation>(1), readLoadingResult.start, readLoadingResult.end);
				}

				});

				futureResults.add(future);
				 *
				 */
			}

			for (int i = 0; i < counter.get(); i++) {

				System.out.println(i + ". job's result is available!");
			}

			System.out.println("All works done, it takes "
					+ ((System.nanoTime() - startTime) / 1000000000.0) + " sec(s) to complete!");


			/*
			for (Future<MutationSearchResult> future : futureResults) {

			try {
			MutationSearchResult searchResult = future.get();
			mutationData.put(new Integer(searchResult.start), searchResult.mutations);
			System.out.println("Done processing [" + searchResult.start + "-" + searchResult.end + "]");
			} catch (InterruptedException ex) {
			java.util.logging.Logger.getLogger(MutationScanner.class.getName()).log(Level.SEVERE, null, ex);
			} catch (ExecutionException ex) {
			java.util.logging.Logger.getLogger(MutationScanner.class.getName()).log(Level.SEVERE, null, ex);
			}
			}
			 */
			System.out.println("All done...dataset's size is :" + mutationData.size());
		}
	}

	private class MutationSearchResult {

		final List<Mutation> mutations;
		final int start;
		final int end;

		public MutationSearchResult(List<Mutation> mutations, int start, int end) {
			this.mutations = mutations;
			this.start = start;
			this.end = end;
		}
	}

	private class CallableComputeTask implements Callable<MutationSearchResult> {

		private final AlignmentLoadingResult loadedAlignemnts;

		public CallableComputeTask(AlignmentLoadingResult loadedAlignemnts) {
			this.loadedAlignemnts = loadedAlignemnts;
		}

		public MutationSearchResult call() throws Exception {
			return new MutationSearchResult(new ArrayList<Mutation>(1), 0, 1);
		}
	}
}

class OccurenceData {

	int occurence;
	int coverage;

	public OccurenceData(int coverage) {
		this.occurence = 0;
		this.coverage = coverage;
	}

	public OccurenceData(int occurence, int coverage) {
		this.occurence = occurence;
		this.coverage = coverage;
	}

	public void increaseOccurenceByOne() {
		this.occurence++;
	}
}
