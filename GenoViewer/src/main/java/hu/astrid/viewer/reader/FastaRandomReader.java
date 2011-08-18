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

package hu.astrid.viewer.reader;

import hu.astrid.contig.AbstractContig;
import hu.astrid.contig.Contig;
import hu.astrid.contig.ContigFactory;
import hu.astrid.contig.ContigImplementationType;
import hu.astrid.core.Nucleotide;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * Egy megadott Fasta fájlból való olvasásra használható osztály.
 * @author Szuni
 */
public class FastaRandomReader {

	public static final int DEFAULT_BUFFER_SIZE = 2048;
	/**Kezelendő fájlhoz tartozó csatorna*/
	private final FileChannel fileChannel;
	/**Kezelt fájl neve*/
	private String filePath;
	/**Olvasáskor használt puffer mérete*/
	private int bufferSize;
	/**Olvasási puffer*/
	private ByteBuffer byteBuffer;
	/**Contigok id-jai*/
	private List<String> contigNames = new ArrayList<String>();
	/**Aktuálisan olvasott contig id-ja*/
	private StringBuilder contigId;
	/**Contigok génszekvenciáinak kezdő pozíciói a fájlban*/
	private List<Long> contigStartIndexes = new ArrayList<Long>();
	/**Contigok génszekvenciáinak utolsó pozíciói a fájlban*/
	private List<Long> contigEndIndexes = new ArrayList<Long>();
	/**Korábban találtunk contig startjelet ({@code >})*/
	private boolean contigHeadFound = false;
	/**Beolvasás meghívásánainak száma*/
	private long readCalled = 0L;
	/**Contigot tartalmazó sor hossza*/
	private int contigLineLength = 0;
	/**Contigot tartalmazó sor hosszának szomlására szükség van e*/
	private boolean countContigLineLengthNeeded = false;
	/**Fájl sorelválasztó karakterének hossza*/
	private int lineSeparatorLength = 0;
	/**Whether this reader should process unrecognized symbols in FASTA files and interpret as 'N'*/
	private final boolean isToleratingUnknownSymbols;

	/**
	 * @return is this instance tolerates unrecognized characters in FASTA files
	 */
	public boolean isIsToleratingUnknownSymbols() {
		return isToleratingUnknownSymbols;
	}

	/**
	 * Fájl által tartalmazott contigok ID-jai
	 * @return
	 */
	public List<String> getContigNames() {
		return contigNames;
	}

	private FastaRandomReader(RandomAccessFile file, boolean tolerateUnknownSymbols) throws IOException, InvalidFastaFileException {
		this(file, DEFAULT_BUFFER_SIZE, tolerateUnknownSymbols);
	}

	private FastaRandomReader(RandomAccessFile file, int bufferSize, boolean toleratingUnknownSymbols) throws IOException, InvalidFastaFileException {
		fileChannel = file.getChannel();
		this.bufferSize = bufferSize;
		byteBuffer = ByteBuffer.allocate(bufferSize);
		this.isToleratingUnknownSymbols = toleratingUnknownSymbols;
		scanFile();
	}

	public FastaRandomReader(String fileName) throws IOException, InvalidFastaFileException {
		this(fileName, DEFAULT_BUFFER_SIZE);
	}

	public FastaRandomReader(File file) throws IOException, InvalidFastaFileException {
		this(file, DEFAULT_BUFFER_SIZE);
	}

	public FastaRandomReader(String fileName, int bufferSize) throws IOException, InvalidFastaFileException {
		this(new RandomAccessFile(fileName, "r"), bufferSize, false);
		this.filePath = fileName;
	}

	public FastaRandomReader(File file, int bufferSize) throws IOException, InvalidFastaFileException {
		this(new RandomAccessFile(file, "r"), bufferSize, false);
		this.filePath = file.getAbsolutePath();
	}

	public FastaRandomReader(String fileName, boolean tolerateUnknownSymbols) throws IOException, InvalidFastaFileException {
		this(fileName, DEFAULT_BUFFER_SIZE, tolerateUnknownSymbols);

	}

	public FastaRandomReader(File file, boolean tolerateUnknownSymbols) throws IOException, InvalidFastaFileException {
		this(file, DEFAULT_BUFFER_SIZE, tolerateUnknownSymbols);
	}

