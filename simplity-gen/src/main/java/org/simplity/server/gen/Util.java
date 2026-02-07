// SPDX-License-Identifier: MIT
package org.simplity.server.gen;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import org.simplity.server.core.valueschema.BooleanSchema;
import org.simplity.server.core.valueschema.DateSchema;
import org.simplity.server.core.valueschema.DecimalSchema;
import org.simplity.server.core.valueschema.IntegerSchema;
import org.simplity.server.core.valueschema.TextSchema;
import org.simplity.server.core.valueschema.TimestampSchema;
import org.simplity.server.core.valueschema.ValueType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

/**
 * Utility methods for this project
 *
 * @author simplity.org
 *
 */
public class Util {
	private static final char Q = '"';
	/**
	 * Gson is not a small object. It is immutable and thread safe. Hence with this
	 * small trick, we can avoid repeated creation of Gson instances
	 */
	private static final Gson GSON = new Gson();
	/**
	 * java Types for each ValueType enum
	 */
	public static final String[] JAVA_VALUE_TYPES = getJavaValueTypes();

	/**
	 * Java getter-types for each of ValueType enum
	 */
	public static final String[] JAVA_GET_TYPES = getJavaGetTypes();

	private static String[] getJavaValueTypes() {
		final ValueType[] types = ValueType.values();

		final String[] result = new String[types.length];
		result[ValueType.Boolean.ordinal()] = "boolean";
		result[ValueType.Date.ordinal()] = "LocalDate";
		result[ValueType.Decimal.ordinal()] = "double";
		result[ValueType.Integer.ordinal()] = "long";
		result[ValueType.Text.ordinal()] = "String";
		result[ValueType.Timestamp.ordinal()] = "Instant";
		return result;
	}

	private static String[] getJavaGetTypes() {
		final ValueType[] types = ValueType.values();

		final String[] result = new String[types.length];
		result[ValueType.Boolean.ordinal()] = "Bool";
		result[ValueType.Date.ordinal()] = "Date";
		result[ValueType.Decimal.ordinal()] = "Decimal";
		result[ValueType.Integer.ordinal()] = "Long";
		result[ValueType.Text.ordinal()] = "String";
		result[ValueType.Timestamp.ordinal()] = "Timestamp";
		return result;
	}

	private static final Logger logger = LoggerFactory.getLogger(Util.class);

	/**
	 * Appends a Java string literal (or concatenated literals) to the output. If
	 * the input contains newlines, it is emitted as multiple quoted lines joined
	 * with '+' so the generated code is readable.
	 *
	 * @param out    output StringBuilder
	 * @param text   input text
	 * @param indent indentation to be used for new lines
	 */
	public static void appendJavaStringLiteral(StringBuilder out, String text, String indent) {
		if (text == null) {
			out.append("null");
			return;
		}

		int firstNl = text.indexOf('\n');
		if (firstNl < 0) {
			// simple case: single literal
			out.append('"').append(escapeForJavaStringLiteral(text)).append('"');
			return;
		}

		// multi-line case
		int start = 0;
		boolean first = true;

		while (true) {
			int nl = text.indexOf('\n', start);
			boolean hasNl = nl >= 0;

			String line = hasNl ? text.substring(start, nl) : text.substring(start);

			if (!first) {
				out.append("\n").append(indent).append("+ ");
			}
			first = false;

			out.append('"').append(escapeForJavaStringLiteral(line));

			if (hasNl) {
				out.append("\\n");
			}

			out.append('"');

			if (!hasNl) {
				break;
			}
			start = nl + 1;
		}
	}

	/**
	 * Escapes a string so that it can be safely embedded inside a Java string
	 * literal.
	 *
	 * Example: hello "world"\n becomes: hello \"world\"\\n
	 *
	 * @param text input text
	 * @return escaped text
	 */
	@SuppressWarnings("boxing")
	public static String escapeForJavaStringLiteral(String text) {
		if (text == null) {
			return "null"; // useful for code generation
		}

		StringBuilder sb = new StringBuilder(text.length() + 16);

		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);

