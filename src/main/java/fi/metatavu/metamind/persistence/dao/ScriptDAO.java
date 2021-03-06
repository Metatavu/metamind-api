package fi.metatavu.metamind.persistence.dao;

import fi.metatavu.metamind.persistence.models.Script;
import fi.metatavu.metamind.persistence.models.Script_;
import fi.metatavu.metamind.persistence.models.Story;
import fi.metatavu.metamind.persistence.models.Story_;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.util.List;
import java.util.UUID;

/**
 * DAO class for Script
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class ScriptDAO extends AbstractDAO<Script> {

  /**
   * Creates new script
   *
   * @param name name
   * @param content content
   * @param version version
   * @param language language
   * @param creatorId creator's id
   * @param lastModifierId last modifier's id
   * @return created script
   */
  public Script create(UUID id, String name, String content, String version, String language, UUID creatorId, UUID lastModifierId) {
    Script script = new Script();
    script.setName(name);
    script.setContent(content);
    script.setVersion(version);
    script.setLanguage(language);
    script.setId(id);
    script.setCreatorId(creatorId);
    script.setLastModifierId(lastModifierId);
    return persist(script);
  }
  
  /**
   * Finds script by name and version
   * 
   * @param name Script name
   * @param version Script version
   * 
   * @return found script or null if not found
   */
  public Script findByNameAndVersion(String name, String version) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Script> criteria = criteriaBuilder.createQuery(Script.class);
    Root<Script> root = criteria.from(Script.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(Script_.name), name),
        criteriaBuilder.equal(root.get(Script_.version), version)
      )
    );
    
    return getSingleResult(entityManager.createQuery(criteria));
  } 
  
  /**
   * Updates name
   *
   * @param name name
   * @param lastModifierId last modifier's id
   * @return updated script
   */
  public Script updateName(Script script, String name, UUID lastModifierId) {
    script.setLastModifierId(lastModifierId);
    script.setName(name);
    return persist(script);
  }

  /**
   * Updates content
   *
   * @param content content
   * @param lastModifierId last modifier's id
   * @return updated script
   */
  public Script updateContent(Script script, String content, UUID lastModifierId) {
    script.setLastModifierId(lastModifierId);
    script.setContent(content);
    return persist(script);
  }

  /**
   * Updates version
   *
   * @param version version
   * @param lastModifierId last modifier's id
   * @return updated script
   */
  public Script updateVersion(Script script, String version, UUID lastModifierId) {
    script.setLastModifierId(lastModifierId);
    script.setVersion(version);
    return persist(script);
  }

  /**
   * Updates language
   *
   * @param language language
   * @param lastModifierId last modifier's id
   * @return updated script
   */
  public Script updateLanguage(Script script, String language, UUID lastModifierId) {
    script.setLastModifierId(lastModifierId);
    script.setLanguage(language);
    return persist(script);
  }

  /**
   * Queries scripts by creator IDs
   *
   * @param creatorIds list of allowed creator IDs
   * @return list of scripts
   */
  public List<Script> listScriptsByCreatorIds(List<UUID> creatorIds) {
    EntityManager entityManager = getEntityManager();
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Script> criteria = criteriaBuilder.createQuery(Script.class);
    Root<Script> root = criteria.from(Script.class);
    criteria.select(root);

    Expression<UUID> creatorExpression = root.get(Script_.creatorId);
    Predicate creatorRestriction = creatorExpression.in(creatorIds);
    criteria.where(creatorRestriction);

    return entityManager.createQuery(criteria).getResultList();
  }
}