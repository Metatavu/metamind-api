package fi.metatavu.metamind.persistence.dao;

import java.util.UUID;
import fi.metatavu.metamind.persistence.models.*;
import fi.metatavu.metamind.rest.model.VariableType;

/**
 * DAO class for Variable
 * 
 * @author Antti Leppä
 */
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
