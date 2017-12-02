package fi.metatavu.metamind.sessions;

import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.metamind.persistence.dao.SessionDAO;
import fi.metatavu.metamind.persistence.models.Session;

@ApplicationScoped
public class SessionController {
  
  @Inject
  private SessionDAO sessionDAO;

  /**
   * Creates new session
   * 
   * @param locale session locale
   * @param visitor visitor details
   * @param data serialized session data
   * @return created session
   */
  public Session createSession(String locale, String timeZone, String visitor, byte[] data) {
    return sessionDAO.create(UUID.randomUUID().toString(), locale, timeZone, visitor, data);
  }
  
  /**
   * Finds a session by an external id
   * 
   * @param externalId 
   * @return
   */
  public Session findSession(UUID externalId) {
    if (externalId == null) {
      return null;
    }
    
    return sessionDAO.findByExternalId(externalId.toString());
  }
  
  /**
   * Finds a metamind session by bot session
   * 
   * @return metamind session
   */
  public Session findSessionFromBotSession(com.rabidgremlin.mutters.core.session.Session botSession) {
    if (botSession == null) {
      return null;
    }
    
    Long sessionId = (Long) botSession.getLongTermAttribute(SessionConsts.METAMIND_SESSION_ID_ATTRIBUTE);
    if (sessionId == null) {
      return null;
    }
    
    return sessionDAO.findById(sessionId);
  }
  
  /**
   * Updates session state
   * 
   * @param session session
   * @param data new session state serialized
   * @return updated session
   */
  public Session updateSessionState(Session session, byte[] data) {
    return sessionDAO.updateData(session, data);
  }
  
}
