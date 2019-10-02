package fi.metatavu.metamind.persistence.dao;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.metatavu.metamind.persistence.models.Message;
import fi.metatavu.metamind.persistence.models.QuickResponse;
import fi.metatavu.metamind.persistence.models.QuickResponse_;

/**
 * DAO for quick responses
 * 
 * @author Heikki Kurhinen
 *
 */
@ApplicationScoped
public class QuickResponseDAO extends AbstractDAO<QuickResponse> {

  /**
   * Creates new quick response
   * 
   * @param message Message that quick response is related to
   * @param text quick response as a string
   * @return created quick response
   */
  public QuickResponse create(Message message, String text) {
    QuickResponse quickResponseEntity = new QuickResponse();
    quickResponseEntity.setMessage(message);
    quickResponseEntity.setText(text);
    return persist(quickResponseEntity);
  }
  
  /**
   * Lists quick responses by message
   * 
   * @param message message
   * @return List of quick responses
   */
  public List<QuickResponse> listByMessage(Message message) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<QuickResponse> criteria = criteriaBuilder.createQuery(QuickResponse.class);
    Root<QuickResponse> root = criteria.from(QuickResponse.class);

    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(QuickResponse_.message), message));
    
    TypedQuery<QuickResponse> query = entityManager.createQuery(criteria);
    
    return query.getResultList();
  }
  
}
