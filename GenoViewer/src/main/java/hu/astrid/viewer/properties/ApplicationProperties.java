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

import hu.astrid.viewer.util.FileTypes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.apache.log4j.Logger;

/**
 *
 * @author onagy
 */
public class ApplicationProperties {

	
	private static final String DEF_LOCALE = "en_US";
	/**Maximum size of BAM file loaded into memory*/
	private int bamFileLimit = 1024 * 1024;
	/**Maximum size of SAM file can be opened*/
	private int samFileLimit = 20 * 1024 * 1024;
	/**Distance of columns of reads in short mode */
	private int readDistance = 100;
	private int bufferSize = 16384;
	private final int MAX_KEPT_OPENED_FILES = 5;
	private static final List<Locale> supportedLocals = new ArrayList<Locale>();
	private final File defaultProfileDirectory = new File("." + File.separator + "Workspace" + File.separator + "Profiles");
	private final Preferences preferences;

	{
		supportedLocals.clear();
		supportedLocals.add(new Locale("en", "US"));
		supportedLocals.add(new Locale("hu", "HU"));
	}
	/** Default logger */
	private static final Logger logger = Logger.getLogger(ApplicationProperties.class);


	/**
	 * Constructs new instance with the given properties
	 * @param preferences
	 */
	public ApplicationProperties(Preferences preferences) {
		this.preferences = preferences;
//		FileFilter filter = new FileFilter() {
//
//			@Override
//			public boolean accept(File file) {
//				return file.getName().startsWith("LabelResources");
//			}
//
//		};
//		for(File languageFile : new File(".").listFiles(filter)) {
//			String localeName = languageFile.getName().substring(languageFile.getName().indexOf('_')+1, languageFile.getName().indexOf('.'));
//			String[] tokens = localeName.split("_");
//			try{
//				supportedLocals.add(new Locale(tokens[0], tokens[1]));
//			} catch (IndexOutOfBoundsException ignore) {}
//		}
	}

	/**
	 * Export {@link Preferences} node for this settings
	 * @param path file path to export
	 */
	public void export(String path) {
		try {
			preferences.exportNode(new FileOutputStream(path));
		} catch (IOException ex) {
			logger.error(ex.getMessage(), ex);
		} catch (BackingStoreException ex) {
			logger.error(ex.getMessage(), ex);
		}
	}

	/**
	 *
	 * @return the directory which is first scanned for profiles, newly created profiles go here, too.
	 */
	public File getDefaultProfileDirectory() {
		return this.defaultProfileDirectory;
	}

	/**
	 * Prints application properties content
	 */
	public void printProperties() {
		System.out.println(preferences.toString());
	}

	/**
	 * Notify the {@link #PropertyHandler(java.lang.String) PropertyHandler} when a new file has been opened, so it's capable of tracking the last opened files
	 * @param file
	 * @param fileType
	 */
	public void notifyFileOpened(File file, FileTypes fileType) {
		preferences.put("lastOpened" + fileType.toString() + "Directory", file.getParent());
		this.maintainOpenedFilesList(file, fileType);
	}

	private void maintainOpenedFilesList(File file, FileTypes fileType) {
		String openedFiles = preferences.get("lastOpened" + fileType.toString() + "Files", "");
		String[] fileTokens = openedFiles.split(";");
		List<String> openedFilesList = new LinkedList<String>();
		for (String fileCandidate : fileTokens) {
			if (fileCandidate != null && new File(fileCandidate).exists()) {
				openedFilesList.add(fileCandidate);
			}
		}
		if (openedFilesList.contains(file.getAbsolutePath())) {
			openedFilesList.remove(file.getAbsolutePath());
		}
		if (openedFilesList.size() >= this.MAX_KEPT_OPENED_FILES) {
			for (int i = this.MAX_KEPT_OPENED_FILES; i <= openedFilesList.size(); i++) {
				logger.info("Removing:" + openedFilesList.get(i - 1));
				openedFilesList.remove(i - 1);
			}
		}
		openedFilesList.add(0, file.getAbsolutePath());
		StringBuilder strBuilder = new StringBuilder();
		for (String pathToFile : openedFilesList) {
			strBuilder.append(";");
			strBuilder.append(pathToFile);
		}
		preferences.put("lastOpened" + fileType.toString() + "Files", strBuilder.toString());

	}

	/**
	 * Returns a list of last opened files. The list is empty and not pointing to null when there is no files. Only existing files opened.
	 * Max size of the list is internally controlled
	 * @param fileType
	 * @return List of last opened files, in chronological order
	 */
	public List<File> getLastOpenedFiles(FileTypes fileType) {
		String openedFiles = preferences.get("lastOpened" + fileType.toString() + "Files", null);
		List<File> openedFilesList = new LinkedList<File>();
		if (openedFiles == null) {
			return openedFilesList;
		}
		String[] fileTokens = openedFiles.split(";");
		File actualFile = null;
		for (String fileCandidate : fileTokens) {
			actualFile = new File(fileCandidate);
			if (actualFile.exists()) {
				openedFilesList.add(actualFile);
			}
		}
		return openedFilesList;
	}

