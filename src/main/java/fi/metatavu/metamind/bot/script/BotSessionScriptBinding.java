package fi.metatavu.metamind.bot.script;

import fi.metatavu.metamind.bot.BotRuntimeContext;
import fi.metatavu.metamind.persistence.models.Knot;
import fi.metatavu.metamind.persistence.models.Session;
import fi.metatavu.metamind.persistence.models.Story;
import fi.metatavu.metamind.persistence.models.Variable;
import fi.metatavu.metamind.sessions.SessionController;
import fi.metatavu.metamind.story.StoryController;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Map;
import java.util.UUID;

/**
 * Bot script binding for session related operations
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class BotSessionScriptBinding {

  @Inject
  private SessionController sessionController; 

  @Inject
  private StoryController storyController;

  @Inject
  private BotRuntimeContext runtimeContext;

  /**
   * Navigates back in session history and returns knot in given time.
   * 
   * Method returns null if the start of the story has been reached
   * 
   * @param steps how many steps to navigate back
   * @return knot in given time or null if start of the story has been reached
   */
  public Knot getPreviousKnot(Integer steps) {
    Session session = runtimeContext.getSession();
    return sessionController.getPreviousKnot(session, steps);
  }
  
  /**
   * Changes current knot where the bot is going to be after the message
   * 
   * @param knot knot
   */
  public void setCurrentKnot(Knot knot) {
    runtimeContext.setCurrentKnot(knot);
  }

  /**
   * Prepends new response text before initial text to bot response
   * 
   * @param response response text
   */
  public void prependResponse(String response) {
    runtimeContext.prependResponse(response);
  }
  
  /**
   * Appends new response text after initial text to bot response
   * 
   * @param response response text
   */
  public void appendResponse(String response) {
    runtimeContext.appendResponse(response);
  }
  
  /**
   * Returns current message's content
   * 
   * @return current message's content
   */
  public String getCurrentMessageContent() {
    return runtimeContext.getCurrentMessageContent();
  }
  
  /**
   * Returns session variable value
   * 
   * @param variableName variable name
   * @return value or null if not found
   */
  public Object getVariableValue(String variableName) {
    Session session = runtimeContext.getSession();
    Story story = session.getStory();
    Variable variable = storyController.findVariableByStoryAndName(story, variableName);
    
    if (variable == null) {
      return null;
    }
    
    Map<UUID, String> variableValues = runtimeContext.getVariableValues();
    
    String value = variableValues.get(variable.getId());
    if (value == null) {
      value = sessionController.getSessionVariableValue(session, variable);
    }
    
    if (StringUtils.isNotBlank(value)) {
      switch (variable.getType()) {
        case NUMBER:
          return NumberUtils.createDouble(value);
        case STRING:
          return value;
      }
    }
    
    return null;
  }
  
  /**
   * Sets session variable value
   * 
   * @param variableName variable name
   * @param value value
   */
  public void setVariableValue(String variableName, Object value) {
    Session session = runtimeContext.getSession();
    Story story = session.getStory();
    Variable variable = storyController.findVariableByStoryAndName(story, variableName);
    
    if (variable == null) {
      return;
    }
    
    runtimeContext.setVariableValue(variable.getId(), value);
  }
  
}
