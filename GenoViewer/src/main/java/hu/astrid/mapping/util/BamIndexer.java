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

import hu.astrid.mapping.exception.MappingFileFormatException;
import hu.astrid.mapping.io.AlignmentRecordCodec;
import hu.astrid.mapping.io.BgzfBlock;
import hu.astrid.mapping.io.BgzfDecompressor;
import hu.astrid.mapping.io.MappingFileParser;
import hu.astrid.mapping.model.AlignmentPosition;
import hu.astrid.mapping.model.BamHeader;
import hu.astrid.mapping.model.BamIndex;
import hu.astrid.mapping.model.Bin;
import hu.astrid.mapping.model.Chunk;
import hu.astrid.mapping.model.ReferenceIndex;
import hu.astrid.mapping.model.VirtualFileOffset;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.DataFormatException;

public class BamIndexer {

	/**
	 * Interval length for linear indices.
	 */
	private static int LINEAR_INDEX_INTERVAL_LENGTH = 1 << 14;
	
	/**
	 * The input stream from which the BAM file content is read.
	 */
	private DataInputStream inputStream;
	
	/**
	 * Decompressor instance to decompress BGZF blocks.
	 */
	private BgzfDecompressor decompressor;
	
	/**
	 * The header of the BAM file.
	 */
	private BamHeader header;
	
	/**
	 * Codec instance to decode binary alignment records.
	 */
	private AlignmentRecordCodec alignmentRecordCodec;
	
	/**
	 * The size of the BAM file.
	 */
	private long fileSize;
	
	/**
	 * The length of the BGZF block read last.
	 */
	private int lastBlockLength;
	
	/**
	 * Uncompressed binary content of the current BGZF block.
	 */
	private byte[] currentBlock;
	
	/**
	 * The block offset of the current BGZF block.
	 */
	private long currentBlockOffset;
	
	/**
	 * The data offset of the next alignment record.
	 */
	private int currentDataOffset;
	
	/**
	 * The location of the alignment record read last.
	 */
	private VirtualFileOffset lastRecordLocation;
	
	/**
	 * Creates a new indexer instance for the BAM file given by its path.
	 * 
	 * @param fileName
	 *            the path of the BAM file to be indexed
	 * @throws FileNotFoundException
	 *             if the BAM file was not found
	 */
	public BamIndexer(String fileName) throws FileNotFoundException {
		File file = new File(fileName);
		this.fileSize = file.length();
		this.inputStream = new DataInputStream(new FileInputStream(file));
		this.decompressor = new BgzfDecompressor();
		this.lastBlockLength = -1;
	}
	
