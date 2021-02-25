package fi.metatavu.metamind.bot;

import java.util.UUID;

/**
 * CDI event for requesting knot training material update
 * 
 * @author Antti Leppä
 */
public class KnotTrainingMaterialUpdateRequestEvent {

  private UUID knotId;

  /**
   * Constructor
   * 
   * @param knotId knot id
   */
  public KnotTrainingMaterialUpdateRequestEvent(UUID knotId) {
    super();
    this.knotId = knotId;
  }

  public UUID getKnotId() {
    return knotId;
  }
  
}
