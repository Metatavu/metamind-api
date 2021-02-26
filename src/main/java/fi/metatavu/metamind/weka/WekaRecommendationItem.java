package fi.metatavu.metamind.weka;


/**
 * Class for recommendation items
 * 
 * @author Simeon Platonov
 */
public class WekaRecommendationItem implements Comparable<WekaRecommendationItem>{
  private Double rating = null;
  private String id;
  private String[] attributes;
  private int[] convertedAttributes;
  public String getId() {
    return this.id;
  }
  public void setId(String id) {
    this.id=id;
  }
  public Double getRating() {
    return this.rating;
  }
  public void setRating(Double rating) {
    this.rating=rating;
  }
  public String[] getAttributes() {
    return this.attributes;
  }
  public void setAttributes(String[] attributes) {
    this.attributes = attributes;
  }
  public int[] getConvertedAttributes() {
    return this.convertedAttributes;
  }
  public void setConvertedAttributes(int[] convertedAttributes) {
    this.convertedAttributes = convertedAttributes;
  }
  @Override
  public int compareTo(WekaRecommendationItem item) {
    double compareRating = (double) item.rating;
    double comparison = (compareRating-(double) this.rating)*100000;
    return (int) comparison;
  }
}
