package fr.mgen.editions.model;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import fr.mgen.editions.util.PatternUtil;
import fr.mgen.editions.util.StringBuilderPlus;
import lombok.Data;

@Data
public class Edition {

	private static final Pattern PAGE_IMPR = Pattern.compile("page impr\\. *: *([0-9]+)");

	private List<EditionPart> editionParts = new ArrayList<>();
	private MetaInfo metaInfo;

	public Edition(MetaInfo metaInfo) {
		super();
		this.metaInfo = metaInfo;
	}

	public String buildContent(int numPage) {
		int numCurrentPage = numPage;
		StringBuilderPlus editionContent = new StringBuilderPlus();

		for (EditionPart part : editionParts) {

			String content = part.getContent();
			String contentWithNumPage = PatternUtil.replaceFirstMatchingElem(content, PAGE_IMPR,
					String.valueOf(numCurrentPage),
					"Page non trouvée dans: " + content);

			editionContent.appendLine(contentWithNumPage);
			numCurrentPage++;
		}

		return editionContent.toString();
	}

}
