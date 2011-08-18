
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

import hu.astrid.mapping.model.AlignmentRecord;
import hu.astrid.mapping.model.OptionalTag;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

/**
 * Conversions for serialization
 * @author Szuni
 */
public class SerializationUtil {
	/**Size of the buffer used to serialize a record*/
	public static final int BUFFER_SIZE = 1536;

	/**
	 * Write an {@link AlignmentRecord} to a {@link ByteBuffer} for serialization
	 * @param r alignment record
	 * @return bytebuffer able to serialize
	 * @throws BufferOverflowException when the buffers size too small
	 */
	public static ByteBuffer toByteBuffer(AlignmentRecord r) {
		ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);

		buffer.put((byte) r.getQueryName().length());
		buffer.put(getBytes(r.getQueryName()));
		buffer.putShort(r.getFlag());
		buffer.put((byte) r.getReferenceName().length());
		buffer.put(getBytes(r.getReferenceName()));
		buffer.putInt(r.getPosition());
		buffer.put(r.getMappingQuality());
		buffer.put((byte) r.getCigar().length());
		buffer.put(getBytes(r.getCigar()));
		buffer.put((byte) r.getMateReferenceName().length());
		buffer.put(getBytes(r.getMateReferenceName()));
		buffer.putInt(r.getMatePosition());
		buffer.putInt(r.getInsertSize());
		buffer.put((byte) r.getSequence().length());
		buffer.put(getBytes(r.getSequence()));
		buffer.put((byte) r.getQuality().length());
		buffer.put(getBytes(r.getQuality()));

		buffer.putInt(r.listOptionalTags().size());
		for(OptionalTag tag : r.listOptionalTags()) {
			appendToByteBuffer(tag, buffer);
		}

		return buffer;
	}

	/**
	 * Append an {@link OptionalTag} to a {@link ByteBuffer} for serialization
	 * @param o optional tag
	 * @param buffer bytebuffer
	 * @throws BufferOverflowException when the buffers size too small
	 */
	private static void appendToByteBuffer(OptionalTag o, ByteBuffer buffer) {
		buffer.put((byte) o.getTagName().length());
		buffer.put(getBytes(o.getTagName()));
		buffer.put((byte)o.getValueType());
		buffer.put((byte) o.getValue().length());
		buffer.put(getBytes(o.getValue()));
	}

	/**
	 * Reads an {@link AlignmentRecord} from a {@link ByteBuffer}. After the load
	 * the buffers {@code position} points to the end of loaded alignment record.
	 * @param buffer bytebuffer loaded from serilized file
	 * @return loaded alignment record
	 * @throws BufferUnderflowException when whole record isnt loaded
	 */
	public static AlignmentRecord fromByteBuffer(ByteBuffer buffer) {
		AlignmentRecord record = new AlignmentRecord();
		byte array[];

		array = new byte[buffer.get()];
		buffer.get(array);
		record.setQueryName(makeString(array));
		record.setFlag(buffer.getShort());
		array = new byte[buffer.get()];
		buffer.get(array);
		record.setReferenceName(makeString(array));
		record.setPosition(buffer.getInt());
		record.setMappingQuality(buffer.get());
		array = new byte[buffer.get()];
		buffer.get(array);
		record.setCigar(makeString(array));
		array = new byte[buffer.get()];
		buffer.get(array);
		record.setMateReferenceName(makeString(array));
		record.setMatePosition(buffer.getInt());
		record.setInsertSize(buffer.getInt());
		array = new byte[buffer.get()];
		buffer.get(array);
		record.setSequence(makeString(array));
		array = new byte[buffer.get()];
		buffer.get(array);
		record.setQuality(makeString(array));


		int count = buffer.getInt();

		for(int i=0; i<count; ++i) {
			record.addOptionalTag(tagFromByteBuffer(buffer));
		}

		return record;
	}

	/**
	 * Reads an {@link OptionalTag} from a {@link ByteBuffer}. After the load
	 * the buffers {@code position} points to the end of loaded tag.
	 * @param buffer bytebuffer loaded from serilized file
	 * @return loaded optional tag
	 * @throws BufferUnderflowException when whole tag isnt loaded
	 */
	private static OptionalTag tagFromByteBuffer(ByteBuffer buffer) {
		OptionalTag tag = new OptionalTag();
		byte array[];

		array = new byte[buffer.get()];
		buffer.get(array);
		tag.setTagName(makeString(array));
		tag.setValueType((char)buffer.get());
		array = new byte[buffer.get()];
		buffer.get(array);
		tag.setValue(makeString(array));

		return tag;
	}

	/**
	 * Convert a {@link String} to array of {@code byte}s. Uses only size narrowing
	 * from character[] to byte[]
	 * @param string string to convert
	 * @return characters of string in array of bytes
	 */
	private static byte[] getBytes(String string) {
		byte array[] = new byte[string.length()];
		for(int i=0; i<string.length(); ++i) {
			array[i] = (byte) string.charAt(i);
		}
		return array;
	}

	/**
	 * Construct a {@link String} from array of bytes. Make char[] from byte[] and
	 * construct a String. Charset conversion doesnt occurs, not like constructing from
	 * byte[]
	 * @param bytes array of bytes
	 * @return constructed string
	 */
	private static String makeString(byte[] bytes) {
		char chars[] = new char[bytes.length];
		for(int i=0; i<bytes.length; ++i) {
			chars[i]=(char) bytes[i];
		}
		return new String(chars);
	}
}
