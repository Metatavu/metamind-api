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
	 * Gets recommendations
	 * @param items items
	 * @return recommendations recommendations
	 * @throws Exception 
	 */
	
	public int[] getRecommendations(WekaRecommendationItem[] items) throws Exception {
		RecommendationData data = new RecommendationData(items);
		data.convertAttributes();
		data.splitData();
		data.createAttributeInfo();
		data.createDatasets();
		LinearRegression model = new LinearRegression();
		model.buildClassifier(data.trainingSet);
		
		ArrayList<WekaRecommendationItem> recommendedItems = new ArrayList<WekaRecommendationItem>();
		for(int i = 0;i<data.recommendationSet.size();i++) {
					double estimatedRating = recommend(data.recommendationSet.get(i),model);
					WekaRecommendationItem item = data.itemsToRecommend.get(i);
					item.rating = estimatedRating;
					recommendedItems.add(item);
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
