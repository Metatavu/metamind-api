package fi.metatavu.metamind.persistence.dao;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.metatavu.metamind.persistence.models.SlotModel;
import fi.metatavu.metamind.persistence.models.SlotModel_;

/**
 * DAO for slot models
 * 
 * @author Heikki Kurhinen
 * @author Antti Leppä
 */
@ApplicationScoped
public class SlotModelDAO extends AbstractDAO<SlotModel> {

  public SlotModel create(String name, byte[] data) {
    SlotModel slotModel = new SlotModel();
    slotModel.setData(data);
    slotModel.setName(name);
    return persist(slotModel);
  }

  /**
   * Find slot model by name
   * 
   * @param session session
   * @return List of messages
   */
  public SlotModel findByName(String name) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<SlotModel> criteria = criteriaBuilder.createQuery(SlotModel.class);
    Root<SlotModel> root = criteria.from(SlotModel.class);

    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(SlotModel_.name), name));
    
    return getSingleResult(entityManager.createQuery(criteria));
  }

  /**
   * Updates model data
   * 
   * @param slotModel slot model
   * @param data data
   * @return updated model
   */
  public SlotModel updateData(SlotModel slotModel, byte[] data) {
    slotModel.setData(data);
    return persist(slotModel);
  }

}
