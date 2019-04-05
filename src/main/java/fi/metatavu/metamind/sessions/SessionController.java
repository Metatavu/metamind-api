package fi.metatavu.metamind.sessions;

import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.metamind.persistence.dao.SessionDAO;
import fi.metatavu.metamind.persistence.models.Knot;
import fi.metatavu.metamind.persistence.models.Session;
import fi.metatavu.metamind.persistence.models.Story;

@ApplicationScoped
public class SessionController {
  
  @Inject
  private SessionDAO sessionDAO;
  
  /**
   * Creates new session
   *
   * @param story story
   * @param locale locale
   * @param timeZone timeZone
   * @param visitor visitor
   * @param creatorId creator's id
   * @return created session
   */
  public Session create(Story story, String locale, String timeZone, String visitor, UUID creatorId) {
    return sessionDAO.create(UUID.randomUUID(), story, null, locale, timeZone, visitor, creatorId, creatorId);
  }
  
  /**
   * Finds a session by an id
   * 
   * @param id 
   * @return
   */
  public Session findSessionById(UUID id) {
    if (id == null) {
      return null;
    }
    
    return sessionDAO.findById(id);
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
   * Updates currentKnot
   *
   * @param currentKnot currentKnot
   * @param lastModifierId last modifier's id
   * @return updated session
   */
  public Session updateSessionCurrentKnot(Session session, Knot currentKnot, UUID lastModifierId) {
    return sessionDAO.updateCurrentKnot(session, currentKnot, lastModifierId);
  }
  
}
