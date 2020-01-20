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

	@EqualsAndHashCode.Exclude
	private String info;
	private String titreEtat;

	public MetaInfo(String content) {
		this.info = content;

		Matcher matcher = TITRE.matcher(content);
		if (matcher.find()) {
			this.titreEtat = matcher.group(1);
		} else {
			log.error("Titre non trouvé dans: " + content);
		}
	}
}
