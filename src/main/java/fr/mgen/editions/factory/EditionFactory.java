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
import fr.mgen.editions.util.StringUtil;
import fr.mgen.editions.util.SystemUtil;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * Gestion des parties d'un fichier d'édition: cf. @buildFromEditionParts
 * - construction du modèle Centre->Edition->EditionPart
 * Constructon de l'édition finale: cf. @buildEditionsRegroupee
 * - regroupement des différentes éditions par centre
 * 
 * */
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
	private static String pageLastEndBottom;
	private static String pageEndRegroupementTemplate;
	static {
		headerTemplate = SystemUtil.toString(EditionFactory.class.getResourceAsStream("/templates/fileHeaderTpl.txt"));
		pageHeaderTemplate = SystemUtil.toString(EditionFactory.class.getResourceAsStream("/templates/pageHeaderTpl.txt"));
		pageBottom = SystemUtil.toString(EditionFactory.class.getResourceAsStream("/templates/pageBottom.txt"));
		pageLastBottom = SystemUtil.toString(EditionFactory.class.getResourceAsStream("/templates/pageLastBottom.txt"));
		pageLastEndBottom = SystemUtil
				.toString(EditionFactory.class.getResourceAsStream("/templates/pageLastEndBottom.txt"));
		pageEndRegroupementTemplate = SystemUtil
				.toString(EditionFactory.class.getResourceAsStream("/templates/pageEndRegroupementTpl.txt"));
	}

	/** Map nom du centre / Centre */
	private Map<String, Centre> mCentre;

	/** Map Nom du centre / nombre d'éditions */
	private Map<String, Integer> mCentreNbEditions;

	private List<FileInfo> fileInfos;

	private int order;

	@Data
	@NoArgsConstructor
	public static class FileInfo {
		String fileName;
		String numEdiaDemande;
		int nbParts;

		public FileInfo(String fileName) {
			super();
			this.fileName = fileName;
		}

	}

	public EditionFactory() {
		fileInfos = new ArrayList<>();
		mCentre = new HashMap<>();
		order = 0;
	}


	/**
	 * Traite un ensemble de parties d'édition (correspond à un fichier)
	 */
	public void buildFromEditionParts(List<String> editionParts, Path path) {

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

	/**
	 * Création d'une instance de @MetaInfo à partir des informations d'entête d'un
	 * fichier d'entrée
	 */
	private MetaInfo createMetaInfo(String str, String fileName, int order) {
		return new MetaInfo(str, fileName, order);
	}

	/**
	 * Traitement d'une partie d'édition pour rattachement à un centre
	 */
	private void buildFromEditionPart(MetaInfo metaInfo, String part) {
		String nomCentre = getNomCentreFromEditionPart(part);
		createEditionPartForCentre(nomCentre, metaInfo, part);
	}

	/**
	 * Création d'une instance d'EditionPart et ajout à un centre L'instance
	 * d'un @Centre est capable de reconstruire ces @Edition à partir
	 * des @EditionPart
	 * 
	 */
	private EditionPart createEditionPartForCentre(String nomCentre, MetaInfo metaInfo, String part) {
		Centre centre = getOrCreateCentre(nomCentre);
		EditionPart editionPart = createEditionPart(metaInfo, part);
		centre.addEditionPart(editionPart);

		Integer nbEditions = mCentreNbEditions.computeIfAbsent(nomCentre, nC -> 0);
		nbEditions++;
		mCentreNbEditions.put(nomCentre, nbEditions);

		return editionPart;
	}

	/**
	 * Récupération d'un @Centre déjà créé ou création s'il n'existe pas
	 */
	private Centre getOrCreateCentre(String nomCentre) {
		return mCentre.computeIfAbsent(nomCentre, this::createCentre);
	}

	/**
	 * Création d'une instance d'un @Centre
	 */
	private Centre createCentre(String nomCentre) {
		return new Centre(nomCentre);
	}

	/**
	 * Création d'une instance @EditionPart: contient les @MetaInfo présentent dans
	 * l'entête d'un fichier d'entrée (impression concernant plusieurs centres)
	 */
	private EditionPart createEditionPart(MetaInfo metaInfo, String part) {
		return new EditionPart(metaInfo, part);
	}

	/**
	 * Récupération du nom du centre dans une partie d'édition
	 */
	private String getNomCentreFromEditionPart(String part) {
		String nomCentre = null;
		Matcher matcher = NOM_CENTRE.matcher(part);
		if (matcher.find()) {
			nomCentre = matcher.group(1);
		} else {
			log.error("Nom de centre non trouvé dans: " + part);
		}
		return nomCentre;
	}

	/**
	 * Construction du fichier final regroupant les éditions par centre
	 */
	public String buildAndGetEditionsRegroupee() {
		StringBuilderPlus editionsGroupees = new StringBuilderPlus();
		int numPage = 1;

		String date = DateUtil.getDate();
		String heure = DateUtil.getHeure();

		editionsGroupees.append(buildHeader(date, heure));

		// Parcours des centres
		List<Centre> orderedCentres = mCentre.values().stream().sorted(Comparator.comparing(Centre::getNom))
				.collect(Collectors.toList());

		int nbPageGarde = 0;
		for (int cptCentre = 0; cptCentre < orderedCentres.size(); cptCentre++) {
			Centre centre = orderedCentres.get(cptCentre);
			log.debug(String.format("Regroupement centre %s (%d editions)", centre.getNom(),
					centre.getEditionPartsSize()));

			
			// Parcours des éditions d'un centre
			int cptEdition = 0;
			for (; cptEdition < centre.getEditions().size(); cptEdition++) {
				Edition edition = centre.getEditions().get(cptEdition);
				MetaInfo metaInfo = edition.getMetaInfo();
				// Page de garde de début d'édition
				editionsGroupees.append(
						buildPageHeader(numPage, centre.getNom(), metaInfo.getTitreEtat(), metaInfo.getFileName(), date,
								metaInfo.getNumEdiaDemande()));
				editionsGroupees.append(edition.buildContent(numPage + 1));
				if (cptEdition != centre.getEditions().size() - 1) {
					// Page de fin d'une édition d'un centre
					editionsGroupees.append(pageBottom);
				} else {
					if (cptCentre != orderedCentres.size() - 1) {
						// Page de fin de toutes les éditions du centre
						editionsGroupees.append(pageLastBottom);
					} else {
						// Page de fin de toutes les éditions du centre et dernière édition du
						// regroupement
						editionsGroupees.append(pageLastEndBottom);
					}
				}
				numPage += edition.getEditionParts().size() + 1;
			}
			nbPageGarde += cptEdition;
		}

		editionsGroupees.append(buildEndPageRegroupement(numPage, nbPageGarde + 1));

		return SystemUtil.cleanCRLF(editionsGroupees.toString());
	}

	/**
	 * Construction du résumé tout à la fin du fichier final
	 */
	private String buildEndPageRegroupement(int nbPagesTotal, int nbPageGarde) {
		return String.format(pageEndRegroupementTemplate, nbPagesTotal, fileInfos.size(), nbPagesTotal, nbPageGarde,
				DateUtil.getDate(), DateUtil.getHeure(), nbPagesTotal, nbPageGarde, nbPagesTotal,
				buildResumeFichiers());
	}

	/** 
	 * Construction d'un résumé nom de fichier/nombre de parties d'édition
	 * Exemple:
	 * 	/*b1re19   +fic1  364+fic2 3147+fic3 2673+fic4 1159+ende
		/*b1re19   +fic5   95+fic6    4+                    ende
	 * 
	 * */
	private String buildResumeFichiers() {
		StringBuilder sb = new StringBuilder();
		String sep = "+";
		fileInfos.forEach(
				fileInfo -> sb.append(sep).append(fileInfo.getFileName()).append(" ").append(fileInfo.getNbParts()));
		// Découpage sur plusieurs lignes pour ne pas dépasser 50 caractères
		return StringUtil.bound(StringUtil.cut(sb.toString(), sep, 50), "/*b1re19  ", "end");
	}

	/**
	 * Construction d'un entête de page positionnée avant toutes les éditions d'un
	 * centre
	 */
	private String buildPageHeader(int numPage, String nom, String titreEtat, String fileName, String date,
			String type) {
		return String.format(pageHeaderTemplate, numPage, nom, numPage, titreEtat, fileName, date, type);
	}

	/**
	 * Construction de l'entête global du fichier final
	 */
	private String buildHeader(String date, String heure) {
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
