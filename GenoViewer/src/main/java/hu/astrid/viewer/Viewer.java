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

import com.apple.eawt.Application;
import com.apple.eawt.ApplicationAdapter;
import com.apple.eawt.ApplicationEvent;
import hu.astrid.mvc.swing.AbstractView;
import hu.astrid.viewer.gui.MainWindow;
import hu.astrid.viewer.gui.StatusBar;
import hu.astrid.viewer.gui.help.About;
import hu.astrid.viewer.model.*;
import hu.astrid.viewer.properties.ApplicationProperties;
import hu.astrid.viewer.properties.ProfileProperties;
import hu.astrid.viewer.properties.ProfileProperties.SequenceDisplayMode;
import hu.astrid.viewer.properties.PropertyHandler;
import hu.astrid.viewer.util.FileTypes;
import hu.astrid.viewer.util.GenoViewerParameterException;
import hu.astrid.viewer.util.Parameter;
import hu.astrid.viewer.util.ParameterParser;
import org.apache.log4j.Logger;

import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.FileFilter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

/**
 * @author Szuni
 */
public class Viewer {

	/**
	 * FelĂĽleten megjelenĹ‘ feliratok elĂ©rĂ©si Ăştja
	 */
	public static final String LABEL_RESOURCES = "LabelResources";
	/**
	 * Show configurations of displayed elements
	 */
	private static PropertyHandler propertyHandler = new PropertyHandler();
	private static ViewerFastaModel fastaModel = new ViewerFastaModel();
	private static ViewerConsensusModel consensusModel = new ViewerConsensusModel();
	private static SelectionModel selectionModel = new SelectionModel();
	private static ViewerReadModel readModel = new ViewerReadModel();
	private static ViewerGffModel gffModel = new ViewerGffModel();
	private static WorkspaceModel workspaceModel = new WorkspaceModel();
	private static ViewerController controller = new ViewerController();
	private static CoverageModel coverageModel = new CoverageModel();
	private static MainWindow mainWindow = null;
	private static PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(new Object());
	private static ResourceBundle labelResources;
	private static File predefinedWorkspace;

	private enum CommandParameter {

		SAMPLE, APPSETTINGS;
	}

	private static class MacApplicationAdapter extends ApplicationAdapter {
		@Override
		public void handleAbout(ApplicationEvent applicationEvent) {
			super.handleAbout(applicationEvent);
			About about = new About(mainWindow, true);
			about.setEnabled(true);
			about.setLocationRelativeTo(mainWindow);
			about.setVisible(true);
			applicationEvent.setHandled(true);
		}

		@Override
		public void handleQuit(ApplicationEvent applicationEvent) {
			super.handleQuit(applicationEvent);
			System.exit(0);
		}
	}

	public static void init() {
		controller = new ViewerController();

		fastaModel = new ViewerFastaModel();
		consensusModel = new ViewerConsensusModel();
		selectionModel = new SelectionModel();
		readModel = new ViewerReadModel();
		gffModel = new ViewerGffModel();
		coverageModel = new CoverageModel();
		workspaceModel = new WorkspaceModel();

		controller.addModel(fastaModel);
		controller.addModel(consensusModel);
		controller.addModel(selectionModel);
		controller.addModel(readModel);
		controller.addModel(gffModel);
		controller.addModel(coverageModel);
		controller.addModel(propertyHandler);
		controller.addModel(workspaceModel);

		mainWindow = new MainWindow();
		controller.addView(mainWindow);
		for (AbstractView view : mainWindow.getAdditionalViews()) {
			controller.addView(view);
		}

		propertyChangeSupport = new PropertyChangeSupport(mainWindow);
		propertyChangeSupport.addPropertyChangeListener(mainWindow.getStatusBar());

		//last workspace load just test
		loadWorkspace();

		propertyHandler.activateLastLoadedProfile();
	}

