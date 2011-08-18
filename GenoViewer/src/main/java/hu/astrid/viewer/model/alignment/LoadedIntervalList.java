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

package hu.astrid.viewer.model.alignment;

/**
 * Stores intervals loaded from alignmnent file
 * @author Szuni
 */
public class LoadedIntervalList {

	/**Length of refrence where readls loaded form*/
	protected int referenceLength;
	/**First element of list*/
	protected LoadIntervalListElement first;

	/**
	 * Construct new list
	 * @param referenceLength length of refrence where readls loaded form
	 */
	public LoadedIntervalList(int referenceLength) {
		this.referenceLength = referenceLength;
	}

	/**
	 * Initialize the list with the given interval
	 * @param start interval start (inclusive)
	 * @param end interval end (exclusive)
	 * @throws IllegalArgumentException - if interval exceeds 0 - {@code referenceLength}<br>
	 *									- if interval length less than 1
	 */
	public synchronized void init(int start, int end) {
		if (start < 0 || end > referenceLength) {
			throw new IllegalArgumentException("Incorrect interval length " + start + "-" + end + " by reference length " + referenceLength);
		}
		first = new LoadIntervalListElement(start, end);
	}

	/**
	 * Add an interval to the list. Merge the new interval to another if posible.
	 * @param start interval start (inclusive)
	 * @param end interval end (exclusive)
	 * @throws IllegalArgumentException - if interval exceeds 0 - {@code referenceLength}<br>
	 *									- if interval overlaps a previous interval<br>
	 *									- if interval length less than 1
	 */
	public synchronized void add(int start, int end) {
		if (start < 0 || end > referenceLength) {
			throw new IllegalArgumentException("Incorrect interval length " + start + "-" + end + " by reference length " + referenceLength);
		}
		LoadIntervalListElement actElement = first;
		LoadIntervalListElement prevElement = null;
		while (actElement != null) {
			if (start < actElement.interval.start) {
				if (end > actElement.interval.start) {
					throw new IllegalArgumentException("Overlapping intervals " + actElement.interval.start + "-" + actElement.interval.end + " " + start + "-" + end);
				} else {
					LoadIntervalListElement newElement = new LoadIntervalListElement(start, end);
					newElement.next = actElement;
					if (prevElement == null) {
						first = newElement;
					} else {
						prevElement.next = newElement;
					}
					merge(prevElement, newElement, actElement);
					return;
				}
			} else if (start >= actElement.interval.start && start < actElement.interval.end) {
				throw new IllegalArgumentException("Overlapping intervals " + actElement.interval.start + "-" + actElement.interval.end + " " + start + "-" + end);
			}
			prevElement = actElement;
			actElement = actElement.next;
		}
		if (start >= prevElement.interval.start && start < prevElement.interval.end) {
			throw new IllegalArgumentException("Overlapping intervals " + actElement.interval.start + "-" + actElement.interval.end + " " + start + "-" + end);
		} else {
			LoadIntervalListElement newElement = new LoadIntervalListElement(start, end);
			prevElement.next = newElement;
			merge(prevElement, newElement, null);
			return;
		}
	}

	/**
	 * Merg the actual element with the neighbours if boundaries match
	 * @param prev previous element
	 * @param act actual element
	 * @param next next element
	 */
	private void merge(LoadIntervalListElement prev, LoadIntervalListElement act, LoadIntervalListElement next) {
		act = mergeToFirst(act, next);
		mergeToSecond(prev, act);
	}

	/**
	 * Merge the second element into the first. The first interval end is extended and
	 * the sublist after second element is concatenated to the first element. After that
	 * try to merge fist element to its next element.
	 * @param firstElement
	 * @param secondElement
	 * @return head of merged sublist start with first element
	 */
	private LoadIntervalListElement mergeToFirst(LoadIntervalListElement firstElement, LoadIntervalListElement secondElement) {
		if (secondElement != null && firstElement.interval.end == secondElement.interval.start) {
			firstElement = new LoadIntervalListElement(firstElement.interval.start, secondElement.interval.end);
			firstElement.next = secondElement.next;
			firstElement = mergeToFirst(firstElement, firstElement.next);
		}
		return firstElement;
	}

