package fi.metatavu.metamind.persistence.dao;

import fi.metatavu.metamind.persistence.models.Knot;
import fi.metatavu.metamind.persistence.models.Knot_;
import fi.metatavu.metamind.persistence.models.Story;
import fi.metatavu.metamind.api.spec.model.KnotType;
import fi.metatavu.metamind.api.spec.model.TokenizerType;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.UUID;

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
   * @param tokenizerType tokenizerType
   * @param name name
   * @param hint hint
   * @param content content
   * @param story story
   * @param creatorId creator's id
   * @param lastModifierId last modifier's id
   * @param coordinateX Double coordinate
   * @param coordinateY Double coordinate
   * @return created knot
   */
  public Knot create(UUID id, KnotType type, TokenizerType tokenizerType, String name, String content, String hint, Story story, UUID creatorId, UUID lastModifierId, Double coordinateX, Double coordinateY) {
    Knot knot = new Knot();
    knot.setType(type);
    knot.setTokenizerType(tokenizerType);
    knot.setName(name);
    knot.setHint(hint);
    knot.setContent(content);
    knot.setStory(story);
    knot.setId(id);
    knot.setCoordinateX(coordinateX);
    knot.setCoordinateY(coordinateY);
    knot.setCreatorId(creatorId);
    knot.setLastModifierId(lastModifierId);
    return persist(knot);
  }

  /**
   * Lists intents by story
   * 
   * @param story story
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
   * Updates tokenizerType
   *
   * @param tokenizerType tokenizerType
   * @param lastModifierId last modifier's id
   * @return updated knot
   */
  public Knot updateTokenizerType(Knot knot, TokenizerType tokenizerType, UUID lastModifierId) {
    knot.setLastModifierId(lastModifierId);
    knot.setTokenizerType(tokenizerType);
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
   * Updates hint
   *
   * @param hint hint
   * @param lastModifierId last modifier's id
   * @return updated knot
   */
  public Knot updateHint(Knot knot, String hint, UUID lastModifierId) {
    knot.setLastModifierId(lastModifierId);
    knot.setHint(hint);
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
  
  /**
   * Updates knot coordinates
   * 
   * @param knot knot to update
   * @param coordinateX Integer coordinateX
   * @param coordinateY Integer coordinateY
   * @return updated knot
   */
  public Knot updateCoordinates(Knot knot, Double coordinateX, Double coordinateY) {
    knot.setCoordinateX(coordinateX);
    knot.setCoordinateY(coordinateY);
    return persist(knot);
  }

}