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

import hu.astrid.mapping.exception.GffFileFormatException;
import hu.astrid.mapping.io.GffReader;
import hu.astrid.mapping.model.GffRecord;
import hu.astrid.mvc.swing.AbstractModel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;

/**
 * Viewer model for handling GFF files
 * @author Szuni
 */
public class ViewerGffModel extends AbstractModel {

	/** Annotations load state <br> {@code true} means loaded, {@code false} means unloaded.<br>
	{@link ViewerGffModel#setAnnotationsLoaded(java.io.File)}  */
	public static final String ANNOTATIONS_LOAD = "AnnotationsLoaded";
	/**GFF reader*/
	private GffReader gffReader;
	/**Name of gff file*/
	private String fileName;
	/**Map of annotations with parent record az key*/
	private Map<String, Set<GffRecord>> annotationsMap = new HashMap<String, Set<GffRecord>>();
	private int maxLengthOfAnnotations;
	private Logger logger = Logger.getLogger(ViewerGffModel.class);

	/**
	 * @return Name of gff file
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @return last annotated position
	 */
	public int getMaxLengthOfAnnotations() {
		return maxLengthOfAnnotations;
	}

	/**
	 * Load annotations from file, build hierarchy and store it in a map.
	 * In the map the keys represent groups - records that have childrens. A group
	 * can be children of another group.
	 * @param file GFF file, if {@code null} just unload happens
	 * @throws IOException
	 * @throws GffFileFormatException
	 */
	public void setAnnotationsLoaded(File file) throws IOException, GffFileFormatException {
		unloadAnnotations();
		if (file != null) {
			try {
				gffReader = new GffReader(file);
				fileName = file.getName();
			} catch (FileNotFoundException ex) {
				throw ex;
			}
			GffRecord record = null;
			
			try {
				while ((record = gffReader.nextRecord()) != null) {
					if (record.getEnd() > maxLengthOfAnnotations) {
						maxLengthOfAnnotations = record.getEnd();
					}
					if (!annotationsMap.containsKey(record.getType())) {
						annotationsMap.put(record.getType(), new HashSet<GffRecord>());
					}
					annotationsMap.get(record.getType()).add(record);
				}
				gffReader.close();
			} catch (IOException ex) {
				logger.error(ex.getMessage(), ex);
				throw ex;
			} catch (GffFileFormatException ex) {
				logger.error(ex.getMessage(), ex);
				throw ex;
			}

			firePropertyChange(ANNOTATIONS_LOAD, null, fileName);
		}
	}

	/**
	 * @return true if there are annotations loaded from GFF file
	 */
	public boolean isAnnotationsLoaded() {
		return !annotationsMap.isEmpty();
	}

	/**
	 * @return Annotation hierarchy. In the map every key represents a feature type,
	 * the values are sets of representative records of specific type.
	 */
	public Map<String, Set<GffRecord>> getAnnotations() {
		return new HashMap<String, Set<GffRecord>>(annotationsMap);
	}

	/**
	 * @return annotations types contained in current file
	 */
	public Set<String> getAnnotationTypes() {
		return new HashSet<String>(annotationsMap.keySet());
	}

	/**
	 * Unload the annotations, notify views
	 */
	private void unloadAnnotations() {
		if (fileName != null) {
			fileName = null;
			maxLengthOfAnnotations = 0;
			annotationsMap.clear();
			firePropertyChange(ANNOTATIONS_LOAD, fileName, null);
		}
	}
}
