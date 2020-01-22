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

	public void reduceFromMapTest() {

		Map<String, Integer> m = new HashMap<>();
		m.put("c1", 1);
		m.put("c1", 2);
		m.put("c1", 3);
		m.put("c2", 1);
		m.put("c2", 2);
		m.put("c3", 1);

		int sum = 0;

		sum = m.values().stream().reduce(0, (a, b) -> a + b);

		assertEquals(10, sum);

	}
}
