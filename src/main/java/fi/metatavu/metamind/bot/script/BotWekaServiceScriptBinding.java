package fi.metatavu.metamind.bot.script;

import java.io.IOException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

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
	public String[] getRecommendations(Object[] items) throws JsonParseException, JsonMappingException, JsonProcessingException, IOException {
	  ObjectMapper objectMapper = new ObjectMapper();  

	  System.out.println(objectMapper.writeValueAsString(items));

    return getWekaRecommendations(objectMapper.readValue(objectMapper.writeValueAsBytes(items), WekaRecommendationItem[].class));
	}

  private String[] getWekaRecommendations(WekaRecommendationItem[] items) {
    return wekaController.getRecommendations(items);
  }
}
