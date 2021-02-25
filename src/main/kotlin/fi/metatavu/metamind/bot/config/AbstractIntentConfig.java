package fi.metatavu.metamind.bot.config;

import java.util.List;

public class AbstractIntentConfig {
  
  private List<String> numberSlots;
  private List<RegexSlot> regExSlots;
  private List<String> textSlots;

  public List<String> getNumberSlots() {
    return numberSlots;
  }
  
  public void setNumberSlots(List<String> numberSlots) {
    this.numberSlots = numberSlots;
  }
  
  public List<RegexSlot> getRegExSlots() {
    return regExSlots;
  }
  
  public void setRegExSlots(List<RegexSlot> regExSlots) {
    this.regExSlots = regExSlots;
  }
  
  public List<String> getTextSlots() {
    return textSlots;
  }
  
  public void setTextSlots(List<String> textSlots) {
    this.textSlots = textSlots;
  }

}
