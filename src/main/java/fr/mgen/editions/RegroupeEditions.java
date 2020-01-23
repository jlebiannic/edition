package fr.mgen.editions;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import fr.mgen.editions.factory.EditionFactory;
import fr.mgen.editions.util.SystemUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

@Log4j2
public class RegroupeEditions {


	public static void main(String[] args) throws IOException {

		Params params = getParams(args);

		if (params != null) {
			String edition = regroupeEditions(params);
			SystemUtil.writeFileWithContent(params.getResultFileName(), edition);
			log.info("Fin.");
		}
	}

	public static String regroupeEditions(Params params) throws IOException {
		EditionFactory editionFactory = new EditionFactory();
		List<Path> paths = SystemUtil.getPathsFromDirs(params.getDirsNames());
		for (Path path : paths) {
			log.info("Traitement: " + path);
			String fileContent = SystemUtil.getFileContent(path);
			List<String> editionParts = SystemUtil.splitContent(fileContent, EditionFactory.DEFAUT_SAUT_DE_PAGE);
			log.debug(String.format("%d parties trouvees (avec entête)", editionParts.size()));
			editionFactory.buildFromEditionParts(editionParts, path);
		}
		log.info("Regroupement ...");
		return editionFactory.buildAndGetEditionsRegroupee();
	}

	/**
	 * Analyse des arguments passés au programme
	 */
	public static Params getParams(String[] args) {
		Params params = null;
		ArgumentParser parser = ArgumentParsers.newFor("RegroupeEditions").build()
				.description("Regroupe les editions par centre dans un fichier unique.");
		parser.addArgument("-reps")
				.dest("reps").metavar("nom repertoire")
				.type(String.class).nargs("+")
				.required(true)
				.help("liste des repertoires contenant les editions");
		
		parser.addArgument("-res")
				.dest("res").metavar("nom fichier")
				.type(String.class)
				.required(true)
				.help("fichier résultat contenant les editions regroupees par centre");
		
		try {
			Namespace res = parser.parseArgs(args);
			params = new Params((List<String>) (res.get("reps")), res.get("res"));
		} catch (ArgumentParserException e) {
			parser.handleError(e);
		}

		return params;
	}

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Params {
		List<String> dirsNames;
		String resultFileName;
	}
}
