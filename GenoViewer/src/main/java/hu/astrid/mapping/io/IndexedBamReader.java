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
import hu.astrid.mapping.exception.MappingFileFormatException;
import hu.astrid.mapping.model.AlignmentPosition;
import hu.astrid.mapping.model.AlignmentRecord;
import hu.astrid.mapping.model.BamHeader;
import hu.astrid.mapping.model.BamIndex;
import hu.astrid.mapping.model.Chunk;
import hu.astrid.mapping.model.VirtualFileOffset;
import hu.astrid.mapping.util.BamUtil;
import java.io.FileNotFoundException;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.zip.DataFormatException;
import org.apache.log4j.Logger;

/**
 * Reader implementation which purpose is to load alignment records from a BAM
 * mapping file using indices from the appropriate BAI file.
 */
public class IndexedBamReader implements IndexedMappingFileReader {

	private static Logger logger = Logger.getLogger(IndexedBamReader.class);
	/**
	 * The header of the BAM file containing the appropriate SAM header and the
	 * names and lengths of the reference sequences.
	 */
	private BamHeader header;
	/**
	 * The index built from the appropriate BAI file in order to load alignment
	 * records efficiently.
	 */
	private BamIndex bamIndex;
	/**
	 * The BGZF-compressed BAM file which contains the alignment records.
	 */
	private RandomAccessFile bamFile;
	/**
	 * File channel for the random file.
	 */
	private FileChannel bamFileChannel;
	/**
	 * Mapped buffer for the random file.
	 */
	private MappedByteBuffer mappedBuffer;
	/**
	 * Decompressor to decompress BAM content.
	 */
	private BgzfDecompressor decompressor;
	private AlignmentRecordCodec alignmentRecordCodec;
	private long channelSize;
	private String bamFileName;
	private String baiFileName;

	/**
	 * Creates a new reader instance for the given BAM file. Looks for the BAI
	 * file next to the BAM: for a filename.bam it looks for the
	 * filename.bam.bai index file.
	 *
	 * @param pathName
	 *            the path of the BAM file
	 * @throws IOException
	 *             if an I/O error occurs
	 * @throws IndexFileFormatException
	 *             if the format of the index file is incorrect
	 * @throws MappingHeaderException
	 *             if the header of the BAM file is incorrect
	 */
	public IndexedBamReader(String pathName)
		throws IOException, IndexFileFormatException, MappingFileFormatException {
		this.bamFileName = pathName;
		this.baiFileName = pathName + ".bai";

		decompressor = new BgzfDecompressor();
		this.loadHeader();
//		this(pathName, pathName + ".bai");
	}

	/**
	 * Creates a new reader instance for the given BAM and BAI files.
	 * 
	 * @param bamFileName
	 *            the path of the BAM file
	 * @param baiFileName
	 *            the path of the BAI file
	 * @throws IOException
	 *             if an I/O error occurs
	 * @throws IndexFileFormatException
	 *             if the format of the index file is incorrect
	 * @throws MappingFileFormatException
	 *             if the header of the BAM file is incorrect
	 */
	public IndexedBamReader(String bamFileName, String baiFileName) throws IOException, IndexFileFormatException, MappingFileFormatException {

		bamFile = new RandomAccessFile(bamFileName, "r");
		bamIndex = new BaiReader().load(baiFileName);

//		this.bamFileName = bamFileName;
//		this.baiFileName = baiFileName;

		bamFileChannel = bamFile.getChannel();
		channelSize = bamFileChannel.size();
		logger.debug("bamFileChannelSize: " + channelSize + " " + Integer.MAX_VALUE + " " + Long.MAX_VALUE);
		mappedBuffer = null; // bamFileChannel.map(MapMode.READ_ONLY, 0L, bamFileChannel.size());
		decompressor = new BgzfDecompressor();

		this.loadHeader();
	}

