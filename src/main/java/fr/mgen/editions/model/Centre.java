package fr.mgen.editions.model;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Centre Mgen
 * 
 */
@Data
@NoArgsConstructor
public class Centre {
	private String nom;

	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private Map<MetaInfo, Edition> mMetaInfoEditionParts = new HashMap<>();

	public Centre(String nom) {
		super();
		this.nom = nom;
	}

	/**
	 * Construction de la liste des @Edition contenant les @EditionPart
	 */
	public List<Edition> getEditions() {
		// Tri sur le titre des méta infos
		return mMetaInfoEditionParts.entrySet().stream()
				.sorted(Map.Entry.comparingByKey(Comparator.comparing(MetaInfo::getOrder)))
				.map(Entry<MetaInfo, Edition>::getValue)
				.collect(Collectors.toList());
	}

	/**
	 * Ajoute une @EditionPart à une @Edition. Les @MetaInfo constituent la clé pour
	 * savoir à quelle @Edition appartient une @EditionPart
	 */
	public void addEditionPart(EditionPart editionPart) {
		MetaInfo metaInfo = editionPart.getMetaInfo();
		Edition editionForMetaInfo = mMetaInfoEditionParts.computeIfAbsent(metaInfo, Edition::new);
		editionForMetaInfo.getEditionParts().add(editionPart);
		mMetaInfoEditionParts.put(metaInfo, editionForMetaInfo);
	}

	/**
	 * Retourn le nombre d' @Edition du centre
	 */
	public int getEditionPartsSize() {
		return this.mMetaInfoEditionParts.size();
	}

}
