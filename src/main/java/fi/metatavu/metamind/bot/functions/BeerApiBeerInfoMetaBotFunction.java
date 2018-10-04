package fi.metatavu.metamind.bot.functions;

import java.text.NumberFormat;
import java.util.Map;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.bladecoder.ink.runtime.Story;
import com.rabidgremlin.mutters.bot.ink.CurrentResponse;
import com.rabidgremlin.mutters.core.IntentMatch;
import com.rabidgremlin.mutters.core.session.Session;

import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;

import fi.metatavu.beer.ApiClient;
import fi.metatavu.beer.client.BarBeersApi;
import fi.metatavu.beer.client.Beer;
import fi.metatavu.beer.client.BeerImage;
import fi.metatavu.beer.client.BeerRating;
import fi.metatavu.metamind.story.StoryController;

/**
 * Metabot function to get single beer info from Beer API
 * 
 * @author Heikki Kurhinen
 */
@ApplicationScoped
@SuppressWarnings ("squid:S00107")
public class BeerApiBeerInfoMetaBotFunction extends AbstractMetaBotFunction {

  @Inject
  private Logger logger;

  @Inject
  private StoryController storyController;

  @Override
  public String getFunctionName() {
    return "BEER_API_BEER_INFO";
  }

  @Override
  public void execute(CurrentResponse currentResponse, Session session, IntentMatch intentMatch, Story story, String param) {
    Map<String, String> functionParams = getParsedParam(param);
    String barId = functionParams.get("barId");
    String beerId = functionParams.get("beerId");

    if (barId == null || beerId == null) {
      logger.error("Cannot get beer info without bar and beer id");
      return;
    }

    String nameVariable = functionParams.get("name");
    String descriptionVariable = functionParams.get("description");
    String abvVariable = functionParams.get("abv");
    String ibuVariable = functionParams.get("ibu");
    String styleVariable = functionParams.get("style");
    String breweryVariable = functionParams.get("brewery");
    String caloriesVariable = functionParams.get("calories");
    String flavorsVariable = functionParams.get("flavors");
    String imagesVariable = functionParams.get("images");
    String untappdRatingVariable = functionParams.get("untappdRating");
    String rateBeerOverallScoreVariable = functionParams.get("rateBeerOverallScore");
    String rateBeerStyleScoreVariable = functionParams.get("rateBeerStyleScore");

    getBeerInfo(session, story, barId, beerId, nameVariable, descriptionVariable, abvVariable, ibuVariable, styleVariable, breweryVariable, caloriesVariable,
        flavorsVariable, imagesVariable, untappdRatingVariable, rateBeerOverallScoreVariable, rateBeerStyleScoreVariable);
  }

  private void getBeerInfo(Session session, Story story, String barId, String beerId, String nameVariable, String descriptionVariable, String abvVariable,
      String ibuVariable, String styleVariable, String breweryVariable, String caloriesVariable, String flavorsVariable, String imagesVariable,
      String untappdRatingVariable, String rateBeerOverallScoreVariable, String rateBeerStyleScoreVariable) {
    try {
      ApiClient apiClient = getApiClient(getMetamindStory(session));
      BarBeersApi barBeersApi = apiClient.buildClient(BarBeersApi.class);
      Beer beer = barBeersApi.findBeer(barId, NumberUtils.createLong(beerId));
      if (beer == null) {
        logger.error("Cannot find beer with barId {} and beerId {}", barId, beerId);
        return;
      }

      printBeerInfo(story, nameVariable, descriptionVariable, abvVariable, ibuVariable, styleVariable, breweryVariable, caloriesVariable, flavorsVariable,
          imagesVariable, untappdRatingVariable, rateBeerOverallScoreVariable, rateBeerStyleScoreVariable, beer);
    } catch (Exception e) {
      logger.error("Could not set variable states", e);
    }
  }

  private void printBeerInfo(Story story, String nameVariable, String descriptionVariable, String abvVariable, String ibuVariable, String styleVariable,
      String breweryVariable, String caloriesVariable, String flavorsVariable, String imagesVariable, String untappdRatingVariable,
      String rateBeerOverallScoreVariable, String rateBeerStyleScoreVariable, Beer beer) throws Exception {
    if (nameVariable != null && beer.getName() != null) {
      story.getVariablesState().set(nameVariable, beer.getName());
    }

    if (descriptionVariable != null && beer.getDescription() != null) {
      story.getVariablesState().set(descriptionVariable, beer.getDescription());
    }

    if (abvVariable != null && beer.getAbv() != null) {
      story.getVariablesState().set(abvVariable, beer.getAbv());
    }

    if (ibuVariable != null && beer.getIbu() != null) {
      story.getVariablesState().set(ibuVariable, formatDouble(beer.getIbu()));
    }

    if (styleVariable != null && beer.getStyle() != null) {
      story.getVariablesState().set(styleVariable, beer.getStyle());
    }

    if (breweryVariable != null && beer.getBrewery() != null) {
      story.getVariablesState().set(breweryVariable, beer.getBrewery());
    }

    if (caloriesVariable != null && beer.getCalories() != null) {
      story.getVariablesState().set(caloriesVariable, beer.getCalories());
    }

    if (flavorsVariable != null && beer.getFlavors() != null && !beer.getFlavors().isEmpty()) {
      story.getVariablesState().set(flavorsVariable, String.join(", ", beer.getFlavors()));
    }

    if (imagesVariable != null && beer.getImages() != null && !beer.getImages().isEmpty()) {
      story.getVariablesState().set(imagesVariable, beer.getImages().stream().map(BeerImage::getMedium).collect(Collectors.joining("|")));
    }

    if (untappdRatingVariable != null && beer.getUntappdRating() != null) {
      story.getVariablesState().set(untappdRatingVariable, formatBeerRating(beer.getUntappdRating()));
    }

    if (rateBeerOverallScoreVariable != null && beer.getRateBeerOverallScore() != null) {
      story.getVariablesState().set(rateBeerOverallScoreVariable, formatBeerRating(beer.getRateBeerOverallScore()));
    }

    if (rateBeerStyleScoreVariable != null && beer.getRateBeerStyleScore() != null) {
      story.getVariablesState().set(rateBeerStyleScoreVariable, formatBeerRating(beer.getRateBeerStyleScore()));
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
   * Formats the beer rating in rating / scale format
   * 
   * @param rating Beer rating to format
   * @return Formatted beer rating
   */
  private String formatBeerRating(BeerRating rating) {
    return String.format("%d / %d", Math.round(rating.getRate()), Math.round(rating.getScale()));
  }

  /**
   * Formats double as string
   * 
   * @param value value
   * @return formatted value
   */
  private String formatDouble(Double value) {
    if (value == null) {
      return "";
    }
    return NumberFormat.getInstance().format(value);
  }
}
