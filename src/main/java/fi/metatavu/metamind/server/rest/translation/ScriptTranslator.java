package fi.metatavu.metamind.server.rest.translation;

import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;

import fi.metatavu.metamind.server.rest.model.Script;

/**
 * Translator for translating JPA script entities into REST entities
 * 
 * @author Heikki Kurhinen
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
  
    Script script = new Script();
    script.setId(UUID.fromString(jpaScript.getExternalId())); 
    script.setName(jpaScript.getName());
    script.setVersion(jpaScript.getVersion());
    script.setLanguage(jpaScript.getLanguage());
    script.setContent(jpaScript.getContent());

    return script;
  }
  
}
