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

import hu.astrid.mapping.exception.MappingFileFormatException;
import hu.astrid.mapping.model.AlignmentPosition;
import hu.astrid.mapping.model.AlignmentRecord;
import hu.astrid.mapping.model.OptionalTag;
import hu.astrid.mapping.util.BamUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

/**
 * This class is contains coding and decoding methods.
 */
public class AlignmentRecordCodec {

    /**
     * Last byte of text data.
     */
    private static final byte CHAR_TERMINATE = 0;

    /**
     * Extended cigar format of operations.
     */
    private static final String CIGAR_REGEX = "MIDNSHP";

    /**
     * Default ASCII charset to decode 1-byte characters.
     */
    private static final Charset ASCII_CHARSET = Charset.forName("US-ASCII");

    /**
     * Map which contains codes for nucleotides.
     */
    private static Map<Character, Integer> nucleotidesToCodes = new HashMap<Character, Integer>();
    
    /**
     * Map which defines how to interpret nucleotide codes.
     */
    private static final Map<Integer, Character> codesToNucleotides = new HashMap<Integer, Character>();
    
    /**
     * Map which defines how to interpret numbers as cigar operators.
     */
    private static final Map<Integer, Character> codesToCigarOperators = new HashMap<Integer, Character>();
    
    static {
    	nucleotidesToCodes.put('=', 0);
    	nucleotidesToCodes.put('A', 1);
    	nucleotidesToCodes.put('C', 2);
    	nucleotidesToCodes.put('G', 4);
    	nucleotidesToCodes.put('T', 8);
    	nucleotidesToCodes.put('N', 15);
    	
    	codesToNucleotides.put(0, '=');
    	codesToNucleotides.put(1, 'A');
    	codesToNucleotides.put(2, 'C');
    	codesToNucleotides.put(4, 'G');
    	codesToNucleotides.put(8, 'T');
    	codesToNucleotides.put(15, 'N');
    	
    	codesToCigarOperators.put(0, 'M');
    	codesToCigarOperators.put(1, 'I');
    	codesToCigarOperators.put(2, 'D');
    	codesToCigarOperators.put(3, 'N');
    	codesToCigarOperators.put(4, 'S');
    	codesToCigarOperators.put(5, 'H');
    	codesToCigarOperators.put(6, 'P');
    }

    /**
     * List of reference sequence name.
     */
    private List<String> referenceSequences;

    /**
     * Creates a new codec instance with the given reference names.
     *
     * @param referenceSequences list of reference sequence name
     */
    public AlignmentRecordCodec(List<String> referenceSequences) {

        this.referenceSequences = referenceSequences;

    }

    /*code ->*/
    /**
     * Extended cigar string packer.
     *
     * @param cigarString extended cigar string
     * 
     * @throws AlignmentRecordFormatException
     *              if missing cigar string field
     * @throws IOException
     *              if an I/O error occurs
     */
    private byte[] packCigarString(String cigarString)
            throws MappingFileFormatException, IOException {

        ByteArrayOutputStream result = new ByteArrayOutputStream();

        StringTokenizer cigarToken = new StringTokenizer(cigarString, CIGAR_REGEX, true);

        try {

            while (cigarToken.hasMoreTokens()) {

                String op_len = cigarToken.nextToken();
                int cigar = Integer.valueOf(op_len) << 4;

                String opString = cigarToken.nextToken();
                int op = CIGAR_REGEX.indexOf(opString);
                cigar |= op;

                result.write(BamUtil.toByteArray(cigar));
            }

        } catch (NoSuchElementException nsee) {
            throw new MappingFileFormatException("Missing cigar string field!");
        }

        return result.toByteArray();

    }

