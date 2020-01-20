package fr.mgen.editions.factory;

import java.io.InputStream;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import fr.mgen.editions.model.Centre;
import fr.mgen.editions.model.EditionPart;
import fr.mgen.editions.model.MetaInfo;
import fr.mgen.editions.util.StringBuilderPlus;
import fr.mgen.editions.util.SystemUtil;
import lombok.Data;
import lombok.extern.log4j.Log4j2;

@Log4j2
public final class EditionFactory {
	public static final Pattern DEFAUT_SAUT_DE_PAGE = Pattern.compile("@      @@      @@  @@ .+(\\n|\\r)");
	public static final Pattern NOM_CENTRE = Pattern.compile(" *centre +dest +: +([0-9]+)");

	public static final String ENTETE_FICHIER_FORMAT = "/*b1re05    %s %s 000 001 0000 ende";
	public static final InputStream ENTETE_FICHIER_TEMPLATE = EditionFactory.class
			.getResourceAsStream("/templates/fileHeaderTpl.txt");

	/** Map nom du centre / Centre */
	private static Map<String, Centre> mCentre = new HashMap<>();

	/** Map Nom du centre / nombre d'éditions pour debug uniquement */
	private static Map<String, Integer> mCentreNbEditions = null;

	private static List<FileInfo> fileInfos = new ArrayList<>();

	@Data
	public static class FileInfo {
		String fileName;
		String numEdiaDemande;

		public FileInfo(String fileName) {
			super();
			this.fileName = fileName;
		}

	}

	private EditionFactory() {
		// empty
	}

	/**
	 * Traite un ensemble de parties d'édition (correspond à un fichier)
	 */
	public static void buildFromEditionParts(List<String> editionParts, Path path) {

		MetaInfo metaInfo = null;
		mCentreNbEditions = new HashMap<>();

		String fileName = path.toFile().getName();
		FileInfo fileInfo = new FileInfo(fileName);
		fileInfos.add(fileInfo);

		if (!editionParts.isEmpty()) {
			metaInfo = createMetaInfo(editionParts.remove(0), fileName);
			fileInfo.setNumEdiaDemande(metaInfo.getNumEdiaDemande());
		} else {
			log.warn("Pas d'édition trouvée");
		}

		for (String editionPart : editionParts) {
			buildFromEditionPart(metaInfo, editionPart);
		}

		log.debug("-> Nombre d'editions par centre trouvees: " + mCentreNbEditions);
	}

	private static MetaInfo createMetaInfo(String str, String fileName) {
		return new MetaInfo(str, fileName);
	}

	private static void buildFromEditionPart(MetaInfo metaInfo, String part) {
		String nomCentre = getNomCentreFromEditionPart(part);
		createEditionPartForCentre(nomCentre, metaInfo, part);
	}

	private static EditionPart createEditionPartForCentre(String nomCentre, MetaInfo metaInfo, String part) {
		Centre centre = getOrCreateCentre(nomCentre);
		EditionPart editionPart = createEditionPart(metaInfo, part);
		centre.addEditionPart(editionPart);

		// Pour debug uniquement
		Integer nbEditions = mCentreNbEditions.computeIfAbsent(nomCentre, nC -> 0);
		nbEditions++;
		mCentreNbEditions.put(nomCentre, nbEditions);

		return editionPart;
	}

	private static Centre getOrCreateCentre(String nomCentre) {
		return mCentre.computeIfAbsent(nomCentre, EditionFactory::createCentre);
	}

	private static Centre createCentre(String nomCentre) {
		return new Centre(nomCentre);
	}

	private static EditionPart createEditionPart(MetaInfo metaInfo, String part) {
		return new EditionPart(metaInfo, part);
	}

	private static String getNomCentreFromEditionPart(String part) {
		String nomCentre = null;
		Matcher matcher = NOM_CENTRE.matcher(part);
		if (matcher.find()) {
			nomCentre = matcher.group(1);
		} else {
			log.error("Nom de centre non trouvé dans: " + part);
		}
		return nomCentre;
	}

	public static String getEditionsRegroupee() {
		StringBuilderPlus editionsGroupees = new StringBuilderPlus();

		editionsGroupees.append(buildHeader());

		// Parcours des centres
		List<Centre> orderedCentres = mCentre.values().stream().sorted(Comparator.comparing(Centre::getNom))
				.collect(Collectors.toList());
		orderedCentres.forEach(centre -> {
			log.debug(String.format("Regroupement centre %s (%d editions)", centre.getNom(),
					centre.getEditionPartsSize()));

			centre.getEditions().forEach(edition -> {
				editionsGroupees.append(edition.buildContent());
			});
		});
		return editionsGroupees.toString();
	}

	private static String buildHeader() {
		StringBuilderPlus sbFilesInfos = new StringBuilderPlus();
		fileInfos.forEach(fileInfo -> {
			sbFilesInfos.appendLine(
					String.format(ENTETE_FICHIER_FORMAT, fileInfo.getFileName(), fileInfo.getNumEdiaDemande()));
		});

		LocalDateTime now = LocalDateTime.now();
		String date = now.format(DateTimeFormatter.ofPattern("dd/MM/yy"));
		String heure = now.format(DateTimeFormatter.ofPattern("HH:mm")).replace(":", "h");
		String type = fileInfos.isEmpty() ? "x35" : fileInfos.get(0).getNumEdiaDemande();
		return String.format(SystemUtil.toString(ENTETE_FICHIER_TEMPLATE), sbFilesInfos.toString(), date, heure, type);
	}

}
