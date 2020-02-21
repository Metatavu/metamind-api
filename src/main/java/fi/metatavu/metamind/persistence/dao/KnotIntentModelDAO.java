package fi.metatavu.metamind.persistence.dao;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.metatavu.metamind.persistence.models.*;
import fi.metatavu.metamind.rest.model.TrainingMaterialType;

/**
 * DAO class for KnotIntentModel
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class KnotIntentModelDAO extends AbstractDAO<KnotIntentModel> {

  /**
   * Creates new KnotIntentModel
   * 
   * @param type model type
   * @param data data
   * @param knot knot
   * @return created knotIntentModel
   */
  public KnotIntentModel create(TrainingMaterialType type, byte[] data, Knot knot) {
    KnotIntentModel knotIntentModel = new KnotIntentModel();
    knotIntentModel.setData(data);
    knotIntentModel.setKnot(knot);
    knotIntentModel.setType(type);
    return persist(knotIntentModel);
  }

  /**
   * Finds knot intent model
   * 
   * @param knot knot
   * @param type type
   * @return found knot intent model or null if non found
   */
  public KnotIntentModel findByKnotAndType(Knot knot, TrainingMaterialType type) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<KnotIntentModel> criteria = criteriaBuilder.createQuery(KnotIntentModel.class);
    Root<KnotIntentModel> root = criteria.from(KnotIntentModel.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(KnotIntentModel_.knot), knot),
        criteriaBuilder.equal(root.get(KnotIntentModel_.type), type)
      )
    );
    
    return getSingleResult(entityManager.createQuery(criteria));
  }
  
  /**
   * Find knot intent model
   * 
   * @param knot
   * @return found knot intent model or null if not found
   */
  public KnotIntentModel findByKnot(Knot knot) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<KnotIntentModel> criteria = criteriaBuilder.createQuery(KnotIntentModel.class);
    Root<KnotIntentModel> root = criteria.from(KnotIntentModel.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(KnotIntentModel_.knot), knot)
      )
    );
    
    return getSingleResult(entityManager.createQuery(criteria));
  }
  
  /**
   * Updates data
   *
   * @param data data
   * @return updated knotIntentModel
   */
  public KnotIntentModel updateData(KnotIntentModel knotIntentModel, byte[] data) {
    knotIntentModel.setData(data);
    return persist(knotIntentModel);
  }

}
