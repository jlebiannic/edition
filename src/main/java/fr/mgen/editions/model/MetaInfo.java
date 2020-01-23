package fr.mgen.editions.model;

import java.util.regex.Pattern;

import fr.mgen.editions.util.PatternUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Informations déduitent d'un entête de fichier d'entrée (contenant une édition
 * pour plusieurs centres)
 */
@Data
public class MetaInfo {
	public static final Pattern TITRE = Pattern.compile(" *titre de l'etat *= *([^\\r\\n]+)", Pattern.CASE_INSENSITIVE);
	public static final Pattern NUM_EDIA_DEMANDES = Pattern.compile(" *numero 'edia' demandes *= *(\\w+)",
			Pattern.CASE_INSENSITIVE);

	@EqualsAndHashCode.Exclude
	private String info;
	@EqualsAndHashCode.Exclude
	private String titreEtat;
	@EqualsAndHashCode.Exclude
	private String numEdiaDemande;
	@EqualsAndHashCode.Exclude
	private String fileName;
	private int order;

	public MetaInfo(String content, String fileName, int order) {
		this.fileName = fileName;
		this.info = content;
		this.order = order;
		this.titreEtat = PatternUtil.getFirstMatchingElem(content, TITRE, "Titre non trouvé dans: " + content);
		this.numEdiaDemande = PatternUtil.getFirstMatchingElem(content, NUM_EDIA_DEMANDES,
				"Numero 'edia' demandes non trouvé dans: " + content);
	}


}
