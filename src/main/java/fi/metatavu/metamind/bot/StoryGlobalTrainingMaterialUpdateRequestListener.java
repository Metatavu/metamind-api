package fi.metatavu.metamind.bot;

import java.io.IOException;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.event.TransactionPhase;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.slf4j.Logger;

import fi.metatavu.metamind.nlp.TrainingMaterialController;
import fi.metatavu.metamind.story.StoryController;

/**
 * CDI event training material update listener
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class StoryGlobalTrainingMaterialUpdateRequestListener {
  
  @Inject
  private Logger logger;

  @Inject
  private TrainingMaterialController trainingMaterialController;
  
  @Inject
  private StoryController storyController;

  /**
   * Event handler for training material update
   * 
   * @param event event
   */
  @Transactional (value = TxType.REQUIRES_NEW)
  public void onStoryGlobalTrainingMaterialUpdateRequestEvent(@Observes (during = TransactionPhase.AFTER_SUCCESS) StoryGlobalTrainingMaterialUpdateRequestEvent event) {
    try {
      updateStory(event.getStoryId());
    } catch (IOException e) {
      logger.error("Failed to update story global training material", e);
    }
  }

  /**
   * Updates story training data for related intents
   * 
   * @param storyId story id
   * @throws IOException thrown when training data building fails
   */
  private void updateStory(UUID storyId) throws IOException {
    trainingMaterialController.updateStoryTrainingMaterial(storyController.findStoryById(storyId));
  }
  
}
