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
import fi.metatavu.metamind.rest.model.KnotType;

/**
 * DAO class for Knot
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class KnotDAO extends AbstractDAO<Knot> {

  /**
   * Creates new Knot
   * 
   * @param id id
   * @param type type
   * @param name name
   * @param content content
   * @param story story
   * @param creatorId creator's id
   * @param lastModifierId last modifier's id
   * @return created knot
   */
  public Knot create(UUID id, KnotType type, String name, String content, Story story, UUID creatorId, UUID lastModifierId) {
    Knot knot = new Knot();
    knot.setType(type);
    knot.setName(name);
    knot.setContent(content);
    knot.setStory(story);
    knot.setId(id);
    knot.setCreatorId(creatorId);
    knot.setLastModifierId(lastModifierId);
    return persist(knot);
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
    Root<Intent> root = criteria.from(Intent.class);
    Join<Intent, Knot> knotJoin = root.join(Intent_.sourceKnot);
    
    criteria.select(knotJoin);
    criteria.where(criteriaBuilder.equal(root.get(Intent_.trainingMaterial), trainingMaterial));
    criteria.groupBy(knotJoin);
    
    TypedQuery<Knot> query = entityManager.createQuery(criteria);
    
    return query.getResultList();
  }
  
  /**
   * Lists intents by story
   * 
   * @param sourceKnot story
   * @return List of intents
   */
  public List<Knot> listByStory(Story story) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Knot> criteria = criteriaBuilder.createQuery(Knot.class);
    Root<Knot> root = criteria.from(Knot.class);
   
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(Knot_.story), story));
    
    TypedQuery<Knot> query = entityManager.createQuery(criteria);
    
    return query.getResultList();
  }

  /**
   * Updates type
   *
   * @param type type
   * @param lastModifierId last modifier's id
   * @return updated knot
   */
  public Knot updateType(Knot knot, KnotType type, UUID lastModifierId) {
    knot.setLastModifierId(lastModifierId);
    knot.setType(type);
    return persist(knot);
  }

  /**
   * Updates name
   *
   * @param name name
   * @param lastModifierId last modifier's id
   * @return updated knot
   */
  public Knot updateName(Knot knot, String name, UUID lastModifierId) {
    knot.setLastModifierId(lastModifierId);
    knot.setName(name);
    return persist(knot);
  }

  /**
   * Updates content
   *
   * @param content content
   * @param lastModifierId last modifier's id
   * @return updated knot
   */
  public Knot updateContent(Knot knot, String content, UUID lastModifierId) {
    knot.setLastModifierId(lastModifierId);
    knot.setContent(content);
    return persist(knot);
  }

  /**
   * Updates story
   *
   * @param story story
   * @param lastModifierId last modifier's id
   * @return updated knot
   */
  public Knot updateStory(Knot knot, Story story, UUID lastModifierId) {
    knot.setLastModifierId(lastModifierId);
    knot.setStory(story);
    return persist(knot);
  }

}