			switch (c) {
			case '\\':
				sb.append("\\\\");
				break;
			case '"':
				sb.append("\\\"");
				break;
			case '\n':
				sb.append("\\n");
				break;
			case '\r':
				sb.append("\\r");
				break;
			case '\t':
				sb.append("\\t");
				break;
			case '\b':
				sb.append("\\b");
				break;
			case '\f':
				sb.append("\\f");
				break;

			default:
				if (c < 0x20 || c > 0x7E) {
					// Any non-printable or non-ASCII → Unicode escape
					sb.append(String.format("\\u%04X", (int) c));
				} else {
					sb.append(c);
				}
			}
		}
		return sb.toString();
	}

	/**
	 * enclose the string inside double quotes, after escaping chars, if required,
	 * for the same
	 *
	 * @param s
	 * @return escaped string with enclosed quotes
	 */
	public static String quotedString(final String s) {
		if (s == null || s.isEmpty()) {
			return "null";
		}
		return Q + escapeForJavaStringLiteral(s) + Q;
	}

	/**
	 *
	 * @param obj to be quoted
	 * @return a quoted string for the object
	 */
	public static String escapeTs(final Object obj) {
		if (obj == null) {
			return "null";
		}
		if (obj instanceof String) {
			return quotedString((String) obj);
		}
		return obj.toString();
	}

	/**
	 * write an import statement for the class
	 *
	 * @param sbf
	 * @param cls
	 */
	public static void emitImport(final StringBuilder sbf, final Class<?> cls) {
		sbf.append("\nimport ").append(cls.getName()).append(';');
	}

	/**
	 *
	 * @param name
	 * @return Properly cased as the name of a Java Class
	 */
	public static String toClassName(final String name) {
		String nam = name;
		int idx = name.lastIndexOf('.');
		if (idx != -1) {
			idx++;
			if (idx == nam.length()) {
				return "";
			}
			nam = name.substring(idx);
		}
		return toUpper(nam.charAt(0)) + nam.substring(1);
	}

	/**
	 *
	 * @param name field/column name
	 * @return default label based on the name
	 */
	public static String toLabel(final String name) {
		StringBuilder sbf = new StringBuilder();
		sbf.append(toUpper(name.charAt(0)));
		int n = name.length();
		/*
		 * labels for id fields should not have the Id at the end
		 */
		if (name.endsWith("Id")) {
			n = n - 2;
		}
		for (int i = 1; i < n; i++) {
			char c = name.charAt(i);
			if (isUpper(c)) {
				sbf.append(' ');
			}
			sbf.append(c);
		}
		return sbf.toString();
	}

	private static final int DIFF = 'a' - 'A';

	/**
	 *
	 * @param c
	 * @return true if this is an upper case character
	 */
	public static boolean isUpper(char c) {
		return c >= 'A' && c <= 'Z';
	}

	/**
	 *
	 * @param c
	 * @return true if this is a lower case character
	 */
	static boolean isLower(char c) {
		return c >= 'a' && c <= 'z';
	}

	/**
	 *
	 * @param c
	 * @return lower-case character
	 */
	static char toLower(char c) {
		if (isUpper(c)) {
			return (char) (c + DIFF);
		}
		return c;
	}

	/**
	 *
	 * @param c
	 * @return upper case character
	 */
	static char toUpper(char c) {
		if (isLower(c)) {
			return (char) (c - DIFF);
		}
		return c;
	}

	/**
	 *
	 * @param name
	 * @return camel-cased version of the name
	 */
	static String toName(final String name) {
		final String nam = name;
		return nam.substring(0, 1).toLowerCase() + nam.substring(1);
	}

	/**
	 * if className is package.name.className, then return package.name
	 *
	 * @param completeClassName
	 * @return prefix for the simple class name
	 */
	public static String getClassQualifier(final String completeClassName) {
		final int idx = completeClassName.lastIndexOf('.');
		if (idx == -1) {
			return null;
		}
		return completeClassName.substring(0, idx);
	}

	/**
	 * write the contents to the named file
	 *
	 * @param fileName non-null
	 * @param text     non-null
	 */
	public static void writeOut(final String fileName, final String text) {
		try (Writer writer = new FileWriter(new File(fileName))) {
			writer.write(text);
			logger.info("File {} generated.", fileName);
		} catch (final Exception e) {
			logger.error("Error while writing file {} \n {}", fileName, e.getMessage());
		}
	}

	/**
	 * quote the string-value for String, else just the string value of the object
	 *
	 * @param obj
	 * @return value of this object, quoted if it is a string
	 */
	public static Object escapeObject(final Object obj) {
		if (obj == null) {
			return "null";
		}

		if (obj instanceof String) {
			return quotedString((String) obj);
		}

		return obj.toString();
	}

	/**
	 *
	 * @param valueType
	 * @return data type class name for this value type
	 */
	public static Class<?> getDataTypeClass(final ValueType valueType) {
		switch (valueType) {
		case Boolean:
			return BooleanSchema.class;
		case Date:
			return DateSchema.class;
		case Decimal:
			return DecimalSchema.class;
		case Integer:
			return IntegerSchema.class;
		case Text:
			return TextSchema.class;
		case Timestamp:
			return TimestampSchema.class;
		default:
			logger.error("{} is not a known value type", valueType);
			return TextSchema.class;
		}
	}

	/**
	 * like new String[]{"firstOne", ....}
	 *
	 * @param arr
	 * @param sbf
	 */
	public static void emitStringArray(final String[] arr, final StringBuilder sbf) {
		sbf.append("new String[]{");
		boolean firstOne = true;
		for (final String s : arr) {
			if (firstOne) {
				firstOne = false;
			} else {
				sbf.append(',');
			}
			sbf.append(quotedString(s));
		}
		sbf.append('}');
	}

	/**
	 * emit java code for an array of fields
	 *
	 * @param sbf
	 * @param fields
	 */
	public static void emitFieldsArray(final StringBuilder sbf, final Field[] fields) {
		sbf.append("new Field[]{");
		boolean firstOne = true;
		for (final Field field : fields) {
			if (firstOne) {
				firstOne = false;
			} else {
				sbf.append(',');
			}
			field.emitJavaCode(sbf, false);
		}
		sbf.append('}');
	}

	/**
	 * emit valueTypes array of fields: like new ValueType{ValueType.Text, ....}
	 *
	 * @param fields
	 * @param sbf
	 */
	public static void emitTypesArray(final Field[] fields, final StringBuilder sbf) {
		sbf.append("new ValueType[]{");
		boolean firstOne = true;
		for (final Field f : fields) {
			if (firstOne) {
				firstOne = false;
			} else {
				sbf.append(',');
			}
			sbf.append("ValueType.").append(f.schemaInstance.valueTypeEnum.name());
		}
		sbf.append('}');
	}

	/**
	 * emit getter functions with proper type from the underlying values Object[]
	 *
	 * @param sbf
	 * @param fields
	 * @param valuesArrayName
	 */
	public static void emitGettersFromValues(final StringBuilder sbf, IField[] fields, String valuesArrayName) {
		for (final IField f : fields) {
			String typ = Util.JAVA_VALUE_TYPES[f.getValueType().ordinal()];

			final String nam = f.getName();
			final String cls = Util.toClassName(nam);

			sbf.append("\n\n\t\t/**\n\t * @return value of ").append(nam).append("\n\t */");
			sbf.append("\n\t\tpublic ").append(typ).append(" get").append(cls).append("(){");
			sbf.append("\n\t\t\treturn (").append(typ).append(") ").append(valuesArrayName).append('[')
					.append(f.getIndex()).append("];");
			sbf.append("\n\t\t}");
		}
	}

	/**
	 * emit setter functions with proper type to the underlying values Object[]
	 *
	 * @param sbf
	 * @param fields
	 * @param valuesArrayName
	 */
	public static void emitSettersValues(final StringBuilder sbf, IField[] fields, String valuesArrayName) {
		for (final IField f : fields) {
			final String typ = Util.JAVA_VALUE_TYPES[f.getValueType().ordinal()];
			final String nam = f.getName();
			final String cls = Util.toClassName(nam);

			sbf.append("\n\n\t/**\n\t * @param value value of ").append(nam).append("\n\t */");
			sbf.append("\n\t\tpublic void set").append(cls).append("( ").append(typ).append(" value){");
			sbf.append("\n\t\t\t").append(valuesArrayName).append('[').append(f.getIndex()).append("] = value;");
			sbf.append("\n\t\t}");
		}
	}

	/**
	 * initialize entries in a Map with name/idx
	 *
	 * @param map
	 *
	 */
	public static void initializeMapEntries(Map<String, ?> map) {
		int idx = 0;
		for (Map.Entry<String, ?> entry : map.entrySet()) {
			Object value = entry.getValue();
			if (value instanceof Initializer) {
				((Initializer) value).initialize(entry.getKey(), idx);
			}
			idx++;
		}

	}

	/**
	 * add att: "value', but only if it is required
	 *
	 * @param sbf
	 * @param prefix
	 * @param att
	 * @param val
	 */
	public static void addAttr(final StringBuilder sbf, final String prefix, final String att, final String val) {
		if (val == null || val.isEmpty()) {
			return;
		}
		sbf.append(prefix).append(Util.quotedString(att)).append(": ").append(Util.quotedString(val)).append(',');
	}

	/**
	 *
	 * @param <T>      type of object to be loaded
	 * @param fileName absolute file name of the json file
	 * @param cls      class of the object to be loaded
	 * @return loaded object instance, or null in case of any error
	 */
	public static <T> T loadJson(String fileName, Class<T> cls) {
		File f = new File(fileName);
		if (f.exists() == false) {
			logger.error("project configuration file {} not found. Aborting..", fileName);
			return null;
		}

		try (JsonReader reader = new JsonReader(new FileReader(f))) {
			return GSON.fromJson(reader, cls);
		} catch (final Exception e) {
			logger.error("Exception while trying to read file {}. Error: {}", f.getPath(), e.getMessage());
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * An object may want to initialize itself after setting all the attributes
	 */
	interface Initializer {
		/**
		 *
		 * @param name member name. typically becomes name attribute of the object
		 * @param idx  0-based index of this member. this is generally not the right
		 *             thing to do in a json because the order of attributes is not
		 *             significant.However,in our design, we would like to make use of
		 *             it and hence this
		 */
		void initialize(String name, int idx);
	}

	/**
	 * read and discard the next token
	 *
	 * @param reader
	 * @throws IOException
	 */
	public static void swallowAToken(final JsonReader reader) throws IOException {
		final JsonToken token = reader.peek();
		switch (token) {

		case BEGIN_ARRAY:
			GSON.fromJson(reader, Object[].class);
			return;

		case BEGIN_OBJECT:
			GSON.fromJson(reader, Object.class);
			return;

		case BOOLEAN:
		case NUMBER:
		case STRING:
			reader.nextString();
			return;

		case NAME:
			reader.nextName();
			return;

		case NULL:
			reader.nextNull();
			return;

		case END_ARRAY:
			reader.endArray();
			return;
		case END_OBJECT:
			reader.endArray();
			return;
		case END_DOCUMENT:
			return;
		default:
			logger.warn("Util is not designed to swallow the token {} ", token.name());
		}
	}

	static void emitJavaGettersAndSetters(final Field[] fields, final StringBuilder sbf) {
		for (final Field f : fields) {

			final boolean isEnum = f.enumName != null;
			String typ = isEnum ? f.enumName : Util.JAVA_VALUE_TYPES[f.valueTypeEnum.ordinal()];
			String get = Util.JAVA_GET_TYPES[f.valueTypeEnum.ordinal()];
			final String nam = f.name;
			final String cls = Util.toClassName(nam);

			/**
			 * setters not generated for the fields that are managed internally
			 */
			if (!f.fieldTypeEnum.isSetInternally()) {
				sbf.append("\n\n\t/**\n\t * set value for ").append(nam);
				sbf.append("\n\t * @param value to be assigned to ").append(nam);
				sbf.append("\n\t */");
				sbf.append("\n\tpublic void set").append(cls).append('(').append(typ).append(" value){");
				sbf.append("\n\t\tthis.fieldValues[").append(f.index).append("] = value");
				if (isEnum) { // get the name() of the enum
					sbf.append(" == null ? \"\" : value.name()");
				}
				sbf.append(";\n\t}");
			}

			// of course we need getters for all fields
			sbf.append("\n\n\t/**\n\t * @return value of ").append(nam).append("\n\t */");
			sbf.append("\n\tpublic ").append(typ).append(" get").append(cls).append("(){");
			if (isEnum) {
				sbf.append("\n\t\tString val = super.fetchStringValue(").append(f.index).append(");");
				sbf.append("\n\t\tif (val != null && val.isEmpty()) {\n\t\t\treturn null;\n\t\t}");
				sbf.append("\n\t\ttry {\n\t\t\treturn ").append(f.enumName)
						.append(".valueOf(val);\n\t\t} catch (IllegalArgumentException e) {");
				sbf.append("\n\t\t\treturn null;\n\t\t}");

			} else {
				sbf.append("\n\t\treturn super.fetch").append(get).append("Value(").append(f.index).append(");");
			}
			sbf.append("\n\t}");
		}

	}

}
