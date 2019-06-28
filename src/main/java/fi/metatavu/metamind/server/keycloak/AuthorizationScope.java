package fi.metatavu.metamind.server.keycloak;

public enum AuthorizationScope {
  /**
   * Authorization scope for editing the data
   */
  REPLY_MANAGE ("reply:manage"),
  
  /**
   * Authorization scope for accessing the data
   */
  REPLY_ACCESS ("reply:access");
  
  private String name;
  
  private AuthorizationScope(String name) {
    this.name = name;
  }
  
  public String getName() {
    return name;
  }

}
