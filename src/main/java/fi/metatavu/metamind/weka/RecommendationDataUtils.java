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
public class RecommendationDataUtils {
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
	public RecommendationDataUtils(WekaRecommendationItem[] items) {
		this.items = items;
	}
	
	/**
	 * Converts text attributes to an integer array of 0s and 1s
	 */
	public void convertAttributes() {
		ArrayList<String> attributes = new ArrayList<String>();
		for(WekaRecommendationItem item:items) {
			for(String attribute:item.getAttributes()) {
				if(!attributes.contains(attribute)) {
					attributes.add(attribute);
				}
			}
		}
		int i = 0;
		for(WekaRecommendationItem item:items) {
			int[] convertedAttributes = new int[attributes.size()];
			for(String attribute:item.getAttributes()) {
				int index = attributes.indexOf(attribute);
				convertedAttributes[index] = 1;
				
			}
			item.setConvertedAttributes(convertedAttributes);
			items[i] = item;
			i++;
		}	
	}
	/**
	 * Splits items into training set and recommendation set
	 * Recommendation set contains items that have not been yet rated by the user
	 */
	public void splitData() {
		 trainingItems = new ArrayList<WekaRecommendationItem>();
		 itemsToRecommend = new ArrayList<WekaRecommendationItem>();
		for(WekaRecommendationItem item:items) {
			if(item.getRating()==null) {
				itemsToRecommend.add(item);
			}else {
				trainingItems.add(item);
			}
		}
	}
	
	/**
	 * Creates attribute info that is required by the weka.core.Instances class
	 */
	public void createAttributeInfo() {
		attributeInfo = new ArrayList<Attribute>();
		for(int i=0;i<items[0].getConvertedAttributes().length;i++) {
			attributeInfo.add(new Attribute(""+i));
		}
		attributeInfo.add(new Attribute("rating"));
	}

	/**
	 * Creates objects that Weka models can accept
	 */
	public void createDatasets() {
		trainingSet = new Instances("trainingSet",attributeInfo,0);
		recommendationSet = new Instances("recommendationSet",attributeInfo,0);
		trainingSet.setClassIndex(attributeInfo.size()-1);
		recommendationSet.setClassIndex(attributeInfo.size()-1);
		
		for(WekaRecommendationItem item:trainingItems) {
			Instance instance = new DenseInstance(item.getConvertedAttributes().length+1);
			for(int i=0;i<item.getConvertedAttributes().length;i++) {	
				instance.setValue(i,item.getConvertedAttributes()[i]);
			}
			instance.setValue(item.getConvertedAttributes().length,(double) item.getRating());
			trainingSet.add(instance);
		}
		for(WekaRecommendationItem item:itemsToRecommend) {
			Instance instance = new DenseInstance(item.getConvertedAttributes().length);
			for(int i=0;i<item.getConvertedAttributes().length;i++) {	
				instance.setValue(i,item.getConvertedAttributes()[i]);
			}
			recommendationSet.add(instance);
		}
	}
}
