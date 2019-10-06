package fi.metatavu.metamind.bot.script;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import fi.metatavu.metamind.bot.BotRuntimeContext;
import fi.metatavu.metamind.persistence.models.Knot;
import fi.metatavu.metamind.persistence.models.Story;
import fi.metatavu.metamind.story.StoryController;

/**
 * Bot script binding for story related operations
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class BotStoryScriptBinding {

  @Inject
  private BotRuntimeContext botRuntimeContext;
  
  @Inject
  private ScriptProcessor scriptProcessor;
  
  @Inject
  private StoryController storyController;

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
  
  /**
   * Search knots by name
   * 
   * @param nameLike like query for knot names
   * @return matching knots
   */
  public Knot[] searchKnotsByName(String nameLike) {
    if (StringUtils.isBlank(nameLike)) {
      return new Knot[0];
    }
    
    Story story = botRuntimeContext.getSession().getStory();
    
    return storyController.listKnots(story, nameLike).toArray(new Knot[0]);
  }
  
}
