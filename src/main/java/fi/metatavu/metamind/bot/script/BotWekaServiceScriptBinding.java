package fi.metatavu.metamind.bot.script;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.metamind.weka.WekaController;
import fi.metatavu.metamind.weka.WekaRecommendationItem;

/**
 * Bot script binding for Weka service
 * @author Simeon Platonov
 *
 */
@ApplicationScoped
public class BotWekaServiceScriptBinding {

	@Inject
	private WekaController controller;
	
	/**
	 * Returns item iDs for unrated items, sorted by the recommendation algorithm
	 * @param items items
	 * @return recommendations
	 */
	public int[] getRecommendations(WekaRecommendationItem[] items) {
		return controller.getRecommendations(items);
	}
}
