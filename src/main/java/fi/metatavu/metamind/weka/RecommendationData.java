package fi.metatavu.metamind.weka;

import java.util.ArrayList;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

/**
 * Helper class for converting data
 * 
 * @author Simeon Platonov
 */
public class RecommendationData {
	ArrayList<WekaRecommendationItem> trainingItems;
	ArrayList<WekaRecommendationItem> itemsToRecommend;
	ArrayList<Attribute> attributeInfo;
	WekaRecommendationItem[] items;
	Instances trainingSet;
	Instances recommendationSet;
	
	/**
	 * 
	 * @param items - All rated and unrated items
	 */
	public RecommendationData(WekaRecommendationItem[] items) {
		this.items = items;
	}
	
	/**
	 * Converts text attributes to an integer array of 0s and 1s
	 */
	void convertAttributes() {
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
	/**
	 * Splits items into training set and recommendation set
	 * Recommendation set contains items that have not been yet rated by the user
	 */
	void splitData() {
		 trainingItems = new ArrayList<WekaRecommendationItem>();
		 itemsToRecommend = new ArrayList<WekaRecommendationItem>();
		for(WekaRecommendationItem item:items) {
			if(item.rating!=null) {
				itemsToRecommend.add(item);
			}else {
				trainingItems.add(item);
			}
		}
	}
	
	/**
	 * Creates attribute info that is required by the weka.core.Instances class
	 */
	void createAttributeInfo() {
		attributeInfo = new ArrayList<Attribute>();
		for(int i=0;i<items[0].convertedAttributes.length;i++) {
			attributeInfo.add(new Attribute(""+i));
		}
		attributeInfo.add(new Attribute("rating"));
	}

	/**
	 * Creates objects that Weka models can accept
	 */
	void createDatasets() {
		trainingSet = new Instances("trainingSet",attributeInfo,0);
		recommendationSet = new Instances("recommendationSet",attributeInfo,0);
		trainingSet.setClassIndex(attributeInfo.size());
		recommendationSet.setClassIndex(attributeInfo.size());
		
		for(WekaRecommendationItem item:trainingItems) {
			Instance instance = new DenseInstance(item.convertedAttributes.length);
			for(int i=0;i<item.convertedAttributes.length;i++) {	
				instance.setValue(i,item.convertedAttributes[i]);
			}
			instance.setValue(item.convertedAttributes.length,(double) item.rating);
			trainingSet.add(instance);
		}
		for(WekaRecommendationItem item:itemsToRecommend) {
			Instance instance = new DenseInstance(item.convertedAttributes.length);
			for(int i=0;i<item.convertedAttributes.length;i++) {	
				instance.setValue(i,item.convertedAttributes[i]);
			}
			recommendationSet.add(instance);
		}
	}
}
