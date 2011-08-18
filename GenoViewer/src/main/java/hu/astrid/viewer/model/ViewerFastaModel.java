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

package hu.astrid.viewer.model;

import hu.astrid.contig.Contig;
import hu.astrid.core.Nucleotide;
import hu.astrid.mvc.swing.AbstractModel;
import hu.astrid.viewer.Viewer;
import hu.astrid.viewer.reader.FastaRandomReader;
import hu.astrid.viewer.reader.FastaRandomReader.FastaRandomReaderException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * Viewer model for handling fasta file
 * @author Szuni, Máté
 */
public class ViewerFastaModel extends AbstractModel {

	/** Reference load state {@link ViewerFastaModel#setReferenceLoaded(java.io.File)} */
	public static final String REFERENCE_LOAD = "ReferenceLoaded";
	/** Actual contig index in fasta file {@link ViewerFastaModel#setActReferenceContigIndex(java.lang.Integer)}  */
	public static final String ACT_CONTIG_INDEX = "ActReferenceContigIndex";
	/**Logger*/
	private static Logger logger = Logger.getLogger(ViewerFastaModel.class);
	/**Fasta reader*/
	private FastaRandomReader reader;
	/**Actual contig inedx*/
	private int actContigIndex = -1;

	/**
	 * Name of fasta file
	 * @return
	 */
	public String getFileName() {
		return reader != null ? reader.getFileName() : null;
	}

	/**
	 * Get the index of the actual contig.
	 * @return
	 */
	public int getActContigIndex() {
		return actContigIndex;
	}

	/**
	 * Set the actual contig index.
	 * @param actContigIndex
	 */
	public void setActReferenceContigIndex(Integer actContigIndex) {
		int oldValue = this.actContigIndex;
		if (this.actContigIndex != actContigIndex) {
			this.actContigIndex = actContigIndex;
			firePropertyChange(ACT_CONTIG_INDEX,
					oldValue, actContigIndex);
		}
	}

	/**
	 * Open a Fasta file to read, nofiy views.
	 * @param file if {@code null} just close happens
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws hu.astrid.viewer.reader.FastaRandomReader.FastaRandomReaderException
	 */
	public void setReferenceLoaded(File file) throws FileNotFoundException, IOException, FastaRandomReaderException {
		closeFastaFile();
		if (file != null) {
			try {
				reader = new FastaRandomReader(file, Viewer.getApplicationProperties().getBufferSize(), true);
			} catch (IOException ex) {
				closeFastaFile();
				throw ex;
			} catch (FastaRandomReaderException ex) {
				closeFastaFile();
				throw ex;
			}
			firePropertyChange(REFERENCE_LOAD, null, reader);
		}
	}

	/**
	 * Read from the fasta file, from position of actual contig.
	 * @param position
	 * @return the loaded part of contig
	 * @throws IOException
	 * @throws hu.astrid.viewer.reader.FastaRandomReader.FastaRandomReaderException
	 * @see FastaRandomReader#load(int, int)
	 */
	public Contig<Nucleotide> readFromPosition(int position) throws IOException,
			FastaRandomReaderException {
		try {
			Contig<Nucleotide> contig = reader.load(position, actContigIndex);
			return contig;
		} catch (FastaRandomReader.MissingContigStartIndicesException ex) {
			closeFastaFile();
			throw ex;
		} catch (FastaRandomReader.EmptyBufferException ex) {
			throw ex;
		} catch (IOException ex) {
			closeFastaFile();
			throw ex;
		} catch (FastaRandomReader.ContigIndexOutOfBoundsException ex) {
			throw ex;
		} catch (FastaRandomReader.ContigPositionOutOfBoundsException ex) {
			throw ex;
		}
	}

	/**
	 * Return the IDs of the contigs.
	 * @return
	 * @see FastaRandomReader#getContigNames()
	 */
	public List<String> getContigNames() {
		return reader != null ? reader.getContigNames() : new ArrayList<String>(0);
	}

	/**
	 * Return the length of the actual contig.
	 * @return
	 * @see FastaRandomReader#getContigLength(int)
	 */
	public int getContigLength() {
		return reader.getContigLength(actContigIndex);
	}

	/**
	 * Close the Fasta file, notify views.
	 * @see FastaRandomReader#closeFile()
	 */
	private void closeFastaFile() {
		if (reader != null) {
			try {
				reader.closeFile();
			} catch (IOException ex) {
				logger.error(ex.getMessage(), ex);
			}
			reader = null;
			actContigIndex = -1;
			firePropertyChange(REFERENCE_LOAD, reader, null);
		}
	}

	/**
	 * Return true if Fasta file is opened.
	 * @return
	 */
	public boolean isFastaFileOpened() {
		return reader != null;
	}

	/**
	 * Part of actual contig reference, started width {@code position} in {@code refLength} length
	 * @param position start position
	 * @param refLength length of demanded sequence
	 * @return specified part of actual contig
	 */
	public String getRefrenceAlignmentString(int position, int refLength) {
		try {
			String loadedRef = reader.load(position - 1, actContigIndex).toString();
			return loadedRef.substring(0, Math.min(loadedRef.length(), refLength));
		} catch (FastaRandomReaderException e) {
			logger.error(e.getMessage(), e);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		} catch (NullPointerException e) {
			logger.warn("No fasta file loaded");
		} catch (StringIndexOutOfBoundsException e) {
			logger.error(e.getMessage(), e);
		}
		return "";
	}
}
