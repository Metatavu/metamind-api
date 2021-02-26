package fi.metatavu.metamind.bot.match;

import java.util.UUID;

/**
 * Intent match
 * 
 * @author Antti Leppä
 */
public class IntentMatch {
  
  private UUID intentId;
  private double score;
  
  /**
   * Constructor
   * 
   * @param intentId intent id
   * @param score score
   */
  public IntentMatch(UUID intentId, double score) {
    super();
    this.intentId = intentId;
    this.score = score;
  }
  
  public UUID getIntentId() {
    return intentId;
  }
  
  public double getScore() {
    return score;
  }

}
