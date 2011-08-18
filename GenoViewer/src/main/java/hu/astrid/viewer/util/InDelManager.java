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

package hu.astrid.viewer.util;

import hu.astrid.mapping.exception.MappingFileFormatException;
import hu.astrid.mapping.model.AlignmentRecord;
import hu.astrid.viewer.Viewer;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;

/**
 *
 * @author onagy
 */
public class InDelManager {

	private int minCoverage;
	private final int BUFFER_SIZE = Viewer.getApplicationProperties().getBufferSize();
	List<InDel> insertions = new LinkedList<InDel>();
	List<InDel> deletions = new LinkedList<InDel>();
	private int maxReadPos;
	private AtomicBoolean isSearchCancelled;
	private ExecutorService executorService;
	private Future<IndelSearchResult> searchResult;
	private IndelSearchResult lastSearch;
	private static Logger logger = Logger.getLogger(InDelManager.class);

	public InDelManager(int coverage, int lastReadEndPos) {
		if (coverage < 1) {
			this.minCoverage = 1;
			logger.warn("Coverage parameter (" + +coverage + ") must not lower than 1, using default (1)");
		} else {
			this.minCoverage = coverage;
		}
		isSearchCancelled = new AtomicBoolean(false);
		executorService = Executors.newSingleThreadExecutor();
		maxReadPos = lastReadEndPos;
		lastSearch = null;

		logger.trace("New IndelManager created  with coverage [" + coverage + "] and lastReadEndPos [" + lastReadEndPos + "]");
	}

	public int getMinCoverage() {
		return minCoverage;
	}

	List<Integer> getValidPositions(List<InDel> originalList) {
		List<Integer> indelPositions = new LinkedList<Integer>();
		for (InDel inDel : originalList) {
			if (inDel.getOccurence() >= minCoverage) {
				indelPositions.add(new Integer(inDel.getInDelPosition()));
			}
		}
		return indelPositions;
	}

	public InDelManager setMinCoverage(int coverageValue) {
		if (coverageValue < 1) {
			throw new IllegalStateException("Coverage cannot be lower than 1. Coverage value was:" + coverageValue);
		}
		this.minCoverage = coverageValue;
		return this;
	}

	public void printValidInsertions() {
		List<Integer> insertionPositions = this.getValidPositions(insertions);
		for (Integer insertion : insertionPositions) {
			logger.info(insertion);
		}
	}

	public void printAllInsertions() {
		for (InDel inDel : insertions) {
			logger.info(inDel.inDelPosition + ";" + inDel.occurence);
		}
	}

	public void printAllDeletions() {
		for (InDel inDel : deletions) {
			logger.info(inDel.inDelPosition + ";" + inDel.occurence);
		}
	}

	public void printValidDeletions() {
		List<Integer> deletionPositions = this.getValidPositions(deletions);
		for (Integer deletion : deletionPositions) {
			logger.info(deletion);
		}
	}

	class InDel implements Comparable<InDel> {

		private int inDelPosition;
		private int occurence;
		private int length;

		InDel(int position, int length, int occurence) {
			this.inDelPosition = position;
			this.occurence = occurence;
			this.length = length;
		}

		void increaseOccurenceByOne() {
			this.occurence++;
		}

		void increaseOccurence(int value) {
			this.occurence += value;
		}

		int getOccurence() {
			return this.occurence;
		}

		int getInDelPosition() {
			return this.inDelPosition;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null || !(obj instanceof InDel)) {
				return false;
			}
			if (obj == this) {
				return true;
			}
			InDel inDel = (InDel) obj;
			if (inDel.inDelPosition == this.inDelPosition && inDel.length == this.length) {
				return true;
			}
			return false;
		}

		@Override
		public int compareTo(InDel o) {
			if (o == null) {
				throw new IllegalArgumentException("compareTo param must not point to null!");
			}
			if (inDelPosition == o.getInDelPosition()) {
				return 0;
			} else if (this.inDelPosition > o.getInDelPosition()) {
				return 1;
			} else {
				return -1;
			}
		}

