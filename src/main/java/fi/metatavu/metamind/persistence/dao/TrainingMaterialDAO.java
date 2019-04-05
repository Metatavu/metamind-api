package fi.metatavu.metamind.persistence.dao;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import java.util.List;
import java.util.UUID;
import fi.metatavu.metamind.persistence.models.*;

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
   * @param name name
   * @param text text
   * @param story story
   * @param creatorId creator's id
   * @param lastModifierId last modifier's id
   * @return created trainingMaterial
   */
  public TrainingMaterial create(UUID id, String name, String text, Story story, UUID creatorId, UUID lastModifierId) {
    TrainingMaterial trainingMaterial = new TrainingMaterial();
    trainingMaterial.setName(name);
    trainingMaterial.setText(text);
    trainingMaterial.setStory(story);
    trainingMaterial.setId(id);
    trainingMaterial.setCreatorId(creatorId);
    trainingMaterial.setLastModifierId(lastModifierId);
    return persist(trainingMaterial);
  }
  
  /**
   * List by story equals or story is null
   * 
   * @param story story
   * 
   * @return found story
   */
  public List<TrainingMaterial> listByStoryOrStoryNull(Story story) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<TrainingMaterial> criteria = criteriaBuilder.createQuery(TrainingMaterial.class);
    Root<TrainingMaterial> root = criteria.from(TrainingMaterial.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.or(
        criteriaBuilder.isNull(root.get(TrainingMaterial_.story)),
        criteriaBuilder.equal(root.get(TrainingMaterial_.story), story)
      )
    );
    
    return entityManager.createQuery(criteria).getResultList();
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

}
