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
	private static final Pattern FIN_PAGE_IMPR = Pattern.compile("(/\\*b1nr16 *nombre de page ecrite.+)",
			Pattern.DOTALL);

	private List<EditionPart> editionParts = new ArrayList<>();
	private MetaInfo metaInfo;

	public Edition(MetaInfo metaInfo) {
		super();
		this.metaInfo = metaInfo;
	}

	public String buildContent(int numPage) {
		int numCurrentPage = numPage;
		StringBuilderPlus editionContent = new StringBuilderPlus();

		int cpt = 0;
		for (EditionPart part : editionParts) {

			String content = part.getContent();
			// Recalcule des numéros de page par rapport à "numPage"
			String contentWithNumPage = PatternUtil.replaceFirstMatchingElem(content, PAGE_IMPR,
					String.valueOf(numCurrentPage),
					"Page non trouvée dans: " + content);

			// Pour la dernière page il peut y avoir un résumé à supprimer si c'est la
			// dernière page du fichier
			if (cpt == editionParts.size() - 1 && FIN_PAGE_IMPR.matcher(contentWithNumPage).find()) {
				contentWithNumPage = PatternUtil.replaceFirstMatchingElem(contentWithNumPage, FIN_PAGE_IMPR, "",
						"Fin de page non trouvée dans: " + contentWithNumPage);
			}

			editionContent.appendLine(contentWithNumPage);
			numCurrentPage++;
			cpt++;
		}

		return editionContent.toString();
	}

}
