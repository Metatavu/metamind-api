package fi.metatavu.metamind.server.rest.security;

import javax.enterprise.context.RequestScoped;

import fi.metatavu.metamind.persistence.models.Client;

/**
 * Container for client
 * 
 * @author Antti Leppä
 */
@RequestScoped
public class ClientContainer {

  private Client client;
  
  /**
   * Sets current client
   * 
   * @param client client
   */
  public void setClient(Client client) {
    this.client = client;
  }
  
  /**
   * Returns current client
   * 
   * @return current client
   */
  public Client getClient() {
    return client;
  }
  
}