	/**
	 * Indexes the BAM file and returns the index structure.
	 * 
	 * @return the index structure for the BAM file
	 * @throws IOException
	 *             if an I/O error occurs
	 * @throws MappingFileFormatException
	 *             if the format of the BAM file is incorrect
	 * @throws DataFormatException
	 *             if the data format in the BAM file is incorrect
	 */
	public BamIndex index() throws IOException, MappingFileFormatException, DataFormatException {
		this.currentBlockOffset = 0L;
		this.currentDataOffset = 0;
		this.loadHeader();
		
		AlignmentPosition record = this.getNextAlignmentPosition();
		if (record == null) {
			return null;
		}
		String lastReferenceName = record.getReferenceName();
		int lastBinId = BamUtil.regToBin(record.getPosition(), record.getPosition() + record.getReadLength());
		VirtualFileOffset chunkStart = this.lastRecordLocation;
		BamIndex bamIndex = new BamIndex();
		Map<Integer, Bin> bins = new HashMap<Integer, Bin>();
		
		List<VirtualFileOffset> linearIndices = new ArrayList<VirtualFileOffset>();
		for (int i = 0; i < record.getPosition() / LINEAR_INDEX_INTERVAL_LENGTH; ++i) {
			linearIndices.add(null);
		}
		linearIndices.add(lastRecordLocation);
		
		while ((record = this.getNextAlignmentPosition()) != null) {
			int lastWindow = linearIndices.size() - 1;
			int currentWindow = record.getPosition() / LINEAR_INDEX_INTERVAL_LENGTH;
			if (lastWindow != currentWindow) {
				for (int i = lastWindow; i < currentWindow - 1; ++i) {
					linearIndices.add(null);
				}
				linearIndices.add(lastRecordLocation);
			}
			
			int binId = BamUtil.regToBin(record.getPosition(), record.getPosition() + record.getReadLength());
			if (!lastReferenceName.equals(record.getReferenceName())) {
				Chunk chunk = new Chunk(chunkStart, lastRecordLocation);
				Bin bin = bins.get(lastBinId);
				if (bin == null) {
					bin = new Bin(lastBinId);
					bins.put(lastBinId, bin);
				}
				bin.addChunk(chunk);
				lastBinId = binId;
				chunkStart = lastRecordLocation;
				
				ReferenceIndex referenceIndex = new ReferenceIndex();
				List<Bin> referenceBins = new ArrayList<Bin>(bins.values());
				Collections.sort(referenceBins);
				for (Bin b : referenceBins) {
					referenceIndex.addBin(b);
				}
				
				for (VirtualFileOffset vfo : linearIndices) {
					referenceIndex.addLinearIndex(vfo);
				}
				bamIndex.addReferenceIndex(referenceIndex);
			}
			
			if (binId != lastBinId) {
				Chunk chunk = new Chunk(chunkStart, lastRecordLocation);
				Bin bin = bins.get(lastBinId);
				if (bin == null) {
					bin = new Bin(lastBinId);
					bins.put(lastBinId, bin);
				}
				bin.addChunk(chunk);
				lastBinId = binId;
				chunkStart = lastRecordLocation;
			}
		}
		
		Chunk chunk = new Chunk(chunkStart, new VirtualFileOffset(fileSize << 16));
		Bin bin = bins.get(lastBinId);
		if (bin == null) {
			bin = new Bin(lastBinId);
			bins.put(lastBinId, bin);
		}
		bin.addChunk(chunk);
		
		ReferenceIndex referenceIndex = new ReferenceIndex();
		List<Bin> referenceBins = new ArrayList<Bin>(bins.values());
		Collections.sort(referenceBins);
		for (Bin b : referenceBins) {
			referenceIndex.addBin(b);
		}
		
		for (VirtualFileOffset vfo : linearIndices) {
			referenceIndex.addLinearIndex(vfo);
		}
		bamIndex.addReferenceIndex(referenceIndex);
		
		BamIndexer.reduceSmallChunks(bamIndex);
		return bamIndex;
	}
	
	/**
	 * Contracts small chunks of an index structure and hence reduces their
	 * number.
	 * 
	 * @param bamIndex
	 *            the index structure to be simplified
	 */
	private static void reduceSmallChunks(BamIndex bamIndex) {
		for (ReferenceIndex referenceIndex : bamIndex.getReferenceIndices()) {
			for (Bin bin : referenceIndex.getBins()) {
				for (int i = 0; i < bin.getChunks().size() - 1; ++i) {
					Chunk chunk1 = bin.getChunks().get(i);
					Chunk chunk2 = bin.getChunks().get(i + 1);
					if (chunk2.getStartOffset().getBlockOffset() <= chunk1.getEndOffset().getBlockOffset()) {
						chunk1.setEndOffset(chunk2.getEndOffset());
						bin.getChunks().remove(i + 1);
						--i;
					}
				}
			}
		}
	}
	
	/**
	 * Reads the next alignment record's position.
	 * 
	 * @return the next alignment position
	 * @throws DataFormatException
	 *             if the data format in the BAM file is incorrect
	 * @throws IOException
	 *             if an I/O error occurs during reading
	 * @throws MappingFileFormatException
	 *             if the format of the BAM file is incorrect
	 */
	private AlignmentPosition getNextAlignmentPosition() throws DataFormatException, IOException, MappingFileFormatException {
		lastRecordLocation = new VirtualFileOffset(currentBlockOffset << 16 | currentDataOffset);
		int recordLength;
		if (this.currentBlock.length < this.currentDataOffset + 4) {
			byte[] lengthArray = new byte[4];
			int count = currentBlock.length - currentDataOffset;
			System.arraycopy(currentBlock, currentDataOffset, lengthArray, 0, count);
			
			this.nextBlockData();
			if (this.currentBlock == null || this.currentBlock.length < 4 - count) {
				return null;
			}
			
            System.arraycopy(currentBlock, 0, lengthArray, count, 4 - count);
            recordLength = BamUtil.toInt(lengthArray);
            currentDataOffset = 4 - count;
		} else {
			recordLength = BamUtil.toInt(Arrays.copyOfRange(currentBlock, currentDataOffset, currentDataOffset + 4));
			currentDataOffset += 4;
		}
		
		AlignmentPosition record;
		if (this.currentBlock.length < this.currentDataOffset + recordLength) {
			byte[] recordArray = new byte[recordLength];
			int count = currentBlock.length - currentDataOffset;
			System.arraycopy(currentBlock, currentDataOffset, recordArray, 0, count);
			
			this.nextBlockData();
			if (this.currentBlock == null || this.currentBlock.length < recordLength - count) {
				return null;
			}
            System.arraycopy(currentBlock, 0, recordArray, count, recordLength - count);
            record = alignmentRecordCodec.decodePosition(recordArray);
            currentDataOffset = recordLength - count;
		} else {
			record = alignmentRecordCodec.decodePosition(
					Arrays.copyOfRange(currentBlock, currentDataOffset, currentDataOffset + recordLength));
			currentDataOffset += recordLength;
		}
			
		return record;
	}
	
