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

import java.awt.Color;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author onagy
 */
public class GffAnnotations {
	/** Known annotation types serve as group for semantically correct initializaton of annotation's group status*/
	public static final Set<String> knownGroups = new HashSet<String>(Arrays.asList(new String[]{"gene", "ORF", "tRNA", "snRNA", "ncRNA", "snoRNA", "rRNA", "pseudogene"})); 

	private List<GffAnnotationType> annotationTypes;

	/**
	 * Creates new instance without any annotation
	 */
	public GffAnnotations() {
		this.annotationTypes = new LinkedList<GffAnnotationType>();
	}

	/**
	 * Initialize and create instance with the given annotations
	 * @param annotationTypes the pre-defined {@link GffAnnotationType annotations}
	 */
	public GffAnnotations(List<GffAnnotationType> annotationTypes) {
		this.annotationTypes = annotationTypes;
	}

	/**
	 * Add new annotation type
	 * @param annotationType annotation tpye to be added
	 * @return same behaviour as in {@link Collection#add(java.lang.Object)}
	 */
	public boolean addAnnotation(GffAnnotationType annotationType) {
		return annotationTypes.add(annotationType);
	}

	/**
	 * Remove and unwatch annotation type
	 * @param annotationType annotation type to be removed
	 * @return true if the parameter exists and remove opertaion was successfull
	 */
	public boolean removeAnnotation(GffAnnotationType annotationType) {
		return annotationTypes.remove(annotationType);
	}

	/**
	 * Data class for grouping annotation information to support filtering
	 */
	public static class GffAnnotationType {

		private final String annotationTypeName;
		private boolean isVisibleAsFeature;
		private boolean isVisibleAsGroup;
		private Color color;

		/**
		 * Create new instance, except the name of the annotation all other attributes can be changed later
		 * @param annotationType the type of the annotatation
		 * @param isVisibleAsFeature if is visible as feature
		 * @param isVisibleAsGroup if is visible as group
		 * @param color color to be used when coloring annotation
		 */
		public GffAnnotationType(String annotationType, boolean isVisibleAsFeature, boolean isVisibleAsGroup, Color color) {
			this.annotationTypeName = annotationType;
			this.isVisibleAsFeature = isVisibleAsFeature;
			this.isVisibleAsGroup = isVisibleAsGroup;
			this.color = color;
		}

		/**
		 * Get color of annotation
		 * @return color of the annotation
		 */
		public Color getColor() {
			return color;
		}

		/**
		 * Set new color of annotation
		 * @param color new color
		 */
		public void setColor(Color color) {
			this.color = color;
		}

		/**
		 * @return true if this annotation is visibel as feature, otherwise false
		 */
		public boolean isVisibleAsFeature() {
			return isVisibleAsFeature;
		}

		/**
		 * @return human-friendly representation of this type of annotation
		 */
		@Override
		public String toString() {
			return (this.annotationTypeName + ((this.isVisibleAsFeature) ? " is" : " isn't") + " visible as feature,"
					+ ((this.isVisibleAsGroup) ? " is" : " isn't") + " visible as group and has color " + this.color.toString());
		}

		/**
		 * Set feature visibility of this annotation type
		 * @param isVisibleAsFeature should be visible or not
		 */
		public void setIsVisibleAsFeature(boolean isVisibleAsFeature) {
			this.isVisibleAsFeature = isVisibleAsFeature;
		}

		/**
		 *
		 * @return true if this annotation is visibel as group, otherwise false
		 */
		public boolean isVisibleAsGroup() {
			return isVisibleAsGroup;
		}

		/**
		 * Set group visibility of this annotation type
		 * @param isVisibleAsGroup should be visible or not
		 */
		public void setIsVisibleAsGroup(boolean isVisibleAsGroup) {
			this.isVisibleAsGroup = isVisibleAsGroup;
		}

		/**
		 * Get the name (so the tpye, too) of this annotation type
		 * @return the name (type) of this annotation type
		 */
		public String getAnnotationType() {
			return annotationTypeName;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			final GffAnnotationType other = (GffAnnotationType) obj;
			if ((this.annotationTypeName == null) ? (other.annotationTypeName != null) : !this.annotationTypeName.equals(other.annotationTypeName)) {
				return false;
			}
			return true;
		}

		@Override
		public int hashCode() {
			int hash = 7;
			hash = 97 * hash + (this.annotationTypeName != null ? this.annotationTypeName.hashCode() : 0);
			hash = 97 * hash + (this.color != null ? this.color.hashCode() : 0);
			return hash;
		}
	}

	/**
	 * Creates view about annotations, containing visibility data
	 * Be aware: this method only creates a view, you cannot modify the back-end collection by modifing the elements returned here
	 * @return annotation types and associated visibility information
	 */
	public Map<String, Boolean> annotationsVisibilityView() {

		Map<String, Boolean> retMap = new HashMap<String, Boolean>();

		for (GffAnnotationType annotationType : annotationTypes) {
			boolean isShowing = annotationType.isVisibleAsFeature;

			retMap.put(annotationType.annotationTypeName, isShowing);
		}

		return retMap;
	}

	/**
	 * Creates view about annotations, containing color data
	 * Be aware: this method only creates a view, you cannot modify the back-end collection by modifing the elements returned here
	 * @return annotation types and associated color information
	 */
	public Map<String, Color> annotationsColorView() {

		Map<String, Color> retMap = new HashMap<String, Color>();

		for (GffAnnotationType annotationType : annotationTypes) {
			
			retMap.put(annotationType.annotationTypeName, annotationType.getColor());
		}

		return retMap;
	}

	/**
	 * Creates view about annotations, containing those interpreted as group
	 * Be aware: this method only creates a view, you cannot modify the back-end collection by modifing the elements returned here
	 * @return annotation types interpreted as group
	 */
	public Set<String> annotationsGroupView() {

		Set<String> retSet = new HashSet<String>();

		for (GffAnnotationType annotationType : annotationTypes) {

			if (annotationType.isVisibleAsGroup) {

				retSet.add(annotationType.annotationTypeName);
			}
		}

		return retSet;
	}

	/**
	 * Creates view about annotations, containing all annotation names(types)
	 * Be aware: this method only creates a view, you cannot modify the back-end collection by modifing the elements returned her Be aware: this method only creates a view, you cannot modify the back-end collection by modifing the elements returned here
	 * @return
	 */
	public Set<String> annotationTypesView() {

		Set<String> retSet = new HashSet<String>();

		for (GffAnnotationType annotationType : this.annotationTypes) {
			retSet.add(annotationType.annotationTypeName);
		}

		return retSet;
	}

	/**
	 * Get {@link GffAnnotationType GffAnnotationType} with the given name, if any
	 * @param key the name of annotation
	 * @throws IllegalArgumentException if there is no any {@link GffAnnotationType GffAnnotationType} with name(type) of the key
	 * @return {@link GffAnnotationType GffAnnotationType} with name (type) equals to key
	 */
	public GffAnnotationType getAnnotation(String key) {

		for (GffAnnotationType annotationType : annotationTypes) {
			if (annotationType.annotationTypeName.equals(key)) {
				return annotationType;
			}
		}

		throw new IllegalArgumentException("Invalid annotation name [" + key + "]");
	}
}
