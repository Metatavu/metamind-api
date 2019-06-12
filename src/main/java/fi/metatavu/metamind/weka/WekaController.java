package fi.metatavu.metamind.weka;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

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
	 * @param items items
	 * @return recommendations recommendations
	 * @throws Exception 
	 */
	
	public int[] getRecommendations(WekaRecommendationItem[] items) {
		RecommendationData data = new RecommendationData(items);
		data.convertAttributes();
		data.splitData();
		data.createAttributeInfo();
		data.createDatasets();
		LinearRegression model = new LinearRegression();
		try {
			model.buildClassifier(data.trainingSet);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		ArrayList<WekaRecommendationItem> recommendedItems = new ArrayList<WekaRecommendationItem>();
		for(int i = 0;i<data.recommendationSet.size();i++) {
				
					try {
						double estimatedRating = recommend(data.recommendationSet.get(i),model);
						WekaRecommendationItem item = data.itemsToRecommend.get(i);
						item.rating = estimatedRating;
						recommendedItems.add(item);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		}
		Collections.sort(recommendedItems);
		int[] ids = new int[recommendedItems.size()];
		for(int i=0;i<recommendedItems.size();i++) {
			ids[i]=recommendedItems.get(i).id;
		}
		
		return ids;
	}
	
	double recommend(Object instanceItem,LinearRegression model) throws Exception {
		Instance instance = (Instance) instanceItem;
		return model.classifyInstance(instance);
	}
	
	
	

}
