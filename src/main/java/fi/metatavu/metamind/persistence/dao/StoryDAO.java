package fi.metatavu.metamind.persistence.dao;

import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.metatavu.metamind.persistence.models.*;

/**
 * DAO class for Story
 * 
 * @author Antti Leppä
 */
public class StoryDAO extends AbstractDAO<Story> {

    /**
     * Creates new story
     *
     * @param name name
     * @param creatorId creator's id
     * @param lastModifierId last modifier's id
     * @return created story
     */
    public Story create(UUID id, String name, UUID creatorId, UUID lastModifierId) {
      Story story = new Story();
      story.setName(name);
      story.setId(id);
      story.setCreatorId(creatorId);
      story.setLastModifierId(lastModifierId);
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
     * Updates name
     *
     * @param name name
     * @param lastModifierId last modifier's id
     * @return updated story
     */
    public Story updateName(Story story, String name, UUID lastModifierId) {
      story.setLastModifierId(lastModifierId);
      story.setName(name);
      return persist(story);
    }

}