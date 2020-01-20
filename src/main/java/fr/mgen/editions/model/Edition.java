package fr.mgen.editions.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class Edition {
	private List<EditionPart> editionParts = new ArrayList<>();
	private MetaInfo metaInfo;

	public Edition(MetaInfo metaInfo) {
		super();
		this.metaInfo = metaInfo;
	}

	public String buildContent() {
		StringBuilder editionContent = new StringBuilder();
		editionParts
				.forEach(part -> editionContent.append(part.getContent()).append(System.getProperty("line.separator")));
		return editionContent.toString();
	}

}
