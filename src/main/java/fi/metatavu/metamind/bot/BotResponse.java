package fi.metatavu.metamind.bot;

import fi.metatavu.metamind.persistence.models.Intent;

/**
 * Match result from bot
 * 
 * @author Antti Leppä
 */
public class BotResponse {

  private double confidence;
  private Intent matchedIntent;

  /**
   * Constructor
   * 
   * @param confidence match confidence
   * @param matchedIntent mached intent
   */
  public BotResponse(double confidence, Intent matchedIntent) {
    super();
    this.confidence = confidence;
    this.matchedIntent = matchedIntent;
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

}
