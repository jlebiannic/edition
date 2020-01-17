package fr.mgen.editions.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import fr.mgen.editions.dataset.DataSet;
import fr.mgen.editions.factory.EditionFactory;
import fr.mgen.editions.util.SystemUtil;

public class SystemUtilTest {

	private static DataSet dataset;

	@BeforeAll
	public static void init() {
		dataset = new DataSet();
	}

	@Test
	public void getPathsFromDirsTest() throws IOException {

		List<String> dirPathNames = new ArrayList<>();
		dirPathNames.add(dataset.getDir1PathName());
		dirPathNames.add(dataset.getDir2PathName());
		List<Path> paths = SystemUtil.getPathsFromDirs(dirPathNames);

		List<Path> filesNames = paths.stream().map(path -> path.getFileName()).sorted().collect(Collectors.toList());
		assertEquals(3, filesNames.size());
		assertEquals("file1.txt", filesNames.get(0).toString());
		assertEquals("file2.txt", filesNames.get(1).toString());
		assertEquals("file3.txt", filesNames.get(2).toString());
	}

	@Test
	public void getFileContentTest() throws IOException {
		String fileContent = SystemUtil.getFileContent(dataset.getFilePathWithContent());
		assertEquals(
				  "&éèàç contenu 1\r\n" 
				+ "content line 2\r\n" 
				+ "content line 3\r\n" 
				+ "content line 4\r\n"
				+ "content line 5\r\n"
				, fileContent);
	}

	@Test
	public void splitContentTest() {
		List<String> parts = SystemUtil.splitContent(dataset.getContent(), EditionFactory.DEFAUT_SAUT_DE_PAGE);
		String expectedResult = "jqshfjqfsh\r\n" + 
				"\r\n" + 
				",\r\n" +
				"lkfqsfdlfdskmk\r\n" + 
				"qkflmqsdfkqsflùk\r\n" + 
				"gklvkckcvjqgkljeilejz\r\n" + 
				"ksjfajkqslkfj\r\n" + 
				"qsdjofjk\r\n" + 
				",\r\n" +
				"kljdfklfj\r\n" + 
				"jkljfklsjqs@      @@      @@  @@klqjsfklsdfj\r\n" + 
				",\r\n" +
				"mlkdfjklfdsj\r\n" + 
				"kqsdlfjmkfdsklj\r\n" + 
				"klqsjdf\r\n" + 
				",aazxcwww";
		assertEquals(expectedResult, parts.stream().collect(Collectors.joining(",")));
	}
}