    /**
     * This method is converts an record to byte array.
     *
     * @param val_type type of optional tag
     * @param value value of optional tag
     *
     * @throws AlignmentRecordFormatException
     *              if type of optional tag is invalid
     */
    private byte[] optionalTagCoder(char val_type, String value) throws MappingFileFormatException {
        byte[] result = null;

        if ((val_type == 'Z') || (val_type == 'H')) {
            byte[] tmp = BamUtil.toByteArray(value.toCharArray());
            result = new byte[tmp.length + 1];
            System.arraycopy(tmp, 0, result, 0, tmp.length);
            result[tmp.length] = CHAR_TERMINATE;

        } else if (val_type == 'A') {
            result = new byte[]{(byte) value.charAt(0)};

        } else if (val_type == 'i') {
            Integer v = new Integer(value);
            result = BamUtil.toByteArray(v);

        } else if (val_type == 'f') {
            float v = new Float(value);
            int floatValueToInt = Float.floatToRawIntBits(v);
            result = BamUtil.toByteArray(floatValueToInt);

        } else {
            throw new MappingFileFormatException("Invalid optional tag type!");
        }

        return result;

    }

    /**
     * This method is generates an nucleotide to number.
     *
     * @throws AlignmentRecordFormatException
     *              if nuc is an invalid BAM nucleotide
     */
    private int nucleotideToNumber(char nuc) throws MappingFileFormatException {
        Integer n = nucleotidesToCodes.get(nuc);
    	if (n != null) {
    		return n.intValue();
    	}
    	throw new MappingFileFormatException("Invalid nucleotide!");
    }

    /**
     * This method pack two characters into one character.
     *
     * @throws AlignmentRecordFormatException
     *              if nuc1 or nuc2 is an invalid BAM nucleotide
     */
    private char packToChar(char nuc1, char nuc2)
            throws MappingFileFormatException {

        return (char) ((nucleotideToNumber(nuc1) << 4) | (nucleotideToNumber(nuc2)));

    }

    /**
     * BAM nucleotide sequence packer.
     *
     * @param seqString BAM nucleotide sequence
     *
     * @throws AlignmentRecordFormatException
     *              if seqString contains invalid BAM nucleotides
     */
    private byte[] packSequence(String seqString)
            throws MappingFileFormatException {

        int seqL = seqString.length();

        boolean seqLOdd = ((seqL & 1) == 1);
        byte[] seq = new byte[(seqLOdd) ? ((seqL >>> 1) + 1) : (seqL >>> 1)];
        int indexLimit = (seqLOdd) ? (seqL - 1) : seqL;

        int seqIndex = 0;

        int i = 0;

        for (; i < indexLimit; i++) {

            char nuc1 = seqString.charAt(i++);
            char nuc2 = seqString.charAt(i);

            char pack = packToChar(nuc1, nuc2);

            seq[seqIndex++] = (byte) pack;

        }

        if (seqLOdd) {
            char nuc1 = seqString.charAt(i);
            char nuc2 = '=';

            char pack = packToChar(nuc1, nuc2);

            seq[seqIndex] = (byte) pack;
        }

        return seq;

    }

