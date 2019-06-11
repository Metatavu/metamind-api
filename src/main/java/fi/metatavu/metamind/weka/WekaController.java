package fi.metatavu.metamind.weka;

import java.util.ArrayList;

import weka.classifiers.functions.LinearRegression;

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
		LinearRegression model = new LinearRegression();
		
		return new int[0];
	}
	
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
