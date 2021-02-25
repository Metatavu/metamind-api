package fi.metatavu.metamind.persistence.dao;

import fi.metatavu.metamind.persistence.models.*;
import fi.metatavu.metamind.api.spec.model.TrainingMaterialType;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import java.util.List;

/**
 * DAO class for IntentTrainingMaterial
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class IntentTrainingMaterialDAO extends AbstractDAO<IntentTrainingMaterial> {

  /**
   * Creates new IntentTrainingMaterial
   * 
   * @param intent intent
   * @param trainingMaterial training material
   * @return created intentTrainingMaterial
   */
  public IntentTrainingMaterial create(Intent intent, TrainingMaterial trainingMaterial) {
    IntentTrainingMaterial intentTrainingMaterial = new IntentTrainingMaterial();
    intentTrainingMaterial.setTrainingMaterial(trainingMaterial);
    intentTrainingMaterial.setIntent(intent);
    return persist(intentTrainingMaterial);
  }

  /**
   * Finds intent training material by intent and type
   * 
   * @param intent intent
   * @param type type
   * @return found intent training material or null if non found
   */
  public IntentTrainingMaterial findByIntentAndType(Intent intent, TrainingMaterialType type) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<IntentTrainingMaterial> criteria = criteriaBuilder.createQuery(IntentTrainingMaterial.class);
    Root<IntentTrainingMaterial> root = criteria.from(IntentTrainingMaterial.class);
    Join<IntentTrainingMaterial, TrainingMaterial> trainingMaterialJoin = root.join(IntentTrainingMaterial_.trainingMaterial);
    
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(IntentTrainingMaterial_.intent), intent),
        criteriaBuilder.equal(trainingMaterialJoin.get(TrainingMaterial_.type), type)
      )
    );
    
    return getSingleResult(entityManager.createQuery(criteria));
  }

  
  /**
   * Lists intents by training material
   * 
   * @param trainingMaterial trainingMaterial
   * @param global global
   * @return List of intents
   */
  public List<Story> listStoriesByTrainingMaterialAndGlobal(TrainingMaterial trainingMaterial, Boolean global) {
    EntityManager entityManager = getEntityManager();
  
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Story> criteria = criteriaBuilder.createQuery(Story.class);
    Root<IntentTrainingMaterial> root = criteria.from(IntentTrainingMaterial.class);
    Join<IntentTrainingMaterial, Intent> intentJoin = root.join(IntentTrainingMaterial_.intent);
    Join<Intent, Knot> targetKnotJoin = intentJoin.join(Intent_.targetKnot);
    
    criteria.select(targetKnotJoin.get(Knot_.story)).distinct(true);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(IntentTrainingMaterial_.trainingMaterial), trainingMaterial),
        criteriaBuilder.equal(intentJoin.get(Intent_.global), global)
      )
    );
    
    return entityManager.createQuery(criteria).getResultList();
  }

  /**
   * Lists knots with attached target intents having specified training material
   * 
   * @param trainingMaterial trainingMaterial
   * @return List of knots with attached target intents having specified training material
   */
  public List<Knot> listByTargetIntentTrainingMaterial(TrainingMaterial trainingMaterial) {
    EntityManager entityManager = getEntityManager();
  
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Knot> criteria = criteriaBuilder.createQuery(Knot.class);
    Root<IntentTrainingMaterial> root = criteria.from(IntentTrainingMaterial.class);
    Join<IntentTrainingMaterial, Intent> intentJoin = root.join(IntentTrainingMaterial_.intent);
    Join<Intent, Knot> knotJoin = intentJoin.join(Intent_.sourceKnot);
    
    criteria.select(knotJoin);
    criteria.where(criteriaBuilder.equal(root.get(IntentTrainingMaterial_.trainingMaterial), trainingMaterial));
    criteria.groupBy(knotJoin);
    
    return entityManager.createQuery(criteria).getResultList();
  }

  /**
   * List training materials by an intent
   * 
   * @param intent intent
   * @return list of training materials
   */
  public List<TrainingMaterial> listTrainingMaterialByIntent(Intent intent) {
    EntityManager entityManager = getEntityManager();
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<TrainingMaterial> criteria = criteriaBuilder.createQuery(TrainingMaterial.class);
    Root<IntentTrainingMaterial> root = criteria.from(IntentTrainingMaterial.class);
    
    criteria.select(root.get(IntentTrainingMaterial_.trainingMaterial));
    criteria.where(criteriaBuilder.equal(root.get(IntentTrainingMaterial_.intent), intent));
    
    return entityManager.createQuery(criteria).getResultList();
  }

  /**
   * List by intent and type
   * 
   * @param intent intent
   * @param type type
   * @return list of intent training materials
   */
  public List<IntentTrainingMaterial> listByIntentAndType(Intent intent, TrainingMaterialType type) {
    EntityManager entityManager = getEntityManager();
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<IntentTrainingMaterial> criteria = criteriaBuilder.createQuery(IntentTrainingMaterial.class);
    Root<IntentTrainingMaterial> root = criteria.from(IntentTrainingMaterial.class);
    Join<IntentTrainingMaterial, TrainingMaterial> trainingMaterialJoin = root.join(IntentTrainingMaterial_.trainingMaterial);
    
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(trainingMaterialJoin.get(TrainingMaterial_.type), type),
        criteriaBuilder.equal(root.get(IntentTrainingMaterial_.intent), intent)
      )
    );
    
    return entityManager.createQuery(criteria).getResultList();
  }

  /**
   * List by intent
   * 
   * @param intent intent
   * @return list of intent training materials
   */
  public List<IntentTrainingMaterial> listByIntent(Intent intent) {
    EntityManager entityManager = getEntityManager();
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<IntentTrainingMaterial> criteria = criteriaBuilder.createQuery(IntentTrainingMaterial.class);
    Root<IntentTrainingMaterial> root = criteria.from(IntentTrainingMaterial.class);
    
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(IntentTrainingMaterial_.intent), intent));
    
    return entityManager.createQuery(criteria).getResultList();
  }
  
}
