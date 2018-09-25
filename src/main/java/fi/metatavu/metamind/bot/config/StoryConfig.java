package fi.metatavu.metamind.bot.config;

import java.util.List;
import java.util.Map;

public class StoryConfig {

  private Integer maxAttemptsBeforeConfused;
  private String confusedKnotName;
  private List<MachineLearningConfig> machineLearningConfigs;
  private TemplateConfig template;
  private Map<String, String> globalIntents;
 
  public TemplateConfig getTemplate() {
    return template;
  }
  
  public void setTemplate(TemplateConfig template) {
    this.template = template;
  }
  
  public List<MachineLearningConfig> getMachineLearning() {
    return machineLearningConfigs;
  }
  
  public void setMachineLearning(List<MachineLearningConfig> machineLearningConfigs) {
    this.machineLearningConfigs = machineLearningConfigs;
  }
  
  public String getConfusedKnotName() {
    return confusedKnotName;
  }
  
  public void setConfusedKnotName(String confusedKnotName) {
    this.confusedKnotName = confusedKnotName;
  }
  
  public Integer getMaxAttemptsBeforeConfused() {
    return maxAttemptsBeforeConfused;
  }
  
  public void setMaxAttemptsBeforeConfused(Integer maxAttemptsBeforeConfused) {
    this.maxAttemptsBeforeConfused = maxAttemptsBeforeConfused;
  }
  
  public Map<String, String> getGlobalIntents() {
    return globalIntents;
  }
  
  public void setGlobalIntents(Map<String, String> globalIntents) {
    this.globalIntents = globalIntents;
  }
  
}
