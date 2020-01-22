package fr.mgen.editions.util;

public final class StringUtil {

	private StringUtil() {
		// empty
	}

	public static String stripTrailing(String str) {
		return str.replaceAll("\\s+$", "");
	}
}