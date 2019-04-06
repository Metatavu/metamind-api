package fi.metatavu.metamind.bot.script;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.metamind.bot.BotRuntimeContext;
import fi.metatavu.metamind.persistence.models.Knot;
import fi.metatavu.metamind.persistence.models.Session;
import fi.metatavu.metamind.sessions.SessionController;

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

}
