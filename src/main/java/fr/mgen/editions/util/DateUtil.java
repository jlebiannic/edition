package fr.mgen.editions.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class DateUtil {

	private DateUtil() {
		// empty
	}

	public static String getDate() {
		return LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yy"));
	}

	public static String getHeure() {
		return LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")).replace(":", "h");
	}

}