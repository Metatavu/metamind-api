package fi.metatavu.metamind.bot.config;

import java.util.Map;

public class TemplateConfig {

  private Map<String, TemplatedIntentConfig> intents;
  private Map<String, TokenizationConfig> tokenization;
  
  public Map<String, TemplatedIntentConfig> getIntents() {
    return intents;
  }
  
  public void setIntents(Map<String, TemplatedIntentConfig> intents) {
    this.intents = intents;
  }
  
  public Map<String, TokenizationConfig> getTokenization() {
    return tokenization;
  }
  
  public void setTokenization(Map<String, TokenizationConfig> tokenization) {
    this.tokenization = tokenization;
  }
  
}
