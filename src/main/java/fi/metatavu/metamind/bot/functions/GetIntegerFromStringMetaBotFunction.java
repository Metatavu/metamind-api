package fi.metatavu.metamind.bot.functions;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;

import com.bladecoder.ink.runtime.Story;
import com.rabidgremlin.mutters.bot.ink.CurrentResponse;
import com.rabidgremlin.mutters.bot.ink.functions.FunctionDetails;
import com.rabidgremlin.mutters.bot.ink.functions.FunctionHelper;
import com.rabidgremlin.mutters.core.IntentMatch;
import com.rabidgremlin.mutters.core.session.Session;

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
  public void execute(CurrentResponse currentResponse, Session session, IntentMatch intentMatch, Story story,
      String param) {
   
   FunctionDetails details = FunctionHelper.parseFunctionString(param);
   
   if (details == null) {
     logger.error("Function parameters is null at GET_INTEGER_FROM_STRING");
     return;
   }
   
   String stringParam = details.getFunctionParams().get("string");
   String variableName = details.getFunctionParams().get("var");
   int weightInt = Integer.parseInt(stringParam.replaceAll("\\D+",""));
   
   try {
     story.getVariablesState().set(variableName, weightInt);
   } catch (Exception e) {
     logger.error("Error while setting variables at KUNTA_API_GET_EVENTS", e);
   }

  }
}
