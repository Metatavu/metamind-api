package fi.metatavu.metamind.sessions;

import fi.metatavu.metamind.persistence.dao.MessageDAO;
import fi.metatavu.metamind.persistence.dao.SessionDAO;
import fi.metatavu.metamind.persistence.dao.SessionVariableValueDAO;
import fi.metatavu.metamind.persistence.models.*;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class SessionController {
  
  @Inject
  private SessionDAO sessionDAO;

  @Inject
  private MessageDAO messageDAO;

  @Inject
  private SessionVariableValueDAO sessionVariableValueDAO;
  
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
   * @param id session id
   * @return found session
   */
  public Session findSessionById(UUID id) {
    if (id == null) {
      return null;
    }
    
    return sessionDAO.findById(id);
  }
  
  /**
   * Returns session variable value as string
   * 
   * @param session session
   * @param variable variable
   * @return session variable value as string or null if not found
   */
  public String getSessionVariableValue(Session session, Variable variable) {
    SessionVariableValue sessionVariableValue = sessionVariableValueDAO.findBySessionAndVariable(session, variable);
    if (sessionVariableValue == null) {
      return null;
    }
    
    return sessionVariableValue.getValue();
  }
  
  /**
   * Sets session variable value as string
   * 
   * @param session session
   * @param variable variable
   * @param value value
   */
  public void setSessionVariableValue(Session session, Variable variable, String value) {
    SessionVariableValue sessionVariableValue = sessionVariableValueDAO.findBySessionAndVariable(session, variable);
    if (sessionVariableValue != null) {
      sessionVariableValueDAO.delete(sessionVariableValue);
    }
    
    if (value != null) {
      sessionVariableValueDAO.create(session, variable, value);
    }
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

  /**
   * Navigates back in session history and returns knot in given time.
   * 
   * Method returns null if the start of the story has been reached
   * 
   * @param session session
   * @param steps how many steps to navigate back
   * @return knot in given time or null if start of the story has been reached
   */
  public Knot getPreviousKnot(Session session, Integer steps) {
    List<Message> messages = messageDAO.listBySession(session, steps, 1);
    if (!messages.isEmpty()) {
      Message message = messages.get(0);
      return message.getTargetKnot();
    }
    
    return null;
  }
  
}
