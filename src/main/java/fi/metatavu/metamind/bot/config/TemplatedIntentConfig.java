package fi.metatavu.metamind.bot.config;

import java.util.List;

public class TemplatedIntentConfig {
  
  private List<String> utterances;
  private List<String> numberSlots;
  
  public List<String> getNumberSlots() {
    return numberSlots;
  }
  
  public void setNumberSlots(List<String> numberSlots) {
    this.numberSlots = numberSlots;
  }
  
  public List<String> getUtterances() {
    return utterances;
  }
  
  public void setUtterances(List<String> utterances) {
    this.utterances = utterances;
  }

}
