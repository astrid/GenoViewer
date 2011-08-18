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

import java.util.Properties;

/**
 *  NOT USED BY PROJECTS YET!
 * @author onagy
 */
public class ProjectProperties {

	private final String projectName;
	private final Properties properties;

	/**
	 *
	 * @param properties
	 * @param projectName
	 */
	public ProjectProperties(Properties properties, String projectName) {
		this.projectName = projectName;
		this.properties = properties;
	}

	@Override
	public String toString() {
		return projectName;
	}

	Properties getProperties() {
		return this.properties;
	}

	/**
	 *
	 */
	public void printProperties() {
		System.out.println(properties.toString());
	}

	/**
	 *
	 * @return
	 */
	public String getProjectName() {
		return projectName;
	}
}
