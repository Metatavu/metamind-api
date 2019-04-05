package fi.metatavu.metamind.server.rest.translation;

import javax.enterprise.context.ApplicationScoped;

import fi.metatavu.metamind.rest.model.Intent;

/**
 * Translator for translating JPA intent entities into REST entities
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class IntentTranslator {

  /**
   * Translates JPA intent into REST intent
   * 
   * @param jpaIntent JPA intent
   * @param quickResponses JPA quick responses
   * @param intentResponses JPA intent responses
   * @return REST intent
   */
  public Intent translateIntent(fi.metatavu.metamind.persistence.models.Intent jpaIntent) {
    if (jpaIntent == null) {
      return null;
    }
    
    Intent result = new Intent();
    result.setCreatedAt(jpaIntent.getCreatedAt());
    result.setId(jpaIntent.getId());
    result.setModifiedAt(jpaIntent.getModifiedAt());
    result.setGlobal(jpaIntent.getGlobal());
    result.setSourceKnotId(jpaIntent.getSourceKnot() != null ? jpaIntent.getSourceKnot().getId() : null);
    result.setTargetKnotId(jpaIntent.getTargetKnot() != null ? jpaIntent.getTargetKnot().getId() : null);
    result.setType(jpaIntent.getType());
    result.setName(jpaIntent.getName());
    result.setTrainingMaterialId(jpaIntent.getTrainingMaterial() != null ? jpaIntent.getTrainingMaterial().getId() : null);

    return result;
  }

}
