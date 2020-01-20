package fr.mgen.editions.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EditionPart {

	private MetaInfo metaInfo;
	private String content;
}
