package fi.metatavu.metamind.server.keycloak;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jboss.security.authorization.AuthorizationException;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.ResourcesResource;
import org.keycloak.admin.client.resource.ScopePermissionsResource;
import org.keycloak.admin.client.resource.UserPoliciesResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.representations.idm.authorization.DecisionEffect;
import org.keycloak.representations.idm.authorization.DecisionStrategy;
import org.keycloak.representations.idm.authorization.PolicyEvaluationRequest;
import org.keycloak.representations.idm.authorization.PolicyEvaluationResponse;
import org.keycloak.representations.idm.authorization.ResourceOwnerRepresentation;
import org.keycloak.representations.idm.authorization.ResourceRepresentation;
import org.keycloak.representations.idm.authorization.ScopePermissionRepresentation;
import org.keycloak.representations.idm.authorization.ScopeRepresentation;
import org.keycloak.representations.idm.authorization.UserPolicyRepresentation;
import org.slf4j.Logger;

import fi.metatavu.metamind.persistence.models.Story;
import fi.metatavu.metamind.story.StoryController;

/**
 * Class providing authentication keycloak system to evaluate permissions
 *  
 * @author DaniilSmirnov1
 *
 */
@ApplicationScoped
public class AuthenticationController {
  
  private static final String REALM = System.getProperty("keycloak-admin-realm");
  private static final String CLIENT_ID = System.getProperty("keycloak-admin-client-id");
  private static final String ADMIN_USER = System.getProperty("keycloak-admin-user");
  private static final String ADMIN_PASSWORD = System.getProperty("keycloak-admin-password");
  private static final String SERVER_URL = System.getProperty("keycloak-admin-server-url");
  private static final String CLIENT_SECRET = System.getProperty("keycloak-admin-client-secret");
  private static final String AUTHORIZATION_EXCEPTION_MESSAGE = "Client representation is NULL";
  
  @Inject 
  private static Logger logger;
  
  @Inject
  private StoryController storyController;
  
  /**
   * Creates protected resource into Keycloak
   * 
   * @param ownerId UUID ownerId
   * @param name String name
   * @param uri String uri
   * @param type String type
   * @param scopes List scopes
   * @return UUID created resource
   * @throws AuthorizationException
   */
  public UUID createProtectedResource(UUID ownerId, String name, String uri, String type, List<AuthorizationScope> scopes) throws AuthorizationException {
    Keycloak keycloak = getAdminClient();
    ResourceOwnerRepresentation owner = new ResourceOwnerRepresentation();
    owner.setId(getClientId());
    owner.setName(getClientId());
    
    ClientRepresentation client = getClient(keycloak);
    
    if (client == null) {
      throw new AuthorizationException(AUTHORIZATION_EXCEPTION_MESSAGE);
    }
    
    ResourcesResource resources = keycloak.realm(getRealmName()).clients().get(client.getId()).authorization().resources();

    Set<ScopeRepresentation> scopeRepresentations = scopes.stream()
      .map(AuthorizationScope::getName)
      .map(ScopeRepresentation::new)
      .collect(Collectors.toSet());
    
    ResourceRepresentation resource = new ResourceRepresentation(name, scopeRepresentations, uri, type);
    resources.create(resource);
    
    List<ResourceRepresentation> foundResources = resources.findByName(name);
    if (foundResources.isEmpty()) {
      return null;
    }
    
    if (foundResources.size() > 1) {
      logger.warn("Found more than one resource with name {}", name);
    }
    
    return UUID.fromString(foundResources.get(0).getId());
  }
  
  /**
   * Finds an existing resource
   * 
   * @param resource name
   * @return UUID found resource
   */
  public UUID findProtectedResource(String name) throws AuthorizationException {
    Keycloak keycloak = getAdminClient();
    ClientRepresentation client = getClient(keycloak);
    
    if (client == null) {
      throw new AuthorizationException(AUTHORIZATION_EXCEPTION_MESSAGE);
    }
    
    ResourcesResource resources = keycloak.realm(getRealmName()).clients().get(client.getId()).authorization().resources();
    List<ResourceRepresentation> foundResources = resources.findByName(name);
    List<String> foundResourcesIds = foundResources.stream().map(ResourceRepresentation::getId).collect(Collectors.toList());
    
    if (foundResourcesIds.isEmpty()) {
      return null;
    }
    
    return UUID.fromString(foundResourcesIds.get(0));
  }

