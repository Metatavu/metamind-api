package fi.metatavu.metamind.persistence.dao;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.metatavu.metamind.persistence.models.IntentModel;
import fi.metatavu.metamind.persistence.models.IntentModel_;

/**
 * DAO for intent models
 * 
 * @author Heikki Kurhinen
 * @author Antti Leppä
 */
@ApplicationScoped
public class IntentModelDAO extends AbstractDAO<IntentModel> {

  public IntentModel create(String name, byte[] data) {
    IntentModel intentModel = new IntentModel();
    intentModel.setData(data);
    intentModel.setName(name);
    return persist(intentModel);
  }

  /**
   * Find intent model by name
   * 
   * @param session session
   * @return List of messages
   */
  public IntentModel findByName(String name) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<IntentModel> criteria = criteriaBuilder.createQuery(IntentModel.class);
    Root<IntentModel> root = criteria.from(IntentModel.class);

    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(IntentModel_.name), name));
    
    return getSingleResult(entityManager.createQuery(criteria));
  }

  /**
   * Updates model data
   * 
   * @param intentModel intent model
   * @param data data
   * @return updated model
   */
  public IntentModel updateData(IntentModel intentModel, byte[] data) {
    intentModel.setData(data);
    return persist(intentModel);
  }

}
