package fi.metatavu.metamind.persistence.dao;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.metatavu.metamind.persistence.models.Script;
import fi.metatavu.metamind.persistence.models.Script_;

/**
 * DAO for scripts
 * 
 * @author Heikki Kurhinen
 *
 */
@ApplicationScoped
public class ScriptDAO extends AbstractDAO<Script> {

  /**
   * Creates new script
   * 
   * @param name script name
   * @param version script version
   * @param language script language
   * @param externalId script external id
   * @param content script content
   * 
   * @return created script
   */
  public Script create(String name, String version, String language, String externalId, String content) {
    Script script = new Script();
    script.setName(name);
    script.setVersion(version);
    script.setLanguage(language);
    script.setExternalId(externalId);
    script.setContent(content);
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
   * Finds single session with external id
   * 
   * @param externalId script external id
   * 
   * @return Found script
   */
  public Script findByExternalId(String externalId) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Script> criteria = criteriaBuilder.createQuery(Script.class);
    Root<Script> root = criteria.from(Script.class);

    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(Script_.externalId), externalId));
    
    TypedQuery<Script> query = entityManager.createQuery(criteria);
    
    return getSingleResult(query);
  }

  /**
   * Updates script language
   * 
   * @param script Script to update
   * @param language new script language
   * 
   * @return updated script
   */
  public Script updateLanguage(Script script, String language) {
    script.setLanguage(language);
    return persist(script);
  }

  /**
   * Updates script content
   *
   * @param script Script to update
   * @param content new script content
   * 
   * @return updated script
   */
  public Script updateContent(Script script, String content) {
    script.setContent(content);
    return persist(script);
  }
}
