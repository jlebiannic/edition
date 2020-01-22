package fr.mgen.editions.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.mgen.editions.RegroupeEditions;
import fr.mgen.editions.dataset.DataSet;
import fr.mgen.editions.factory.EditionFactory;

public class IntegrationTest {

	private static DataSet dataset;

	@BeforeAll
	public static void init() {
		dataset = new DataSet();
	}

	@BeforeEach
	public void initEach() {
		EditionFactory.init();
	}

	@Test
	public void integrationTest() throws IOException, InterruptedException {
		String integrationContentResult = clean(RegroupeEditions.regroupeEditions(dataset.getParams()));
		String integrationContentExpected = clean(dataset.getIntegrationContentExpected());
		assertEquals(integrationContentExpected, integrationContentResult);
	}
	
	@Test
	public void integrationTest2() throws IOException, InterruptedException {
		String integrationContentResult = clean(RegroupeEditions.regroupeEditions(dataset.getParams2()));
		String integrationContentExpected = clean(dataset.getIntegrationContentExpected2());
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
