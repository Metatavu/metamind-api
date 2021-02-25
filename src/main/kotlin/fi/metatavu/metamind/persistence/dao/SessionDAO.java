package fi.metatavu.metamind.persistence.dao;

import fi.metatavu.metamind.persistence.models.Knot;
import fi.metatavu.metamind.persistence.models.Session;
import fi.metatavu.metamind.persistence.models.Story;

import javax.enterprise.context.ApplicationScoped;
import java.util.UUID;

/**
 * DAO class for Session
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class SessionDAO extends AbstractDAO<Session> {

  /**
   * Creates new Session
   * 
   * @param id id
   * @param story story
   * @param currentKnot currentKnot
   * @param locale locale
   * @param timeZone timeZone
   * @param visitor visitor
   * @param creatorId creator's id
   * @param lastModifierId last modifier's id
   * @return created session
   */
  public Session create(UUID id, Story story, Knot currentKnot, String locale, String timeZone, String visitor, UUID creatorId, UUID lastModifierId) {
    Session session = new Session();
    session.setStory(story);
    session.setCurrentKnot(currentKnot);
    session.setLocale(locale);
    session.setTimeZone(timeZone);
    session.setVisitor(visitor);
    session.setId(id);
    session.setCreatorId(creatorId);
    session.setLastModifierId(lastModifierId);
    return persist(session);
  }

  /**
   * Updates story
   *
   * @param story story
   * @param lastModifierId last modifier's id
   * @return updated session
   */
  public Session updateStory(Session session, Story story, UUID lastModifierId) {
    session.setLastModifierId(lastModifierId);
    session.setStory(story);
    return persist(session);
  }

  /**
   * Updates currentKnot
   *
   * @param currentKnot currentKnot
   * @param lastModifierId last modifier's id
   * @return updated session
   */
  public Session updateCurrentKnot(Session session, Knot currentKnot, UUID lastModifierId) {
    session.setLastModifierId(lastModifierId);
    session.setCurrentKnot(currentKnot);
    return persist(session);
  }

  /**
   * Updates locale
   *
   * @param locale locale
   * @param lastModifierId last modifier's id
   * @return updated session
   */
  public Session updateLocale(Session session, String locale, UUID lastModifierId) {
    session.setLastModifierId(lastModifierId);
    session.setLocale(locale);
    return persist(session);
  }

  /**
   * Updates timeZone
   *
   * @param timeZone timeZone
   * @param lastModifierId last modifier's id
   * @return updated session
   */
  public Session updateTimeZone(Session session, String timeZone, UUID lastModifierId) {
    session.setLastModifierId(lastModifierId);
    session.setTimeZone(timeZone);
    return persist(session);
  }

  /**
   * Updates visitor
   *
   * @param visitor visitor
   * @param lastModifierId last modifier's id
   * @return updated session
   */
  public Session updateVisitor(Session session, String visitor, UUID lastModifierId) {
    session.setLastModifierId(lastModifierId);
    session.setVisitor(visitor);
    return persist(session);
  }

}
