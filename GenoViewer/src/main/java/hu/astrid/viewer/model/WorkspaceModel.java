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

import hu.astrid.mvc.swing.AbstractModel;
import hu.astrid.viewer.Viewer;
import hu.astrid.viewer.util.FileTypes;
import java.awt.Color;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;

public class WorkspaceModel extends AbstractModel {

	/** Workspace open {@link WorkspaceModel#setWorkspace(java.io.File) } */
	public static final String WORKSPACE_LOAD = "Workspace";
//	/** Indicate change in workspace structure (file or project addition/deletion) */
//	public static final String WORKSPACE_STATE_CHANGE = "WorkspaceState";
	/** Actual project change {@link WorkspaceModel#setActProject(java.lang.Integer) } */
	public static final String WORKSPACE_PROJECT_CHANGE = "ActProject";
	/** New project addition {@link WorkspaceModel#setNewProject(java.lang.String) } */
	public static final String WORKSPACE_NEW_PROJECT = "NewProject";
	/** New file addition to a project {@link WorkspaceModel#setNewFile(java.io.File, hu.astrid.viewer.util.FileTypes, java.lang.Integer)  } */
	public static final String PROJECT_NEW_FILE = "NewFile";
	/** Deletion of a project {@link WorkspaceModel#setDeletedProject(java.lang.Integer) } */
	public static final String WORKSPACE_PROJECT_DELETED = "DeletedProject";
	/** Project name change {@link WorkspaceModel#setProjectName(java.lang.String, java.lang.Integer)   } */
	public static final String PROJECT_NAME = "ProjectName";
	/** File removal from a project {@link WorkspaceModel#setFileRemove(java.lang.String, java.lang.Integer)   } */
	public static final String PROJECT_FILE_REMOVE = "FileRemove";
	private File workspace;
	private int actProject = -1;
	private List<Project> projects;
	/** Default logger */
	private static final Logger logger = Logger.getLogger(WorkspaceModel.class);