    /**
     * This method code alignment record to byte array.
     *
     * @param alignmentRecord an alignment record object
     *
     * @throws AlignmentRecordFormatException
     *              if the reference name is invalid
     * @throws IOException
     *              if an I/O error occurs
     */
    public byte[] code( AlignmentRecord alignmentRecord )
            throws MappingFileFormatException, IOException {

        String refName = alignmentRecord.getReferenceName();

        int rID = referenceSequences.indexOf(refName);

        if ((rID == -1) && !refName.equals("*")) {
            throw new MappingFileFormatException("Invalid reference name![" + refName + "]");
        }

        ByteArrayOutputStream byteBuff = new ByteArrayOutputStream();

        /*rID ->*/
        byteBuff.write(BamUtil.toByteArray(rID));
        /*<- rID*/

        /*pos ->*/
        int pos = (alignmentRecord.getPosition() - 1);
        byteBuff.write(BamUtil.toByteArray(pos));
        /*<- pos*/

        /*bin_mq_nl ->*/
        int beg = alignmentRecord.getPosition();
        int end = beg + alignmentRecord.getSequence().length();
        int bin_mq_nl = BamUtil.regToBin(beg, end) << 16;

        int mapQual = alignmentRecord.getMappingQuality() << 8;
        bin_mq_nl |= mapQual;

        int read_name_len = alignmentRecord.getQueryName().length() + 1;
        bin_mq_nl |= read_name_len;

        byteBuff.write(BamUtil.toByteArray(bin_mq_nl));
        /*<- bin_mq_nl*/

        /*flag_nc ->*/
        int flag_nc = (alignmentRecord.getFlag() & 0xFFFF) << 16;
        int cigar_len = alignmentRecord.getCigar().split("[" + CIGAR_REGEX + "]").length;
        flag_nc |= cigar_len;

        byteBuff.write(BamUtil.toByteArray(flag_nc));
        /*<- flag_nc*/

        /*read_len ->*/
        int read_len = alignmentRecord.getSequence().length();
        byteBuff.write(BamUtil.toByteArray(read_len));
        /*read_len ->*/

        /*mate_rID ->*/
        int mate_rID = referenceSequences.indexOf(alignmentRecord.getMateReferenceName());
        byteBuff.write(BamUtil.toByteArray(mate_rID));
        /*<- mate_rID*/

        /*mate_pos ->*/
        int mate_pos = (alignmentRecord.getMatePosition() - 1);
        byteBuff.write(BamUtil.toByteArray(mate_pos));
        /*<- mate_pos*/

        /*ins_size ->*/
        int ins_size = alignmentRecord.getInsertSize();
        byteBuff.write(BamUtil.toByteArray(ins_size));
        /*<- ins_size*/

        /*read_name ->*/
        String read_name = alignmentRecord.getQueryName();

        byteBuff.write(BamUtil.toByteArray(read_name.toCharArray()));
        byteBuff.write(CHAR_TERMINATE);
        /*<- read_name*/

        /*cigar ->*/
        String cigarString = alignmentRecord.getCigar();
        byte[] cigars = packCigarString(cigarString);
        byteBuff.write( cigars );
        /*<- cigar*/

        /*seq ->*/
        String seqString = alignmentRecord.getSequence();
        byte[] seq = packSequence(seqString);
        byteBuff.write(seq);
        /*<- seq*/

        /*qual ->*/
        char[] qual = alignmentRecord.getQuality().toCharArray();
        BamUtil.offset(qual, -33);
        byteBuff.write(BamUtil.toByteArray(new String(qual).toCharArray()));
        /*<- qual*/

        /*option_tags ->*/
        List<OptionalTag> optionalTags = alignmentRecord.listOptionalTags();

        for (OptionalTag optionalTag : optionalTags) {

            String tag = optionalTag.getTagName();
            byteBuff.write(BamUtil.toByteArray(tag.toCharArray()));

            char val_type = optionalTag.getValueType();
            byteBuff.write(val_type);

            String value = optionalTag.getValue();
            byte[] tagBytes = optionalTagCoder(val_type, value);
            byteBuff.write(tagBytes);
        }
        /*<- option_tags*/

        return byteBuff.toByteArray();

    }

    /*<- code*/

    /*decode ->*/
    private char numberToCigarOperator(int number)
            throws MappingFileFormatException {
    	Character c = codesToCigarOperators.get(number);
    	if (c != null) {
    		return c.charValue();
    	}
    	throw new MappingFileFormatException("Invalid BAM operator!");
    }

    private String bamCigarByteToString(byte [] bamCigarByteArray) 
            throws MappingFileFormatException {

            int operatorLength = BamUtil.toInt(bamCigarByteArray) >>4;
            int operator = BamUtil.toInt(bamCigarByteArray) & 0x0000000F;
            return "" + operatorLength + numberToCigarOperator(operator);
    }

    private String parseCigarData(byte[] cigarArray)
            throws MappingFileFormatException {
        
            StringBuilder result = new StringBuilder();

            for (int i = 0; i + 3 < cigarArray.length; i+=4) {
                    result.append(bamCigarByteToString(Arrays.copyOfRange(cigarArray, i, i + 4)));
            }

            return result.toString();
    }

