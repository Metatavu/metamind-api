package fi.metatavu.metamind.bot.functions;

import java.util.Random;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;

import com.bladecoder.ink.runtime.Story;
import com.rabidgremlin.mutters.bot.ink.CurrentResponse;
import com.rabidgremlin.mutters.bot.ink.functions.FunctionDetails;
import com.rabidgremlin.mutters.bot.ink.functions.FunctionHelper;
import com.rabidgremlin.mutters.core.IntentMatch;
import com.rabidgremlin.mutters.core.session.Session;

/**
 * Metabot function to get random number
 * 
 * @author Ville Koivukangas
 */
@ApplicationScoped
public class GetRandomNumberMetaBotFunction extends AbstractMetaBotFunction {
  @Inject
  private Logger logger;

  @Override
  public String getFunctionName() {
    return "GET_RANDOM_NUMBER";
  }

  @Override
  public void execute(CurrentResponse currentResponse, Session session, IntentMatch intentMatch, Story story,
      String param) {
    FunctionDetails details = FunctionHelper.parseFunctionString(param);
    String scaleString = details.getFunctionParams().get("scale");
    String var = details.getFunctionParams().get("var");

    int scale = NumberUtils.toInt(scaleString);

    Random random = new Random();
    int rand = random.nextInt(scale);

    try {
      story.getVariablesState().set(var, rand);
    } catch (Exception e) {
      logger.error("Failed to set variable at KUNTA_API_GET_RANDOM_NUMBER", e);
    }
  }

}
