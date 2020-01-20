package fr.mgen.editions.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.extern.log4j.Log4j2;

@Log4j2
public final class PatternUtil {

	public PatternUtil() {
		// empty
	}

	public static String getFirstMatchingElem(String content, Pattern pattern, String errorMsg) {
		Matcher matcher = pattern.matcher(content);
		if (matcher.find()) {
			return matcher.group(1);
		} else {
			log.error(errorMsg);
			return null;
		}
	}

	public static String replaceFirstMatchingElem(String content, Pattern pattern, String replacement,
			String errorMsg) {
		Matcher matcher = pattern.matcher(content);
		if (matcher.find()) {
			return new StringBuilder(content).replace(matcher.start(1), matcher.end(1), replacement).toString();
		} else {
			log.error(errorMsg);
			return null;
		}
	}


}