	public FastaRandomReader(String fileName, int bufferSize, boolean tolerateUnknownSymbols) throws IOException, InvalidFastaFileException {
		this(new RandomAccessFile(fileName, "r"), bufferSize, tolerateUnknownSymbols);
		this.filePath = fileName;
	}

	public FastaRandomReader(File file, int bufferSize, boolean tolerateUnknownSymbols) throws IOException, InvalidFastaFileException {
		this(new RandomAccessFile(file, "r"), bufferSize, tolerateUnknownSymbols);
		this.filePath = file.getAbsolutePath();
	}

	/**
	 * Megnyitott fájl neve
	 * @return
	 */
	public String getFileName() {
		return filePath.substring(filePath.lastIndexOf(System.getProperty("file.separator")) + 1);
	}

	/**
	 * Adatok összegyűjtése a fájlról, contigok id-jai, kezdő és végpozíciói
	 * @throws IOException
	 * @throws InvalidFastaFileException
	 */
	private void scanFile() throws IOException, InvalidFastaFileException {
		int loadedBytes = 0;
		int whiteSpacesAtEndOfBuffer = 0;
		char foundedChar = 0;

		while (loadedBytes != -1) {
			if (fileChannel.isOpen()) {

				// Ha whitespacek vannak a fájl végén akkor megszámolja és
				// ennyivel korábban lesz a kontignak vége
				int whiteSpacePosition, whiteSpacesInCurrentBuffer = 0;
				for (whiteSpacePosition = loadedBytes - 1; whiteSpacePosition > -1
						&& Character.isWhitespace(byteBuffer.get(whiteSpacePosition)); --whiteSpacePosition) {
					whiteSpacesInCurrentBuffer++;
				}
				if (whiteSpacePosition != -1) {
					whiteSpacesAtEndOfBuffer = whiteSpacesInCurrentBuffer;
				} else {
					whiteSpacesAtEndOfBuffer += whiteSpacesInCurrentBuffer;
				}

				loadedBytes = fileChannel.read((ByteBuffer) byteBuffer.clear());

				if (loadedBytes == -1) {
					contigEndIndexes.add(fileChannel.position() - whiteSpacesAtEndOfBuffer);
					break;
				}

				/*
				 * Folyamatos olvasás esetében ez a rész tárolja contigok
				 * génszekvenciáinak kezdő helyeit Minden egyes beolvasott
				 * puffert karakterenként végignéz
				 */
				for (int i = 0; i < loadedBytes; ++i) {
					if (byteBuffer.get(i) == (byte) '>') {
						// Ha nem az első contignál járunk, akkor egy előzőnek
						// vége van.
						if (!contigStartIndexes.isEmpty()) {
							contigEndIndexes.add(readCalled * bufferSize + i - lineSeparatorLength);
						}
						// Kezdődik egy új contig
						contigHeadFound = true;
						contigId = new StringBuilder();
					} else {
						// contigot tartalmazó sor hosszának mérése
						if (countContigLineLengthNeeded) {
							if (byteBuffer.get(i) == (byte) '\r') {
								lineSeparatorLength++;
							} else if (byteBuffer.get(i) == (byte) '\n') {
								lineSeparatorLength++;
								countContigLineLengthNeeded = false;
							} else {
								contigLineLength++;
							}
						}

						foundedChar = (char) byteBuffer.get(i);

						if (contigHeadFound) {
							contigId.append(foundedChar);
						} else {
							foundedChar = (char) byteBuffer.get(i);
							switch (Character.toUpperCase(foundedChar)) {
								case 'A':
								case 'C':
								case 'G':
								case 'T':
								case 'N':
									break;
								default:
									if (!Character.isWhitespace(foundedChar)) {
										if (!isToleratingUnknownSymbols) {
											throw new InvalidFastaFileException(filePath+" invalid character: "+ byteBuffer.get(i));
										}
										
										Logger.getLogger(FastaRandomReader.class).warn("Cannot interpert symbol [" + (char) byteBuffer.get(i) + "], marked as not known and "
												+ "replaced with \'N\'");
										foundedChar = 'N';
									}
							}
						}
						// Ha megvan a contig id, letárolom és sorhossz
						// mérésének indítása
						if (foundedChar == '\n' && contigHeadFound) {
							contigHeadFound = false;
							contigStartIndexes.add(readCalled * bufferSize + i + 1);
							contigNames.add(contigId.toString().trim());
							contigId = null;
							if (contigLineLength == 0) {
								countContigLineLengthNeeded = true;
							}
						}
					}

				}
				readCalled++;
			} else {
				throw new IOException("Channel closed");
			}
		}
		byteBuffer.clear();
		if (contigStartIndexes.isEmpty()) {
			throw new InvalidFastaFileException(filePath);
		}
	}

