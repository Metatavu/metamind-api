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
		convertAttributes(items);
		RecommendationData data = new RecommendationData(items);
		data.splitData();
		data.createAttributes();
		


		
		return new int[0];
	}
	
	
	
	/**
	 * Converts text attributes to int[] of 0s and 1s
	 * @param items items
	 */
	void convertAttributes(WekaRecommendationItem[] items) {
		ArrayList<String> attributes = new ArrayList<String>();
		for(WekaRecommendationItem item:items) {
			for(String attribute:item.attributes) {
				if(!attributes.contains(attribute)) {
					attributes.add(attribute);
				}
			}
		}
		int i = 0;
		for(WekaRecommendationItem item:items) {
			int[] convertedAttributes = new int[attributes.size()];
			for(String attribute:item.attributes) {
				int index = attributes.indexOf(attribute);
				convertedAttributes[index] = 1;
				
			}
			item.convertedAttributes = convertedAttributes;
			items[i] = item;
			i++;
		}	
	}
}
