package fi.metatavu.metamind.bot.script;

<<<<<<< HEAD


import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
=======
import java.io.IOException;
>>>>>>> 0ea4ecda4ae229ca6ecb6f167b2cd29183144f42

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

<<<<<<< HEAD
import org.graalvm.polyglot.Value;
import org.json.JSONObject;

=======
>>>>>>> 0ea4ecda4ae229ca6ecb6f167b2cd29183144f42
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
<<<<<<< HEAD
	public String[] getRecommendations(Value[] items) throws JsonParseException, JsonMappingException, JsonProcessingException, IOException {
	  WekaRecommendationItem[] wekaItems = new WekaRecommendationItem[items.length];
	  //int i=0;
	  //for(Value item:items) {
	  //  wekaItems[i] = translateWekaRecommendationItem(item);
	  //  i++;
	  //}
	  
	  wekaItems = Arrays.stream(items)
	      .map(this::translateWekaRecommendationItem)
	      .filter(Objects::nonNull)
	      .toArray(WekaRecommendationItem[]::new);
	  
	  String[] recommendations = wekaController.getRecommendations(wekaItems);
	  return recommendations;
	}
	
	private WekaRecommendationItem translateWekaRecommendationItem(Value item) {
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
	  Double rating = ratingValue.fitsInDouble()?ratingValue.asDouble():null;
	  WekaRecommendationItem wekaItem = new WekaRecommendationItem();
	  wekaItem.setId(id);
	  wekaItem.setAttributes(attrs);
	  wekaItem.setRating(rating);
	  return wekaItem;
	  
	}

  
=======
	public String[] getRecommendations(Object[] items) throws JsonParseException, JsonMappingException, JsonProcessingException, IOException {
	  ObjectMapper objectMapper = new ObjectMapper();  

	  System.out.println(objectMapper.writeValueAsString(items));

    return getWekaRecommendations(objectMapper.readValue(objectMapper.writeValueAsBytes(items), WekaRecommendationItem[].class));
	}

  private String[] getWekaRecommendations(WekaRecommendationItem[] items) {
    return wekaController.getRecommendations(items);
  }
>>>>>>> 0ea4ecda4ae229ca6ecb6f167b2cd29183144f42
}
