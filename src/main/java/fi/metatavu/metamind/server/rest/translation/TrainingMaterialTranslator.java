package fi.metatavu.metamind.server.rest.translation;

import javax.enterprise.context.ApplicationScoped;

import fi.metatavu.metamind.rest.model.TrainingMaterial;

/**
 * Translator for translating JPA trainingMaterial entities into REST entities
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class TrainingMaterialTranslator {

  /**
   * Translates JPA trainingMaterial into REST trainingMaterial
   * 
   * @param jpaTrainingMaterial JPA trainingMaterial
   * @param quickResponses JPA quick responses
   * @param trainingMaterialResponses JPA trainingMaterial responses
   * @return REST trainingMaterial
   */
  public TrainingMaterial translateTrainingMaterial(fi.metatavu.metamind.persistence.models.TrainingMaterial jpaTrainingMaterial) {
    if (jpaTrainingMaterial == null) {
      return null;
    }

    TrainingMaterial result = new TrainingMaterial();
    result.setCreatedAt(jpaTrainingMaterial.getCreatedAt());
    result.setId(jpaTrainingMaterial.getId());
    result.setModifiedAt(jpaTrainingMaterial.getModifiedAt());
    result.setText(jpaTrainingMaterial.getText());
    result.setVisibility(jpaTrainingMaterial.getVisibility());
    result.setName(jpaTrainingMaterial.getName());
    result.setStoryId(jpaTrainingMaterial.getStory() != null ? jpaTrainingMaterial.getId() : null);
    result.setType(jpaTrainingMaterial.getType());
    
    return result;
  }

}