	/**
	 * Megadja az adott kontig hosszát bájtokban
	 * @param contigIndex contig sorszáma a fájlban
	 * @return contig által tartalmazott nukleotidsorozat bájthossza
	 */
	public int getContigLength(int contigIndex) {
		int length = new Long(contigEndIndexes.get(contigIndex) - contigStartIndexes.get(contigIndex)).intValue();
		return length - length / (contigLineLength + lineSeparatorLength) * lineSeparatorLength;
	}

	/**
	 * Fasta fájl adott contigjából olvas be a pufferbe az adott pozícióval kezdődően maximum {@code BUFFER_SIZE} mennyiségű bájtot.
	 * A metódus használata előtt szükséges tudni a contigok kezdő indexeit a fájlban.
	 * @param position kezdő pozíció
	 * @param contigIndex contig indexe
	 * @return beolvasott bájtok száma<br>-1 ha vége a csatornának
	 * @throws IOException
	 * @throws MissingContigStartIndicesException
	 * @throws ContigIndexOutOfBoundsException
	 * @throws ContigPositionOutOfBoundsException
	 * @throws hu.astrid.viewer.reader.FastaRandomReader.EmptyBufferException 
	 */
	public synchronized Contig<Nucleotide> load(int position, int contigIndex) throws IOException, MissingContigStartIndicesException,
			ContigIndexOutOfBoundsException, ContigPositionOutOfBoundsException, EmptyBufferException {
		if (fileChannel.isOpen()) {
			if (contigStartIndexes.isEmpty()) {
				throw new MissingContigStartIndicesException();
			}
			if (contigStartIndexes.size() <= contigIndex) {
				throw new ContigIndexOutOfBoundsException(contigIndex);
			}
			// Nukleotid pozíció átszámítása fájlbeli pozícióra
			long filePosition = contigStartIndexes.get(contigIndex) + position + (position / contigLineLength)
					* lineSeparatorLength;
			if (filePosition >= contigEndIndexes.get(contigIndex)) {
				throw new ContigPositionOutOfBoundsException(contigIndex, position);
			}
			int loadedBytes = fileChannel.read((ByteBuffer) byteBuffer.clear(), filePosition);

			return getLoadedContig();
		}
		throw new IOException("Channel closed");
	}

	/**
	 * Korábban beolvasott bájtok lekérdezése a pufferből. Csak a nukleotidsorozatot adja vissza.
	 * @return beolvasott bájtok karakterekként
	 * @throws EmptyBufferException
	 * @deprecated use {@code getLoadedContig()} instead
	 */
	@Deprecated
	private CharSequence getLoadedContent() throws EmptyBufferException {
		byteBuffer.flip();
		if (byteBuffer.limit() == 0) {
			throw new EmptyBufferException();
		}

		StringBuilder sb = new StringBuilder(byteBuffer.limit());
		while (byteBuffer.position() < byteBuffer.limit()) {
			char c = (char) byteBuffer.get();
			if (!Character.isWhitespace(c)) {
				if (c == '>') {
					break;
				}
				sb.append(c);
			}
		}

		return sb.subSequence(0, sb.length());
	}

	/**
	 * Korábban beolvasott contig lekérdezése. Puffer tartalmából hozza létre a contigot.
	 * @return beolvasott contig
	 * @throws EmptyBufferException Ha üres a puffer
	 */
	private Contig<Nucleotide> getLoadedContig() throws EmptyBufferException {
		AbstractContig<Nucleotide> contig = ContigFactory.createContig(ContigImplementationType.SIMPLE, null);
		byteBuffer.flip();
		if (byteBuffer.limit() == 0) {
			throw new EmptyBufferException();
		}

		// TODO contigId és egyebek
		while (byteBuffer.position() < byteBuffer.limit()) {
			char c = (char) byteBuffer.get();
			if (!Character.isWhitespace(c)) {
				if (c == '>') {
					break;
				}
				contig.put(Nucleotide.valueOf(c));
			}
		}

		return contig;
	}

