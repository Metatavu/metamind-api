package fi.metatavu.metamind.weka;

/**
 * Class for recommendation items
 * 
 * @author Simeon Platonov
 */
public class WekaRecommendationItem implements Comparable<WekaRecommendationItem>{
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

	@Override
	public int compareTo(WekaRecommendationItem item) {
		double compareRating = (double) item.rating;
		double comparison = (compareRating-(double) this.rating)*100000;
		return (int) comparison;
	}
	
	
}
