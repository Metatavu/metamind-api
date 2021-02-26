package fi.metatavu.metamind.bot.config;

public class TokenizationConfig {
  
  private String comment;
  private Boolean untokenized;
  private Boolean stripWhitespace;
  
  public String getComment() {
    return comment;
  }
  
  public void setComment(String comment) {
    this.comment = comment;
  }
  
  public Boolean getStripWhitespace() {
    return stripWhitespace;
  }
  
  public void setStripWhitespace(Boolean stripWhitespace) {
    this.stripWhitespace = stripWhitespace;
  }
  
  public Boolean getUntokenized() {
    return untokenized;
  }
  
  public void setUntokenized(Boolean untokenized) {
    this.untokenized = untokenized;
  }
  
}
