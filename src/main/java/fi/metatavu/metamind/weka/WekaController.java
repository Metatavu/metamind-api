package fi.metatavu.metamind.weka;

import java.util.ArrayList;
import java.util.Collections;

import weka.classifiers.functions.LinearRegression;
import weka.core.Instance;


/**
 * Functionality from Weka framework
 * 
 * @author Simeon Platonov
 */
public class WekaController {
	public WekaController() {
		
	}
	/**
	 * Returns item iDs for unrated items, sorted by the recommendation algorithm
	 * 
	 * @param items items
	 * @return recommendations recommendations
	 */
	
	public String[] getRecommendations(WekaRecommendationItem[] items) {
		RecommendationDataUtils dataUtils = new RecommendationDataUtils(items);
		dataUtils.convertAttributes();
		dataUtils.splitData();
		dataUtils.createAttributeInfo();
		dataUtils.createDatasets();
		LinearRegression model = new LinearRegression();
		try {
			model.buildClassifier(dataUtils.trainingSet);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		ArrayList<WekaRecommendationItem> recommendedItems = new ArrayList<WekaRecommendationItem>();
		for(int i = 0;i<dataUtils.recommendationSet.size();i++) {
				
					try {
						double estimatedRating = recommend(dataUtils.recommendationSet.get(i),model);
						WekaRecommendationItem item = dataUtils.itemsToRecommend.get(i);
						item.setRating(estimatedRating);
						recommendedItems.add(item);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		}
		Collections.sort(recommendedItems);
		String[] ids = new String[recommendedItems.size()];
		for ( int i = 0; i < recommendedItems.size(); i++ ) {
			ids[i]=recommendedItems.get(i).getId();
		}
		
		return ids;
	}
	
	double recommend(Object instanceItem,LinearRegression model) throws Exception {
		Instance instance = (Instance) instanceItem;
		return model.classifyInstance(instance);
	}
	
	
	

}
