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

package hu.astrid.mapping.io;

import hu.astrid.mapping.exception.IndexFileFormatException;
import hu.astrid.mapping.model.BamIndex;
import hu.astrid.mapping.model.Bin;
import hu.astrid.mapping.model.Chunk;
import hu.astrid.mapping.model.ReferenceIndex;
import hu.astrid.mapping.model.VirtualFileOffset;
import hu.astrid.mapping.util.BamUtil;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Index file writer implementation.
 */
public class BaiWriter {
	
	/**
	 * Flag which indicates whether an index structure where written or not.
	 */
	private boolean written;
	
	/**
	 * The output stream in which the index structure is written.
	 */
	private OutputStream outputStream;
	
	/**
	 * Creates a new writer instance using the given output stream.
	 * 
	 * @param outputStream
	 *            the stream in which the index structure shall be written
	 */
	public BaiWriter(OutputStream outputStream) {
		this.outputStream = outputStream;
		this.written = false;
	}
	
	/**
	 * Writes an index structure to the output stream and closes the stream.
	 * Calling this method multiple times is not allowed.
	 * 
	 * @param bamIndex
	 *            the index structure to be written to the output stream
	 * @throws IndexFileFormatException
	 *             if an index structure was already written to the stream
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	public void write(BamIndex bamIndex) throws IndexFileFormatException, IOException {
		if (this.written) {
			throw new IndexFileFormatException("Cannot write multiple indices to the same stream.");
		}
		
		try {
			this.outputStream.write(BamUtil.BAI_MAGIC_BYTES);
			this.outputStream.write(BamUtil.toByteArray(bamIndex.getReferenceIndices().size()));
			for (ReferenceIndex referenceIndex : bamIndex.getReferenceIndices()) {
				this.outputStream.write(BamUtil.toByteArray(referenceIndex.getBins().size()));
				for (Bin bin : referenceIndex.getBins()) {
					this.outputStream.write(BamUtil.toByteArray(bin.getId()));
					this.outputStream.write(BamUtil.toByteArray(bin.getChunks().size()));
					for (Chunk chunk : bin.getChunks()) {
						this.outputStream.write(BamUtil.toByteArray(chunk.getStartOffset().longRepresentation()));
						this.outputStream.write(BamUtil.toByteArray(chunk.getEndOffset().longRepresentation()));
					}
				}
				
				this.outputStream.write(BamUtil.toByteArray(referenceIndex.getLinearIndices().size()));
				for (VirtualFileOffset offset : referenceIndex.getLinearIndices()) {
					this.outputStream.write(BamUtil.toByteArray(offset.longRepresentation()));
				}
			}
			
			this.outputStream.flush();
		} finally {
			this.outputStream.close();
		}
		
		this.written = true;
	}

}