	/**
	 * Reads and decompresses the next BGZF block.
	 * 
	 * @throws DataFormatException
	 *             if the data format in the BAM file is incorrect
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	private void nextBlockData() throws DataFormatException, IOException {
		BgzfBlock block = readBlock();
		if (block == null) {
			this.currentBlock = null;
			return;
		}

		this.currentBlock = decompressor.decompress(block);
	}
	
	/**
	 * Reads the next BGZF block from the input stream.
	 * 
	 * @return the next compression block
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	private BgzfBlock readBlock() throws IOException {
		if (this.lastBlockLength > 0) {
			this.currentBlockOffset += this.lastBlockLength;
		}
		
        byte[] byteArray = new byte[18];
        if (inputStream.read(byteArray) == -1) {
        	return null;
        }

        int length = BamUtil.toShort(Arrays.copyOfRange(byteArray, 16, 18)) + 1;
        byte[] block = Arrays.copyOf(byteArray, length);

        int bytesRead = inputStream.read(block, 18, length - 18);
        if (bytesRead == -1) {
        	return null;
        }
        int inputSize = BamUtil.toInt(Arrays.copyOfRange(block, block.length - 4, block.length));

        BgzfBlock bgzfBlock = new BgzfBlock(block, length - 26, inputSize);
        this.lastBlockLength = length;

        return bgzfBlock;
    }
	
	/**
	 * Loads the header of the BM file.
	 * 
	 * @throws IOException
	 *             if an I/O error occurs
	 * @throws MappingFileFormatException
	 *             if the format of the BAM file is incorrect
	 */
	private void loadHeader() throws IOException, MappingFileFormatException {
        byte[] bamHeader = null;
        int offset = 0;

        BgzfBlock block = readBlock();
        try {
			bamHeader = decompressor.decompress(block);
			this.currentBlock = bamHeader;
		} catch (DataFormatException e) {
			throw new MappingFileFormatException(e.getMessage());
		}
        offset += block.getDeflatedSize() + 26;
        
        while (!MappingFileParser.isValidBamHeader(bamHeader)) {
            block = readBlock();
            byte[] data = null;
			try {
				data = decompressor.decompress(block);
			} catch (DataFormatException e) {
				throw new MappingFileFormatException(e.getMessage());
			}
            offset += block.getDeflatedSize() + 26;

            byte[] temp = new byte[bamHeader.length + data.length];
            System.arraycopy(bamHeader, 0, temp, 0, bamHeader.length);
            System.arraycopy(data, 0, temp, bamHeader.length, data.length);
            bamHeader = temp;
            this.currentBlock = data;
        }
        
        int headerLength = BamUtil.toInt(Arrays.copyOfRange(bamHeader, 4, 8));
        offset = 8 + headerLength;
        int referenceCount = BamUtil.toInt(Arrays.copyOfRange(bamHeader, offset, offset + 4));
        offset += 4;
        for (int i = 0; i < referenceCount; ++i) {
        	int referenceLength = BamUtil.toInt(Arrays.copyOfRange(bamHeader, offset, offset + 4));
        	offset += 4 + referenceLength + 4;
        }
        
        this.header = MappingFileParser.parseBamHeader(Arrays.copyOfRange(bamHeader, 0, offset));
        this.alignmentRecordCodec = new AlignmentRecordCodec(this.header.getReferenceNames());
        this.currentDataOffset = offset - bamHeader.length + currentBlock.length;
    }

}
