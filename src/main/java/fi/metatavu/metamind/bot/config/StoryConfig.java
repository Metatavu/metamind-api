package fi.metatavu.metamind.bot.config;

public class StoryConfig {

  private Integer maxAttemptsBeforeConfused;
  private String confusedKnotName;
  private MachineLearningConfig machineLearning;
  private TemplateConfig template;
 
  public TemplateConfig getTemplate() {
    return template;
  }
  
  public void setTemplate(TemplateConfig template) {
    this.template = template;
  }
  
  public MachineLearningConfig getMachineLearning() {
    return machineLearning;
  }
  
  public void setMachineLearning(MachineLearningConfig machineLearning) {
    this.machineLearning = machineLearning;
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
  
}