	/**
	 * Merger the {@code firstElement} into {@code secondElement}. The second interval
	 * start is extended and {@code firstElement} in the list is changed to the extended
	 * secondElement. After that try to merge first elements previous to the extended secondElement.
	 * If the head of the list is merged into another element, head is changed.
	 * @param firstElement
	 * @param secondElement
	 * @return head of merged sublist start with element same interval start as {@code firstElement}
	 */
	private LoadIntervalListElement mergeToSecond(LoadIntervalListElement firstElement, LoadIntervalListElement secondElement) {
		if (firstElement != null && firstElement.interval.end == secondElement.interval.start) {
			secondElement = new LoadIntervalListElement(firstElement.interval.start, secondElement.interval.end);
			if(firstElement.equals(first)) {
				return mergeToSecond(null, secondElement);
			}
			LoadIntervalListElement actElement = first;
			while (actElement.next != null && !actElement.next.equals(firstElement)) {
				actElement = actElement.next;
			}
			actElement.next=secondElement;
			secondElement = mergeToSecond(actElement, secondElement);
		}
		if(firstElement==null) {
			first = secondElement;
		}
		return secondElement;
	}

	/**
	 * Clear list
	 */
	public synchronized void clear() {
		first = null;
	}

	/**
	 * Determine that list contains the given interval.
	 * @param start interval start
	 * @param end interval end
	 * @return {@code true} - if list contains an interval that contains the given interval
	 */
	public synchronized boolean contains(int start, int end) {
		if (start < first.interval.start) {
			return false;
		}
		LoadIntervalListElement actElement = first;
		while (actElement != null && actElement.interval.end <= start) {
			actElement = actElement.next;
		}
		if (actElement == null) {
			return false;
		} else {
			return actElement.interval.start <= start && actElement.interval.end >= end;
		}
	}

	/**
	 * @param point point to be searched wether it it contained
	 * @return an interval that containing point or null if point is not loaded
	 */
	public synchronized Interval getContainigInterval(int point) {
		if (point < first.interval.start) {
			return null;
		}
		LoadIntervalListElement actElement = first;
		while (actElement != null && actElement.interval.end <= point) {
			actElement = actElement.next;
		}
		if (actElement == null || actElement.interval.start > point) {
			return null;
		} else {
			return actElement.interval;
		}
	}

	/**
	 * //TODO nincs kész
	 * @param start
	 * @param end
	 * @return
	 */
	private Interval clipInterval(int start, int end) {
		if (start < 0 || end > referenceLength) {
			throw new IllegalArgumentException("Incorrect interval length " + start + "-" + end + " by reference length " + referenceLength);
		}
		LoadIntervalListElement actElement = first;
		LoadIntervalListElement prevElement = null;
		while (actElement != null) {
			if (actElement.interval.start >= start) {
				int nextStart = actElement.next != null ? actElement.next.interval.start : referenceLength;
				return new Interval(Math.max(actElement.interval.end, start), Math.min(end, actElement.interval.start));
			}
			prevElement = actElement;
			actElement = actElement.next;
		}
		return new Interval(start, end);
	}

	/**
	 * @return number of elements in list
	 */
	public synchronized int size() {
		int size = 0;
		LoadIntervalListElement act = first;
		while (act != null) {
			size++;
			act = act.next;
		}
		return size;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("LoadedIntervalList{");
		LoadIntervalListElement act = first;
		while (act != null) {
			sb.append(act);
			act = act.next;
			if (act != null) {
				sb.append(", ");
			}
		}
		return sb.toString() + '}';
	}

	/**
	 * Element of the list
	 */
	protected static class LoadIntervalListElement {

		/**Next element in the list*/
		protected LoadIntervalListElement next;
		public final Interval interval;

		/**
		 * Creates an initial interval
		 * @param start interval start
		 * @param end interval end
		 */
		public LoadIntervalListElement(int start, int end) {
			this.interval = new Interval(start, end);
		}


		@Override
		public String toString() {
			return interval.start + "-" + interval.end;
		}
	}
}
