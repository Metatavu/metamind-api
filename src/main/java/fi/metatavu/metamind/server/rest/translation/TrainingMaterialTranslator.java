package fi.metatavu.metamind.server.rest.translation;

import fi.metatavu.metamind.api.spec.model.TrainingMaterial;

import javax.enterprise.context.ApplicationScoped;

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
    result.setStoryId(jpaTrainingMaterial.getStory() != null ? jpaTrainingMaterial.getStory().getId() : null);
    result.setType(jpaTrainingMaterial.getType());
    
    return result;
  }

}
