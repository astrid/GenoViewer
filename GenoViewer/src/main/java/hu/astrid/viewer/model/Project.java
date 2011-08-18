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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.astrid.viewer.model;

import hu.astrid.viewer.Viewer;
import hu.astrid.viewer.util.FileTypes;
import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.apache.log4j.Logger;

/**
 *
 * @author OTTO, Szuni
 */
public class Project {

	private File rootProject;
	private EnumMap<FileTypes, File> filesMap = new EnumMap<FileTypes, File>(FileTypes.class);
	/** Default logger */
	private static final Logger logger = Logger.getLogger(WorkspaceModel.class);
	private GffAnnotations annotations = null;
	private Properties loadedProperties = null;
	private boolean annotationsReady = false;

	public Project(File root) {
		this.rootProject = root;
		File projectProperty = new File(root.getAbsolutePath() + System.getProperty("file.separator") + root.getName() + ".properties");
		if (!projectProperty.exists()) {
			try {
				projectProperty.createNewFile();
			} catch (IOException ex) {
				logger.error(ex.getMessage(), ex);
			}
		}

		loadFiles();
	}

	public boolean isIsAnnotationsReady() {
		return annotationsReady;
	}

	/**
	 *
	 * @return name of project
	 */
	public String getName() {
		return rootProject.getName();
	}

	/**
	 * View about all annotation's visibility
	 * @return annotations and its visibility
	 */
	public Map<String, Boolean> getAnnotationsVisibility() {
		return annotations.annotationsVisibilityView();
	}

	/**
	 * Set all given annotations visibility to the specified value
	 * @param annotationsVisibility visibility data
	 */
	public void setAnnotationsVisibility(Map<String, Boolean> annotationsVisibility) {

		for (String key : annotationsVisibility.keySet()) {
			annotations.getAnnotation(key).setIsVisibleAsFeature(annotationsVisibility.get(key));
		}
		save();
	}

	/**
	 * Get color of annotation
	 * @param key the name(type) of the annotation which color we are interested in
	 * @return the demanded color
	 */
	public Color getAnnotationColor(String key) {
		return annotations.getAnnotation(key).getColor();
	}

	/**
	 * Get all color information associated to all known annotations
	 * @return color information
	 */
	public Map<String, Color> getAnnotationColors() {

		return annotations.annotationsColorView();
	}

	/**
	 * Set color of the specified annotation
	 * @param key name(type) of the annotation
	 * @param color the new color
	 */
	public void setAnnotationColor(String key, Color color) {
		annotations.getAnnotation(key).setColor(color);
		save();
	}

	/**
	 * Set all given annotations color information to the specified value
	 * @param colorMap color data
	 */
	public void setAnnotationColors(Map<String, Color> colorMap) {

		for (String key : colorMap.keySet()) {

			annotations.getAnnotation(key).setColor(colorMap.get(key));
		}
		save();
	}

	/**
	 * @return annotation types represented as group
	 */
	public Set<String> getGroups() {
		return annotations.annotationsGroupView();
	}

	/**
	 * Set all annotations' group information. If groups param contains annotation's name(type), the annotation will be interpreted as group,
	 * otherwise not
	 * @param groups annotation types represented as group
	 */
	public void setGroups(Set<String> groups) {

		if (annotations.annotationTypesView().containsAll(groups)) {
			for (String key : annotations.annotationTypesView()) {

				if (groups.contains(key)) {
					annotations.getAnnotation(key).setIsVisibleAsGroup(true);
				} else {
					annotations.getAnnotation(key).setIsVisibleAsGroup(false);
				}

				save();
			}
			
		} else {
			throw new IllegalStateException("Illegal annotation group found in " + groups);
		}
		save();
	}

	/**
	 * Load project properties (files of project) from persistent project file
	 * @return project properties
	 */
	private Properties loadPropertyFile() {

		if (loadedProperties == null) {
			loadedProperties = new Properties();
			FileInputStream in = null;
			try {
				in = new FileInputStream((rootProject.listFiles())[0]);
				loadedProperties.load(in);
			} catch (IOException ex) {
				logger.error(ex.getMessage(), ex);
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (IOException ex) {
						logger.error(ex.getMessage(), ex);
					}
				}
			}
		}

