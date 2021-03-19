package fi.metatavu.metamind.persistence.dao;

import fi.metatavu.metamind.persistence.models.Story;
import fi.metatavu.metamind.persistence.models.Variable;
import fi.metatavu.metamind.persistence.models.Variable_;
import fi.metatavu.metamind.api.spec.model.VariableType;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.UUID;

/**
 * DAO class for Variable
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class VariableDAO extends AbstractDAO<Variable> {

  /**
   * Creates new variable
   *
   * @param type type
   * @param story story
   * @param name name
   * @param validationScript validationScript
   * @param creatorId creator's id
   * @param lastModifierId last modifier's id
   * @return created variable
   */
  public Variable create(UUID id, VariableType type, Story story, String name, String validationScript, UUID creatorId, UUID lastModifierId) {
    Variable variable = new Variable();
    variable.setType(type);
    variable.setStory(story);
    variable.setName(name);
    variable.setValidationScript(validationScript);
    variable.setId(id);
    variable.setCreatorId(creatorId);
    variable.setLastModifierId(lastModifierId);
    return persist(variable);
  }
  
  /**
   * Lists variables by story
   * 
   * @param story story
   * @return List of variables
   */
  public List<Variable> listByStory(Story story) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Variable> criteria = criteriaBuilder.createQuery(Variable.class);
    Root<Variable> root = criteria.from(Variable.class);
   
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(Variable_.story), story));
    
    return entityManager.createQuery(criteria).getResultList();
  }

  /**
   * Finds variable by story and name
   * 
   * @param story story
   * @param variableName name
   * @return found variable or null if not found
   */
  public Variable findByStoryNameName(Story story, String variableName) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Variable> criteria = criteriaBuilder.createQuery(Variable.class);
    Root<Variable> root = criteria.from(Variable.class);
   
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(Variable_.story), story),
        criteriaBuilder.equal(root.get(Variable_.name), variableName)
      )
    );
    
    return getSingleResult(entityManager.createQuery(criteria));
  }

  /**
   * Updates type
   *
   * @param type type
   * @param lastModifierId last modifier's id
   * @return updated variable
   */
  public Variable updateType(Variable variable, VariableType type, UUID lastModifierId) {
    variable.setLastModifierId(lastModifierId);
    variable.setType(type);
    return persist(variable);
  }

  /**
   * Updates story
   *
   * @param story story
   * @param lastModifierId last modifier's id
   * @return updated variable
   */
  public Variable updateStory(Variable variable, Story story, UUID lastModifierId) {
    variable.setLastModifierId(lastModifierId);
    variable.setStory(story);
    return persist(variable);
  }

  /**
   * Updates name
   *
   * @param name name
   * @param lastModifierId last modifier's id
   * @return updated variable
   */
  public Variable updateName(Variable variable, String name, UUID lastModifierId) {
    variable.setLastModifierId(lastModifierId);
    variable.setName(name);
    return persist(variable);
  }

  /**
   * Updates validationScript
   *
   * @param validationScript validationScript
   * @param lastModifierId last modifier's id
   * @return updated variable
   */
  public Variable updateValidationScript(Variable variable, String validationScript, UUID lastModifierId) {
    variable.setLastModifierId(lastModifierId);
    variable.setValidationScript(validationScript);
    return persist(variable);
  }

}
