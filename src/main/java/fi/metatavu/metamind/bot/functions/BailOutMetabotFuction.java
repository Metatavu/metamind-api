package fi.metatavu.metamind.bot.functions;

import com.bladecoder.ink.runtime.Story;
import com.rabidgremlin.mutters.bot.ink.CurrentResponse;
import com.rabidgremlin.mutters.core.IntentMatch;
import com.rabidgremlin.mutters.core.session.Session;

/**
 * Metabot function reset story after bot getting confused so it wont return to the story anymore
 * 
 * @author Heikki Kurhinen
 *
 */
public class BailOutMetabotFuction extends AbstractMetaBotFunction {
  
  @Override
  public String getFunctionName() {
    return "BAIL_OUT";
  }

  @Override
  public void execute(CurrentResponse currentResponse, Session session, IntentMatch intentMatch, Story story,
      String param) {

    session.reset();
    currentResponse.setAskResponse(false);
  }

}
