package fr.mgen.editions.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.log4j.Log4j2;

@Data
@Log4j2
public class MetaInfo {
	public static final Pattern TITRE = Pattern.compile(" *titre de l'etat *= *([^\\r\\n]+)");
	public static final Pattern NUM_EDIA_DEMANDES = Pattern.compile(" *numero 'edia' demandes *= *(\\w+)");

	@EqualsAndHashCode.Exclude
	private String info;
	private String titreEtat;
	private String numEdiaDemande;
	private String fileName;

	public MetaInfo(String content, String fileName) {
		this.fileName = fileName;
		this.info = content;
		this.titreEtat = getAttribute(content, TITRE, "Titre non trouvé dans: " + content);
		this.numEdiaDemande = getAttribute(content, NUM_EDIA_DEMANDES,
				"Numero 'edia' demandes non trouvé dans: " + content);
	}

	private String getAttribute(String content, Pattern pattern, String msg) {
		Matcher matcher;
		matcher = pattern.matcher(content);
		if (matcher.find()) {
			return matcher.group(1);
		} else {
			log.error(msg);
			return null;
		}
	}
}
