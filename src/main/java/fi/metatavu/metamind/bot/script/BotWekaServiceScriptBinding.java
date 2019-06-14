package fi.metatavu.metamind.bot.script;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.io.IOException;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.graalvm.polyglot.Value;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fi.metatavu.metamind.weka.WekaController;
import fi.metatavu.metamind.weka.WekaRecommendationItem;

/**
 * Bot script binding for Weka service
 * 
 * @author Simeon Platonov
 */
@ApplicationScoped
public class BotWekaServiceScriptBinding {

  @Inject
  private WekaController wekaController;
	
  /**
   * Returns item iDs for unrated items, sorted by the recommendation algorithm
   * 
   * @param items items
   * @return recommendations
   * @throws IOException 
   * @throws JsonProcessingException 
   * @throws JsonMappingException 
   * @throws JsonParseException 
   */

  public String[] getRecommendations(Value[] items) throws JsonParseException, JsonMappingException, JsonProcessingException, IOException {
    WekaRecommendationItem[] wekaItems = new WekaRecommendationItem[items.length];
	  
    wekaItems = Arrays.stream(items)
    .map(this::translateWekaRecommendationItem)
    .filter(Objects::nonNull)
    .toArray(WekaRecommendationItem[]::new);
	  
    String[] recommendations = wekaController.getRecommendations(wekaItems);
    return recommendations;
  }
	
  private WekaRecommendationItem translateWekaRecommendationItem(Value item) {
    if( !item.hasMember("id") || !item.hasMember("attributes") || item.getMember("id").isNull() || item.getMember("attributes").isNull() ) {
      return null;
    }
    String id = item.getMember("id").asString();
    Value attributes = item.getMember("attributes");
    if(!attributes.hasArrayElements()){
      return null;
    }
    int size = (int) attributes.getArraySize();
    String[] attrs = new String[size];
    for(int i = 0; i < size; i++) {
      attrs[i] = attributes.getArrayElement(i).asString();
    }
    Value ratingValue = item.getMember("rating");
    Double rating = ratingValue.isNumber()?ratingValue.asDouble():null;
    WekaRecommendationItem wekaItem = new WekaRecommendationItem();
    wekaItem.setId(id);
    wekaItem.setAttributes(attrs);
    wekaItem.setRating(rating);
    return wekaItem;
  }

}
