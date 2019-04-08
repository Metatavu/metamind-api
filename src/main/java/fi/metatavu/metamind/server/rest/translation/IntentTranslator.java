package fi.metatavu.metamind.server.rest.translation;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import fi.metatavu.metamind.persistence.models.IntentTrainingMaterial;
import fi.metatavu.metamind.persistence.models.TrainingMaterial;
import fi.metatavu.metamind.rest.model.Intent;
import fi.metatavu.metamind.rest.model.IntentTrainingMaterials;

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
   * @param trainingMaterials intent training materials
   * @return REST intent
   */
  public Intent translateIntent(fi.metatavu.metamind.persistence.models.Intent intent, List<TrainingMaterial> trainingMaterials) {
    TrainingMaterial openNlpDocatTrainingMaterial = null;
    TrainingMaterial openNlpNerTrainingMaterial = null;
    
    for (TrainingMaterial trainingMaterial : trainingMaterials) {
      switch (trainingMaterial.getType()) {
        case OPENNLPDOCCAT:
          openNlpDocatTrainingMaterial = trainingMaterial;
        break;
        case OPENNLPNER:
          openNlpNerTrainingMaterial = trainingMaterial;
        break;
      }
    }
    
    return translateIntent(intent, openNlpDocatTrainingMaterial, openNlpNerTrainingMaterial);
  }
  
  /**
   * Translates JPA intent into REST intent
   * 
   * @param jpaIntent JPA intent
   * @param openNlpNerTrainingMaterial OpenNLP training material for NER
   * @param openNlpDocatTrainingMaterial OpenNLP training material for Doccat
   * @return REST intent
   */
  public Intent translateIntent(fi.metatavu.metamind.persistence.models.Intent jpaIntent, IntentTrainingMaterial openNlpDocatTrainingMaterial, IntentTrainingMaterial openNlpNerTrainingMaterial) {
    return translateIntent(jpaIntent, openNlpDocatTrainingMaterial != null ? openNlpDocatTrainingMaterial.getTrainingMaterial() : null, openNlpNerTrainingMaterial != null ? openNlpNerTrainingMaterial.getTrainingMaterial() : null);
  }
  
  /**
   * Translates JPA intent into REST intent
   * 
   * @param jpaIntent JPA intent
   * @param openNlpNerTrainingMaterial OpenNLP training material for NER
   * @param openNlpDocatTrainingMaterial OpenNLP training material for Doccat
   * @return REST intent
   */
  public Intent translateIntent(fi.metatavu.metamind.persistence.models.Intent jpaIntent, TrainingMaterial openNlpDocatTrainingMaterial, TrainingMaterial openNlpNerTrainingMaterial) {
    if (jpaIntent == null) {
      return null;
    }
    
    IntentTrainingMaterials trainingMaterials = new IntentTrainingMaterials();
    trainingMaterials.setOpenNlpDoccatId(openNlpDocatTrainingMaterial != null ? openNlpDocatTrainingMaterial.getId() : null);
    trainingMaterials.setOpenNlpNerId(openNlpNerTrainingMaterial != null ? openNlpNerTrainingMaterial.getId() : null);
    
    Intent result = new Intent();
    result.setCreatedAt(jpaIntent.getCreatedAt());
    result.setId(jpaIntent.getId());
    result.setModifiedAt(jpaIntent.getModifiedAt());
    result.setGlobal(jpaIntent.getGlobal());
    result.setSourceKnotId(jpaIntent.getSourceKnot() != null ? jpaIntent.getSourceKnot().getId() : null);
    result.setTargetKnotId(jpaIntent.getTargetKnot() != null ? jpaIntent.getTargetKnot().getId() : null);
    result.setType(jpaIntent.getType());
    result.setName(jpaIntent.getName());
    result.setTrainingMaterials(trainingMaterials);
    result.setQuickResponse(jpaIntent.getQuickResponse());
    
    return result;
  }

}