	public WorkspaceModel() {
		//Empty
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Project project : projects) {
			sb.append(project.toString());
			sb.append(" | ");
		}
		return sb.toString();
	}

	/**
	 * Create a workspace and open it
	 * @param workspacePath
	 * @throws IllegalStateException thrown if directory exists with given path
	 */
	public void create(String workspacePath) throws IllegalStateException {
		File newWorkspace = new File(workspacePath);
		FileOutputStream out = null;
		if (!newWorkspace.mkdir()) {
			IllegalStateException ex = new IllegalStateException(newWorkspace.getPath() + " is already Exists");
			logger.warn(ex);
			throw ex;
		}
		final String separator = System.getProperty("file.separator");
		File workspaceMetaInf = new File(newWorkspace.getPath() + separator + workspacePath.substring(workspacePath.lastIndexOf(separator) + separator.length()) + ".info");
		try {
			workspaceMetaInf.createNewFile();
			Properties property = new Properties();
			out = new FileOutputStream(workspaceMetaInf);
			property.setProperty("workspaceOwner", System.getProperty("user.name"));
			property.store(out, "WorkspaceCreated");
			setWorkspace(newWorkspace);
		} catch (IOException ex) {
			logger.error("Name: " + newWorkspace.getName() + " " + ex, ex);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException ex) {
					logger.error(ex);
				}
			}

		}
	}

	/**
	 *
	 * @return contained projects in workspace, empty Collection if there is no projects
	 * @see Project
	 */
	public List<Project> getProjects() {

		if (projects == null) {
			return new ArrayList<Project>();
		} else {
			return new ArrayList<Project>(projects);
		}
	}

	/**
	 *
	 * @return name of workspace
	 */
	public String getName() {
		return workspace.getName();
	}

	/**
	 * Add a file to the specified project and notify views. If file added to actual project,
	 * it has to be opened
	 * @param file file to add
	 * @param fileType type of file to add
	 * @param projectIndex 
	 * @see Project#addFile(hu.astrid.viewer.util.FileTypes, java.io.File)
	 */
	public void setNewFile(File file, FileTypes fileType, Integer projectIndex) {
		logger.trace("set new file" + file);
		projects.get(projectIndex).addFile(fileType, file);
		int index;
		if (projectIndex == actProject) {
			index = actProject;
			closeFile(fileType);
		} else {
			index = -1;
		}

		firePropertyChange(PROJECT_NEW_FILE, index, projects);
	}

	/**
	 * Set actual project. Views are notified about project change
	 * @param actProject index of requested project
	 * @see WorkspaceModel#WORKSPACE_PROJECT_CHANGE
	 */
	public void setActProject(Integer actProject) {
		logger.debug("actProj: " + actProject);
		if (this.actProject != actProject) {
			Viewer.getController().closeAllFiles();
			this.actProject = actProject;
			firePropertyChange(WORKSPACE_PROJECT_CHANGE, actProject, projects.get(this.actProject).getFiles());
		}
	}

	/**
	 * Get annotations' visibility information from the active project
	 * @return visibility information 
	 * @see Project#getAnnotationsVisibility()
	 */
	public Map<String, Boolean> getAnnotationsVisibility() {
		return projects.get(this.actProject).getAnnotationsVisibility();
	}

	/**
	 * Get annotation's color information from the active project
	 * @param key the annotation's name(type)
	 * @return the color of the annotation
	 * @see Project#getAnnotationColor(java.lang.String) 
	 */
	public Color getAnnotationColor(String key) {
		return projects.get(this.actProject).getAnnotationColor(key);
	}

	/**
	 * Get all annotations's color information from the active project
	 * @return annotation names(types) and associated color informations
	 * @see Project#getAnnotationColors() 
	 */
	public Map<String, Color> getAnnotationColors() {
		return projects.get(this.actProject).getAnnotationColors();
	}

	/**
	 * Set annotation's color information in the active project
	 * @param key the annotation's name(type)
	 * @param color new color
	 * @throws IllegalStateException if there is no active project
	 * @see Project#setAnnotationColor(java.lang.String, java.awt.Color) 
	 */
	public void setAnnotationColor(String key, Color color) {
		if (actProject < 0) {
			throw new IllegalStateException("There is no active project!");
		}
		this.projects.get(actProject).setAnnotationColor(key, color);
	}

	/**
	 * Set all annotations's color information in the active project
	 * @param colorMap map containing annotation names(types) and associated colors
	 * @throws IllegalStateException if there is no active project
	 * @see Project#setAnnotationColors(java.util.Map) 
	 */
	public void setAnnotationColors(Map<String, Color> colorMap) {
		if (actProject < 0) {
			throw new IllegalStateException("There is no active project!");
		}
		this.projects.get(actProject).setAnnotationColors(colorMap);
	}

	/**
	 * Set all annotations's visibility information in the active project
	 * @param annotationsVisibility map containing annotation names(types) and associated visibility information
	 * @throws IllegalStateException if there is no active project
	 * @see Project#setAnnotationsVisibility(java.util.Map)
	 */
	public void setAnnotationsVisibility(Map<String, Boolean> annotationsVisibility) {
		if (actProject < 0) {
			throw new IllegalStateException("There is no active project!");
		}
		projects.get(this.actProject).setAnnotationsVisibility(annotationsVisibility);
	}

	/**
	 * Read (if any) data for annotation settings from properties associated with this project
	 * @throws IllegalStateException if there is no active project
	 * @see Project#generateAnnotationData() 
	 */
	public void loadAnnotationData() {
		if (actProject < 0) {
			throw new IllegalStateException("There is no active project!");
		}
		projects.get(this.actProject).generateAnnotationData();
	}

	/**
	 * Get annotation names(types) interpreted as group
	 * @return annotation names(types)
	 * @see Project#getGroups() 
	 */
	public Set<String> getGroups() {
		return projects.get(this.actProject).getGroups();
	}

	/**
	 * Set if group of annotations should be interpreted as group
	 * @param groups the annotation names(types) to be interpreted as group
	 * @throws IllegalStateException if there is no active project
	 * @see Project#setGroups(java.util.Set)
	 */
	public void setGroups(Set<String> groups) {
		if (actProject < 0) {
			throw new IllegalStateException("There is no active project!");
		}
		projects.get(this.actProject).setGroups(groups);
	}

	/**
	 * Create new project
	 * @param projectName
	 */
	public void setNewProject(String projectName) {
		if (isProjectExists(projectName)) {
			firePropertyChange(WORKSPACE_NEW_PROJECT, projectName, null);
		} else {
			File newProject = new File(workspace.getPath() + System.getProperty("file.separator") + projectName);
			newProject.mkdir();
			Project project = new Project(newProject);

			this.projects.add(project);
			firePropertyChange(WORKSPACE_NEW_PROJECT, null, projects);
		}
	}

	private boolean isProjectExists(String projectName) {
		for (Project project : projects) {
			if (project.getName().equals(projectName)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Set a specific workspace, load projects and notify views and recent file handler
	 * @param file file representating workspace directory
	 */
	public void setWorkspace(File file) {
		if (Viewer.getController().isFileOpenInProgress()) {
			logger.debug("file opening(s) in progress");
			return;
		}
		actProject = -1;
		projects = new ArrayList<Project>();
		this.workspace = file;
		if (file != null) {
			for (File project : workspace.listFiles()) {
				if (project.isDirectory()) {
					projects.add(new Project(project));
				}
			}
		}
		firePropertyChange(WORKSPACE_LOAD, null, projects);
		Viewer.getApplicationProperties().notifyFileOpened(file, FileTypes.WORKSPACE);
	}

	//TODO: valahol egy stream nyitva van de nem találom :(
	/**
	 * Remove a file from project. If it removed from actual project, the file is closed
	 * @param key file type
	 * @param projectIndex project index remove from
	 * @see Project#removeFile(hu.astrid.viewer.util.FileTypes)
	 */
	public void setFileRemove(String key, Integer projectIndex) {
		//TODO enum átadása reflectionnel
		FileTypes type = FileTypes.valueOf(key.toUpperCase());
		projects.get(projectIndex).removeFile(type);
		if (projectIndex == actProject) {
			closeFile(type);
		}
		firePropertyChange(PROJECT_FILE_REMOVE, null, projects);
		logger.debug("fileDeleted!");
	}

	/**
	 * Ask controller to close file.
	 * @param type 
	 */
	private void closeFile(FileTypes type) {
		Viewer.getController().closeFile(type);
	}

	/**
	 * Rename a project
	 * @param name new name
	 * @param projectIndex project index to rename
	 * @see Project#renameProject(java.lang.String)
	 */
	public void setProjectName(String name, Integer projectIndex) {
		projects.get(projectIndex).renameProject(name);
		firePropertyChange(PROJECT_NAME, null, projects);
	}

	/**
	 * Delete specified project and notify views. If actual project deleted, files must be closed
	 * @param project
	 */
	public void setDeletedProject(Integer project) {
		logger.debug("ProjectToDel: " + project);
		projects.get(project).delete();
		projects.remove(project.intValue());
		if (project == actProject) {
			Viewer.getController().closeAllFiles();
		}
		firePropertyChange(WORKSPACE_PROJECT_DELETED, null, projects);
	}

	/**
	 * @return index of actual project, -1 if no project open
	 */
	public int getActProject() {
		return actProject;
	}

	/**
	 *
	 */
	public void save() {

		for (Project project : this.projects) {
			project.save();
		}
	}
	/**
	 * Get name of file in actual project
	 * @param type type of requested file
	 * @return {@code null} - if no project open, or project doesn't contain file with specified type<br>
	 *			name of file - else
	 */
	public String getFileName(FileTypes type) {
		if(actProject<0)
			return null;
		EnumMap<FileTypes, File> projectFiles = projects.get(actProject).getFiles();
		if(projectFiles.containsKey(type))
			return projectFiles.get(type).getName();
		else
			return null;
	}

	/**
	 *
	 * @return
	 * @see Project#isIsAnnotationsReady() 
	 */
	public boolean isAnnotationsReady() {
		if(actProject<0)
			return false;
		return projects.get(actProject).isIsAnnotationsReady();
	}
}
