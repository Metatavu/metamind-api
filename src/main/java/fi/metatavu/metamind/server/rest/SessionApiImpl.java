package fi.metatavu.metamind.server.rest;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import fi.metatavu.metamind.bot.BotController;
import fi.metatavu.metamind.server.rest.model.Session;
import fi.metatavu.metamind.server.rest.translation.SessionTranslator;
import fi.metatavu.metamind.sessions.SessionController;

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
  private SessionTranslator sessionTranslator;

  @Override
  public Response createSession(Session body) throws Exception {
    com.rabidgremlin.mutters.core.session.Session botSession = botController.createBotSession();
    if (botSession == null) {
      return respondInternalServerError("Could not initialize bot session");
    }
    
    byte[] sessionData = botController.serializeBotSession(botSession);
    if (sessionData == null) {
      return respondInternalServerError("Failed to serialize bot session");
    }
    
    fi.metatavu.metamind.persistence.models.Session session = sessionController.createSession(body.getLocale(), body.getVisitor(), sessionData);
    if (session == null) {
      return respondInternalServerError("Could not initialize session");
    }
    
    return respondOk(sessionTranslator.translateSession(session));
  }

}
