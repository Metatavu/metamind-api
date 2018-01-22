package fi.metatavu.metamind.security;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.metamind.persistence.dao.ClientDAO;
import fi.metatavu.metamind.persistence.models.AccessType;
import fi.metatavu.metamind.persistence.models.Client;

/**
 * Controller for security related functions
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class SecurityController {
  
  @Inject
  private ClientDAO clientDAO;
  
  /**
   * Returns a client with given client id and secret or null if not found
   * 
   * @param clientId client id
   * @param clientSecret client secret
   * @return a client with given client id and secret or null if not found
   */
  public Client findClientByClientIdAndSecret(String clientId, String clientSecret) {
    return clientDAO.findByClientIdAndClientSecret(clientId, clientSecret);
  }
  
  /**
   * Returns whether client is an unrestricted client or not
   * 
   * @param client client
   * @return whether client is an unrestricted client or not
   */
  public boolean isUnrestrictedClient(Client client) {
    return client != null && client.getAccessType() == AccessType.UNRESTRICTED;
  }
  
}
