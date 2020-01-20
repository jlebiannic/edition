package fr.mgen.editions.model;

import java.util.ArrayList;
import java.util.List;

import fr.mgen.editions.util.StringBuilderPlus;
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
		StringBuilderPlus editionContent = new StringBuilderPlus();
		editionParts.forEach(part -> editionContent.appendLine(part.getContent()));
		return editionContent.toString();
	}

}
