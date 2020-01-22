package fr.mgen.editions.dataset;

import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import fr.mgen.editions.RegroupeEditions.Params;
import fr.mgen.editions.util.SystemUtil;

public class DataSet {
	private String aboslutPath;
	private String resourcesAbsolutePath;
	public DataSet() {
		Path currentRelativePath = Paths.get("");
		aboslutPath = currentRelativePath.toAbsolutePath().toString();
		resourcesAbsolutePath = aboslutPath + "/src/test/resources";
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

	public String getIntegrationContentExpected() {
		InputStream stream = DataSet.class.getResourceAsStream("/integration/gcos/regroupe.txt");
		return SystemUtil.toString(stream);
	}

	public String getIntegrationContentExpected2() {
		InputStream stream = DataSet.class.getResourceAsStream("/integration/linux/regroupe.txt");
		return SystemUtil.toString(stream);
	}

	public Params getParams() {
		List<String> dirsNames = new ArrayList<>();
		dirsNames.add(resourcesAbsolutePath+"/integration/gcos/lps");
		dirsNames.add(resourcesAbsolutePath+"/integration/gcos/lmj");
		return new Params(dirsNames, null);
	}

	public Params getParams2() {
		List<String> dirsNames = new ArrayList<>();
		dirsNames.add(resourcesAbsolutePath + "/integration/linux/lps");
		dirsNames.add(resourcesAbsolutePath + "/integration/linux/lmj");
		return new Params(dirsNames, null);
	}
}
