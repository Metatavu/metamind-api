package fi.metatavu.metamind.server.rest.translation;

import fi.metatavu.metamind.api.spec.model.Session;

import javax.enterprise.context.ApplicationScoped;

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
    result.setCreatedAt(jpaSession.getCreatedAt());
    result.setId(jpaSession.getId());
    result.setLocale(jpaSession.getLocale());
    result.setModifiedAt(jpaSession.getModifiedAt());
    result.setStoryId(jpaSession.getStory() != null ? jpaSession.getStory().getId() : null);
    result.setTimeZone(jpaSession.getTimeZone());
    result.setVisitor(jpaSession.getVisitor());
        
    return result;
  }
  
}
