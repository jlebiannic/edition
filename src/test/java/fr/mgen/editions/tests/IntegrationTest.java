package fr.mgen.editions.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import fr.mgen.editions.RegroupeEditions;
import fr.mgen.editions.dataset.DataSet;

public class IntegrationTest {

	private static DataSet dataset;

	@BeforeAll
	public static void init() {
		dataset = new DataSet();
	}

	@Test
	public void integrationTest() throws IOException {
		RegroupeEditions.main(dataset.getArgs());
		String integrationContentExpected = clean(dataset.getIntegrationContentExpected());
		String integrationContentResult = clean(dataset.getIntegrationContentResult());
		assertEquals(integrationContentExpected, integrationContentResult);
	}
	
	
	public String clean(String str) {
		str = str.replaceAll("date  de regroupement.+", "");
		str = str.replaceAll("heure de regroupement.+", "");
		str = str.replaceAll("date d'edition.+", "");
		str = str.replaceAll("date\\.de\\.regroupement.+", "");
		str = str.replaceAll("heure\\.de\\.regroupement.+", "");
		return str;
	}
}
