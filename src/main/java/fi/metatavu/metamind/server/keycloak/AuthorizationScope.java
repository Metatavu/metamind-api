package fi.metatavu.metamind.server.keycloak;

public enum AuthorizationScope {
  /**
   * Authorization scope for editing the data
   */
  STORY_MANAGE ("story:manage"),
  
  /**
   * Authorization scope for accessing the data
   */
  STORY_ACCESS ("story:access");
  
  private String name;
  
  private AuthorizationScope(String name) {
    this.name = name;
  }
  
  public String getName() {
    return name;
  }

}
