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

import java.io.File;
import java.io.FileFilter;

/**
 * Enum type for GenoViewer command line parameters
 * @author onagy
 */
public enum Parameter {

	SAMPLE("--sample", "-s", "<sample files location>", "Open sample files after start", new ParameterValueValidator() {

@Override
boolean validate(String candidate) {
	return (new File(candidate).exists());
}
}),
	APPSETTINGS("--appsettings", "-a", "<application property file>", "File storing application porperties", new ParameterValueValidator() {

@Override
boolean validate(String candidate) {
	return (new File(candidate).exists() && (candidate.endsWith(".properties")  || candidate.endsWith(".xml")));
}
}),
	WORKSPACE("--workspace", "-w", "<workspace directory>", "Open the specified workspace and it's contained projects."
	+ "\n\t\tA valid workspace has a \"<workspace_dir_name>.info\" file in it's root directory,\n\t\tcontains at least one directory for a project.\n\t\tIn "
	+ "that project directory there must be a properties file called \"<project_dir_name>.properties\"", new ParameterValueValidator() {

@Override
boolean validate(String candidate) {
	File workspaceDir = new File(candidate);

	if (!workspaceDir.exists() || !workspaceDir.isDirectory()) {
		return false;
	}

	File[] workspaceDirContent = workspaceDir.listFiles();
	boolean infoFileFounded = false;
	boolean atLeastOneDirFound = false;
	String expectedInfoFileName = workspaceDir.getName() + ".info";

	for (File file : workspaceDirContent) {
		if (file != null && file.getName().equals(expectedInfoFileName)) {
			infoFileFounded = true;
		} else if (file != null && file.isDirectory()) {
			atLeastOneDirFound = true;
		}

		if (infoFileFounded && atLeastOneDirFound) {
			break;
		}
	}

	if (!infoFileFounded || !atLeastOneDirFound) {
		return false;
	}

	File[] projectDirectories = workspaceDir.listFiles(new FileFilter() {

		public boolean accept(File pathname) {
			return pathname.isDirectory();
		}
	});

	for (File projectDir : projectDirectories) {

		File[] projectContent = projectDir.listFiles();
		boolean projectPropertiesFileFound = false;

		for (File file : projectContent) {

			if (file != null && file.getName().equals(projectDir.getName() + ".properties")) {
				projectPropertiesFileFound = true;
				break;
			}
		}

		if (!projectPropertiesFileFound) {
			return false;
		}
	}

	return true;
}
}),
	HELP("--help", "-h", null, "Print Available command line parameters", null);
	private final String fullParamName;
	private final String shortParamName;
	private final String description;
	private final String expectedParamDesc;
	private final ParameterValueValidator validator;
	private final boolean onlyCmdLineSwitch;

	private Parameter(String fullParamName, String shortParamName, String expectedParamDesc, String description, ParameterValueValidator validator) {
		this.fullParamName = fullParamName;
		this.shortParamName = shortParamName;
		this.expectedParamDesc = expectedParamDesc;
		this.description = description;
		this.validator = validator;

		if (expectedParamDesc == null || expectedParamDesc.length() == 0) {
			onlyCmdLineSwitch = true;
		} else {
			onlyCmdLineSwitch = false;
		}
	}

	/**
	 * 
	 * @return the proper and formatted usage scenario of the parameter
	 */
	public String getFullDescription() {

		return new StringBuffer("\t").append(this.fullParamName).append(", ").append(this.shortParamName).append(" \t").
				append((this.expectedParamDesc == null) ? "no value" : this.expectedParamDesc).append(" ").append(this.description).toString();

	}

	/**
	 * 
	 * @param paramCandidate the parameter to check, the format is either "--param" or "-p"
	 * @return the parameter if it can be successfully interpret, or null if there is no such parameter
	 */
	public Parameter canInterpretParameter(String paramCandidate) {

		if (paramCandidate.equals(this.fullParamName) || paramCandidate.equals(this.shortParamName)) {
			return this;
		} else {
			return null;
		}
	}

	/**
	 *
	 * @param paramValueCandidate value part of the parameter
	 * @return if the value can be validated as an applicable value for the parameter
	 */
	public boolean isApplicableParameterValue(String paramValueCandidate) {

		if (this.onlyCmdLineSwitch) {
			return false;
		}

		if (this.validator == null) {
			return true;
		} else {
			return this.validator.validate(paramValueCandidate);
		}
	}

	/**
	 *
	 * @return if the parameter is just a command line switch
	 */
	public boolean onlyCmdLineSwitch() {
		return this.onlyCmdLineSwitch;
	}

	private static abstract class ParameterValueValidator {

		abstract boolean validate(String candidate);
	}

	@Override
	public String toString() {
		return ("<" + this.fullParamName + ">");
	}
}
