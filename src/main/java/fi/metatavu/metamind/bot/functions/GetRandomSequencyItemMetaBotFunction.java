package fi.metatavu.metamind.bot.functions;

import java.util.Map;
import java.util.Random;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.bladecoder.ink.runtime.Story;
import com.rabidgremlin.mutters.bot.ink.CurrentResponse;
import com.rabidgremlin.mutters.core.IntentMatch;
import com.rabidgremlin.mutters.core.session.Session;

import org.slf4j.Logger;

/**
 * Metabot function to get random item from sequency string
 * 
 * @author Heikki Kurhinen
 */
@ApplicationScoped
public class GetRandomSequencyItemMetaBotFunction extends AbstractMetaBotFunction {
  @Inject
  private Logger logger;

  @Override
  public String getFunctionName() {
    return "GET_RANDOM_ITEM";
  }

  @Override
  public void execute(CurrentResponse currentResponse, Session session, IntentMatch intentMatch, Story story,
      String paramString) {

    Map<String, String> params = getParsedParam(paramString);
    String sourceVariableName = params.get("source");
    String targetVariableName = params.get("target");

    if (sourceVariableName == null || targetVariableName == null) {
      logger.error("Could not get random item without source and target variable names");
      return;
    }

    String sequencyString = getVariableString(story, sourceVariableName);
    if (sequencyString == null) {
      logger.error("Source variable has no value");
      return;
    }

    String[] items = sequencyString.split("\\|");
    String item = items[new Random().nextInt(items.length)];

    try {
      story.getVariablesState().set(targetVariableName, item);
    } catch (Exception e) {
      logger.error("Failed to get random item from sequency", e);
    }
  }

}
