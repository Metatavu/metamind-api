package fi.metatavu.metamind.weka;

import java.util.ArrayList;

import weka.classifiers.functions.LinearRegression;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

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
	 */
	
	public int[] getRecommendations(WekaRecommendationItem[] items) {
		RecommendationData data = new RecommendationData(items);
		data.convertAttributes();
		data.splitData();
		data.createAttributes();
		data.createDatasets();
		
		return new int[0];
	}
	
	
	

}