	/**
	 *Returns the parent directory of the last opened file
	 * @param fileType type of the demanded directory
	 * @return a directory reference
	 */
	public File getLastOpenedDirectory(FileTypes fileType) {
		String openedDir = preferences.get("lastOpened" + fileType.toString() + "Directory", null);
		return (openedDir != null) ? new File(openedDir) : new File(".");
	}

	/**
	 * @return currently active locale
	 */
	public Locale getLocale() {
		String localeName = preferences.get("locale", DEF_LOCALE);
		Locale locale = null;
		String[] tokens = localeName.split("_");
		locale = new Locale(tokens[0], tokens[1]);
		return locale;
	}

	/**
	 *	Register locale changes
	 * @param locale the new Locale
	 * @return {@code true} - if new value is valid and it had been set
	 */
	public boolean notifiyNewLocale(Locale locale) {
		if (!(supportedLocals.contains(locale))) {
			return false;
		}
		preferences.put("locale", locale.toString());
		return true;
	}

	/**
	 * Get locales that lanuage file availible for
	 * @return supported locales
	 */
	public List<Locale> getSupportedLocales() {
		return supportedLocals;
	}

	/**
	 *
	 * @return the current buffer size
	 */
	public int getBufferSize() {
		bufferSize = preferences.getInt("bufferSize", bufferSize);
		return bufferSize;
	}

	/**
	 * sets new buffer size in bytes
	 * @param newBufferSize if parameter is lesser than 512 or greater than {@link Integer#MAX_VALUE Integer.MAX_VALUE} / 2, paramter is forced to be valid
	 * @return {@code true} - if new value is valid and it had been set
	 */
	public boolean  setBufferSize(int newBufferSize) {
		if (newBufferSize >= 512 && newBufferSize <= (Integer.MAX_VALUE / 2)) {
			preferences.putInt("bufferSize", newBufferSize);
			return true;
		}
		return false;
	}

	/**
	 *
	 * @return maximum size of {@link FileTypes#SAM SAM} files can be opened
	 */
	public int getSamFileLimit() {
		samFileLimit = preferences.getInt("samFileSizeLimit", samFileLimit);
		return samFileLimit;
	}

	/**
	 *
	 * @return currently set maximum size of {@link FileTypes#BAM BAM} files which stored in memory
	 */
	public int getBamFileLimit() {
		bamFileLimit = preferences.getInt("bamFileSizeLimit", bamFileLimit);
		return bamFileLimit;
	}

	/**
	 *
	 * @param newBamFileLimit uppers size limit of {@link FileTypes#BAM BAM} files stored in memory,
	 * if parameter is lesser than 512 or greater than {@link Integer#MAX_VALUE Integer.MAX_VALUE} / 2, paramter is forced to be valid(>512 or <Integer.MAX_VALUE/2)
	 * @return {@code true} - if new value is valid and it had been set
	 */
	public boolean setBamFileLimit(int newBamFileLimit) {
		if (newBamFileLimit >= 512 && newBamFileLimit <= (Integer.MAX_VALUE / 2)) {
			preferences.putInt("bamFileSizeLimit", newBamFileLimit);
			return true;
		}
		return false;
	}

	/**
	 * @return distance of columns of reads in {@link ProfileProperties.ReadsShowType#SHORT short} mode
	 */
	public int getReadDistance() {
		readDistance = preferences.getInt("readDistance", readDistance);
		return readDistance;
	}

	/**
	 * @param readDistance distance of columns of reads in {@link ProfileProperties.ReadsShowType#SHORT short} mode
	 */
	public void setReadDistance(int readDistance) {
		this.readDistance = readDistance;
		preferences.putInt("readDistance", readDistance);
	}

	// TODO load another directories from application-setting.properties
	/**
	 *
	 * @return {@link List List> of directories whiches scanned for profile files
	 */
	public List<File> getProfileDirectories() {
		List<File> profileDirectories = new LinkedList<File>();
		profileDirectories.add(new File("." + File.separator));
		profileDirectories.add(new File("." + File.separator + "Profiles"));
		profileDirectories.add(defaultProfileDirectory);
		return profileDirectories;
	}

	/**
	 *
	 * @return last active profile
	 */
	public String getLastProfile() {
		return preferences.get("lastProfile", null);
	}

	/**
	 *
	 * @param profileName to be set as last profile
	 */
	public void setLastProfile(String profileName) {
		preferences.put("lastProfile", profileName);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + MAX_KEPT_OPENED_FILES;
		result = prime * result + ((defaultProfileDirectory == null) ? 0 : defaultProfileDirectory.hashCode());
		result = prime * result + ((preferences == null) ? 0 : preferences.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ApplicationProperties)) {
			return false;
		} else if (obj == this) {
			return true;
		}
		return (this.preferences.equals(((ApplicationProperties) obj).preferences));
	}
}
