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

package hu.astrid.viewer.util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * This utility class writes objects to a CSV file. Filed names will be the header of the file.
 * Fields are determined by getting object getter and setter methods, if both found,
 * corresponding field is persisted. Separator is semicolon (';').
 * The separators in objects content aren't replaced yet, so this
 * case casue problems.
 * @param <V>
 * @author Szuni
 */
public class CSVWriter<V extends Serializable> {

	private static final long serialVersionUID = 1L;
	/** Writer for output */
	private final Writer writer;
	private final Logger logger = Logger.getLogger(CSVWriter.class);
	/** Getter methods of class to write */
	private final List<Method> getters = new ArrayList<Method>();

	/**
	 * Create a new CSVWriter for persisting specified objects. <br>
	 * By construction the column titles are determined and stored.<br>
	 * If any error occured by determining getter methods, writer is closed.
	 * @param writer
	 * @param classs class of objects to write, need to determine row titles and getters
	 * @throws IOException
	 */
	public CSVWriter(Writer writer, Class<V> classs) throws IOException {
		this.writer = new BufferedWriter(writer);
		for (Method method : classs.getMethods()) {
			try {
				if (method.getName().startsWith("get") && classs.getMethod("set" + method.getName().substring(3), method.getReturnType()) != null) {
					getters.add(method);
				}
			} catch (NoSuchMethodException e) {
				//Getter without setter
			} catch (Exception ex) {
				close();
				logger.error(ex.getMessage(), ex);
			}
		}
		Collections.sort(getters, new Comparator<Method>() {

			@Override
			public int compare(Method o1, Method o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		for (Method method : getters) {
			writer.append(method.getName().substring(3) + ";");
		}
		writer.append("\n");
	}

	/**
	 * Write collectiond of records. One record takes one line.<br>
	 * If any error occured by invoking getters, writer is closed.
	 * @param records
	 * @throws IOException
	 */
	public void write(Collection<V> records) throws IOException {
		for (V record : records) {
			write(record);
		}
	}

	/**
	 * Write a record to a line.<br>
	 * If any error occured by invoking getters, writer is closed.
	 * @param record
	 * @throws IOException
	 */
	public void write(V record) throws IOException {
		for (Method m : getters) {
			String s = null;
			try {
				s = m.invoke(record).toString();
			} catch (NullPointerException e) {
				s = "null";
			} catch (Exception ex) {
				close();
				logger.error(ex.getMessage(), ex);
			} //				s=s.replaceAll(";", "\u003B");
			writer.append(s).append(';');
		}
		writer.append('\n');
	}

	/**
	 * Close writer
	 * @throws IOException
	 */
	public void close() throws IOException {
		if (writer != null) {
			try {
				writer.flush();
				writer.close();
			} catch (IOException ex) {
				logger.error(ex.getMessage(), ex);
			}
		}
	}
}
