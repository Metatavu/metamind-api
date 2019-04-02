package fi.metatavu.metamind.persistence.dao;

import java.util.UUID;
import fi.metatavu.metamind.persistence.models.*;

/**
 * DAO class for Session
 * 
 * @author Antti Leppä
 */
public class SessionDAO extends AbstractDAO<Session> {

    /**
     * Creates new session
     *
     * @param story story
     * @param locale locale
     * @param timeZone timeZone
     * @param visitor visitor
     * @param creatorId creator's id
     * @param lastModifierId last modifier's id
     * @return created session
     */
    public Session create(UUID id, Story story, String locale, String timeZone, String visitor, UUID creatorId, UUID lastModifierId) {
      Session session = new Session();
      session.setStory(story);
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
