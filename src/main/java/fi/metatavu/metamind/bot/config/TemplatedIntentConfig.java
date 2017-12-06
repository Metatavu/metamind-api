package fi.metatavu.metamind.bot.config;

import java.util.List;

public class TemplatedIntentConfig extends AbstractIntentConfig {
  
  private List<String> utterances;
  
  public List<String> getUtterances() {
    return utterances;
  }
  
  public void setUtterances(List<String> utterances) {
    this.utterances = utterances;
  }
  
}
