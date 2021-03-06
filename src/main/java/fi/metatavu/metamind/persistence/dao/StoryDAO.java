package fi.metatavu.metamind.persistence.dao;

import fi.metatavu.metamind.persistence.models.Story;
import fi.metatavu.metamind.persistence.models.Story_;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.*;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * DAO class for Story
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class StoryDAO extends AbstractDAO<Story> {

  /**
   * Creates new Story
   * 
   * @param id id
   * @param locale locale
   * @param name name
   * @param creatorId creator's id
   * @param lastModifierId last modifier's id
   * @return created story
   */
  public Story create(UUID id, Locale locale, String name, String defaultHint, UUID creatorId, UUID lastModifierId) {
    Story story = new Story();
    story.setLocale(locale);
    story.setName(name);
    story.setDefaultHint(defaultHint);
    story.setId(id);
    story.setCreatorId(creatorId);
    story.setLastModifierId(lastModifierId);
    return persist(story);
  }

  /**
   * Find slot model by name
   * 
   * @param name name
   * @return found story
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
   * Lists stories by creator ids
   *
   * @param creatorIds list of creator ids allowed fields
   * @return filtered stories list
   */
  public List<Story> listStoriesByCreatorIds(List<UUID> creatorIds) {
    EntityManager entityManager = getEntityManager();
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Story> criteria = criteriaBuilder.createQuery(Story.class);
    Root<Story> root = criteria.from(Story.class);
    criteria.select(root);

    Expression<UUID> creatorExpression = root.get(Story_.creatorId);
    Predicate creatorRestriction = creatorExpression.in(creatorIds);
    criteria.where(creatorRestriction);

    return entityManager.createQuery(criteria).getResultList();
  }

  /**
   * Updates locale
   *
   * @param locale locale
   * @param lastModifierId last modifier's id
   * @return updated story
   */
  public Story updateLocale(Story story, Locale locale, UUID lastModifierId) {
    story.setLastModifierId(lastModifierId);
    story.setLocale(locale);
    return persist(story);
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
  
  /**
   * Updates defaultHint
   * 
   * @param story story
   * @param defaultHint string defaultHint
   * @param lastModifiedId UUID lastModifiedId
   * @return updated story
   */
  public Story updateDefaultHint(Story story, String defaultHint, UUID lastModifiedId) {
    story.setLastModifierId(lastModifiedId);
    story.setDefaultHint(defaultHint);
    return persist(story);
  }

}