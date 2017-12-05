package fi.metatavu.metamind.bot.config;

import java.util.Map;

public class TemplateConfig {

  private Map<String, TemplatedIntentConfig> intents;
  
  public Map<String, TemplatedIntentConfig> getIntents() {
    return intents;
  }
  
  public void setIntents(Map<String, TemplatedIntentConfig> intents) {
    this.intents = intents;
  }
  
}