		public int getLength() {
			return this.length;
		}
	}

	public boolean hasNextDeletion(int fromPosition) {
		isSearchCancelled.set(false);
		if (this.hasInLocalCache(deletions, fromPosition, Direction.FORWARD)) {
			return true;
		}
		try {
			searchResult = executorService.submit(new SearchForIndelTask(fromPosition, maxReadPos, IndelType.DELETION, Direction.FORWARD, minCoverage));
			lastSearch = searchResult.get();
			deletions = lastSearch.indelList;
			while (!lastSearch.lastIndelReached && getValidPositions(deletions).isEmpty() && !isSearchCancelled.get()) {
				fromPosition += BUFFER_SIZE;
				searchResult = executorService.submit(new SearchForIndelTask(fromPosition, maxReadPos, IndelType.DELETION, Direction.FORWARD, minCoverage));
				lastSearch = searchResult.get();
				deletions = lastSearch.indelList;
			}
			/*
			 * System.out.println("Search cancelled:" +
			 * isSearchCancelled.get()); System.out.println("LastIndelReached:"
			 * + lastSearch.lastIndelReached);
			 * System.out.println("List is empty:" +
			 * getValidPositions(deletions).isEmpty());
			 */
			if (isSearchCancelled.get() && getValidPositions(deletions).isEmpty()) {
				return false;
			}
			if (lastSearch.lastIndelReached && getValidPositions(deletions).isEmpty()) {
				return false;
			}
		} catch (Exception exc) {
			logger.error(exc.getMessage(), exc);
		}
		return true;
	}

	public boolean hasNextInsertion(int fromPosition) {
		isSearchCancelled.set(false);
		if (this.hasInLocalCache(insertions, fromPosition, Direction.FORWARD)) {
			return true;
		}
		try {
			searchResult = executorService.submit(new SearchForIndelTask(fromPosition, maxReadPos, IndelType.INSERTION, Direction.FORWARD, minCoverage));
			lastSearch = searchResult.get();
			insertions = lastSearch.indelList;
			while (!lastSearch.lastIndelReached && getValidPositions(insertions).isEmpty() && !isSearchCancelled.get()) {
				fromPosition += BUFFER_SIZE;
				searchResult = executorService.submit(new SearchForIndelTask(fromPosition, maxReadPos, IndelType.INSERTION, Direction.FORWARD, minCoverage));
				lastSearch = searchResult.get();
				insertions = lastSearch.indelList;
			}
			if (isSearchCancelled.get() && getValidPositions(insertions).isEmpty()) {
				return false;
			}
			if (lastSearch.lastIndelReached && getValidPositions(insertions).isEmpty()) {
				return false;
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return true;
	}

	public boolean hasPrevDeletion(int toPosition) {
		isSearchCancelled.set(false);
		if (this.hasInLocalCache(deletions, toPosition, Direction.BACKWARD)) {
			return true;
		}
		try {
			searchResult = executorService.submit(new SearchForIndelTask(toPosition, maxReadPos, IndelType.DELETION, Direction.BACKWARD, minCoverage));
			lastSearch = searchResult.get();
			deletions = lastSearch.indelList;
			while (!lastSearch.firstIndelReached && getValidPositions(deletions).isEmpty() && !isSearchCancelled.get()) {
				toPosition -= BUFFER_SIZE;
				searchResult = executorService.submit(new SearchForIndelTask(toPosition, maxReadPos, IndelType.DELETION, Direction.BACKWARD, minCoverage));
				lastSearch = searchResult.get();
				deletions = lastSearch.indelList;
			}
			if (isSearchCancelled.get() && getValidPositions(deletions).isEmpty()) {
				return false;
			}
			if (lastSearch.firstIndelReached && getValidPositions(deletions).isEmpty()) {
				return false;
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return true;
	}

	public boolean hasPrevInsertion(int toPosition) {
		isSearchCancelled.set(false);
		if (this.hasInLocalCache(insertions, toPosition, Direction.BACKWARD)) {
			return true;
		}
		try {
			searchResult = executorService.submit(new SearchForIndelTask(toPosition, maxReadPos, IndelType.INSERTION, Direction.BACKWARD, minCoverage));
			lastSearch = searchResult.get();
			insertions = lastSearch.indelList;
			while (!lastSearch.firstIndelReached && getValidPositions(insertions).isEmpty() && !isSearchCancelled.get()) {
				toPosition -= BUFFER_SIZE;
				searchResult = executorService.submit(new SearchForIndelTask(toPosition, maxReadPos, IndelType.INSERTION, Direction.BACKWARD, minCoverage));
				lastSearch = searchResult.get();
				insertions = lastSearch.indelList;
			}
			if (isSearchCancelled.get() && getValidPositions(insertions).isEmpty()) {
				return false;
			}
			if (lastSearch.firstIndelReached && getValidPositions(insertions).isEmpty() && !isSearchCancelled.get()) {
				return false;
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return true;
	}

	public int getNextDeletion(int position) {
		for (InDel inDel : deletions) {
			if (inDel.getInDelPosition() >= position && inDel.getOccurence() >= minCoverage) {
				return inDel.getInDelPosition();
			}
		}
		throw new UnsupportedOperationException("Ooops... Never should happen ");
	}

	public int getLengthOfNextDeletion(int position) {
		for (InDel inDel : deletions) {
			if (inDel.getInDelPosition() >= position && inDel.getOccurence() >= minCoverage) {
				return inDel.getLength();
			}
		}
		throw new UnsupportedOperationException("WTF... Never should happen ");
	}

	public int getNextInsertion(int position) {
		for (InDel inDel : insertions) {
			if (inDel.getInDelPosition() >= position && inDel.getOccurence() >= minCoverage) {
				return inDel.getInDelPosition();
			}
		}
		throw new UnsupportedOperationException("Ooops... Never should happen ");
	}

	public int getLengthOfNextInsertion(int position) {
		for (InDel inDel : insertions) {
			if (inDel.getInDelPosition() >= position && inDel.getOccurence() >= minCoverage) {
				return inDel.getLength();
			}
		}
		throw new UnsupportedOperationException("Ooops... Never should happen ");
	}

	public int getPrevDeletion(int position) {
		for (int i = deletions.size() - 1; i >= 0; i--) {
			if (deletions.get(i).getInDelPosition() <= position && deletions.get(i).getOccurence() >= minCoverage) {
				return deletions.get(i).getInDelPosition();
			}
		}
		throw new UnsupportedOperationException("Ooops... Never should happen ");
	}

	public int getPrevInsertion(int position) {
		for (int i = insertions.size() - 1; i >= 0; i--) {
			if (insertions.get(i).getInDelPosition() <= position && insertions.get(i).getOccurence() >= minCoverage) {
				return insertions.get(i).getInDelPosition();
			}
		}
		throw new UnsupportedOperationException("Ooops... Never should happen ");
	}

	private boolean hasInLocalCache(List<InDel> inDelList, int position, Direction direction) {
		if (lastSearch == null) {
			return false;
		} else if ((position < lastSearch.searchedFrom) || (position > lastSearch.searchedTo) || inDelList.isEmpty()) {
			return false;
		}
		if (direction == Direction.BACKWARD && inDelList.get(0).getInDelPosition() > position) {
			return false;
		} else if (direction == Direction.FORWARD && inDelList.get(inDelList.size() - 1).getInDelPosition() < position) {
			return false;
		}
		if (direction == Direction.BACKWARD) {
			for (InDel inDel : inDelList) {
				if (inDel.getInDelPosition() <= position && inDel.getOccurence() >= this.minCoverage) {
					return true;
				}
			}
		} else if (direction == Direction.FORWARD) {
			for (InDel inDel : inDelList) {
				if (inDel.getInDelPosition() >= position && inDel.getOccurence() >= this.minCoverage) {
					return true;
				}
			}
		}
		return false;
	}

	public Integer[] getSearchInterval() {
		if (lastSearch == null) {
			throw new NullPointerException("Not happened any search yet!");
		}
		return (new Integer[]{lastSearch.searchedFrom, lastSearch.searchedTo});
	}

	public void cancelRunnningSearch() {

		isSearchCancelled.set(true);
	}

	public void setMaxReadLength(int maxReadPos) {

		this.maxReadPos = maxReadPos;
	}

	public boolean isSearchCancelled() {
		return isSearchCancelled.get();
	}

	private class IndelSearchResult {

		private boolean lastIndelReached;
		private boolean firstIndelReached;
		private int searchedFrom;
		private int searchedTo;
		private List<InDel> indelList;
		private boolean indelFound;
	}

	public class SearchForIndelTask implements Callable<IndelSearchResult> {

		private final IndelType indelType;
		private final Direction direction;
		private final int position;
		private final int lastReadPos;
		private final int coverage;

		SearchForIndelTask(int position, int lastReadPos, IndelType indelType, Direction direction, int minCoverage) {
			this.indelType = indelType;
			this.direction = direction;
			this.position = position;
			this.lastReadPos = lastReadPos;
			this.coverage = minCoverage;
		}

		@Override
		public IndelSearchResult call() throws Exception {
			IndelSearchResult searchResult = new IndelSearchResult();

			List<AlignmentRecord> reads = null;
			List<InDel> indelList = new LinkedList<InDel>();
			if (direction == Direction.BACKWARD) {
				searchResult.searchedFrom = Math.max(position - BUFFER_SIZE, 0);
				searchResult.searchedTo = position;
			} else if (direction == Direction.FORWARD) {
				searchResult.searchedFrom = position;
				searchResult.searchedTo = Math.min(position + BUFFER_SIZE, maxReadPos);
			}
			try {
				reads = Viewer.getController().loadReadsForInDel(searchResult.searchedFrom, searchResult.searchedTo);
				logger.trace("Demanded region:[" + searchResult.searchedFrom + "-" + searchResult.searchedTo + "]");
				logger.trace("Given region:[" + (reads.size() > 0 ? reads.get(0).getPosition() + "-" + reads.get(reads.size() - 1).getPosition() : "empty") + "]");
			} catch (MappingFileFormatException exc) {
				logger.error("Error loading reads!", exc);
			} catch (IOException exc) {
				logger.error("Error loading reads!", exc);
			}
			if (reads != null) {
				processAlignments(searchResult.searchedFrom, searchResult.searchedTo, indelList, reads, indelType);
				searchResult.firstIndelReached = false;
				searchResult.lastIndelReached = false;
				if (searchResult.searchedFrom == 0) {
					searchResult.firstIndelReached = true;
				} else if (searchResult.searchedTo >= this.lastReadPos) {
					searchResult.lastIndelReached = true;
				}
			} else {
				throw new IllegalArgumentException("reads returned from loadReads(int, int) cannot be null!");
			}
			logger.trace("Original indel list:");
			for (InDel inDel : indelList) {
				logger.trace("\t" + inDel.getInDelPosition() + " with occ:" + inDel.getOccurence());
			}
			// searchResult.indelList = cementIndelList(indelList);
			searchResult.indelList = indelList;
			if (searchResult.indelList.size() > 0) {
				Collections.sort(indelList);
				searchResult.indelFound = true;
			} else {
				searchResult.indelFound = false;
			}
			logger.trace("Not really \"cemented\" indel list:");
			for (InDel inDel : searchResult.indelList) {
				logger.trace("\t" + inDel.getInDelPosition() + " with occ:" + inDel.getOccurence());
			}
			return searchResult;
		}

		private void addNewIndel(List<InDel> indelList, int indelPosition, int indelLength) {
			InDel inDel = new InDel(indelPosition, indelLength, 1);
			int indexOfInDel = indelList.indexOf(inDel);
			if (indexOfInDel != -1) {
				indelList.get(indexOfInDel).increaseOccurenceByOne();
			} else {
				indelList.add(inDel);
			}
		}

		private void processAlignments(int from, int to, List<InDel> indelList, List<AlignmentRecord> AlignmentRecordList, IndelType inDelType) {
			for (AlignmentRecord item : AlignmentRecordList) {
				int deletionsLength = 0;
				int insertionsLength = 0;
				List<Character> operators = Alignment.parseOperators(item.getCigar());
				List<Integer> sizes = Alignment.parseSizes(item.getCigar());
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
							if (inDelType == IndelType.INSERTION) {
								int pos = item.getPosition() + index + diff - clipSize;
								if (pos >= from && pos <= to) {
									this.addNewIndel(indelList, pos, sizes.get(i));
								}
							}
							index += size;
							break;
						}
						case 'D': {
							int diff = deletionsLength - insertionsLength;
							deletionsLength += sizes.get(i);
							if (inDelType == IndelType.DELETION) {
								int pos = item.getPosition() + index + diff - clipSize;
								if (pos >= from && pos <= to) {
									this.addNewIndel(indelList, pos, sizes.get(i));
								}
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
			}
		}
	}

	public int getDeletionCoverage(int position) {
		for (InDel inDel : deletions) {
			if (inDel.getInDelPosition() >= position) {
				return inDel.getOccurence();
			}
		}
		throw new UnsupportedOperationException("WTF... Never should happen ");
	}

	public float getInsertionCoverage(int pos) {
		for (InDel inDel : insertions) {
			if (inDel.getInDelPosition() >= pos) {
				return inDel.getOccurence();
			}
		}
		throw new UnsupportedOperationException("WTF... Never should happen ");
	}
}

enum Direction {

	FORWARD, BACKWARD;
}

enum IndelType {

	INSERTION, DELETION;
}
