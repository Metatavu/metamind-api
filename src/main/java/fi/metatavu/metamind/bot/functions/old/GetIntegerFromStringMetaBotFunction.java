package fi.metatavu.metamind.bot.functions.old;

import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.bladecoder.ink.runtime.Story;
import com.rabidgremlin.mutters.bot.ink.CurrentResponse;
import com.rabidgremlin.mutters.core.IntentMatch;
import com.rabidgremlin.mutters.core.session.Session;

import org.slf4j.Logger;

/**
 * Metabot function to find integers from string
 * 
 * @author Ville Koivukangas
 */
@ApplicationScoped
public class GetIntegerFromStringMetaBotFunction extends AbstractMetaBotFunction {
  @Inject
  private Logger logger;

  @Override
  public String getFunctionName() {
    return "GET_INTEGER_FROM_STRING";
  }

  @Override
  public void execute(CurrentResponse currentResponse, Session session, IntentMatch intentMatch, Story story, String param) {
    Map<String, String> functionParams = getParsedParam(param);
    
    String stringParam = functionParams.get("string");
    String targetVariableName = functionParams.get("target");
    int result = Integer.parseInt(stringParam.replaceAll("\\D+",""));
    
    try {
      story.getVariablesState().set(targetVariableName, result);
    } catch (Exception e) {
      logger.error("Error while getting integer from string", e);
    }
  }
}
