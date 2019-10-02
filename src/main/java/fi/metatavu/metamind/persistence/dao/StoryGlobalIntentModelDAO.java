package fi.metatavu.metamind.persistence.dao;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.metatavu.metamind.persistence.models.*;
import fi.metatavu.metamind.rest.model.TrainingMaterialType;

/**
 * DAO class for StoryGlobalIntentModel
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class StoryGlobalIntentModelDAO extends AbstractDAO<StoryGlobalIntentModel> {

  /**
   * Creates new StoryGlobalIntentModel
   * 
   * @param type type
   * @param data data
   * @param story story
   * @return created storyGlobalIntentModel
   */
  public StoryGlobalIntentModel create(TrainingMaterialType type, byte[] data, Story story) {
    StoryGlobalIntentModel storyGlobalIntentModel = new StoryGlobalIntentModel();
    storyGlobalIntentModel.setData(data);
    storyGlobalIntentModel.setStory(story);
    storyGlobalIntentModel.setType(type);
    return persist(storyGlobalIntentModel);
  }

  /**
   * Finds story intent model
   * 
   * @param story story
   * @return found story intent model or null if non found
   */
  public StoryGlobalIntentModel findByStory(Story story) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<StoryGlobalIntentModel> criteria = criteriaBuilder.createQuery(StoryGlobalIntentModel.class);
    Root<StoryGlobalIntentModel> root = criteria.from(StoryGlobalIntentModel.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(StoryGlobalIntentModel_.story), story)
    );
    
    return getSingleResult(entityManager.createQuery(criteria));
  }
  
  /**
   * Updates data
   *
   * @param data data
   * @return updated storyGlobalIntentModel
   */
  public StoryGlobalIntentModel updateData(StoryGlobalIntentModel storyGlobalIntentModel, byte[] data) {
    storyGlobalIntentModel.setData(data);
    return persist(storyGlobalIntentModel);
  }

}