	private synchronized FileChannel loadBamFileChannel() {
		try {
			RandomAccessFile randomAccessFile = new RandomAccessFile(bamFileName, "r");
			return randomAccessFile.getChannel();
		} catch (FileNotFoundException ex) {
			java.util.logging.Logger.getLogger(IndexedBamReader.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IOException ex) {
			java.util.logging.Logger.getLogger(IndexedBamReader.class.getName()).log(Level.SEVERE, null, ex);
		}
		return null;
	}

	private synchronized BamIndex loadBamIndex() {
		try {
			return new BaiReader().load(baiFileName);
		} catch (IOException ex) {
			java.util.logging.Logger.getLogger(IndexedBamReader.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IndexFileFormatException ex) {
			java.util.logging.Logger.getLogger(IndexedBamReader.class.getName()).log(Level.SEVERE, null, ex);
		}
		return null;
	}

	@Override
	public BamHeader getHeader() {
		return header;
	}

	@Override
	public synchronized List<AlignmentRecord> loadRecords(String referenceName, int start, int end)//
		throws IOException, MappingFileFormatException {

		if (start >= end) {
			return new ArrayList<AlignmentRecord>();
		}

		int referenceIndex = header.indexOf(referenceName);
		if (referenceIndex == -1) {
			throw new IllegalArgumentException("Unknown reference: " + referenceName);
		}

//		List<Chunk> chunks = bamIndex.getChunks(referenceIndex, start, end);
//		VirtualFileOffset linearIndex = bamIndex.linearIndexOf(referenceIndex, start);

		List<Chunk> chunks = loadBamIndex().getChunks(referenceIndex, start, end);
		VirtualFileOffset linearIndex = loadBamIndex().linearIndexOf(referenceIndex, start);

		for (Chunk chunk : chunks) {
			if (linearIndex != null
				&& chunk.getEndOffset().compareTo(linearIndex) < 0) {
				continue;
			}

			List<byte[]> binaryRecords = this.readFrom(chunk.getStartOffset(), start, end);

			if (binaryRecords != null) {
				return alignmentRecordCodec.decode(binaryRecords);
			}
			break;
		}

		return new ArrayList<AlignmentRecord>();
	}

	@Override
	public synchronized List<AlignmentPosition> loadPositions(String referenceName,
		int start, int end) throws IOException, MappingFileFormatException {

		if (start >= end) {
			return new ArrayList<AlignmentPosition>();
		}

		int referenceIndex = header.indexOf(referenceName);
		if (referenceIndex == -1) {
			throw new IllegalArgumentException("Unknown reference: " + referenceName);
		}

//		List<Chunk> chunks = bamIndex.getChunks(referenceIndex, start, end);
//		VirtualFileOffset linearIndex = bamIndex.linearIndexOf(referenceIndex, start);

		List<Chunk> chunks = loadBamIndex().getChunks(referenceIndex, start, end);
		VirtualFileOffset linearIndex = loadBamIndex().linearIndexOf(referenceIndex, start);

		for (Chunk chunk : chunks) {
			if (linearIndex != null
				&& chunk.getEndOffset().compareTo(linearIndex) < 0) {
				continue;
			}

			List<byte[]> binaryRecords = this.readFrom(chunk.getStartOffset(), start, end);

			if (binaryRecords != null) {
				return alignmentRecordCodec.decodePositions(binaryRecords);
			}
			break;
		}

		return new ArrayList<AlignmentPosition>();
	}

	@Override
	public synchronized AlignmentRecord loadRecord(String referenceName, String queryName,
		int position, int length) throws IOException, MappingFileFormatException {

		int referenceIndex = header.indexOf(referenceName);
		if (referenceIndex == -1) {
			throw new IllegalArgumentException("Unknown reference: " + referenceName);
		}

//		List<Chunk> chunks = bamIndex.getChunks(referenceIndex, BamUtil.regToBin(position, position + length));
//		VirtualFileOffset linearIndex = bamIndex.linearIndexOf(referenceIndex, position);

		List<Chunk> chunks = loadBamIndex().getChunks(referenceIndex, BamUtil.regToBin(position, position + length));
		VirtualFileOffset linearIndex = loadBamIndex().linearIndexOf(referenceIndex, position);

		for (Chunk chunk : chunks) {
			if (linearIndex != null
				&& chunk.getEndOffset().compareTo(linearIndex) < 0) {
				continue;
			}

			List<byte[]> binaryRecords = this.readFrom(chunk.getStartOffset(), position, position + 1);

			if (binaryRecords != null) {
				for (byte[] binaryRecord : binaryRecords) {
					String name = alignmentRecordCodec.decodeQueryName(binaryRecord);
					if (queryName.equals(name)) {
						return alignmentRecordCodec.decode(binaryRecord);
					}
				}
			}
		}

		return null;
	}

	/**
	 * Reads binary alignment records in a given interval. The reading starts at a given file offset.
	 * 
	 * @param from
	 *            the virtual file offset from which to start the reading
	 * @param start
	 *            the beginning of the interval, inclusive
	 * @param end
	 *            the end of the interval, exclusive
	 * @return the binary alignment records in the given interval
	 * @throws IOException
	 *             if an I/O error occurs
	 * @throws MappingFileFormatException
	 *             if the format of the alignment records is incorrect
	 */
	private List<byte[]> readFrom(VirtualFileOffset from, int start, int end)
		throws IOException, MappingFileFormatException {

		List<byte[]> records = new ArrayList<byte[]>();

		boolean firstBlock = true;
		long blockOffset = from.getBlockOffset();
		byte[] uncompressedData = null;

		while (true) {
			BgzfBlock block = this.readBlock((int) blockOffset);
			if (block == null) {
				break;
			}

			blockOffset += block.getDeflatedSize() + 26;
			if (firstBlock) {
				try {
					uncompressedData = decompressor.decompress(block);
				} catch (DataFormatException e) {
					throw new MappingFileFormatException(e.getMessage());
				}
				uncompressedData = Arrays.copyOfRange(uncompressedData,
					from.getDataOffset(), uncompressedData.length);

				firstBlock = false;
			} else {
				byte[] uncompressedBlock = null;
				try {
					uncompressedBlock = decompressor.decompress(block);
				} catch (DataFormatException e) {
					throw new MappingFileFormatException(e.getMessage());
				}
				byte[] tmp = new byte[uncompressedData.length + uncompressedBlock.length];
				System.arraycopy(uncompressedData, 0, tmp, 0, uncompressedData.length);
				System.arraycopy(uncompressedBlock, 0, tmp, uncompressedData.length, uncompressedBlock.length);
				uncompressedData = tmp;
			}

			int offset = 0;
			for (; offset < uncompressedData.length;) {
				if (offset + 4 > uncompressedData.length) {
					uncompressedData = Arrays.copyOfRange(uncompressedData, offset, uncompressedData.length);
					break;
				}
				int recordSize = BamUtil.toInt(Arrays.copyOfRange(
					uncompressedData, offset, offset + 4));
				if (offset + 4 + recordSize > uncompressedData.length) {
					uncompressedData = Arrays.copyOfRange(uncompressedData, offset, uncompressedData.length);
					break;
				}

				offset += 4;
				byte[] binaryRecord = Arrays.copyOfRange(uncompressedData, offset,
					offset + recordSize);
				offset += recordSize;

				int position = alignmentRecordCodec.getPosition(binaryRecord);
				if (position >= start && position < end) {
					records.add(binaryRecord);
				} else if (position >= end) {
					return records;
				}
			}

			if (offset == uncompressedData.length) {
				uncompressedData = new byte[0];
			}

		}

		return records;
	}

	/**
	 * Reads a block from a given position of the BGZF-compressed BAM file.
	 *
	 * @param position
	 *            the start position of the block
	 * @return the block read from the given position
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	private BgzfBlock readBlock(int position) throws IOException {
		FileChannel bamFileChannel = loadBamFileChannel();

//		bamFileChannel=bamFile.getChannel();

		if (position < 0 || position >= bamFileChannel.size()) {
			return null;
		}
		MappedByteBuffer mappedBuffer = bamFileChannel.map(MapMode.READ_ONLY, position, 18);
		mappedBuffer = bamFileChannel.map(MapMode.READ_ONLY, position, 18);
		mappedBuffer.position(0);
		byte[] byteArray = new byte[18];
		mappedBuffer.get(byteArray, 0, 18);

		int length = BamUtil.toShort(Arrays.copyOfRange(byteArray, 16,
			18)) + 1;
		byte[] block = new byte[length];

		mappedBuffer = bamFileChannel.map(MapMode.READ_ONLY, position, length);
		mappedBuffer.get(block, 0, length);

		int inputSize = BamUtil.toInt(Arrays.copyOfRange(block,
			block.length - 4, block.length));

		BgzfBlock bgzfBlock = new BgzfBlock(block, length - 26, inputSize);

		bamFileChannel.close();
		return bgzfBlock;
	}

	/**
	 * Loads the header of the BAM file.
	 * @throws IOException if an I/O error occurs
	 * @throws MappingHeaderException if the header format is incorrect
	 */
	private void loadHeader() throws IOException, MappingFileFormatException {
		byte[] bamHeader = null;
		int offset = 0;

		BgzfBlock block = readBlock(offset);
		try {
			bamHeader = decompressor.decompress(block);
		} catch (DataFormatException e) {
			throw new MappingFileFormatException(e.getMessage());
		}
		offset += block.getDeflatedSize() + 26;
		while (!MappingFileParser.isValidBamHeader(bamHeader)) {
			block = readBlock(offset);
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
		}

		this.header = MappingFileParser.parseBamHeader(bamHeader);

		alignmentRecordCodec = new AlignmentRecordCodec(this.header.getReferenceNames());
	}

	@Override
	public void close() throws IOException {
		if (bamFile != null) {
			bamFile.close();
		}
	}
}
