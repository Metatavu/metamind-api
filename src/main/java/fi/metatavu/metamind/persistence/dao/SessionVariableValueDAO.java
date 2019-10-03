package fi.metatavu.metamind.persistence.dao;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.metatavu.metamind.persistence.models.Session;
import fi.metatavu.metamind.persistence.models.SessionVariableValue;
import fi.metatavu.metamind.persistence.models.SessionVariableValue_;
import fi.metatavu.metamind.persistence.models.Variable;

/**
 * DAO class for SessionVariableValue
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class SessionVariableValueDAO extends AbstractDAO<SessionVariableValue> {

  /**
   * Creates new SessionVariableValue
   * 
   * @param value value
   * @param variable variable
   * @param session session
   * @return created sessionVariableValue
   */
  public SessionVariableValue create(Session session, Variable variable, String value) {
    SessionVariableValue sessionVariableValue = new SessionVariableValue();
    sessionVariableValue.setValue(value);
    sessionVariableValue.setVariable(variable);
    sessionVariableValue.setSession(session);
    return persist(sessionVariableValue);
  }

  /**
   * Find session variable value by session and variable
   * 
   * @param session session
   * @param variable variable
   * @return session variable value or null if not found
   */
  public SessionVariableValue findBySessionAndVariable(Session session, Variable variable) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<SessionVariableValue> criteria = criteriaBuilder.createQuery(SessionVariableValue.class);
    Root<SessionVariableValue> root = criteria.from(SessionVariableValue.class);

    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(SessionVariableValue_.session), session),
        criteriaBuilder.equal(root.get(SessionVariableValue_.variable), variable)        
      )
    );
    
    return getSingleResult(entityManager.createQuery(criteria));
  }
  
}
