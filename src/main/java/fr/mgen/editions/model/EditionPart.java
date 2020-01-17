package fr.mgen.editions.model;

import lombok.Data;

@Data
public class EditionPart {

	private MetaInfo metaInfo;
	private String content;

	public EditionPart(String content) {
		super();
		this.content = content;
	}

}
