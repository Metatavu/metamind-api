package fi.metatavu.metamind.persistence.dao;

import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.metatavu.metamind.persistence.models.Session;
import fi.metatavu.metamind.persistence.models.Session_;

/**
 * DAO for sessions
 * 
 * @author Heikki Kurhinen
 *
 */
@ApplicationScoped
public class SessionDAO extends AbstractDAO<Session> {

  /**
   * Creates new session
   * 
   * @param externalId external id in uuid format
   * @param locale users locale
   * @param visitor visitor details
   * @param data serialized session data
   * @return new session
   */
  public Session create(String externalId, String locale, String visitor, byte[] data) {
    Session session = new Session();
    session.setExternalId(externalId);
    session.setLocale(locale);
    session.setData(data);
    session.setVisitor(visitor);
    return persist(session);
  }
  
  /**
   * Finds single session with external id
   * 
   * @param externalId external id
   * @return Session or null if not found
   */
  public Session findByExternalId(UUID externalId) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Session> criteria = criteriaBuilder.createQuery(Session.class);
    Root<Session> root = criteria.from(Session.class);

    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(Session_.externalId), externalId));
    
    TypedQuery<Session> query = entityManager.createQuery(criteria);
    
    return getSingleResult(query);
  }
  
}
