package fr.mgen.editions.dataset;

import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import fr.mgen.editions.util.SystemUtil;

public class DataSet {
	private String aboslutPath;
	public DataSet() {
		Path currentRelativePath = Paths.get("");
		aboslutPath = currentRelativePath.toAbsolutePath().toString();
	}

	public String getDir1PathName() {
		return aboslutPath + "/src/test/resources/path/dir1";
	}

	public String getDir2PathName() {
		return aboslutPath + "/src/test/resources/path/dir2";
	}

	public Path getFilePathWithContent() {
		return Paths.get(aboslutPath + "/src/test/resources/dir/file.txt");
	}

	public String getContent() {
		InputStream stream = DataSet.class.getResourceAsStream("/content.txt");
		return SystemUtil.toString(stream);
	}
}
