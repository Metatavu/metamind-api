package fi.metatavu.metamind.models;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.metamind.persistence.dao.IntentModelDAO;
import fi.metatavu.metamind.persistence.dao.SlotModelDAO;
import fi.metatavu.metamind.persistence.models.IntentModel;
import fi.metatavu.metamind.persistence.models.SlotModel;

/**
 * Model controller
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class ModelsContoller {
  
  @Inject
  private SlotModelDAO slotModelDAO;

  @Inject
  private IntentModelDAO intentModelDAO;

  /**
   * Finds a slot model by name
   * 
   * @param name name
   * @return found slot model or null if not found
   */
  public SlotModel findSlotModelByName(String name) {
    return slotModelDAO.findByName(name);
  }

  /**
   * Finds a intent model by name
   * 
   * @param name name
   * @return found intent model or null if not found
   */
  public IntentModel findIntentModelByName(String name) {
    return intentModelDAO.findByName(name);
  }

  /**
   * Sets intent model's data by name. If model does not exist new is created
   * 
   * @param name name
   * @param data data
   * @return updated or created intent model
   */
  public IntentModel setIntentModelData(String name, byte[] data) {
    IntentModel intentModel = findIntentModelByName(name);
    if (intentModel != null) {
      return intentModelDAO.updateData(intentModel, data);
    }
    
    return intentModelDAO.create(name, data);
  }

  /**
   * Sets slot model's data by name. If model does not exist new is created
   * 
   * @param name name
   * @param data data
   * @return updated or created slot model
   */
  public SlotModel setSlotModelData(String name, byte[] data) {
    SlotModel slotModel = findSlotModelByName(name);
    if (slotModel != null) {
      return slotModelDAO.updateData(slotModel, data);
    }
    
    return slotModelDAO.create(name, data);
  }
  
}
