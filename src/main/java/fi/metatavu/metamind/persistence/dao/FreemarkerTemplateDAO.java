package fi.metatavu.metamind.persistence.dao;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.metatavu.metamind.persistence.models.FreemarkerTemplate;
import fi.metatavu.metamind.persistence.models.FreemarkerTemplate_;

/**
 * DAO for FreemarkerTemplate
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class FreemarkerTemplateDAO extends AbstractDAO<FreemarkerTemplate> {

  /**
   * Creates new freemarker template
   * 
   * @param name name
   * @param data data
   * @return created template
   */
  public FreemarkerTemplate create(String name, String data) {
    FreemarkerTemplate freemarkerTemplate = new FreemarkerTemplate();
    freemarkerTemplate.setData(data);
    freemarkerTemplate.setName(name);
    return persist(freemarkerTemplate);
  }

  /**
   * Finds single freemarker template by name
   * 
   * @param externalId external id
   * @return Session or null if not found
   */
  public FreemarkerTemplate findByName(String name) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<FreemarkerTemplate> criteria = criteriaBuilder.createQuery(FreemarkerTemplate.class);
    Root<FreemarkerTemplate> root = criteria.from(FreemarkerTemplate.class);

    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(FreemarkerTemplate_.name), name));
    
    TypedQuery<FreemarkerTemplate> query = entityManager.createQuery(criteria);
    
    return getSingleResult(query);
  }

}
