package fi.metatavu.metamind.bot.script;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.metamind.persistence.models.Knot;

/**
 * Bot script binding for story related operations
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class BotStoryScriptBinding {
  
  @Inject
  private ScriptProcessor scriptProcessor;

  /**
   * Returns knot content with scripts processed
   * 
   * @param knot knot
   * @return knot content with scripts processed
   */
  public String getKnotContent(Knot knot) {
    if (knot == null) {
      return "";
    }
    
    return scriptProcessor.processScripts(knot.getContent());
  }

}
