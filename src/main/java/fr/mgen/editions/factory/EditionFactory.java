package fr.mgen.editions.factory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.mgen.editions.model.Centre;
import fr.mgen.editions.model.EditionPart;
import fr.mgen.editions.model.MetaInfo;
import lombok.extern.log4j.Log4j2;

@Log4j2
public final class EditionFactory {
	public static final Pattern DEFAUT_SAUT_DE_PAGE = Pattern.compile("@      @@      @@  @@ *\\r?\\n");
	public static final Pattern NOM_CENTRE = Pattern.compile(" *centre +dest +: +([0-9]+)");

	private static Map<String, Centre> mCentre = new HashMap<>();

	private EditionFactory() {
		// empty
	}

	public static void buildFromEditionParts(List<String> editionParts) {

		MetaInfo metaInfo = null;

		if (!editionParts.isEmpty()) {
			metaInfo = createMetaInfo(editionParts.remove(0));
		} else {
			log.warn("Pas d'édition trouvée");
		}

		for (String editionPart : editionParts) {
			buildFromEditionPart(metaInfo, editionPart);
		}
	}

	private static MetaInfo createMetaInfo(String str) {
		return new MetaInfo(str);
	}

	private static void buildFromEditionPart(MetaInfo metaInfo, String part) {
		String nomCentre = getNomCentreFromEditionPart(part);
		EditionPart editionPart = createEditionPartForCentre(nomCentre, part);
		editionPart.setMetaInfo(metaInfo);
	}

	private static EditionPart createEditionPartForCentre(String nomCentre, String part) {
		Centre centre = getOrCreateCentre(nomCentre);
		EditionPart editionPart = createEditionPart(part);
		centre.getEditionParts().add(editionPart);
		return editionPart;
	}

	private static Centre getOrCreateCentre(String nomCentre) {
		return mCentre.computeIfAbsent(nomCentre, n -> createCentre(n));
	}

	private static Centre createCentre(String nomCentre) {
		return new Centre(nomCentre);
	}

	private static EditionPart createEditionPart(String part) {
		return new EditionPart(part);
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
		// TODO Auto-generated method stub
		return "todo";
	}

}
