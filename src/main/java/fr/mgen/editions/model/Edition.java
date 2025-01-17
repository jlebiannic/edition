package fr.mgen.editions.model;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.mgen.editions.factory.EditionFactory;
import fr.mgen.editions.util.PatternUtil;
import fr.mgen.editions.util.StringBuilderPlus;
import fr.mgen.editions.util.StringUtil;
import fr.mgen.editions.util.SystemUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Edition {

	private static final Pattern PAGE_IMPR = Pattern.compile("page impr\\. *: *([0-9]+)", Pattern.CASE_INSENSITIVE);
	private static final Pattern FIN_PAGE_IMPR = Pattern.compile("(\\s+/\\*b1nr16 *nombre de page ecrite.+)",
			Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
	private static final Pattern POUR_FORMATAGE_PAGE_IMPR = Pattern.compile("([\\n\\r]+) *page impr\\. *: *[0-9]+",
			Pattern.CASE_INSENSITIVE);

	private static String sautDePage;
	static {
		sautDePage = SystemUtil.toString(EditionFactory.class.getResourceAsStream("/templates/pageFormFeed.txt"));
	}

	private List<EditionPart> editionParts = new ArrayList<>();
	private MetaInfo metaInfo;

	public Edition(MetaInfo metaInfo) {
		super();
		this.metaInfo = metaInfo;
	}

	/**
	 * Construction du contenu de l' @Edition en concaténant ses @EditionPart
	 * 
	 */
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

			// suppression des retours à la ligne multiples en fin de texte
			contentWithNumPage = StringUtil.stripTrailing(contentWithNumPage);

			// Pour la dernière page il peut y avoir un résumé à supprimer si c'est la
			// dernière page du fichier
			if (cpt == editionParts.size() - 1 && FIN_PAGE_IMPR.matcher(contentWithNumPage).find()) {
				contentWithNumPage = PatternUtil.replaceFirstMatchingElem(contentWithNumPage, FIN_PAGE_IMPR, "",
						"Fin de page non trouvée dans: " + contentWithNumPage);
			} else if (cpt != editionParts.size() - 1 && contentWithNumPage.indexOf('\f') == -1) {
				// Insertion des sauts de page après chaque partie de l'édition (si pas déjà présent)
				contentWithNumPage += sautDePage;
			}

			// Formatage supplémentaire
			// Dans certain cas la ligne contenant "PAGE IMPR. :" est précédée de deux
			// lignes vides (pb programme linux)
			// => Suppression de ces deux lignes inutiles et non iso par rapport à GCOS
			Matcher m = POUR_FORMATAGE_PAGE_IMPR.matcher(contentWithNumPage);
			if (m.find()) {
				contentWithNumPage = new StringBuilder(contentWithNumPage)
						.replace(m.start(1), m.end(1), SystemUtil.LINE_SEP).toString();
			}

			editionContent.appendLine(contentWithNumPage);
			numCurrentPage++;
			cpt++;
		}

		return editionContent.toString();
	}

}
