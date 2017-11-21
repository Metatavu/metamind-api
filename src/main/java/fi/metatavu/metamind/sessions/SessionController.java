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
  public Session createSession(String locale, String visitor, byte[] data) {
    return sessionDAO.create(UUID.randomUUID().toString(), locale, visitor, data);
  }
  
}
