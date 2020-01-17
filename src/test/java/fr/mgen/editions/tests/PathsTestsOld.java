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
import fr.mgen.editions.util.SystemUtil;

class PathsTestsOld {

	private static DataSet dataset;

	@BeforeAll
	public static void init() {
		dataset = new DataSet();
	}

	@Test
	public void testRetrieveFilesFromDirectories() throws IOException {

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

}
