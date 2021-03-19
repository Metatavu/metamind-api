package fi.metatavu.metamind.bot;

import fi.metatavu.metamind.persistence.models.Intent;

import java.util.Map;
import java.util.UUID;

/**
 * Match result from bot
 * 
 * @author Antti Leppä
 */
public class BotResponse {

  private double confidence;
  private Intent matchedIntent;
  private Map<UUID, String> variableValues;

  /**
   * Constructor
   * 
   * @param confidence match confidence
   * @param matchedIntent mached intent
   */
  public BotResponse(double confidence, Intent matchedIntent, Map<UUID, String> variableValues) {
    super();
    this.confidence = confidence;
    this.matchedIntent = matchedIntent;
    this.variableValues = variableValues;
  }

  public void setConfidence(double confidence) {
    this.confidence = confidence;
  }

  public double getConfidence() {
    return confidence;
  }

  public void setMatchedIntent(Intent matchedIntent) {
    this.matchedIntent = matchedIntent;
  }

  public Intent getMatchedIntent() {
    return matchedIntent;
  }
  
  public Map<UUID, String> getVariableValues() {
    return variableValues;
  }

}