  /**
   * Creates new scope permission for resource
   * 
   * @param resourceId UUID resourceId
   * @param scopes AuthorizationScope collection
   * @param name String name
   * @param decisionStrategy decisionStrategy
   * @param policyIds UUID collection
   * @throws AuthorizationException
   * @return String permission name
   */
  public String upsertScopePermission(UUID resourceId, Collection<AuthorizationScope> scopes, String name, DecisionStrategy decisionStrategy,
      Collection<UUID> policyIds) throws AuthorizationException {
    Keycloak keycloak = getAdminClient();
    ClientRepresentation client = getClient(keycloak);
    String realmName = getRealmName();
    
    if (client == null) {
      throw new AuthorizationException(AUTHORIZATION_EXCEPTION_MESSAGE);
    }
    
    RealmResource realm = keycloak.realm(realmName);
    ScopePermissionsResource scopeResource = realm.clients().get(client.getId()).authorization().permissions().scope();
    ScopePermissionRepresentation existingPermission = scopeResource.findByName(name);

    ScopePermissionRepresentation representation = new ScopePermissionRepresentation();
    representation.setResources(Collections.singleton(resourceId.toString()));
    representation.setDecisionStrategy(decisionStrategy);
    representation.setName(name);
    representation.setScopes(scopes.stream().map(AuthorizationScope::getName).collect(Collectors.toSet()));
    representation.setPolicies(policyIds.stream().map(UUID::toString).collect(Collectors.toSet()));

    String result = representation.getName();
    Response response = scopeResource.create(representation);
    try {
      
      if (existingPermission == null) {
        int status = response.getStatus();
        
        if (status != 201) {
          String message = "Unknown error";
          
          try {
            message = IOUtils.toString((InputStream) response.getEntity(), StandardCharsets.UTF_8);
          } catch (IOException e) {
            logger.warn("Failed read error message", e);
          }

          logger.warn("Failed to create scope permission for resource {} with message {}", resourceId, message);
        }
        
      } else {
        realm.clients().get(client.getId()).authorization().permissions().scope().findById(existingPermission.getId()).update(representation);
      }
      
    } finally {
      response.close();
    }
    
    return result;
  }

  /**
   * Updates users and user policies into Keycloak
   * 
   * @param keycloak admin client
   * @param realmName realm name
   * @param clientId client id
   * @param userMap users names
   */
  public List<UUID> updatePermissionUsers() throws AuthorizationException {
    List<UUID> result = new ArrayList();
    String realmName = getRealmName();
    Keycloak keycloak = getAdminClient();
    RealmResource realm = keycloak.realm(realmName);
    UsersResource users = realm.users();
    ClientRepresentation client = getClient(keycloak);
    
    if (client == null) {
      throw new AuthorizationException(AUTHORIZATION_EXCEPTION_MESSAGE);
    }
    
    UserPoliciesResource userPolicies = realm.clients().get(client.getId()).authorization().policies().user();
    List<UserRepresentation> existingUsers = users.list().stream().collect(Collectors.toList());
    
    for (UserRepresentation existingUser : existingUsers) {
      UUID userId = UUID.fromString(existingUser.getId());
      String userName = existingUser.getUsername();
      String policyNameTemplate = String.format("user-%s", userName);
      result.add(userId);
      UserPolicyRepresentation policyRepresentation = userPolicies.findByName(policyNameTemplate);
     
      if (policyRepresentation == null) {

        policyRepresentation = new UserPolicyRepresentation();
        policyRepresentation.setName(policyNameTemplate);
        policyRepresentation.setDecisionStrategy(DecisionStrategy.AFFIRMATIVE);
        policyRepresentation.addUser(userName);
        policyRepresentation.setId(userId.toString());
        userPolicies.create(policyRepresentation);
      }
    }
    
     return result; 
  }
  
