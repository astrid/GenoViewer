
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
import hu.astrid.mapping.io.SamReader;
import hu.astrid.mapping.io.SamWriter;

import java.io.File;
import java.io.IOException;

/**
 * Sorter for SAM file
 * @see MappingFileSorter
 * @author Szuni
 */
public class SamSorter extends MappingFileSorter{

	/**
	 *
	 * @param unsortedFile
	 * @param sortedFile
	 * @throws IOException
	 * @throws MappingFileException
	 */
	public SamSorter(File unsortedFile, File sortedFile) throws IOException, MappingFileException{
		super(new SamReader(unsortedFile), new SamWriter(sortedFile));
	}

}
