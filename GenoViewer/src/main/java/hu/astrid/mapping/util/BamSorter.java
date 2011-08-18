
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

package hu.astrid.mapping.util;

import hu.astrid.mapping.exception.MappingFileException;
import hu.astrid.mapping.io.BamReader;
import hu.astrid.mapping.io.BamWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Sorter for BAM file
 * @see MappingFileSorter
 * @author Szuni
 */
public class BamSorter extends MappingFileSorter {

	/**
	 *
	 * @param unsortedFile
	 * @param sortedFile
	 * @throws MappingFileException
	 * @throws IOException
	 */
	public BamSorter(File unsortedFile, File sortedFile) throws MappingFileException, IOException {
		super(new BamReader(new FileInputStream(unsortedFile)), new BamWriter(new FileOutputStream(sortedFile)));
	}

}
