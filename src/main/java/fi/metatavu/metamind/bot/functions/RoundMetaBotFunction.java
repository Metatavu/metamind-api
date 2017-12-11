package fi.metatavu.metamind.bot.functions;

import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;

import com.bladecoder.ink.runtime.Story;
import com.rabidgremlin.mutters.bot.ink.CurrentResponse;
import com.rabidgremlin.mutters.core.IntentMatch;
import com.rabidgremlin.mutters.core.session.Session;

/**
 * Metabot function for rounding numbers.
 * 
 * @author Antti Leppä
 * @author Heikki Kurhinen
 */
@ApplicationScoped
public class RoundMetaBotFunction extends AbstractMetaBotFunction {

  @Inject
  private Logger logger;

  @Override
  public String getFunctionName() {
    return "ROUND";
  }
  
  @Override
  public void execute(CurrentResponse currentResponse, Session session, IntentMatch intentMatch, Story story, String paramString) {
    Map<String, String> params = getParsedParam(paramString);
    
    String variableName = params.get("variable");
    String nearValue = params.get("near");
    if (variableName == null) {
      logger.error("Could not capitalize without variable name");
      return;
    }
    
    Integer near = null;
    if (nearValue != null) {
      try {
        near = Integer.parseInt(nearValue);
      } catch (NumberFormatException e) {
        logger.warn("Failed to parse near value", e);
      }
    }
    
    Float value = getVariableNumber(story, variableName);
    if (value != null) {
      Integer rounded = null;
      
      if (near != null) {
        rounded = roundNear(value, near);
      } else {
        rounded = Math.round(value);
      }
      
      try {
        setVariable(story, variableName, rounded);
      } catch (Exception e) {
        logger.error("Could not set variable state", e);
        return;
      }
    }
  }

  private int roundNear(float value, int near) {
    return Math.round((value / near)) * near;
  }

}
