package fr.mgen.editions.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class StringTest {
	@Test
	public void replacementTest() {
		String str = "\r\naaa\n\rbb\n\r\r\n";
		assertEquals("\r\naaa\n\rbb", str.stripTrailing());
		assertEquals("aaa\n\rbb\n\r\r\n", str.stripLeading());
	}
}