    private char numberToNucleotide(int number) 
            throws MappingFileFormatException {

    	Character c = codesToNucleotides.get(number);
    	if (c != null) {
    		return c.charValue();
    	}
    	throw new MappingFileFormatException("Invalid BAM nucleotide!");
    }

    private String bamByteToNucleotidePairString(byte bamNucleotidePair) throws MappingFileFormatException {
            int high = (bamNucleotidePair & 0xF0) >>> 4;

            int low = bamNucleotidePair & 0x0F;

            String result =
                    String.valueOf(numberToNucleotide(high)) +
                    String.valueOf(numberToNucleotide(low));
            return result;
    }

    private String parseByteAlignmentSequence(byte [] byteArray, int readLength) 
            throws MappingFileFormatException {

        StringBuilder result = new StringBuilder(readLength + 1);

        for (byte byteValue : byteArray) {
            result.append(bamByteToNucleotidePairString(byteValue));
        }

        result.setLength(readLength);
        return result.toString();
    }

    private String parseSequenceQuality(byte [] sequenceQualityByteArray) {
            String qualityString = null;
            boolean noQuality = false;

            for (int i = 0; i < sequenceQualityByteArray.length; ++i) {
                    if (sequenceQualityByteArray[i] == -1) {
                            noQuality = true;
                            break;
                    }
                    sequenceQualityByteArray[i] += 33;
            }

            if (!noQuality) {
                    qualityString = new String(sequenceQualityByteArray, MappingFileParser.ASCII_CHARSET);
            } else {
                    qualityString = "*";
            }

            return qualityString;
    }

    private List<OptionalTag> parseOptionalTags(byte [] byteArrayOfTag) {
    	List<OptionalTag> result = new LinkedList<OptionalTag>();
    	byte byteArrayPosition = 0;

    	while (byteArrayPosition < byteArrayOfTag.length) {
	    	String tagName = new String(Arrays.copyOfRange(byteArrayOfTag, byteArrayPosition, byteArrayPosition + 2), ASCII_CHARSET);
	    	char tagType = (char) byteArrayOfTag[byteArrayPosition + 2];
	    	byteArrayPosition += 3;

	    	String stringValue = null;
	    	int intValue = 0;
	    	float floatValue = Float.NaN;
	    	if (tagType == 'A' || tagType == 'Z') {
	    		if (tagType == 'A') {
	    			stringValue = new String(Arrays.copyOfRange(byteArrayOfTag, byteArrayPosition, byteArrayPosition + 1), ASCII_CHARSET);
	    			byteArrayPosition += 1;
	    		} else {
	    			StringBuilder stringValueBuilder = new StringBuilder();
	    			int l = 0;
	    			while (byteArrayOfTag[byteArrayPosition + l] != 0) {
	    				l++;
	    			}
    				stringValueBuilder.append(new String(Arrays.copyOfRange(byteArrayOfTag, byteArrayPosition, byteArrayPosition + l), ASCII_CHARSET));
    				byteArrayPosition += l + 1;

	    			stringValue = stringValueBuilder.toString();
	    		}
	    	} else if (tagType == 'c' || tagType == 'C') {
    			intValue = byteArrayOfTag[byteArrayPosition];
    			byteArrayPosition += 1;
	    	} else if (tagType == 's' || tagType == 'S') {
    			intValue = BamUtil.toShort(Arrays.copyOfRange(byteArrayOfTag, byteArrayPosition, byteArrayPosition + 2));//(byteArrayOfTag[byteArrayPosition + 1] << 8) | byteArrayOfTag[byteArrayPosition];
    			byteArrayPosition += 2;
	    	} else if (tagType == 'i' || tagType == 'I') {
    			intValue = BamUtil.toInt(Arrays.copyOfRange(byteArrayOfTag, byteArrayPosition, byteArrayPosition + 4));
    			byteArrayPosition += 4;
	    	} else if (tagType == 'f') {
	    		floatValue = Float.intBitsToFloat(BamUtil.toInt(Arrays.copyOfRange(byteArrayOfTag, byteArrayPosition, byteArrayPosition + 4)));
	    		byteArrayPosition += 4;
	    	}

    		OptionalTag newOptionalTag = new OptionalTag();
    		newOptionalTag.setTagName(tagName);
	    	if (stringValue != null) {
	    		newOptionalTag.setValueType(tagType);
	    		newOptionalTag.setValue(stringValue);
	    	} else if (Float.compare(floatValue, Float.NaN) != 0) {
	    		newOptionalTag.setValueType('f');
	    		newOptionalTag.setValue(String.valueOf(floatValue));
	    	} else {
	    		newOptionalTag.setValueType('i');
	    		newOptionalTag.setValue(String.valueOf(intValue));
	    	}
    		result.add(newOptionalTag);
    	}

    	return result;
    }
    
