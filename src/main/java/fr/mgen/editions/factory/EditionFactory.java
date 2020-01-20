package fr.mgen.editions.factory;

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
import fr.mgen.editions.model.Edition;
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

	private static String headerTemplate;
	private static String pageHeaderTemplate;
	private static String pageBottom;
	private static String pageLastBottom;
	static {
		headerTemplate = SystemUtil.toString(EditionFactory.class.getResourceAsStream("/templates/fileHeaderTpl.txt"));
		pageHeaderTemplate = SystemUtil.toString(EditionFactory.class.getResourceAsStream("/templates/pageHeaderTpl.txt"));
		pageBottom = SystemUtil.toString(EditionFactory.class.getResourceAsStream("/templates/pageBottom.txt"));
		pageLastBottom = SystemUtil.toString(EditionFactory.class.getResourceAsStream("/templates/pageLastBottom.txt"));
	}

	/** Map nom du centre / Centre */
	private static Map<String, Centre> mCentre = new HashMap<>();

	/** Map Nom du centre / nombre d'éditions pour debug uniquement */
	private static Map<String, Integer> mCentreNbEditions = null;

	private static List<FileInfo> fileInfos = new ArrayList<>();

	private static int order = 0;

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
			metaInfo = createMetaInfo(editionParts.remove(0), fileName, order);
			order++;
			fileInfo.setNumEdiaDemande(metaInfo.getNumEdiaDemande());
		} else {
			log.warn("Pas d'édition trouvée");
		}

		for (String editionPart : editionParts) {
			buildFromEditionPart(metaInfo, editionPart);
		}

		log.debug("-> Nombre d'editions par centre trouvees: " + mCentreNbEditions);
	}

	private static MetaInfo createMetaInfo(String str, String fileName, int order) {
		return new MetaInfo(str, fileName, order);
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

	public static String buildEditionsRegroupee() {
		StringBuilderPlus editionsGroupees = new StringBuilderPlus();
		int numPage = 1;
		LocalDateTime now = LocalDateTime.now();
		String date = now.format(DateTimeFormatter.ofPattern("dd/MM/yy"));
		String heure = now.format(DateTimeFormatter.ofPattern("HH:mm")).replace(":", "h");
		String type = fileInfos.isEmpty() ? "x35" : fileInfos.get(0).getNumEdiaDemande();

		editionsGroupees.append(buildHeader(date, heure, type));

		// Parcours des centres
		List<Centre> orderedCentres = mCentre.values().stream().sorted(Comparator.comparing(Centre::getNom))
				.collect(Collectors.toList());

		for (Centre centre : orderedCentres) {
			log.debug(String.format("Regroupement centre %s (%d editions)", centre.getNom(),
					centre.getEditionPartsSize()));

			int cpt = 0;
			for (Edition edition : centre.getEditions()) {
				MetaInfo metaInfo = edition.getMetaInfo();
				editionsGroupees.append(
						buildPageHeader(numPage, centre.getNom(), metaInfo.getTitreEtat(), metaInfo.getFileName(), date,
								type));
				editionsGroupees.append(edition.buildContent(numPage + 1));
				if (cpt == centre.getEditions().size() - 1) {
					editionsGroupees.append(pageLastBottom);
				} else {
					editionsGroupees.append(pageBottom);
				}
				numPage += edition.getEditionParts().size() + 1;
				cpt++;
			}
		}

		return editionsGroupees.toString();
	}

	private static String buildPageHeader(int numPage, String nom, String titreEtat, String fileName, String date,
			String type) {
		return String.format(pageHeaderTemplate, numPage, nom, numPage, titreEtat, fileName, date, type);
	}

	private static String buildHeader(String date, String heure, String type) {
		StringBuilderPlus sbFilesInfos = new StringBuilderPlus();
		fileInfos.forEach(fileInfo -> {
			sbFilesInfos.appendLine(
					String.format(ENTETE_FICHIER_FORMAT, fileInfo.getFileName(), fileInfo.getNumEdiaDemande()));
		});

		return String.format(headerTemplate, sbFilesInfos.toString(), date, heure, type);
	}

}
