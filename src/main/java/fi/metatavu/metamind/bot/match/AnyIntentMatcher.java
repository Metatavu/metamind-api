package fi.metatavu.metamind.bot.match;

import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.RandomUtils;

/**
 * Intent matcher that matches random intent in given set of intents
 * 
 * @author Antti Leppä
 */
public class AnyIntentMatcher implements IntentMatcher {
  
  private List<UUID> intentIds;

  /**
   * Constructor
   * 
   * @param intentIds intent ids
   */
  public AnyIntentMatcher(List<UUID> intentIds) {
    super();
    this.intentIds = intentIds;
  }
  
  @Override
  public IntentMatch matchIntents(String text) {
    return new IntentMatch(intentIds.get(RandomUtils.nextInt(0, intentIds.size())), 1d);
  }


}