  /**
   * Finds a id from Keycloak create response 
   * 
   * @param response response object
   * @return UUID create response id
   */
  public UUID getCreateResponseId(Response response) {
    if (response.getStatus() != 201) {
      try {
        if (logger.isErrorEnabled()) {
          logger.error("Failed to execute create: {}", IOUtils.toString((InputStream) response.getEntity(), StandardCharsets.UTF_8));
        }
      } catch (IOException e) {
        logger.error("Failed to extract error message", e);
      }
      
      return null;
    }
   
    String location = response.getHeaderString("location");
    if (StringUtils.isBlank(location)) {
      return null;
    }
    
    Pattern pattern = Pattern.compile(".*\\/(.*)$");
    Matcher matcher = pattern.matcher(location);
    
    if (matcher.find()) {
      return UUID.fromString(matcher.group(1));
    }
    
    return null;
  }
  
  /**
   * Evaluates eligibility of a user to access resources
   * 
   * @param loggedUserId UUID user id
   * @param scopes Authorization scopes
   * @return list of UUID id's that resources refer to
   */
  public List<UUID> resourceAccessEvaluate(UUID loggedUserId) {
    try {
      List<AuthorizationScope> scopes = Collections.singletonList(AuthorizationScope.STORY_ACCESS);
      Keycloak keycloak = getAdminClient();
      RealmResource realm = keycloak.realm(REALM);
      ClientRepresentation client = getClient(keycloak);
      List<UUID> storiesIdList = storyController.listStories().stream().map(Story::getId).collect(Collectors.toList());
      String userId = loggedUserId.toString();
      Map<UUID, DecisionEffect> result = new HashMap<>();
      
      for (UUID listedStoryid : storiesIdList) {
        String foundResourceName = String.format("story-%s", listedStoryid.toString());
        UUID foundResourceId = findProtectedResource(foundResourceName); 
        PolicyEvaluationRequest evaluationRequest = createEvaluationRequest(client, foundResourceId, foundResourceName, userId, scopes);
        PolicyEvaluationResponse response = realm.clients().get(client.getId()).authorization().policies().evaluate(evaluationRequest);
        result.put(listedStoryid, response.getStatus());
      }

      return result.entrySet().stream()
          .filter(entry -> DecisionEffect.PERMIT.equals(entry.getValue()))
          .map(Entry::getKey)
          .collect(Collectors.toList());
      
    } catch (AuthorizationException e) {
      logger.error("Failed to evaluate resource policy for {}", loggedUserId, e);
      return Collections.emptyList();
    }
  }
  
  /**
   * Returns admin client 
   * 
   * @return admin
   */
  private Keycloak getAdminClient() {
    return KeycloakBuilder.builder().serverUrl(SERVER_URL).realm(REALM).clientId(CLIENT_ID).clientSecret(CLIENT_SECRET).username(ADMIN_USER).password(ADMIN_PASSWORD).build();
  }

  /**
   * Returns String realm name
   * 
   * @return String realm name
   */
  private String getRealmName() {
    return REALM;
  }

  /**
   * Returns String client id
   * 
   * @return String CLIENT_ID
   */
  private String getClientId() {
    return CLIENT_ID;
  }

  /**
   * Returns the ClientRepresentation based on client id
   * 
   * @param keycloak
   * @return ClientRepresentation
   */
  private ClientRepresentation getClient(Keycloak keycloak) {
    List<ClientRepresentation> clients = keycloak.realm(getRealmName()).clients().findByClientId(getClientId());
    return clients.isEmpty() ? null : clients.get(0);
  }
  
  /**
   * Creates Keycloak policy evaluation request
   * 
   * @param client client
   * @param resourceId resource id
   * @param resourceName resource name
   * @param userId userId 
   * @param scopes scopes
   * @return created request
   */
  private PolicyEvaluationRequest createEvaluationRequest(ClientRepresentation client, UUID resourceId, String resourceName, String userId, Collection<AuthorizationScope> scopes) {
    Set<ScopeRepresentation> resourceScopes = scopes.stream().map(AuthorizationScope::getName).map(ScopeRepresentation::new).collect(Collectors.toSet());
    ResourceRepresentation resource = new ResourceRepresentation(resourceName, resourceScopes);
    resource.setId(resourceId.toString());
    
    PolicyEvaluationRequest evaluationRequest = new PolicyEvaluationRequest();
    evaluationRequest.setClientId(client.getId());
    evaluationRequest.setResources(Arrays.asList(resource));
    evaluationRequest.setUserId(userId);
    
    return evaluationRequest;
  }  

}
