package fi.metatavu.metamind.bot.functions.old;

import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;

import com.bladecoder.ink.runtime.Story;
import com.rabidgremlin.mutters.bot.ink.CurrentResponse;
import com.rabidgremlin.mutters.core.IntentMatch;
import com.rabidgremlin.mutters.core.session.Session;

/**
 * Function to save knot so it can be returned later with go to checkpoint function
 * 
 * @author Heikki Kurhinen
 */
@ApplicationScoped
public class SaveCheckpointMetaBotFunction extends AbstractMetaBotFunction {

  @Inject
  private Logger logger;
  
  @Override
  public String getFunctionName() {
    return "SAVE_CHECKPOINT";
  }

  @Override
  public void execute(CurrentResponse currentResponse, Session session, IntentMatch intentMatch, Story story,
      String param) {

    Map<String, String> parsedParams = getParsedParam(param);
    
    
    if (!parsedParams.containsKey("knotname")) {
      logger.error("Cannot save checkpoint without knotname");
      return;
    }
    
    String knotName = parsedParams.get("knotname");
    session.setLongTermAttribute("CHECKPOINT", knotName); 
  }

}
