package fr.mgen.editions.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

public class PatternTest {

	@Test
	public void replacementTest() {
		Pattern p = Pattern.compile("(\\d)(.*)(\\d)");
		String input = "6 example input 4";
		Matcher m = p.matcher(input);
		if (m.find()) {
			assertEquals("test example input 4",
					new StringBuilder(input).replace(m.start(1), m.end(1), "test").toString());
			assertEquals("6test4", new StringBuilder(input).replace(m.start(2), m.end(2), "test").toString());
			assertEquals("6 example input test",
					new StringBuilder(input).replace(m.start(3), m.end(3), "test").toString());
		}
	}
	
}
