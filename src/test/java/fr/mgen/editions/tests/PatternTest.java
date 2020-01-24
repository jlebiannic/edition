package fr.mgen.editions.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

import fr.mgen.editions.util.StringBuilderPlus;

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

	@Test
	public void replacementTest2() {
		Pattern p = Pattern.compile("([\\n\\r]+) *page impr\\. *: *[0-9]+", Pattern.CASE_INSENSITIVE);
		String str = "\r\n" + 
				"  LE 16/09/19 A 15H53                                                                                                  PAGE :    1\r\n" + 
				"  LFI51L    CIN TOURS                  LISTE DES PROPOSITIONS DE MODIFICATIONS DANS PRADES\r\n" + 
				"  CENTRE  DEST  : 001                                   EN DATE DU 18 02 2019\n" + 
				"\n" + 
				"\n" + 
				"  PAGE IMPR. :      2\r\n" + 
				"ZONE  NO IDENT  019112150    P R A D E S                                  F I N P S\r\n" + 
				"\r\n" + 
				"\r\n" + 
				"1     CPAM                   011                                          011\r\n";
		
		String strExpected = "\r\n" + 
				"  LE 16/09/19 A 15H53                                                                                                  PAGE :    1\r\n" + 
				"  LFI51L    CIN TOURS                  LISTE DES PROPOSITIONS DE MODIFICATIONS DANS PRADES\r\n" + 
				"  CENTRE  DEST  : 001                                   EN DATE DU 18 02 2019\r\n" + 
				"  PAGE IMPR. :      2\r\n" + 
				"ZONE  NO IDENT  019112150    P R A D E S                                  F I N P S\r\n" + 
				"\r\n" + 
				"\r\n" + 
				"1     CPAM                   011                                          011\r\n";

		Matcher m = p.matcher(str);
		assertTrue(m.find());
		String strRes = new StringBuilder(str).replace(m.start(1), m.end(1), StringBuilderPlus.LINE_SEP).toString();
		assertEquals(strExpected, strRes);
	}
}
