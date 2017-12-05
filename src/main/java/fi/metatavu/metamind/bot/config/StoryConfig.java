package fi.metatavu.metamind.bot.config;

public class StoryConfig {

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
  
}
