package fi.metatavu.metamind.bot.script;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * Bot script binding for services
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class BotServicesScriptBinding {
  
  @Inject
  private BotEmailServiceScriptBinding email;
  
  @Inject
  private BotNgramServiceScriptBinding ngram;
  
  @Inject
  private BotWekaServiceScriptBinding weka;

  /**
   * Returns email service
   * 
   * @return email service
   */
  public BotEmailServiceScriptBinding email() {
    return email;
  }
  
  /**
   * Returns ngram services
   * 
   * @return ngram services
   */
  public BotNgramServiceScriptBinding ngram() {
    return ngram;
  }
  
  /**
   * Returns weka services
   * 
   * @return weka services
   */
  public BotWekaServiceScriptBinding weka() {
    return weka;
  }

}
