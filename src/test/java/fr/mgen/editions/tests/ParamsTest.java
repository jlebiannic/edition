package fr.mgen.editions.tests;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import fr.mgen.editions.RegroupeEditions.Params;
import fr.mgen.editions.dataset.DataSet;

public class ParamsTest {
	private static DataSet dataset;

	@BeforeAll
	public static void before() {
		dataset = new DataSet();
	}

	@Test
	public void equalsTest() {
		Params params = dataset.getArgs();
		List<String> dirs = new ArrayList<>();
		dirs.add("rep1");
		dirs.add("rep2");
		String res = "res";
		assertTrue(new Params(dirs, res).equals(params));

	}
}
