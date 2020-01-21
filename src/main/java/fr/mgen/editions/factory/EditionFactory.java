package fr.mgen.editions.factory;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import fr.mgen.editions.model.Centre;
import fr.mgen.editions.model.Edition;
import fr.mgen.editions.model.EditionPart;
import fr.mgen.editions.model.MetaInfo;
import fr.mgen.editions.util.DateUtil;
import fr.mgen.editions.util.StringBuilderPlus;
import fr.mgen.editions.util.SystemUtil;
import lombok.Data;
import lombok.extern.log4j.Log4j2;

@Log4j2
public final class EditionFactory {
	public static final Pattern DEFAUT_SAUT_DE_PAGE = Pattern.compile("@      @@      @@  @@.*(\\n|\\r)+");
	public static final Pattern NOM_CENTRE = Pattern.compile(" *centre +dest +: +(\\w+)", Pattern.CASE_INSENSITIVE);

	public static final String ENTETE_FICHIER_FORMAT = "/*b1re05    %s %s 000 001 0000 ende";
	public static final String ENTETE_FICHIER_END_FORMAT = "/*a2$dia%s$ende";

	private static String headerTemplate;
	private static String pageHeaderTemplate;
	private static String pageBottom;
	private static String pageLastBottom;
	private static String pageEndRegroupementTemplate;
	static {
		headerTemplate = SystemUtil.toString(EditionFactory.class.getResourceAsStream("/templates/fileHeaderTpl.txt"));
		pageHeaderTemplate = SystemUtil.toString(EditionFactory.class.getResourceAsStream("/templates/pageHeaderTpl.txt"));
		pageBottom = SystemUtil.toString(EditionFactory.class.getResourceAsStream("/templates/pageBottom.txt"));
		pageLastBottom = SystemUtil.toString(EditionFactory.class.getResourceAsStream("/templates/pageLastBottom.txt"));
		pageEndRegroupementTemplate = SystemUtil
				.toString(EditionFactory.class.getResourceAsStream("/templates/pageEndRegroupementTpl.txt"));
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
		int nbParts;

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

		fileInfo.setNbParts(mCentreNbEditions.values().stream().reduce(0, (a, b) -> a + b));
		
		log.debug("Nombre de parties d'editions par centre: " + mCentreNbEditions);
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

		String date = DateUtil.getDate();
		String heure = DateUtil.getHeure();

		editionsGroupees.append(buildHeader(date, heure));

		// Parcours des centres
		List<Centre> orderedCentres = mCentre.values().stream().sorted(Comparator.comparing(Centre::getNom))
				.collect(Collectors.toList());

		int nbPageGarde = 0;
		for (Centre centre : orderedCentres) {
			log.debug(String.format("Regroupement centre %s (%d editions)", centre.getNom(),
					centre.getEditionPartsSize()));

			int cptEdition = 0;
			// Parcours des éditions d'un centre
			for (Edition edition : centre.getEditions()) {
				MetaInfo metaInfo = edition.getMetaInfo();
				// Page de garde de début d'édition
				editionsGroupees.append(
						buildPageHeader(numPage, centre.getNom(), metaInfo.getTitreEtat(), metaInfo.getFileName(), date,
								metaInfo.getNumEdiaDemande()));
				editionsGroupees.append(edition.buildContent(numPage + 1));
				if (cptEdition == centre.getEditions().size() - 1) {
					// Page de fin de toutes les éditions du centre
					editionsGroupees.append(pageLastBottom);
				} else {
					// Page de fin d'une édition d'un centre
					editionsGroupees.append(pageBottom);
				}
				numPage += edition.getEditionParts().size() + 1;
				cptEdition++;
			}
			nbPageGarde += cptEdition;
		}

		editionsGroupees.append(buildEndPageRegroupement(numPage, nbPageGarde + 1));

		return editionsGroupees.toString();
	}

	private static String buildEndPageRegroupement(int nbPagesTotal, int nbPageGarde) {
		return String.format(pageEndRegroupementTemplate, nbPagesTotal, fileInfos.size(), nbPagesTotal, nbPageGarde,
				DateUtil.getDate(), DateUtil.getHeure(), nbPagesTotal, nbPageGarde, nbPagesTotal,
				buildResumeFichiers());
	}

	private static String buildResumeFichiers() {
		StringBuilder sb = new StringBuilder();
		fileInfos.forEach(
				fileInfo -> sb.append("+").append(fileInfo.getFileName()).append(" ").append(fileInfo.getNbParts()));
		return sb.toString();
	}

	private static String buildPageHeader(int numPage, String nom, String titreEtat, String fileName, String date,
			String type) {
		return String.format(pageHeaderTemplate, numPage, nom, numPage, titreEtat, fileName, date, type);
	}

	private static String buildHeader(String date, String heure) {
		StringBuilderPlus sbFilesInfos = new StringBuilderPlus();
		StringBuilderPlus sbEndInfos = new StringBuilderPlus();
		Set<String> numEdiaDemandes = new HashSet<>();
		fileInfos.forEach(fileInfo -> {
			String numEdiaDemande = fileInfo.getNumEdiaDemande();
			sbFilesInfos.appendLine(
					String.format(ENTETE_FICHIER_FORMAT, fileInfo.getFileName(), numEdiaDemande));

			if (!numEdiaDemandes.contains(numEdiaDemande)) {
				sbEndInfos.appendLine(String.format(ENTETE_FICHIER_END_FORMAT, numEdiaDemande));
				numEdiaDemandes.add(numEdiaDemande);
			}

			});


		return String.format(headerTemplate, sbFilesInfos.toString(), date, heure, sbEndInfos.toString());
	}

}
