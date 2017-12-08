package fi.metatavu.metamind.bot.config;

import java.util.Map;

public class MachineLearningConfig {

  private String intentModel;
  private Map<String, MachineLearningIntentConfig> intents;
  private Map<String, String> slotModels;
  
  public Map<String, MachineLearningIntentConfig> getIntents() {
    return intents;
  }
  
  public void setIntents(Map<String, MachineLearningIntentConfig> intents) {
    this.intents = intents;
  }
  
  public Map<String, String> getSlotModels() {
    return slotModels;
  }
  
  public void setSlotModels(Map<String, String> slotModels) {
    this.slotModels = slotModels;
  }

  public String getIntentModel() {
    return intentModel;
  }
  
  public void setIntentModel(String intentModel) {
    this.intentModel = intentModel;
  }
  
}
