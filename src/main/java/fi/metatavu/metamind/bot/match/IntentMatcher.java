package fi.metatavu.metamind.bot.match;

/**
 * Interface that describes single intent matcher
 * 
 * @author Antti Leppä
 */
public interface IntentMatcher {

  /**
   * Match intents for given text
   * 
   * @param text text
   * @return intent match
   */
  public IntentMatch matchIntents(String text);

}
