package fr.mgen.editions.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import fr.mgen.editions.RegroupeEditions;
import fr.mgen.editions.dataset.DataSet;
import fr.mgen.editions.util.SystemUtil;

public class IntegrationTest {

	private static DataSet dataset;

	@BeforeAll
	public static void init() {
		dataset = new DataSet();
	}

	@Test
	public void integrationTest() throws IOException, InterruptedException {
		String integrationContentResult = clean(RegroupeEditions.regroupeEditions(dataset.getParams()));
		String integrationContentExpected = clean(dataset.getIntegrationContentExpected());
		SystemUtil.writeFileWithContent("d:\\tmpRes.txt", integrationContentResult);
		SystemUtil.writeFileWithContent("d:\\tmpExpected.txt", integrationContentExpected);
		assertEquals(integrationContentExpected, integrationContentResult);
	}
	
	@Test
	public void integrationTest2() throws IOException, InterruptedException {
		String integrationContentResult = clean(RegroupeEditions.regroupeEditions(dataset.getParams2()));
		String integrationContentExpected = clean(dataset.getIntegrationContentExpected2());
		assertEquals(integrationContentExpected, integrationContentResult);
	}
	
	public String clean(String str) {
		str = str.replaceAll("[\\r\\n]+", "");
		str = str.replaceAll("date  de regroupement.+", "");
		str = str.replaceAll("heure de regroupement.+", "");
		str = str.replaceAll("date d'edition.+", "");
		str = str.replaceAll("date\\.de\\.regroupement.+", "");
		str = str.replaceAll("heure\\.de\\.regroupement.+", "");
		return str;
	}
}
