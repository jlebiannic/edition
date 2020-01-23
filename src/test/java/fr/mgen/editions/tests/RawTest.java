package fr.mgen.editions.tests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.meanbean.test.BeanTester;

import fr.mgen.editions.RegroupeEditions.Params;
import fr.mgen.editions.factory.EditionFactory.FileInfo;
import fr.mgen.editions.model.Centre;
import fr.mgen.editions.model.Edition;
import fr.mgen.editions.model.EditionPart;
import fr.mgen.editions.model.MetaInfo;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

public class RawTest {

	private static BeanTester beanTester;

	@BeforeAll
	public static void before() {
		beanTester = new BeanTester();
	}

	@Test
	public void equalsTest() {
		verify(Centre.class, Edition.class, EditionPart.class, MetaInfo.class, Params.class, FileInfo.class);
	}

	public void verify(Class<?>... classes) {
		for (Class<?> classe : classes) {
			beanTester.testBean(classe);
			EqualsVerifier.forClass(classe)
					.suppress(Warning.ALL_FIELDS_SHOULD_BE_USED, Warning.NONFINAL_FIELDS, Warning.STRICT_INHERITANCE)
					.verify();
		}
	}
}
