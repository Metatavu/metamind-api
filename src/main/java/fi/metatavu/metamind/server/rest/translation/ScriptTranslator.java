package fi.metatavu.metamind.server.rest.translation;

import fi.metatavu.metamind.api.spec.model.Script;

import javax.enterprise.context.ApplicationScoped;

/**
 * Translator for translating JPA script entities into REST entities
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class ScriptTranslator {

  /**
   * Translates JPA script entity into REST entity
   * 
   * @param jpaScript JPA script
   * @return translated REST script entity
   */
  public Script translateScript(fi.metatavu.metamind.persistence.models.Script jpaScript) {
    if (jpaScript == null) {
      return null;
    }
  
    Script result = new Script();
    result.setContent(jpaScript.getContent());
    result.setCreatedAt(jpaScript.getCreatedAt());
    result.setId(jpaScript.getId());
    result.setLanguage(jpaScript.getLanguage());
    result.setModifiedAt(jpaScript.getModifiedAt());
    result.setName(jpaScript.getName());
    result.setVersion(jpaScript.getVersion());

    return result;
  }
  
}
