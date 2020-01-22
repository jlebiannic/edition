package fr.mgen.editions.util;

import java.util.StringTokenizer;

public final class StringUtil {

	private StringUtil() {
		// empty
	}

	public static String stripTrailing(String str) {
		return str.replaceAll("\\s+$", "");
	}

	/**
	 * Découpe une chaîne de caractère en bloc de 50 caractères max par ligne
	 * 
	 */
	public static String cut(String str, String sep, int max) {
		StringBuilderPlus sb = new StringBuilderPlus();
		StringTokenizer tokenizer = new StringTokenizer(str, sep, true);
		StringBuilder currentLine = new StringBuilder();
		while (tokenizer.hasMoreElements()) {
			String elem = tokenizer.nextToken();
			if (currentLine.length() + elem.length() > max) {
				sb.append(currentLine);
				sb.appendLine();
				currentLine = new StringBuilder();
			}
			currentLine.append(elem);
		}
		sb.append(currentLine);
		return sb.toString();
	}

	/**
	 * Ajoute un préfix et un suffix en début et fin de charque ligne d'une chaîne
	 * de caractères
	 */
	public static String bound(String str, String prefix, String suffix) {
		String strWithLinesBounded = str;
		strWithLinesBounded = prefix + strWithLinesBounded;
		strWithLinesBounded = strWithLinesBounded.replaceAll("([\r\n]+)", suffix + "$1" + prefix);
		strWithLinesBounded = strWithLinesBounded + suffix;
		return strWithLinesBounded;
	}
}