		return loadedProperties;
	}

	/**
	 * Load file paths of files of this project
	 */
	private void loadFiles() {
		Properties property = this.loadPropertyFile();

		if (rootProject.listFiles().length > 0) {
			loadFile(FileTypes.FASTA, property);
			loadFile(FileTypes.BAM, property);
			loadFile(FileTypes.SAM, property);
			loadFile(FileTypes.GFF, property);
		}
	}

	public void generateAnnotationData() {

		if (annotations == null) {
			logger.info("Generating annotation filtering data");
		} else {
			logger.info("Regenerating annotation filtering data");
		}

		Properties property = loadPropertyFile();
		annotations = new GffAnnotations();
		Set<String> availableAnnotations = Viewer.getGffModel().getAnnotationTypes();

		for (String annotation : availableAnnotations) {
			GffAnnotations.GffAnnotationType newGffAnnotationType = new GffAnnotations.GffAnnotationType(annotation, true,
					GffAnnotations.knownGroups.contains(annotation), new Color(44, 141, 153));

			try {
				boolean showAsFeature = Boolean.parseBoolean(property.get(annotation + "-ShowAsFeature").toString());
				newGffAnnotationType.setIsVisibleAsFeature(showAsFeature);
			} catch (Exception e) {
				annotationsReady = false;
				property.setProperty(annotation + "-ShowAsFeature", Boolean.TRUE.toString());
			}

			try {
				boolean showAsGroup = Boolean.parseBoolean(property.get(annotation + "-ShowAsGroup").toString());
				newGffAnnotationType.setIsVisibleAsGroup(showAsGroup);
			} catch (Exception e) {
				annotationsReady = false;
				property.setProperty(annotation + "-ShowAsGroup", Boolean.TRUE.toString());
			}

			try {
				Color color = new Color(Integer.parseInt(property.get(annotation + "-Color").toString()));
				newGffAnnotationType.setColor(color);
			} catch (Exception e) {
				annotationsReady = false;
				property.setProperty(annotation + "-Color", Integer.toString(0));
			}

			annotations.addAnnotation(newGffAnnotationType);
		}
	}

	/**
	 * Load a file form porperties with given type and store it further use.
	 * File wont be opened.
	 * @param fileType
	 * @param property project properties
	 */
	private void loadFile(FileTypes fileType, Properties property) {
		String extension = fileType.toString().toLowerCase();
		try {
			String key = extension + "File";
			File file = new File(property.getProperty(key));
			addFile(fileType, file);
		} catch (NullPointerException ex) {
			//if there is no file with a type on initial load
		}
	}

	/**
	 * Add a file to project collection. {@link FileTypes#BAM} and {@link FileTypes#SAM}
	 * are switching each other. New colection is saved to project properties.
	 * @param fileType
	 * @param file
	 */
	public void addFile(FileTypes fileType, File file) {
		if (fileType == FileTypes.SAM) {
			filesMap.remove(FileTypes.BAM);
		} else if (fileType == FileTypes.BAM) {
			filesMap.remove(FileTypes.SAM);
		}
		filesMap.put(fileType, file);
		save();
	}

	/**
	 * Remove file from a project. New collection is saved.
	 *
	 * @param type
	 */
	public void removeFile(FileTypes type) {
		filesMap.remove(type);
		save();
	}

	/**
	 * Delete projects data
	 */
	public void delete() {
		for (File file : rootProject.listFiles()) {
			file.delete();
		}
		boolean del = rootProject.delete();
		logger.debug("ProjDeleted: " + del);
	}

	/**
	 * Rename projects directory and also project
	 * @param name
	 */
	public void renameProject(String name) {
		File newDir = new File(rootProject.getParent() + System.getProperty("file.separator") + name);
		boolean renamed = false;
		for (File files : rootProject.listFiles()) {
			files.delete();
		}
		renamed = rootProject.renameTo(newDir);
		if (renamed) {
			rootProject = newDir;
		}
		try {
			new File(rootProject + System.getProperty("file.separator") + rootProject.getName() + ".properties").createNewFile();
		} catch (IOException ex) {
			logger.error(ex.getMessage() + " " + rootProject + System.getProperty("file.separator") + rootProject.getName() + ".properties", ex);
		}
		save();
	}

	/**
	 *
	 * @return files in the project
	 */
	public EnumMap<FileTypes, File> getFiles() {
		return new EnumMap<FileTypes, File>(filesMap);
	}

	/**
	 * 
	 * @return {@code true} - if project doesnt contain any file<br>
	 * {@code false} - if there is at least one file contained in project
	 */
	public boolean isEmpty() {
		return filesMap.keySet().isEmpty();
	}

	/**
	 * Persist collection of files in project properties
	 */
	public void save() {

		FileOutputStream out = null;
		try {
			out = new FileOutputStream(rootProject + System.getProperty("file.separator") + rootProject.getName() + ".properties");
			for (FileTypes key : filesMap.keySet()) {
				File value = filesMap.get(key);
				if (value != null) {
					loadedProperties.setProperty(key.toString().toLowerCase() + "File", value.getAbsolutePath());
				}
			}
			this.registerAnnotationSettingChanges();

			loadedProperties.store(out, null);
			annotationsReady = true;
		} catch (IOException exp) {
			logger.error("Project save method IOExcpetion", exp);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException ex) {
					logger.error(ex.getMessage(), ex);
				}
			}
		}
	}

	private void registerAnnotationSettingChanges() {

		if (annotations == null) {
			return;
		}

		for (String annotation : annotations.annotationTypesView()) {

			loadedProperties.setProperty(annotation + "-ShowAsFeature", Boolean.toString(annotations.getAnnotation(annotation).isVisibleAsFeature()));
			loadedProperties.setProperty(annotation + "-ShowAsGroup", Boolean.toString(annotations.getAnnotation(annotation).isVisibleAsGroup()));
			loadedProperties.setProperty(annotation + "-Color", Integer.toString(annotations.getAnnotation(annotation).getColor().getRGB()));
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Project)) {
			return false;


		}
		if (obj == this) {
			return true;


		}
		Project project = (Project) obj;


		return rootProject.equals(project.rootProject);


	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(rootProject.getName());
		sb.append(" [");



		for (FileTypes fileKey : filesMap.keySet()) {
			sb.append(fileKey).append("=").append((filesMap.get(fileKey) != null) ? filesMap.get(fileKey).getName() : "none").append(" ");


		}
		sb.append("]");


		return sb.toString();

	}
}
