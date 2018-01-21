package fi.metatavu.metamind.bot.functions;

import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;

import com.bladecoder.ink.runtime.Story;
import com.rabidgremlin.mutters.bot.ink.CurrentResponse;
import com.rabidgremlin.mutters.core.IntentMatch;
import com.rabidgremlin.mutters.core.session.Session;

import fi.metatavu.metamind.story.StoryController;

/**
 * Metabot function for getting story global variables.
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class GetStoryGlobalVariableBotFunction extends AbstractMetaBotFunction {

  @Inject
  private Logger logger;

  @Inject
  private StoryController storyController;

  @Override
  public String getFunctionName() {
    return "GET_STORY_GLOBAL_VARIABLE";
  }
  
  @Override
  public void execute(CurrentResponse currentResponse, Session botSession, IntentMatch intentMatch, Story botStory, String paramString) {
    Map<String, String> params = getParsedParam(paramString);
    
    String variableName = params.get("variable");
    if (variableName == null) {
      logger.error("Missing required parameter 'variable'");
      return;
    }

    String targetVariable = params.get("target");
    if (targetVariable == null) {
      logger.error("Missing required parameter 'target'");
      return;
    }
    
    fi.metatavu.metamind.persistence.models.Story metamindStory = getMetamindStory(botSession);
    if (metamindStory == null) {
      logger.error("Failed to resolve Metamind story for the session");
      return;
    }
    
    String value = storyController.getGlobalStoryVariable(metamindStory, variableName);
    
    try {
      setVariable(botStory, targetVariable, value);
    } catch (Exception e) {
      logger.error("Could not set variable state", e);
      return;
    }
  }

}
