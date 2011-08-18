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

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 * This utility class reads objects from a CSV file that contains a header with field names.
 * This reader uses objects setters with field names in header. Object has to have exactly
 * the same setters that indicated in file header.
 * @param <V>
 * @author Szuni
 */
public class CSVReader<V> {

	private final LineNumberReader reader;
	private final List<Method> setters = new ArrayList<Method>();
	private final static Logger logger = Logger.getLogger(CSVReader.class);
	private final Class<V> classs;
	/**
	 * A mapping of known primitive wrappers.
	 */
	private static final Map<Class<?>, Constructor<?>> constructors;

	static {
		/*
		 * Initialize the mapping.
		 */
		constructors = new HashMap<Class<?>, Constructor<?>>();
		try {

			constructors.put(boolean.class, Boolean.class.getConstructor(String.class));
			constructors.put(byte.class, Byte.class.getConstructor(String.class));
			constructors.put(short.class, Short.class.getConstructor(String.class));
			constructors.put(int.class, Integer.class.getConstructor(String.class));
			constructors.put(long.class, Long.class.getConstructor(String.class));
			constructors.put(float.class, Float.class.getConstructor(String.class));
			constructors.put(double.class, Double.class.getConstructor(String.class));
			constructors.put(Boolean.class, Boolean.class.getConstructor(String.class));
			constructors.put(Byte.class, Byte.class.getConstructor(String.class));
			constructors.put(Short.class, Short.class.getConstructor(String.class));
			constructors.put(Integer.class, Integer.class.getConstructor(String.class));
			constructors.put(Long.class, Long.class.getConstructor(String.class));
			constructors.put(Float.class, Float.class.getConstructor(String.class));
			constructors.put(Double.class, Double.class.getConstructor(String.class));
			constructors.put(char.class, Character.class.getConstructor(char.class));
			constructors.put(Character.class, Character.class.getConstructor(char.class));
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
		}
	}

	/**
	 *
	 * @param reader
	 * @param classs
	 * @throws IOException
	 * @throws IllegalArgumentException if object hasn't got corresponding setter
	 * @throws IllegalStateException if file has less fields than setters in object
	 */
	public CSVReader(Reader reader, Class<V> classs) throws IOException, IllegalArgumentException, IllegalStateException {
		this.reader = new LineNumberReader(reader);
		this.classs = classs;
		String header = this.reader.readLine();
		String[] fields = split(header);
		for (String field : fields) {
			try {
				setters.add(classs.getMethod("set" + field, classs.getMethod("get" + field).getReturnType()));
			} catch (NoSuchMethodException ex) {
				close();
				throw new IllegalArgumentException("Field " + field + " doesn't have setter method");
			} catch (SecurityException ex) {
				logger.error(ex.getMessage(), ex);
			}
		}
		int fieldNumber = 0;
		for (Method method : classs.getMethods()) {
			try {
				if (method.getName().startsWith("get") && classs.getMethod("set" + method.getName().substring(3), method.getReturnType()) != null) {
					fieldNumber++;
				}
			} catch (Exception e) {
			}
		}
		if (fields.length != fieldNumber) {
			close();
			throw new IllegalStateException("Input contains " + fields.length + " fields and class has " + fieldNumber + " setter methods");
		}
	}

	/**
	 *
	 * @return next record in file
	 * @throws IOException
	 * @throws IllegalStateException if file has different amount of fields in a row than setters in object
	 */
	@SuppressWarnings("unchecked")
	public V read() throws IOException, IllegalStateException {
		String line = reader.readLine();
		if (line == null) {
			return null;
		}
		String[] fieldValues = split(line);
		if (fieldValues.length != setters.size()) {
			close();
			throw new IllegalStateException("Input line " + reader.getLineNumber() + " contains " + fieldValues.length + " fields and class has " + setters.size() + " setter methods");
		}
		V record = null;

		try {
			record = classs.newInstance();
		} catch (Exception ex) {
			close();
			logger.error(ex.getMessage(), ex);
		}

		for (int i = 0; i < fieldValues.length; ++i) {
			Method setter = setters.get(i);
			try {
				Class parameterClass = setter.getParameterTypes()[0];
				Object parameter = null;
				if (!fieldValues[i].equals("null")) {
					if (parameterClass.isEnum()) {
						for (Object enumConst : parameterClass.getEnumConstants()) {
							if (enumConst.toString().equals(fieldValues[i])) {
								parameter = enumConst;
								break;
							}
						}
					} else if (parameterClass == String.class) {
						parameter = fieldValues[i];
					} else {
						Constructor constructor = constructors.get(parameterClass);
						parameter = constructor.getDeclaringClass() == Character.class ? constructor.newInstance(fieldValues[i].charAt(0)) : constructor.newInstance(fieldValues[i]);
					}
				}
				setter.invoke(record, parameter);

			} catch (Exception ex) {
				close();
				logger.error(ex.getMessage(), ex);
			}
		}


		return record;


	}

	/**
	 *
	 * @return every record stored in the file
	 * @throws IOException
	 * @throws IllegalStateException if file has different amount of fields in a row than setters in object
	 */
	public List<V> readAll() throws IOException, IllegalStateException {
		List<V> list = new ArrayList<V>();
		V record = read();


		while (record != null) {
			list.add(record);
			record = read();


		}
		return list;


	}

	/**
	 * Close reader
	 * @throws IOException
	 */
	public void close() throws IOException {
		if (reader != null) {
			try {
				reader.close();


			} catch (IOException ex) {
				logger.error(ex.getMessage(), ex);


			}
		}
	}

	private static String[] split(String s) {
		ArrayList<String> list = new ArrayList<String>();


		int startPos = 0;


		for (int i = startPos; i
				< s.length();
				++i) {
			if (s.charAt(i) == ';') {
				list.add(s.substring(startPos, i));
				startPos =
						i + 1;


			}
		}
		if (startPos != s.length()) {
			list.add(s.substring(startPos, s.length()));
		}


		return list.toArray(new String[list.size()]);

	}
}
