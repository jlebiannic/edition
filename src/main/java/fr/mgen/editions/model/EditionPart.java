package fr.mgen.editions.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Partie d'une @Edition Contient la @MetaInfo créée partir des informations
 * d'entête d'un fichier d'entrée
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EditionPart {

	private MetaInfo metaInfo;
	private String content;
}
