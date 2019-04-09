package fi.metatavu.metamind.server.rest.translation;

import javax.enterprise.context.ApplicationScoped;

import fi.metatavu.metamind.rest.model.Variable;

/**
 * Translator for translating JPA variable entities into REST entities
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class VariableTranslator {

  /**
   * Translates JPA variable into REST variable
   * 
   * @param jpaVariable JPA variable
   * 
   * @return REST variable
   */
  public Variable translateVariable(fi.metatavu.metamind.persistence.models.Variable jpaVariable) {
    if (jpaVariable == null) {
      return null;
    }

    Variable result = new Variable();
    result.setCreatedAt(jpaVariable.getCreatedAt());
    result.setId(jpaVariable.getId());
    result.setModifiedAt(jpaVariable.getModifiedAt());
    result.setValidationScript(jpaVariable.getValidationScript());
    result.setName(jpaVariable.getName());
    result.setStoryId(jpaVariable.getStory() != null ? jpaVariable.getStory().getId() : null);
    result.setType(jpaVariable.getType());

    return result;
  }

}