	/**
	 * Fájlban tárolt contigok száma
	 * @return
	 */
	public int getNumberOfContigs() {
		return contigStartIndexes.size();
	}

	/**
	 * Megnyitott Fasta fájl lezárása
	 * @throws IOException
	 */
	public void closeFile() throws IOException {
		if (fileChannel != null) {
			fileChannel.close();
		}
		contigNames.clear();
		contigStartIndexes.clear();
		contigEndIndexes.clear();
		byteBuffer.clear();
	}

	public static class FastaRandomReaderException extends Exception {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public FastaRandomReaderException(String message) {
			super(message);
		}

		public FastaRandomReaderException() {
			//Empty
		}
	}

	/**
	 * Ez a kivétel akkor keletkezik, ha contigok kezdeteinek ismerete nélkül hívjuk meg az abszolút pozíció szerinti olvasást egy Fasta fájlba.
	 */
	public static class MissingContigStartIndicesException extends FastaRandomReaderException {

		/**
		 *
		 */
		private static final long serialVersionUID = 3260298439283959449L;

		public MissingContigStartIndicesException() {
			super("Contig start indices are unknown");
		}
	}

	/**
	 * Ez a kivétel akkor keletkezik, ha nagyobb sorszámú contigból akarunk olvasni, mint amennyit a fálj tartalmaz
	 */
	public static class ContigIndexOutOfBoundsException extends FastaRandomReaderException {

		/**
		 *
		 */
		private static final long serialVersionUID = 1922379024412259583L;
		/**Contig sorszáma, amelyiket kerestük de nincs benne a fájlban*/
		private int contigIndex;

		/**
		 *
		 * @param contigNumber contig sorszáma, amit nem találtunk
		 */
		public ContigIndexOutOfBoundsException(int contigNumber) {
			super("File doesn't contain contig number " + contigNumber);
			this.contigIndex = contigNumber;
		}

		/**
		 * Fájlban keresett contig sorszáma
		 * @return
		 */
		public int getContigIndex() {
			return contigIndex;
		}
	}

	/**
	 * Ez a kivétel akkor keletkezik, ha a contig hosszánál nagyobb pozícióról akarunk olvasni
	 */
	public static class ContigPositionOutOfBoundsException extends FastaRandomReaderException {

		/**
		 *
		 */
		private static final long serialVersionUID = -7692909215269280662L;
		/**Contig sorszáma, amelyből olvasni akartunk*/
		private int contigIndex;
		/**Nukleotid pozíció, amelyet keresünk, de nincs az adott contigban*/
		private int position;

		/**
		 *
		 * @param contigNumber contig amelyben kerestünk
		 * @param newPosition pozíció amit nem találtunk
		 */
		public ContigPositionOutOfBoundsException(int contigNumber, int newPosition) {
			super("Contig " + contigNumber + " doesn't contain position " + newPosition);
			this.contigIndex = contigNumber;
			this.position = newPosition;
		}

		/**
		 * Contig amelyben kerstünk
		 * @return
		 */
		public int getContigIndex() {
			return contigIndex;
		}

		/**
		 * Contigban keresett pozíció
		 * @return
		 */
		public int getPosition() {
			return position;
		}
	}

	/**
	 * Ez a kivétel akkor keletkezik, ha előzetes beolvasás nélkül akarjuk lekérdezni a puffer tartalmát. Ekkor a puffer üres.
	 */
	public static class EmptyBufferException extends FastaRandomReaderException {

		/**
		 *
		 */
		private static final long serialVersionUID = 3260242478963019449L;

		public EmptyBufferException() {
			super("Buffer is empty");
		}
	}

	/**
	 * Thrown, when parse the file as fasta file is impossipble
	 */
	public static class InvalidFastaFileException extends FastaRandomReaderException {

		/**
		 *
		 */
		private static final long serialVersionUID = 3260298439283959449L;

		public InvalidFastaFileException(String fileName) {
			super("File " + fileName + " is not a valid fasta file");
		}
	}
}