    public int getPosition(byte[] record) {
    	return BamUtil.toInt(Arrays.copyOfRange(record, 4, 8)) + 1;
    }

    /**
     * This method decode byte array to alignment record.
     *
     * @param record array of bytes
     *
     * @throws AlignmentRecordFormatException
     *              if the reference name is invalid
     */
    public AlignmentRecord decode( byte[] record )
            throws MappingFileFormatException {
    	
        AlignmentRecord result = new AlignmentRecord();

    	/* Setting reference sequence. [0,1,2,3] */
    	int referenceReadID = BamUtil.toInt(Arrays.copyOfRange(record, 0, 4));
    	result.setReferenceName(referenceSequences.get(referenceReadID));

    	/* Setting query position. [4,5,6,7] */
    	result.setPosition(BamUtil.toInt(Arrays.copyOfRange(record, 4, 8)) + 1);

    	/* Skipping 2 byte (bin value). [11,10]*/
    	//int bin = (record[11] <<8) | record[10];

    	/* Setting mapping quality. record[9] */
    	byte mappingQuality = (byte)((record[9]) & 0x000000FF);
    	if (mappingQuality == 0) {
    		mappingQuality = (byte) 255;
    	}
    	result.setMappingQuality(mappingQuality);

    	/* Getting read name length. [8]*/
    	int readNameLength = record[8] & 0x000000FF;

    	/* Setting flag. [15,14] */
    	int flag = BamUtil.toShort(Arrays.copyOfRange(record, 14, 16));
    		//(record[15] <<8) | ((record[14] & 0x000000FF));
    	result.setFlag((short) flag);

    	/* Getting CIGAR length. [13,12] */
    	int cigarLength = (record[13] <<8) | (record[12] & 0x000000FF);

    	/* Getting read length. [16-19] */
    	int readLength = BamUtil.toInt(Arrays.copyOfRange(record, 16, 20));

    	/* Setting mate reference sequence ID. [20-23] */
    	int mateReadID = BamUtil.toInt(Arrays.copyOfRange(record, 20, 24));
        if( mateReadID == -1 )
            result.setMateReferenceName("*");  // '=' replaced to '*'
        else if (mateReadID == referenceReadID) {
    		result.setMateReferenceName("=");
    	} else {
    		result.setMateReferenceName(referenceSequences.get(mateReadID));
    	}

    	/* Setting mate position. [24-27] */
    	result.setMatePosition(BamUtil.toInt(Arrays.copyOfRange(record, 24, 28)) + 1);

    	/* Setting insert size of the mate paired. [28-31] */
    	int insertOfSize = BamUtil.toInt(Arrays.copyOfRange(record, 28, 32));
    	result.setInsertSize(insertOfSize);

    	/* Setting read name. [32-readNameLength]*/
    	byte [] readNameArray = Arrays.copyOfRange(record, 32, 32 + readNameLength - 1);
    	result.setQueryName(new String(readNameArray, ASCII_CHARSET));

    	int recordPosition = 32 + readNameLength;

    	/* Setting CIGAR string. [32 + readNameLength, 4*cigarLength*/
    	byte [] cigarArray = Arrays.copyOfRange(record, recordPosition, recordPosition + (cigarLength * 4));
    	result.setCigar(parseCigarData(cigarArray));

    	recordPosition += (cigarLength * 4);

    	/* Setting query sequence. */
    	byte [] readSequenceArray = Arrays.copyOfRange(record, recordPosition, recordPosition + ((readLength + 1)/2));
    	result.setSequence(parseByteAlignmentSequence(readSequenceArray, readLength));

    	recordPosition += ((readLength + 1) / 2);

    	/* Setting quality string of the query sequence. */
    	result.setQuality(parseSequenceQuality(Arrays.copyOfRange(record, recordPosition, recordPosition + readLength)));

    	recordPosition += readLength;

    	for (OptionalTag optionalTag : parseOptionalTags(Arrays.copyOfRange(record, recordPosition, record.length))) {
    		result.addOptionalTag(optionalTag);
    	}

        return result;

    }
    /*<- decode*/

