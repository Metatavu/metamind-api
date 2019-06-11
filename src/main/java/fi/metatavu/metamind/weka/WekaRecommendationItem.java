package fi.metatavu.metamind.weka;

/**
 * Class for recommendation items
 * 
 * @author Simeon Platonov
 */
public class WekaRecommendationItem {
	public Object rating = null;
	public int id;
	public String[] attributes;
	public int[] convertedAttributes;
	
	public WekaRecommendationItem(int id,String[] attributes) {
		this.id = id;
		this.attributes = attributes;
	}
	
	public WekaRecommendationItem(int id,String[] attributes,double rating) {
		this.id = id;
		this.attributes = attributes;
		this.rating = rating;
	}
	
}
