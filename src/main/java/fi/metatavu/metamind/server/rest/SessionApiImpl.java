package fi.metatavu.metamind.server.rest;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import fi.metatavu.metamind.bot.BotController;
import fi.metatavu.metamind.persistence.models.Story;
import fi.metatavu.metamind.server.rest.model.Session;
import fi.metatavu.metamind.server.rest.translation.SessionTranslator;
import fi.metatavu.metamind.sessions.SessionConsts;
import fi.metatavu.metamind.sessions.SessionController;
import fi.metatavu.metamind.story.StoryController;

/**
 * REST - endpoints for sessions
 * 
 * @author Heikki Kurhinen
 *
 */
@Path("/sessions")
@Consumes({ "application/json;charset=utf-8" })
@Produces({ "application/json;charset=utf-8" })
@RequestScoped
@Stateful
public class SessionApiImpl extends AbstractRestApi implements SessionsApi {

  @Inject
  private SessionController sessionController;

  @Inject
  private BotController botController;

  @Inject
  private StoryController storyController;

  @Inject
  private SessionTranslator sessionTranslator;
  
  @Override
  public Response createSession(Session body) throws Exception {
    com.rabidgremlin.mutters.core.session.Session botSession = botController.createBotSession();
    if (botSession == null) {
      return respondInternalServerError("Could not initialize bot session");
    }
    
    Story story = storyController.findStoryByName(body.getStory());
    if (story == null) {
      return respondBadRequest("Invalid story parameter");
    }
    
    fi.metatavu.metamind.persistence.models.Session session = sessionController.createSession(story, body.getLocale(), body.getTimeZone(), body.getVisitor(), new byte[0]);
    if (session == null) {
      return respondInternalServerError("Could not initialize session");
    }
    
    botSession.setLongTermAttribute(SessionConsts.METAMIND_SESSION_ID_ATTRIBUTE, session.getId());
    
    byte[] sessionData = botController.serializeBotSession(botSession);
    if (sessionData == null) {
      return respondInternalServerError("Failed to serialize bot session");
    }
    
    sessionController.updateSessionState(session, sessionData);
    
    return respondOk(sessionTranslator.translateSession(session));
  }

}
