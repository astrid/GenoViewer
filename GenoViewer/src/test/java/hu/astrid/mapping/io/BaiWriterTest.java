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

import static org.junit.Assert.assertEquals;
import hu.astrid.mapping.exception.IndexFileFormatException;
import hu.astrid.mapping.model.BamIndex;
import hu.astrid.mapping.model.Bin;
import hu.astrid.mapping.model.Chunk;
import hu.astrid.mapping.model.ReferenceIndex;
import hu.astrid.mapping.model.VirtualFileOffset;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class BaiWriterTest {
	
	private static final String TEMP_DIR = System.getProperty("java.io.tmpdir");
	
	private static BamIndex bamIndex;
	private BaiWriter baiWriter;
	private File tempFile;
	
	@BeforeClass
	public static void setUpIndex() {
		bamIndex = new BamIndex();
		
		ReferenceIndex referenceIndex1 = new ReferenceIndex();
		Bin bin11 = new Bin(0);
		bin11.addChunk(new Chunk(new VirtualFileOffset(0L), new VirtualFileOffset(1L)));
		bin11.addChunk(new Chunk(new VirtualFileOffset(2L), new VirtualFileOffset(3L)));
		Bin bin12 = new Bin(1);
		bin12.addChunk(new Chunk(new VirtualFileOffset(4L), new VirtualFileOffset(5L)));
		bin12.addChunk(new Chunk(new VirtualFileOffset(6L), new VirtualFileOffset(7L)));
		bin12.addChunk(new Chunk(new VirtualFileOffset(8L), new VirtualFileOffset(9L)));
		referenceIndex1.addBin(bin11);
		referenceIndex1.addBin(bin12);
		referenceIndex1.addLinearIndex(new VirtualFileOffset(0L));
		referenceIndex1.addLinearIndex(new VirtualFileOffset(1L));
		
		ReferenceIndex referenceIndex2 = new ReferenceIndex();
		Bin bin21 = new Bin(2);
		bin11.addChunk(new Chunk(new VirtualFileOffset(10L), new VirtualFileOffset(11L)));
		bin11.addChunk(new Chunk(new VirtualFileOffset(12L), new VirtualFileOffset(13L)));
		Bin bin22 = new Bin(3);
		bin11.addChunk(new Chunk(new VirtualFileOffset(14L), new VirtualFileOffset(15L)));
		bin11.addChunk(new Chunk(new VirtualFileOffset(16L), new VirtualFileOffset(17L)));
		referenceIndex2.addBin(bin21);
		referenceIndex2.addBin(bin22);
		referenceIndex2.addLinearIndex(new VirtualFileOffset(0L));
		referenceIndex2.addLinearIndex(new VirtualFileOffset(1L));
		referenceIndex2.addLinearIndex(new VirtualFileOffset(2L));
	}
	
	@Before
	public void setUp() throws Exception {
		tempFile = File.createTempFile("bai-test-", "", new File(TEMP_DIR));
		tempFile.deleteOnExit();
		baiWriter = new BaiWriter(new FileOutputStream(tempFile));
	}
	
	@Test
	public void testWrite() throws IndexFileFormatException, IOException {
		baiWriter.write(bamIndex);
		
		BamIndex actualBamIndex = new BaiReader().load(tempFile);
		assertEquals(bamIndex, actualBamIndex);
	}
	
	@Test(expected = IndexFileFormatException.class)
	public void testMultipleWrite() throws IndexFileFormatException, IOException {
		baiWriter.write(bamIndex);
		baiWriter.write(bamIndex);
	}

}
