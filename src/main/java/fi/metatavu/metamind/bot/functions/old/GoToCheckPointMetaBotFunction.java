package fi.metatavu.metamind.bot.functions.old;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;

import com.bladecoder.ink.runtime.Story;
import com.rabidgremlin.mutters.bot.ink.CurrentResponse;
import com.rabidgremlin.mutters.core.IntentMatch;
import com.rabidgremlin.mutters.core.session.Session;

/**
 * Function to divert story back to stored checkpoint
 * 
 * @author Heikki Kurhinen
 */
@ApplicationScoped
public class GoToCheckPointMetaBotFunction extends AbstractMetaBotFunction {
  
  @Inject
  private Logger logger;
  
  @Override
  public String getFunctionName() {
    return "GO_TO_CHECKPOINT";
  }

  @Override
  public void execute(CurrentResponse currentResponse, Session botSession, IntentMatch intentMatch, Story story,
      String param) {
    
    String checkpointKnotName = (String) botSession.getLongTermAttribute("CHECKPOINT");
    if (checkpointKnotName == null) {
      logger.error("Checkpoint not defined");
      return;
    }
    
    try {
      story.choosePathString(checkpointKnotName);
    } catch (Exception e) {
      logger.error(String.format("Error diverting to checkpoint %s", checkpointKnotName), e);
    }
    
  }

}
