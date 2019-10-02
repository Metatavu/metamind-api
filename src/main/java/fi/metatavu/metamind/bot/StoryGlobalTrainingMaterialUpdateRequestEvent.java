package fi.metatavu.metamind.bot;

import java.util.UUID;

/**
 * CDI event for requesting story global training material update
 * 
 * @author Antti Leppä
 */
public class StoryGlobalTrainingMaterialUpdateRequestEvent {

  private UUID storyId;

  /**
   * Constructor
   * 
   * @param storyId story id
   */
  public StoryGlobalTrainingMaterialUpdateRequestEvent(UUID storyId) {
    super();
    this.storyId = storyId;
  }

  public UUID getStoryId() {
    return storyId;
  }
  
}
