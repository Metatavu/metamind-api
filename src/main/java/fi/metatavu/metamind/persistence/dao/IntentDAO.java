package fi.metatavu.metamind.persistence.dao;

import java.util.List;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

import fi.metatavu.metamind.persistence.models.*;
import fi.metatavu.metamind.rest.model.IntentType;

/**
 * DAO class for Intent
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class IntentDAO extends AbstractDAO<Intent> {

  /**
   * Creates new Intent
   * 
   * @param id id
   * @param type type
   * @param name name
   * @param sourceKnot sourceKnot
   * @param targetKnot targetKnot
   * @param global global
   * @param creatorId creator's id
   * @param lastModifierId last modifier's id
   * @return created intent
   */
  public Intent create(UUID id, IntentType type, String name, Knot sourceKnot, Knot targetKnot, Boolean global, String quickResponse, UUID creatorId, UUID lastModifierId) {
    Intent intent = new Intent();
    intent.setType(type);
    intent.setName(name);
    intent.setSourceKnot(sourceKnot);
    intent.setTargetKnot(targetKnot);
    intent.setGlobal(global);
    intent.setId(id);
    intent.setQuickResponse(quickResponse);
    intent.setCreatorId(creatorId);
    intent.setLastModifierId(lastModifierId);
    return persist(intent);
  }
  
  /**
   * Lists intents by sourceKnot
   * 
   * @param sourceKnot sourceKnot
   * @return List of intents
   */
  public List<Intent> listBySourceKnot(Knot sourceKnot) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Intent> criteria = criteriaBuilder.createQuery(Intent.class);
    Root<Intent> root = criteria.from(Intent.class);

    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(Intent_.sourceKnot), sourceKnot));
    
    TypedQuery<Intent> query = entityManager.createQuery(criteria);
    
    return query.getResultList();
  }
  
  /**
   * Lists intents by story
   * 
   * @param story story
   * @return List of intents
   */
  public List<Intent> listByStory(Story story) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Intent> criteria = criteriaBuilder.createQuery(Intent.class);
    Root<Intent> root = criteria.from(Intent.class);
    Join<Intent, Knot> sourceKnotJoin = root.join(Intent_.targetKnot);

    criteria.select(root);
    criteria.where(criteriaBuilder.equal(sourceKnotJoin.get(Knot_.story), story));
    
    TypedQuery<Intent> query = entityManager.createQuery(criteria);
    
    return query.getResultList();
  }
  
  /**
   * Lists intents by story
   * 
   * @param story story
   * @param global global
   * @return List of intents
   */
  public List<Intent> listByStoryAndGlobal(Story story, Boolean global) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Intent> criteria = criteriaBuilder.createQuery(Intent.class);
    Root<Intent> root = criteria.from(Intent.class);
    Join<Intent, Knot> sourceKnotJoin = root.join(Intent_.targetKnot);

    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(sourceKnotJoin.get(Knot_.story), story),
        criteriaBuilder.equal(root.get(Intent_.global), global)
      )
    );
    
    TypedQuery<Intent> query = entityManager.createQuery(criteria);
    
    return query.getResultList();
  }


  /**
   * Lists intents by story, global and type
   * 
   * @param story story
   * @param global global
   * @return List of intents
   */
  public List<Intent> listByStoryAndGlobalAndType(Story story, Boolean global, IntentType type) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Intent> criteria = criteriaBuilder.createQuery(Intent.class);
    Root<Intent> root = criteria.from(Intent.class);
    Join<Intent, Knot> sourceKnotJoin = root.join(Intent_.targetKnot);

    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(sourceKnotJoin.get(Knot_.story), story),
        criteriaBuilder.equal(root.get(Intent_.global), global),
        criteriaBuilder.equal(root.get(Intent_.type), type)
      )
    );
    
    TypedQuery<Intent> query = entityManager.createQuery(criteria);
    
    return query.getResultList();
  }

  /**
   * Lists intents by source knot and type
   * 
   * @param sourceKnot source knot
   * @param intentType type
   * @return intents by source knot and type
   */
  public List<Intent> listBySourceKnotAndType(Knot sourceKnot, IntentType intentType) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Intent> criteria = criteriaBuilder.createQuery(Intent.class);
    Root<Intent> root = criteria.from(Intent.class);

    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(Intent_.sourceKnot), sourceKnot),
        criteriaBuilder.equal(root.get(Intent_.type), intentType)
      )
    );
    
    TypedQuery<Intent> query = entityManager.createQuery(criteria);
    
    return query.getResultList();
  }

  /**
   * Lists intent quick responses by sourceKnot
   * 
   * @param sourceKnot sourceKnot
   * @return List of intents
   */
  public List<String> listQuickResponsesBySourceKnot(Knot sourceKnot) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<String> criteria = criteriaBuilder.createQuery(String.class);
    Root<Intent> root = criteria.from(Intent.class);

    criteria.select(root.get(Intent_.quickResponse));
    criteria.where(
      criteriaBuilder.isNotNull(root.get(Intent_.quickResponse)),
      criteriaBuilder.equal(root.get(Intent_.sourceKnot), sourceKnot)
    );
    
    return entityManager.createQuery(criteria).getResultList();
  }

  /**
   * Updates type
   *
   * @param type type
   * @param lastModifierId last modifier's id
   * @return updated intent
   */
  public Intent updateType(Intent intent, IntentType type, UUID lastModifierId) {
    intent.setLastModifierId(lastModifierId);
    intent.setType(type);
    return persist(intent);
  }

  /**
   * Updates sourceKnot
   *
   * @param sourceKnot sourceKnot
   * @param lastModifierId last modifier's id
   * @return updated intent
   */
  public Intent updateSourceKnot(Intent intent, Knot sourceKnot, UUID lastModifierId) {
    intent.setLastModifierId(lastModifierId);
    intent.setSourceKnot(sourceKnot);
    return persist(intent);
  }

  /**
   * Updates targetKnot
   *
   * @param targetKnot targetKnot
   * @param lastModifierId last modifier's id
   * @return updated intent
   */
  public Intent updateTargetKnot(Intent intent, Knot targetKnot, UUID lastModifierId) {
    intent.setLastModifierId(lastModifierId);
    intent.setTargetKnot(targetKnot);
    return persist(intent);
  }

  /**
   * Updates name
   *
   * @param name name
   * @param lastModifierId last modifier's id
   * @return updated intent
   */
  public Intent updateName(Intent intent, String name, UUID lastModifierId) {
    intent.setLastModifierId(lastModifierId);
    intent.setName(name);
    return persist(intent);
  }
  
  /**
   * Updates global
   *
   * @param global global
   * @param lastModifierId last modifier's id
   * @return updated intent
   */
  public Intent updateGlobal(Intent intent, Boolean global, UUID lastModifierId) {
    intent.setLastModifierId(lastModifierId);
    intent.setGlobal(global);
    return persist(intent);
  }

  /**
   * Updates quickResponse
   *
   * @param quickResponse quickResponse
   * @param lastModifierId last modifier's id
   * @return updated intent
   */
  public Intent updateQuickResponse(Intent intent, String quickResponse, UUID lastModifierId) {
    intent.setLastModifierId(lastModifierId);
    intent.setQuickResponse(quickResponse);
    return persist(intent);
  }

}
