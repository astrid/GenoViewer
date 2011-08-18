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

package hu.astrid.viewer.properties;

import hu.astrid.mvc.swing.AbstractModel;
import hu.astrid.mvc.swing.AbstractView;
import hu.astrid.viewer.Viewer;
import hu.astrid.viewer.ViewerController;
import hu.astrid.viewer.util.FileTypes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.prefs.InvalidPreferencesFormatException;
import java.util.prefs.Preferences;

import org.apache.log4j.Logger;

/**
 * @author onagy
 * This class is responsible for storing and manipulating all kind of properties(application, profile, project properties)
 * In that case when the property file is manipulated by hand so may had malformed properties, default settings are used and application-settings.properties file created when neccessary
 * On the other hand this class serves as an {@link AbstractModel AbstractModel} so capable of notifying {@link AbstractView AbstractViews} when profile settings changed
 */
public class PropertyHandler extends AbstractModel {

	/**Logger for this class*/
	public static Logger logger = Logger.getLogger(PropertyHandler.class);
//	private static final String APPLICATION_PROPERTIES_FILE = "application-settings.properties";
	private final ApplicationProperties applicationProperties;
	private Map<String, ProfileProperties> profiles = new HashMap<String, ProfileProperties>();
	private Map<String, File> profileFiles = new HashMap<String, File>();
	private Map<String, ProjectProperties> projects = new HashMap<String, ProjectProperties>();
	private Map<String, File> projectFiles = new HashMap<String, File>();
	private String activeProfile;
	// private String activeProject;
	private final ProfileProperties defaultProfile;
	/**
	 * Default profile name, which can't be modified and serves as a template for new profiles
	 */
	public static final String DEF_PROFILE_NAME = "default";

	{
		defaultProfile = new ProfileProperties(new Properties(), DEF_PROFILE_NAME);
		defaultProfile.setDirectionBackgroundColor(ProfileProperties.DEF_DIRECTION_BACKGROND_COLOR);
		defaultProfile.setDirectionIndicatorColor(ProfileProperties.DEF_DIRECTION_INDICATOR_COLOR);
		defaultProfile.setReadErrorColor(ProfileProperties.DEF_READ_ERROR_COLOR);
		defaultProfile.setSNPColor(ProfileProperties.DEF_SNP_COLOR);
		defaultProfile.setShowDirection(ProfileProperties.DEF_SHOW_DIRECTION);
		defaultProfile.setShowReadErrors(ProfileProperties.DEF_SHOW_READ_ERRORS);
		defaultProfile.setShowSNPs(ProfileProperties.DEF_SHOW_SNPS);
		defaultProfile.setShowNavigationPanel(ProfileProperties.DEF_SHOW_SNPS);
		defaultProfile.setShowFeatureTable(ProfileProperties.DEF_SHOW_FEATURE_TABLE);
	}

	/**
	 *
	 * @return {@link ProfileProperties ProfileProperties} where all fields are set to default values (profile defaults declared
	 * in {@link ProfileProperties ProfileProperties})
	 */
	public ProfileProperties getDefaultProfile() {
		return defaultProfile;
	}

	/**
	 *
	 * Creates a new PropertyHandler object, uses resourceName as an application-wide property file. Loads profile properties, too.
	 * @param resourceName path to resource
	 */
	public PropertyHandler(String resourceName) {
		if (resourceName != null) {
			try {
				Preferences.importPreferences(new FileInputStream(resourceName));
			} catch (IOException ex) {
				logger.error(ex.getMessage(), ex);
			} catch (InvalidPreferencesFormatException ex) {
				logger.error(ex.getMessage(), ex);
			}
			applicationProperties = new ApplicationProperties(Preferences.userNodeForPackage(PropertyHandler.class));
		} else {
			applicationProperties = new ApplicationProperties(Preferences.userNodeForPackage(Viewer.class));
		}

		ProfileProperties viewerProfile = null;
		List<File> profileDirectories = applicationProperties.getProfileDirectories();
		if (profileDirectories == null) {
			throw new IllegalArgumentException("Cannot find any profile directory!");
		}
		for (File profileDir : profileDirectories) {
			if (profileDir.exists()) {
				logger.info("Profile directory [" + profileDir.getAbsoluteFile() + "] found, searching for profiles...");
				for (File profile : profileDir.listFiles(FileTypes.PROFILE.getFileFilter())) {
					viewerProfile = new ProfileProperties(PropertyHandler.loadProperties(profile), profile.getName().replaceAll(".profile", ""));
					profiles.put(profile.getName().replaceAll(".profile", ""), viewerProfile);
					profileFiles.put(profile.getName().replaceAll(".profile", ""), profile);
				}
			}
		}
	}