	public static void main(String args[]) {
		Map<Parameter, String> parameterMap = null;

		try {
			parameterMap = ParameterParser.getMapIfCmdLineIsValid(args);
		} catch (GenoViewerParameterException exc) {
			System.out.println(exc.getMessage());
			System.out.println(ParameterParser.generateUsageMessage());
			System.exit(-1);
		}

		List<String> parameterConflicts = ParameterParser.checkConflicts(parameterMap);

		if (parameterConflicts.size() > 0) {

			for (String conflictMessage : parameterConflicts) {

				System.out.println(conflictMessage);
			}

			System.out.println(ParameterParser.generateUsageMessage());
			System.exit(-1);
		}

		if (parameterMap.containsKey(Parameter.HELP)) {
			System.out.println(ParameterParser.generateUsageMessage());
			System.exit(-1);
		}

		if (parameterMap.containsKey(Parameter.APPSETTINGS)) {
			propertyHandler = new PropertyHandler(parameterMap.get(Parameter.APPSETTINGS));
		}else {
			propertyHandler = new PropertyHandler();
		}

		if (parameterMap.containsKey(Parameter.SAMPLE)) {
			if (buildSampleWorkspace(parameterMap.get(Parameter.SAMPLE))) {
				predefinedWorkspace = new File(parameterMap.get(Parameter.SAMPLE) +
						File.separator + "SampleWorkspace");
				setSampleProfile();
			}
		}

		if (parameterMap.containsKey(Parameter.WORKSPACE)) {
			predefinedWorkspace = new File(parameterMap.get(Parameter.WORKSPACE));
			System.out.println("Path to workspace:" + predefinedWorkspace);
		}

		Viewer.init();
		Viewer.initShutdownHook();
		if (System.getProperty("mrj.version") != null) {
			//mac specific code
			Application.getApplication().addApplicationListener(new MacApplicationAdapter());
		}

		java.awt.EventQueue.invokeLater(new Runnable() {

			@Override
			public void run() {
				mainWindow.setVisible(true);
			}
		});
	}

	private static void initShutdownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

			Logger logger = Logger.getLogger("Shutdown thread");

