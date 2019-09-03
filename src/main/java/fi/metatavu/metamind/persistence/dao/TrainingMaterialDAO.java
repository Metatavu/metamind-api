package fi.metatavu.metamind.persistence.dao;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import fi.metatavu.metamind.persistence.models.*;
import fi.metatavu.metamind.rest.model.TrainingMaterialType;
import fi.metatavu.metamind.rest.model.TrainingMaterialVisibility;

/**
 * DAO class for TrainingMaterial
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class TrainingMaterialDAO extends AbstractDAO<TrainingMaterial> {

  /**
   * Creates new TrainingMaterial
   * 
   * @param id id
   * @param type type
   * @param name name
   * @param text text
   * @param story story
   * @param creatorId creator's id
   * @param lastModifierId last modifier's id
   * @param visibility visibility
   * @return created trainingMaterial
   */
  @SuppressWarnings("squid:S00107")
  public TrainingMaterial create(UUID id, TrainingMaterialType type, String name, String text, Story story, UUID creatorId, UUID lastModifierId, TrainingMaterialVisibility visibility) {
    TrainingMaterial trainingMaterial = new TrainingMaterial();
    trainingMaterial.setType(type);
    trainingMaterial.setVisibility(visibility);
    trainingMaterial.setName(name);
    trainingMaterial.setText(text);
    trainingMaterial.setStory(story);
    trainingMaterial.setId(id);
    trainingMaterial.setCreatorId(creatorId);
    trainingMaterial.setLastModifierId(lastModifierId);
    return persist(trainingMaterial);
  }

  /**
   * List training materials
   * 
   * @param includeNullStories whether to include stories with null story
   * @param story story include also stories
   * @param type filter by type
   * 
   * @return found training materials
   */
  public List<TrainingMaterial> list(boolean includeNullStories, Story story, TrainingMaterialType type, TrainingMaterialVisibility visibility) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<TrainingMaterial> criteria = criteriaBuilder.createQuery(TrainingMaterial.class);
    Root<TrainingMaterial> root = criteria.from(TrainingMaterial.class);
    criteria.select(root);
    
    List<Predicate> restrictions = new ArrayList<>();
    
    if (includeNullStories) {
      if (story == null) {
        restrictions.add(criteriaBuilder.isNull(root.get(TrainingMaterial_.story)));        
      } else {
        restrictions.add(criteriaBuilder.or(criteriaBuilder.isNull(root.get(TrainingMaterial_.story)), criteriaBuilder.equal(root.get(TrainingMaterial_.story), story)));
      }
    } else {
      restrictions.add(criteriaBuilder.equal(root.get(TrainingMaterial_.story), story));
    }
    
    if (type != null) {
      restrictions.add(criteriaBuilder.equal(root.get(TrainingMaterial_.type), type));
    }
    
    if (visibility != null) {
      restrictions.add(criteriaBuilder.equal(root.get(TrainingMaterial_.visibility), visibility));
    }

    criteria.where(restrictions.toArray(new Predicate[0]));
    
    return entityManager.createQuery(criteria).getResultList();
  } 

  /**
   * Updates type
   *
   * @param type type
   * @param lastModifierId last modifier's id
   * @return updated trainingMaterial
   */
  public TrainingMaterial updateType(TrainingMaterial trainingMaterial, TrainingMaterialType type, UUID lastModifierId) {
    trainingMaterial.setLastModifierId(lastModifierId);
    trainingMaterial.setType(type);
    return persist(trainingMaterial);
  }

  /**
   * Updates name
   *
   * @param name name
   * @param lastModifierId last modifier's id
   * @return updated trainingMaterial
   */
  public TrainingMaterial updateName(TrainingMaterial trainingMaterial, String name, UUID lastModifierId) {
    trainingMaterial.setLastModifierId(lastModifierId);
    trainingMaterial.setName(name);
    return persist(trainingMaterial);
  }

  /**
   * Updates text
   *
   * @param text text
   * @param lastModifierId last modifier's id
   * @return updated trainingMaterial
   */
  public TrainingMaterial updateText(TrainingMaterial trainingMaterial, String text, UUID lastModifierId) {
    trainingMaterial.setLastModifierId(lastModifierId);
    trainingMaterial.setText(text);
    return persist(trainingMaterial);
  }

  /**
   * Updates story
   *
   * @param story story
   * @param lastModifierId last modifier's id
   * @return updated trainingMaterial
   */
  public TrainingMaterial updateStory(TrainingMaterial trainingMaterial, Story story, UUID lastModifierId) {
    trainingMaterial.setLastModifierId(lastModifierId);
    trainingMaterial.setStory(story);
    return persist(trainingMaterial);
  }
  
  /**
   * Updates visibility
   * 
   * @param trainingMaterial trainingMaterial
   * @param visibility TrainingMaterilaVisibility
   * @param lastModifiedId trainingMaterial
   * @return updated trainingMaterial
   */
  public TrainingMaterial updateVisibility(TrainingMaterial trainingMaterial, TrainingMaterialVisibility visibility, UUID lastModifiedId) {
    trainingMaterial.setLastModifierId(lastModifiedId);
    trainingMaterial.setVisibility(visibility);
    return persist(trainingMaterial);
  }

}