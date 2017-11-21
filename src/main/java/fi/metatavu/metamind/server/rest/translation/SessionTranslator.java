package fi.metatavu.metamind.server.rest.translation;

import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;

import fi.metatavu.metamind.server.rest.model.Session;

/**
 * Translator for translating JPA entities into REST entities
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class SessionTranslator {

  /**
   * Translates JPA session into REST session
   * 
   * @param jpaSession JPA session
   * @return REST session
   */
  public Session translateSession(fi.metatavu.metamind.persistence.models.Session jpaSession) {
    if (jpaSession == null) {
      return null;
    }
    
    Session result = new Session();
    result.setCreated(jpaSession.getCreated());
    result.setId(UUID.fromString(jpaSession.getExternalId()));
    result.setLocale(jpaSession.getLocale());
    result.setVisitor(jpaSession.getVisitor());
    
    return result;
  }
  
}
