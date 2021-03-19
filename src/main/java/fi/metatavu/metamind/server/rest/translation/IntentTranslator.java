package fi.metatavu.metamind.server.rest.translation;

import fi.metatavu.metamind.persistence.models.IntentTrainingMaterial;
import fi.metatavu.metamind.persistence.models.TrainingMaterial;
import fi.metatavu.metamind.api.spec.model.Intent;
import fi.metatavu.metamind.api.spec.model.IntentTrainingMaterials;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

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
   * @param intent JPA intent
   * @param trainingMaterials intent training materials
   * @return REST intent
   */
  public Intent translateIntent(fi.metatavu.metamind.persistence.models.Intent intent, List<TrainingMaterial> trainingMaterials) {
    TrainingMaterial intentOpenNlpDocatTrainingMaterial = null;
    TrainingMaterial intentRegexTrainingMaterial = null;
    TrainingMaterial variableOpenNlpNerTrainingMaterial = null;
    TrainingMaterial variableOpenNlpRegexTrainingMaterial = null;
    
    for (TrainingMaterial trainingMaterial : trainingMaterials) {
      switch (trainingMaterial.getType()) {
        case INTENTOPENNLPDOCCAT:
          intentOpenNlpDocatTrainingMaterial = trainingMaterial;
        break;
        case VARIABLEOPENNLPNER:
          variableOpenNlpNerTrainingMaterial = trainingMaterial;
        break;
        case INTENTREGEX:
          intentRegexTrainingMaterial = trainingMaterial;
        break;
        case VARIABLEOPENNLPREGEX:
          variableOpenNlpRegexTrainingMaterial = trainingMaterial;
        break;
      }
    }
    
    return translateIntent(intent, intentOpenNlpDocatTrainingMaterial, variableOpenNlpNerTrainingMaterial, intentRegexTrainingMaterial, variableOpenNlpRegexTrainingMaterial);
  }
  
  /**
   * Translates JPA intent into REST intent
   * 
   * @param jpaIntent JPA intent
   * @param variableOpenNlpNerTrainingMaterial OpenNLP training material for NER
   * @param intentRegexTrainingMaterial OpenNLP training material for Doccat
   * @return REST intent
   */
  public Intent translateIntent(fi.metatavu.metamind.persistence.models.Intent jpaIntent, IntentTrainingMaterial intentOpenNlpDocatTrainingMaterial, IntentTrainingMaterial variableOpenNlpNerTrainingMaterial, IntentTrainingMaterial intentRegexTrainingMaterial, IntentTrainingMaterial variableOpenNlpRegexTrainingMaterial) {
    return translateIntent(jpaIntent, 
      intentOpenNlpDocatTrainingMaterial != null ? intentOpenNlpDocatTrainingMaterial.getTrainingMaterial() : null, 
      variableOpenNlpNerTrainingMaterial != null ? variableOpenNlpNerTrainingMaterial.getTrainingMaterial() : null,
      intentRegexTrainingMaterial != null ? intentRegexTrainingMaterial.getTrainingMaterial() : null,
      variableOpenNlpRegexTrainingMaterial != null ? variableOpenNlpRegexTrainingMaterial.getTrainingMaterial() : null
    );
  }

  /**
   * Translates JPA intent into REST intent
   *
   * @param jpaIntent JPA intent
   * @param intentOpenNlpDocatTrainingMaterial Docat training material
   * @param variableOpenNlpNerTrainingMaterial OpenNLP training material for NER
   * @param intentRegexTrainingMaterial Regex training material
   * @param variableOpenNlpRegexTrainingMaterial OpenNLP training material
   * @return REST intent
   */
  public Intent translateIntent(fi.metatavu.metamind.persistence.models.Intent jpaIntent, TrainingMaterial intentOpenNlpDocatTrainingMaterial, TrainingMaterial variableOpenNlpNerTrainingMaterial, TrainingMaterial intentRegexTrainingMaterial, TrainingMaterial variableOpenNlpRegexTrainingMaterial) {
    if (jpaIntent == null) {
      return null;
    }
    
    IntentTrainingMaterials trainingMaterials = new IntentTrainingMaterials();
    trainingMaterials.setIntentOpenNlpDoccatId(intentOpenNlpDocatTrainingMaterial != null ? intentOpenNlpDocatTrainingMaterial.getId() : null);
    trainingMaterials.setIntentRegexId(intentRegexTrainingMaterial != null ? intentRegexTrainingMaterial.getId() : null);
    trainingMaterials.setVariableOpenNlpNerId(variableOpenNlpNerTrainingMaterial != null ? variableOpenNlpNerTrainingMaterial.getId() : null);
    trainingMaterials.setVariableOpenNlpRegex(variableOpenNlpRegexTrainingMaterial != null ? variableOpenNlpRegexTrainingMaterial.getId() : null);
    
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
    result.setQuickResponseOrder(jpaIntent.getQuickResponseOrder());
    
    return result;
  }

}