	/**
	 * Delegate to {@link PropertyHandler#PropertyHandler(java.lang.String) PropertyHandler(String resourceName)} constructor,
	 * uses "application-settings.properties" as default property file
	 */
	public PropertyHandler() {
		this(null);
	}

	/**
	 * This method is responsible for writing the modified profile properties to the disk
	 * @param profileName the profile to be saved
	 */
	public void saveProfile(String profileName) {
		ProfileProperties viewerProfile = profiles.get(profileName);
		if (viewerProfile == null) {
			throw new IllegalArgumentException("There is no profile called [" + profileName + "]");
		}
		try {
			OutputStream outStream = new FileOutputStream(profileFiles.get(profileName));
			viewerProfile.getProperties().store(outStream, null);
		} catch (Exception exc) {
			logger.error("Cannot save profile properties [" + profileFiles.get(profileName) + "]!", exc);
		}
		logger.info("Profile properties [" + profileFiles.get(profileName) + "] sucessfully saved!");
	}

	/**
	 * NOT USED BY PROJECTS YET!
	 * This method is responsible for writing the modified project properties to the disk
	 * @param projectName the project to be saved
	 */
	public void saveProject(String projectName) {
		ProjectProperties project = projects.get(projectName);
		if (project == null) {
			throw new IllegalArgumentException("There is no project called [" + projectName + "]");
		}
		try {
			OutputStream outStream = new FileOutputStream(projectFiles.get(projectName));
			project.getProperties().store(outStream, null);
		} catch (Exception exc) {
			logger.error("Cannot save project properties [" + projectFiles.get(projectName) + "]!", exc);
		}
		logger.info("Project properties [" + projectFiles.get(projectName) + "] sucessfully saved!");
	}

	/**
	 * This method is responsible for writing the modified profile properties to the disk. Saves all loaded and on-the-fly created profile.
	 */
	public void saveAllProfile() {
		for (String profil : profiles.keySet()) {
			this.saveProfile(profil);
		}
	}

	/**
	 * NOT USED BY PROJECTS YET!
	 * This method is responsible for writing the modified project properties to the disk. Saves all loaded and on-the-fly created project.
	 */
	public void saveAllProject() {
		for (String project : projects.keySet()) {
			this.saveProject(project);
		}
	}

	/**
	 * Saves application properties, all pofile properties and all project properties too.
	 */
	public void saveAll() {
		saveAllProfile();
		saveAllProject();
	}

	/**
	 * @return {@link ApplicationProperties ApplicationProperties} containing application-wide settings
	 */
	public ApplicationProperties getApplicationProperties() {
		return this.applicationProperties;
	}

	/**
	 * Returns default profile if profileName is "default", and return null if cannot find any profile with the given name.
	 * @param profileName name of the profile
	 * @return {@link ProfileProperties ProfileProperties} containing profile settings
	 */
	public ProfileProperties getProfile(String profileName) {
		if (profileName.equals(DEF_PROFILE_NAME)) {
			return getDefaultProfile();
		}
		ProfileProperties viewerProfile = profiles.get(profileName);
		if (viewerProfile == null) {
			logger.warn("There is no profile called [" + profileName + "], returning null");
			return null;
		}
		return viewerProfile;
	}

	/**
	 * Returns all registered profile
	 * @return {@link Collection Collection<ProfileProperties>}
	 */
	public Collection<ProfileProperties> getAllProfile() {
		return this.profiles.values();
	}