			@Override
			public void run() {
				//invoke later is necessary to avoid synchronization problem
				java.awt.EventQueue.invokeLater(new Runnable() {

					@Override
					public void run() {
						logger.info("Entering run method of shutdown thread");
						try {
							Viewer.getPropertyHandler().saveAll();
							Viewer.getController().closeAllFiles();
						} catch (Throwable t) {
							PrintWriter pw = new PrintWriter(new StringWriter());
							t.printStackTrace(pw);
							logger.error(pw.toString());
						}
					}
				});
			}
		}));
	}

	public static ViewerController getController() {
		return controller;
	}

	public static StatusBar getStatusBar() {
		return mainWindow.getStatusBar();
	}

	/**
	 * Start the busy animation and diplays a message in statusbar
	 *
	 * @param message message
	 * @see StatusBar
	 */
	public static void startStatusbarJob(String message) {
		propertyChangeSupport.firePropertyChange(StatusBar.JOB_STARTED, null, message);
	}

	/**
	 * Stop the busy animation
	 *
	 * @param message message
	 * @see StatusBar
	 */
	public static void stopStatusbarJob(String message) {
		propertyChangeSupport.firePropertyChange(StatusBar.JOB_DONE, message, null);
	}

	/**
	 * Show a message in the statusbar
	 *
	 * @param message
	 * @see StatusBar
	 */
	public static void showStatusbarMessage(String message) {
		propertyChangeSupport.firePropertyChange(StatusBar.MESSAGE, getStatusBar().getMessage(), message);
	}

	/**
	 * Set progresbars value
	 *
	 * @param value value of acutal progress
	 * @see StatusBar
	 */
	public static void setStatusbarProgresValue(StatusBar.ProgressValue value) {
		propertyChangeSupport.firePropertyChange(StatusBar.PROGRESS_VALUE, null, value);
	}

	public static Locale getViewerLocale() {
		return propertyHandler.getApplicationProperties().getLocale();
	}

	public static void setViewerLocale(Locale viewerLocale) {

		propertyHandler.getApplicationProperties().notifiyNewLocale(viewerLocale);
		ResourceBundle.getBundle(LABEL_RESOURCES, propertyHandler.getApplicationProperties().getLocale());
	}

	public static ResourceBundle getLabelResources() {
		if (labelResources == null || !labelResources.getLocale().equals(propertyHandler.getApplicationProperties().getLocale())) {
			try {
				labelResources = ResourceBundle.getBundle(LABEL_RESOURCES, propertyHandler.getApplicationProperties().getLocale());
			} catch (MissingResourceException ex) {
				labelResources = ResourceBundle.getBundle(LABEL_RESOURCES);
				propertyHandler.getApplicationProperties().notifiyNewLocale(labelResources.getLocale());
			}
		}
		return labelResources;
	}

	public static MainWindow getMainWindow() {
		return mainWindow;
	}

	public static void initForTest(String settingsFileName) {
		if (settingsFileName != null) {
			propertyHandler = new PropertyHandler(settingsFileName);
		} else {
			propertyHandler = new PropertyHandler();
		}

		fastaModel = new ViewerFastaModel();
		consensusModel = new ViewerConsensusModel();
		selectionModel = new SelectionModel();
		readModel = new ViewerReadModel();
		gffModel = new ViewerGffModel();
		coverageModel = new CoverageModel();
		workspaceModel = new WorkspaceModel();
		controller = new ViewerController();
		mainWindow = new MainWindow();

		controller.addModel(fastaModel);
		controller.addModel(consensusModel);
		controller.addModel(selectionModel);
		controller.addModel(readModel);
		controller.addModel(gffModel);
		controller.addModel(coverageModel);
		controller.addModel(propertyHandler);
		controller.addModel(workspaceModel);

		controller.addView(mainWindow);
		for (AbstractView view : mainWindow.getAdditionalViews()) {
			controller.addView(view);
		}

		propertyChangeSupport = new PropertyChangeSupport(mainWindow);
		propertyChangeSupport.addPropertyChangeListener(mainWindow.getStatusBar());
	}

	//Just for tests

	public static ViewerFastaModel getFastaModel() {
		return fastaModel;
	}

	//Just for tests

	public static ViewerReadModel getReadModel() {
		return readModel;
	}

	public static PropertyHandler getPropertyHandler() {
		return propertyHandler;
	}

	public static ApplicationProperties getApplicationProperties() {
		return propertyHandler.getApplicationProperties();
	}

	/**
	 * @return active user profile, containig showing preferences
	 * @see PropertyHandler#getActiveProfile()
	 */
	public static ProfileProperties getActiveProfile() {
		return propertyHandler.getActiveProfile();
	}

	public static ViewerGffModel getGffModel() {
		return gffModel;
	}

	public static ViewerConsensusModel getConsensusModel() {
		return consensusModel;
	}

	public static WorkspaceModel getWorkspaceModel() {
		return workspaceModel;
	}

	public static void setMainWindow(MainWindow mainWindow) {
		Viewer.mainWindow = mainWindow;
	}

	/**
	 * Init workspacePanel
	 */
	private static void loadWorkspace() {

		if (predefinedWorkspace != null) {
			workspaceModel.setWorkspace(predefinedWorkspace);
		}
		List<Project> projects = workspaceModel.getProjects();

		if (projects != null && projects.size() > 0) {
			workspaceModel.setActProject(0);
		}
	}

	/**
	 * If all sample files are available at the location, create a sample workspace
	 * with sample project with sample files within. Is workspace already exists,
	 * no receation or validity check occurs, just successful creation returned.
	 *
	 * @param location sample files location
	 * @return creation success
	 */
	private static boolean buildSampleWorkspace(String location) {
		final String[] sampleFileNames = new String[]{"example-sequence.fasta", "example-alignment.bam", "example-annotation.gff"};
		FileFilter filter = new FileFilter() {

			@Override
			public boolean accept(File file) {
				for (String fileName : sampleFileNames) {
					if (fileName.equals(file.getName())) {
						return true;
					}
				}
				return false;
			}
		};

		File sampleDir = new File(location);
		if (!sampleDir.exists()) {
			System.out.println("Invalid sample directory: " + location);
		}
		File[] sampleFiles = sampleDir.listFiles(filter);
		if (sampleFiles.length != 3) {
			System.out.println("Following files doesn't match sample files: " + Arrays.toString(sampleFiles) + "\n");
			return false;
		}
		File sampleWorkspace = new File(sampleDir.getAbsolutePath() + "/SampleWorkspace");
		if (!sampleWorkspace.exists()) {
			workspaceModel.create(sampleWorkspace.getPath());
			workspaceModel.setNewProject("SampleProject");
			for (File file : sampleFiles) {
				workspaceModel.setNewFile(file, FileTypes.valueOf(file.getName().substring(file.getName().lastIndexOf('.') + 1).toUpperCase()), 0);
			}
		}
		return true;
	}

	/**
	 * Create a sample profile displaying sequences in color and nucleotide mode,
	 * showing coverage and navigation panel, indicatind raeds strand direction,
	 * and activate it.
	 */
	private static void setSampleProfile() {
		ProfileProperties prof = Viewer.getPropertyHandler().getActiveProfile().makeCopy("sample");
		prof.setSequenceDisplayMode(SequenceDisplayMode.BOTH);
		prof.setShowCoveragePanel(true);
		prof.setShowNavigationPanel(true);
		prof.setShowDirection(true);
		Viewer.getPropertyHandler().addNewProfile(prof);
		Viewer.getPropertyHandler().setActiveProfile("sample");
	}
}

