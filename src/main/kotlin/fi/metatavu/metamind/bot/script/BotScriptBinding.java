package fi.metatavu.metamind.bot.script;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * Bot script bindings
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class BotScriptBinding {
  
  @Inject
  private BotSessionScriptBinding session;

  @Inject
  private BotStoryScriptBinding story;

  @Inject
  private BotServicesScriptBinding services;
  
  /**
   * Returns session bindings
   * 
   * @return session bindings
   */
  public BotSessionScriptBinding session() {
    return session;
  }

  /**
   * Returns story bindings
   * 
   * @return story bindings
   */
  public BotStoryScriptBinding story() {
    return story;
  }
  
  /**
   * Returns services bindings
   * 
   * @return services bindings
   */
  public BotServicesScriptBinding services() {
    return services;
  }
  
}
