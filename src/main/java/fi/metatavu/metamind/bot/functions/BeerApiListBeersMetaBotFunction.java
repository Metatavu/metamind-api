package fi.metatavu.metamind.bot.functions;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;

import com.bladecoder.ink.runtime.Story;
import com.rabidgremlin.mutters.bot.ink.CurrentResponse;
import com.rabidgremlin.mutters.core.IntentMatch;
import com.rabidgremlin.mutters.core.session.Session;

import fi.metatavu.beer.ApiClient;
import fi.metatavu.beer.client.BarBeersApi;
import fi.metatavu.beer.client.BarBeersApi.ListBeersQueryParams;
import fi.metatavu.beer.client.Beer;
import fi.metatavu.metamind.story.StoryController;

/**
 * Metabot function to list beers from Beer API
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class BeerApiListBeersMetaBotFunction extends AbstractMetaBotFunction {

  @Inject
  private Logger logger;

  @Inject
  private StoryController storyController;

  @Override
  public String getFunctionName() {
    return "BEER_API_LIST_BEERS";
  }

  @Override
  public void execute(CurrentResponse currentResponse, Session session, IntentMatch intentMatch, Story story, String param) {
    Map<String, String> functionParams = getParsedParam(param);
    String variableName = functionParams.get("variable");
    String barId = functionParams.get("barId");
    String includeFlavors = getCommaDelimetedVariable(story, functionParams.get("includeFlavors"));
    String excludeFlavors = getCommaDelimetedVariable(story, functionParams.get("excludeFlavors"));
    String preferFlavors = getCommaDelimetedVariable(story, functionParams.get("preferFlavors"));

    Float minAbv = NumberUtils.createFloat(functionParams.get("minAbv"));
    Float maxAbv = NumberUtils.createFloat(functionParams.get("maxAbv"));
    Long firstResult = NumberUtils.createLong(functionParams.get("firstResult"));
    Long maxResults = NumberUtils.createLong(functionParams.get("maxResults"));
    
    ApiClient apiClient = getApiClient(getMetamindStory(session));
    BarBeersApi barBeersApi = apiClient.buildClient(BarBeersApi.class);
    ListBeersQueryParams params = new ListBeersQueryParams();

    if (excludeFlavors != null) {
      params.excludeFlavors(excludeFlavors);
    }

    if (includeFlavors != null) {
      params.excludeFlavors(includeFlavors);
    }

    if (preferFlavors != null) {
      params.excludeFlavors(preferFlavors);
    }
    
    if (maxAbv != null) {
      params.maxAbv(maxAbv);
    }
    
    if (minAbv != null) {
      params.minAbv(minAbv);
    }
    
    if (firstResult != null) {
      params.firstResult(firstResult);
    }
    
    if (maxResults != null) {
      params.maxResults(maxResults);
    }
    
    List<Beer> beers = barBeersApi.listBeers(barId, params);
    
    try {
      story.getVariablesState().set(variableName, beers.stream().map(beer -> String.valueOf(beer.getId())).collect(Collectors.joining("|")));
    } catch (Exception e) {
      logger.error("Could not set variable state", e);
      return;
    }
  }
  
  /**
   * Returns Unauthorized API client for Beer API
   * @param metamindStory 
   * 
   * @return Unauthorized API client for Beer API
   */
  private ApiClient getApiClient(fi.metatavu.metamind.persistence.models.Story metamindStory) {
    ApiClient apiClient = new ApiClient();

    String host = storyController.getGlobalStoryVariable(metamindStory, "beer-api-host");
    Integer port = NumberUtils.createInteger(storyController.getGlobalStoryVariable(metamindStory, "beer-api-port"));

    String basePath = String.format("%s:%d/rest/v1", host, port);
    apiClient.setBasePath(basePath);
    return apiClient;
  }

  /**
   * Returns trimmed value for a comma delimited variable
   * 
   * @param story story
   * @param variableName variableName
   * @return trimmed value for a comma delimited variable
   */
  private String getCommaDelimetedVariable(Story story, String variableName) {
    if (variableName == null) {
      return null;
    }

    String result = getVariableString(story, variableName);
    return StringUtils.trimToNull(StringUtils.stripEnd(StringUtils.stripStart(result, ","), ","));
  }
}
