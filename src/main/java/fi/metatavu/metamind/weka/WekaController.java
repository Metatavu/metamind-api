package fi.metatavu.metamind.weka;

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
	 * @return 
	 * @return recommendations recommendations
	 * @throws Exception 
	 */
	
	public double[] getRecommendations(WekaRecommendationItem[] items) throws Exception {
		RecommendationData data = new RecommendationData(items);
		data.convertAttributes();
		data.splitData();
		data.createAttributeInfo();
		data.createDatasets();
		double[] recommendations = new double[data.itemsToRecommend.size()];
		LinearRegression model = new LinearRegression();
		model.buildClassifier(data.trainingSet);
		
		for(int i = 0;i<data.recommendationSet.size();i++) {
					recommendations[i] = recommend(data.recommendationSet.get(i),model);	 
		}
		return recommendations;
	}
	
	double recommend(Object instanceItem,LinearRegression model) throws Exception {
		Instance instance = (Instance) instanceItem;
		return model.classifyInstance(instance);
	}
	
	
	

}
