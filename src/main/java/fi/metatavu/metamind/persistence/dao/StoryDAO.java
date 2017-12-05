package fi.metatavu.metamind.persistence.dao;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.metatavu.metamind.persistence.models.Story;
import fi.metatavu.metamind.persistence.models.Story_;

/**
 * DAO for stories
 * 
 * @author Heikki Kurhinen
 * @author Antti Leppä
 */
@ApplicationScoped
public class StoryDAO extends AbstractDAO<Story> {

  public Story create(String name, String configJson, String storyJson) {
    Story story = new Story();
    story.setName(name);
    story.setConfigJson(configJson);
    story.setStoryJson(storyJson);
    return persist(story);
  }

  /**
   * Find slot model by name
   * 
   * @param session session
   * @return List of messages
   */
  public Story findByName(String name) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Story> criteria = criteriaBuilder.createQuery(Story.class);
    Root<Story> root = criteria.from(Story.class);

    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(Story_.name), name));
    
    return getSingleResult(entityManager.createQuery(criteria));
  }

  /**
   * Updates config json
   * 
   * @param story story
   * @param data config json
   * @return updated config json
   */
  public Story updateConfigJson(Story story, String configJson) {
    story.setConfigJson(configJson);
    return persist(story);
  }

  /**
   * Updates story json
   * 
   * @param story story
   * @param data story json
   * @return updated story json
   */
  public Story updateStoryJson(Story story, String storyJson) {
    story.setStoryJson(storyJson);
    return persist(story);
  }

}
