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

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.junit.Test;

public class SamToBamConverterTest {
	
	private static final String RESOURCES_DIR = System.getProperty("ngsc.testfiles.dir");

	private static final String SAM_PATH = "test.sam";

	@Test
	public void testConvert() throws Exception {
		BufferedReader inSam = new BufferedReader(new FileReader(new File(RESOURCES_DIR, SAM_PATH)));
		ByteArrayOutputStream outBam = new ByteArrayOutputStream();
		SamToBamConverter samToBamConverter = new SamToBamConverter(inSam, outBam);
		samToBamConverter.convert();

		assertEquals("e964a7295f87242db8cd98617aabe8", getMd5Sum(outBam.toByteArray()));

		inSam.close();
	}

	private String getMd5Sum(byte[] data) throws NoSuchAlgorithmException {
		MessageDigest algorithm = MessageDigest.getInstance("MD5");
		algorithm.reset();
		algorithm.update(data);
		byte messageDigest[] = algorithm.digest();

		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < messageDigest.length; i++) {
			hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
		}
		return hexString.toString();
	}

}