	private static Properties loadProperties(File file) {
		if (file == null) {
			throw new IllegalArgumentException("File cannot be null!");
		}
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				logger.error(e);
			}
		}
		Properties properties = null;
		InputStream inputStream = null;
		try {
			if (file.exists()) {
				inputStream = new FileInputStream(file);
			}
			if (inputStream != null) {
				properties = loadProperties(inputStream);
			} else {
				throw new IllegalArgumentException("could not load [" + file + "]" + " as a file resource and property file creation isn't allowed");
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			properties = null;
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (Throwable ignore) {
					logger.error(ignore.getMessage());
				}
			}
		}
		if (properties == null) {
			throw new IllegalArgumentException("could not load [" + file + "]" + " as a file resource");
		}
		return properties;
	}

	private static Properties loadProperties(InputStream inputStream) {
		Properties properties = null;
		try {
			properties = new Properties();
			if (inputStream != null) {
				properties.load(inputStream);
			} else {
				throw new IllegalArgumentException("could not load form input stream");
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			properties = null;
		}

		return properties;
	}

	/**
	 * Returns active profile, if no active profile set, returns the default
	 * @return the currently active profile
	 */
	public ProfileProperties getActiveProfile() {
		ProfileProperties profile = this.profiles.get(activeProfile);
		if (profile == null) {
			return defaultProfile;
		}
		return profile;
	}

	/**
	 *	Delegates to {@link ProfileProperties#ProfileProperties(java.util.Properties, java.lang.String) addNewProfile(ProfileProperties profile, File file)}
	 *  If this is a new profile, the file name will be equals to the profile name. If profile exists, override prevoius profile settings
	 * @param profile {@link ProfileProperties ProfileProperties} containing information about the new profile
	 */
	public void addNewProfile(ProfileProperties profile) {
		if (profiles.containsKey(profile.getProfileName())) {
			this.addNewProfile(profile, profileFiles.get(profile.getProfileName()));
		} else {
			this.addNewProfile(profile, new File(getApplicationProperties().getDefaultProfileDirectory() + File.separator + profile.getProfileName() + ".profile"));
		}
	}

	/**
	 * Register new profile, override if existed before. When saving modification goes to the given file. Overwrite asssociated file if existed before.
	 * @param profile {@link ProfileProperties ProfileProperties} containing information about the new profile
	 * @param file {@link File File} where profile will be saved
	 */
	public void addNewProfile(ProfileProperties profile, File file) {
		logger.info("Profile created " + file.toString());
		profiles.put(profile.getProfileName(), profile);
		profileFiles.put(profile.getProfileName(), file);
	}

	/**
	 * Removes profile from the profiles list and delete from the file system if can
	 * @param profileName profile to be removed
	 * @throws IllegalArgumentException if profileName is null
	 */
	public void removeProfile(String profileName) {
		if (profileName == null) {
			throw new IllegalArgumentException("profileName cannot be null!");
		} else if (profileName.equals(DEF_PROFILE_NAME)) {
			logger.warn(DEF_PROFILE_NAME + " cannot be removed!");
			return;
		}
		ProfileProperties profile = profiles.get(profileName);
		if (profile == null) {
			logger.warn("Profile" + profileName + " doesn't exist!");
		} else if (profileFiles.get(profile.getProfileName()) != null) {
			File file = profileFiles.get(profile.getProfileName());
			if (file.exists()) {
				logger.info("Removing profile:" + file.toString());
				try {
					SecurityManager securityManager = System.getSecurityManager();
					if (securityManager != null) {
						securityManager.checkDelete(file.getAbsoluteFile().toString());
					} else if (file.delete() != true) {
						throw new Exception("Cannot delete file " + file.toString());
					}
				} catch (Exception exc) {
					logger.error(exc);
				}
			}
			profileFiles.remove(profile.getProfileName());
			profiles.remove(profile.getProfileName());
		}
	}

	/**
	 * Set new active profile, param must be a valid profile's name or default. Notify all view's about the changes.
	 * @param profileName
	 * @throws IllegalArgumentException if profileName doesn't exists
	 */
	public void setActiveProfile(String profileName) {
		ProfileProperties profile = profiles.get(profileName);
		if (!(profileName.equals(DEF_PROFILE_NAME)) && profile == null) {
			throw new IllegalArgumentException("There is no profile called [" + profileName + "]");
		} else if (profileName.equals(DEF_PROFILE_NAME)) {
			activeProfile = DEF_PROFILE_NAME;
			profile = defaultProfile;
		}
		activeProfile = profileName;
		applicationProperties.setLastProfile(profileName);
		firePropertyChange(ViewerController.VIEWER_PROFILE_PROPERTY, null, profile);
		profile.noNeedReload();
	}

	/**
	 * Use when launching application, restores the last state before exiting.
	 * Gets the last profile from the application-settings.properties, and if cannot found, activate default profile
	 * @see #setActiveProfile(java.lang.String) 
	 */
	public void activateLastLoadedProfile() {
		if (applicationProperties.getLastProfile() != null && (profiles.get(applicationProperties.getLastProfile()) != null)) {
			activeProfile = profiles.get(applicationProperties.getLastProfile()).getProfileName();
			this.setActiveProfile(activeProfile);
		} else {
			activeProfile = DEF_PROFILE_NAME;
			this.setActiveProfile(DEF_PROFILE_NAME);
		}
	}
}
