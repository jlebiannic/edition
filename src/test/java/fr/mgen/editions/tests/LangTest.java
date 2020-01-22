package fr.mgen.editions.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import fr.mgen.editions.util.StringUtil;

public class LangTest {
	@Test
	public void replacementTest() {
		String str = "\r\naaa \n\rbb \n\r\r\n";
		assertEquals("\r\naaa \n\rbb", StringUtil.stripTrailing(str));
		// assertEquals("aaa\n\rbb\n\r\r\n", str.stripLeading());
	}

	@Test
	public void reduceFromMapTest() {
		Map<String, Integer> m = new HashMap<>();
		m.put("c1", 1);
		m.put("c2", 2);
		m.put("c3", 3);
		m.put("c4", 1);
		m.put("c5", 2);
		m.put("c6", 1);
		int sum = 0;
		sum = m.values().stream().reduce(0, (a, b) -> a + b);
		assertEquals(10, sum);
	}

	@Test
	public void cutAndBoundTest() {
		String str = "+lmj03e  364+lps10e  3147+lps11e  2673+lfi511l 1159+lfi531l 95+lfi611l   4";
		String expected = "+lmj03e  364+lps10e  3147+lps11e  2673+\r\n" + "lfi511l 1159+lfi531l 95+lfi611l   4";
		assertEquals(expected, StringUtil.cut(str, "+", 50));

		String str2 = expected;
		String expected2 = "/*b1re19  +lmj03e  364+lps10e  3147+lps11e  2673+end\r\n"
				+ "/*b1re19  lfi511l 1159+lfi531l 95+lfi611l   4end";
		assertEquals(expected2, StringUtil.bound(str2, "/*b1re19  ", "end"));

	}
}
