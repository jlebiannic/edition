package fr.mgen.editions.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class SystemUtil {

	// MODE UNIX (même sous Windows)
	public static final String LINE_SEP = "\n";

	private SystemUtil() {
		// empty
	}


	public static List<Path> getPathsFromDirs(List<String> dirNames) {
		return dirNames.stream()
				.map(SystemUtil::wrapGetFilesFromDir)
				.flatMap(List<Path>::stream)
				.collect(Collectors.toList());
	}

	private static List<Path> wrapGetFilesFromDir(String dirName) {
		try {
			try (Stream<Path> paths = Files.walk(Paths.get(dirName))) {
				return paths.filter(path -> path.toFile().isFile()).collect(Collectors.toList());
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static String getFileContent(Path path) throws IOException {
		return new String(Files.readAllBytes(path), Charset.forName(StandardCharsets.UTF_8.name()));
	}

	public static List<String> splitContent(String content, Pattern pattern) {
		return Arrays.asList(pattern.split(content)).stream().map(x -> x).collect(Collectors.toList());
	}

	public static void writeFileWithContent(String resultFileName, String content) throws IOException {
		Files.write(Paths.get(resultFileName), content.getBytes());
	}

	// public static String toStringJava9(InputStream inputStream) {
	// try {
	// return new String(inputStream.readAllBytes());
	// } catch (IOException e) {
	// throw new RuntimeException(e);
	// }
	// }

	public static String toString(InputStream inputStream) {
		StringBuilder textBuilder = new StringBuilder();
		try (Reader reader = new BufferedReader(
				new InputStreamReader(inputStream, Charset.forName(StandardCharsets.UTF_8.name())))) {
			int c = 0;
			while ((c = reader.read()) != -1) {
				textBuilder.append((char) c);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return textBuilder.toString();
	}

	public static String cleanCRLF(String str) {
		// Permet de générer correctement les retours à la ligne même si l'exécution
		// s'effectue sous Windows
		return str.replaceAll("\r\n", LINE_SEP);
	}
}

