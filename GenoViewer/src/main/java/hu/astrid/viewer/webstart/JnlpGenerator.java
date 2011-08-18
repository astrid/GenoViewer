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
package hu.astrid.viewer.webstart;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;

/**
 *
 * @author OTTO
 */
public class JnlpGenerator extends Task {

	private String codebase;
	private String title;
	private String vendor;
	private String homepage;
	private String shortDescription;
	private String mainClass;
	private String menuGroup;
	private List<FileSet> jarFiles = new ArrayList<FileSet>();
	private List<Resource> resources = new ArrayList<Resource>();

	public static class Resource extends FileSet {

		private boolean main = false;
		private String mainClass;

		public Resource() {
			super();
		}

		public Resource(FileSet fileset) {
			super(fileset);
		}

		public boolean isMain() {
			return main;
		}

		public void setMain(boolean main) {
			this.main = main;
		}

		public String getMainClass() {
			return mainClass;
		}

		public void setMainClass(String mainClass) {
			this.mainClass = mainClass;
		}
	}

	public void addResource(Resource resource) {
		resources.add(resource);
	}

	public void addFileSet(FileSet fs) {
		jarFiles.add(fs);
	}

	@Override
	public void execute() throws BuildException {
		jnlpGeneration();
		htmlGeneration();
	}

	private void jnlpGeneration() {
		PrintWriter xmlWriter = null;
		try {
			//homepage = "http://localhost:8080/ViewerWebStart";
			xmlWriter = new PrintWriter(new FileOutputStream(
					"target/webstart/launch.jnlp"));

			xmlWriter.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			xmlWriter.println("<jnlp codebase=\"" + codebase + "\" href=\"launch.jnlp\" spec=\"1.5+\">");
			xmlWriter.println("\t<information>");
			xmlWriter.println("\t\t<title>" + title + "</title>");
			xmlWriter.println("\t\t<vendor>" + vendor + "</vendor>");
			xmlWriter.println("\t\t<homepage href=\"" + homepage + "\"/>");
			xmlWriter.println("\t\t<description>" + shortDescription + "</description>");
			xmlWriter.println("\t\t<icon href=\"icon.png\"/>");
			xmlWriter.println("\t\t<icon kind=\"splash\" href=\"splash.png\"/>");
			xmlWriter.println("\t\t<offline-allowed/>");
			xmlWriter.println("\t\t<shortcut online=\"true\">");
			xmlWriter.println("\t\t\t<desktop/>");
			xmlWriter.println("\t\t\t<menu submenu=\""+menuGroup+"\"/>");
			xmlWriter.println("\t\t</shortcut>");
			xmlWriter.println("\t</information>");
			//
			xmlWriter.println("\t<update check=\"timeout\" policy=\"prompt-update\"/>");
			//
			xmlWriter.println("\t<security>");
			xmlWriter.println("\t\t<all-permissions/>");
			xmlWriter.println("\t</security>");

			StringBuilder sb = new StringBuilder();
			sb.append("\t<resources>\n");
			sb.append("\t\t<j2se version=\"1.6+\"/>\n");
			for (Resource file : resources) {
				DirectoryScanner directoryScanner = file.getDirectoryScanner(getProject());
				String[] includedFiles = directoryScanner.getIncludedFiles();
				for (int i = 0; i < includedFiles.length; i++) {
					if (file.isMain()) {
						sb.append("\t\t<jar href=\"" + includedFiles[i]
								+ "\" main=\"true\"/>\n");
						this.mainClass = file.getMainClass();
					} else {
						sb.append("\t\t<jar href=\"lib/" + includedFiles[i]
								+ "\"/>\n");
					}
				}
			}
			sb.append("\t</resources>");
			xmlWriter.println(sb.toString());
			//
			xmlWriter.println("\t<application-desc main-class=\"" + mainClass
					+ "\"/>");
			xmlWriter.println("</jnlp>");
		} catch (FileNotFoundException e) {
//			logger.error(e.getMessage());
		} finally {
			if (xmlWriter != null) {
				xmlWriter.close();
			}
		}
	}

	private void htmlGeneration() {
		PrintWriter printWriter = null;
		try {
			printWriter = new PrintWriter(new FileOutputStream(
					"target/webstart/index.html"));
			printWriter.println("<html>");
			printWriter.println("<body>");
			printWriter.println("\t<a href=\"launch.jnlp\">Start Viewer</a>");
			printWriter.println("<br/>\t<a href=\"testfiles.zip\">Test files</a>");
			printWriter.println("</body>");
			printWriter.println("</html>");
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} finally {
			if (printWriter != null) {
				printWriter.close();
			}
		}
	}

	public void setCodebase(String codebase) {
		this.codebase = codebase;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setVendor(String vendor) {
		this.vendor = vendor;
	}

	public void setHomepage(String homepage) {
		this.homepage = homepage;
	}

	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}

	public void setMenuGroup(String menuGroup) {
		this.menuGroup = menuGroup;
	}
	
}