    /*simpleDecode ->*/
	/**
	 * Decodes only the alignment position of a binary alignment record.
	 * 
	 * @param record
	 *            the binary representation of the alignment record
	 * @return the position of the alignment record
	 * @throws MappingFileFormatException
	 *             if the format of the binary record is incorrect
	 */
    public AlignmentPosition decodePosition( byte[] record )
            throws MappingFileFormatException {

        AlignmentPosition result = new AlignmentPosition();

    	/* Setting reference sequence. [0,1,2,3] */
    	int referenceReadID = BamUtil.toInt(Arrays.copyOfRange(record, 0, 4));
    	result.setReferenceName(referenceSequences.get(referenceReadID));

    	/* Setting query position. [4,5,6,7] */
    	result.setPosition(BamUtil.toInt(Arrays.copyOfRange(record, 4, 8)) + 1);

    	/* Getting read name length. [8]*/
    	int readNameLength = record[8] & 0x000000FF;

    	/* Setting read name. [32-readNameLength]*/
    	byte [] readNameArray = Arrays.copyOfRange(record, 32, 32 + readNameLength - 1);
    	result.setQueryName(new String(readNameArray, ASCII_CHARSET));
    	
    	/* Getting read length. [16-19] */
    	result.setReadLength(BamUtil.toInt(Arrays.copyOfRange(record, 16, 20)));

        return result;

    }
    /*<- simpleDecode*/
    
	/**
	 * Decodes a list of binary alignment records.
	 * 
	 * @param binaryRecords
	 *            the binary record list
	 * @return the list of decoded alignment records
	 * @throws MappingFileFormatException
	 *             if the format of the binary record is incorrect
	 */
    public List<AlignmentRecord> decode(List<byte[]> binaryRecords) throws MappingFileFormatException {
    	List<AlignmentRecord> records = new ArrayList<AlignmentRecord>();
    	for (byte[] binaryRecord : binaryRecords) {
    		records.add(this.decode(binaryRecord));
    	}
    	return records;
    }
    
	/**
	 * Decodes only the alignment positions from a list of binary alignment
	 * records.
	 * 
	 * @param binaryRecords
	 *            the binary record list
	 * @return the list of decoded alignment positions
	 * @throws MappingFileFormatException
	 *             if the format of the binary record is incorrect
	 */
    public List<AlignmentPosition> decodePositions(List<byte[]> binaryRecords) throws MappingFileFormatException {
    	List<AlignmentPosition> records = new ArrayList<AlignmentPosition>();
    	for (byte[] binaryRecord : binaryRecords) {
    		records.add(this.decodePosition(binaryRecord));
    	}
    	return records;
    }
    
	/**
	 * Decodes only the query name of an alignment record.
	 * 
	 * @param binaryRecord
	 *            the binary record
	 * @return the decoded query name
	 */
    public String decodeQueryName(byte[] binaryRecord) {
    	int readNameLength = binaryRecord[8] & 0x000000FF;
    	byte [] readNameArray = Arrays.copyOfRange(binaryRecord, 32, 32 + readNameLength - 1);
    	
    	return new String(readNameArray, ASCII_CHARSET);
    }

}
