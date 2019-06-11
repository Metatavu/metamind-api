package fi.metatavu.metamind.weka;

import weka.classifiers.functions.LinearRegression;

/**
 * Functionality from Weka framework
 * 
 * @author Simeon Platonov
 */
public class WekaController {
	public WekaController() {
		
	}
	
	public int[] getRecommendations(WekaRecommendationItem[] items) {
		LinearRegression model = new LinearRegression();
		
		return new int[0];
	}
}
