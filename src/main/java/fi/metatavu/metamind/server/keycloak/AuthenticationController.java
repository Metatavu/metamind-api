package fi.metatavu.metamind.server.keycloak;

import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.security.PublicKey;
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
import javax.security.auth.Subject;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.admin.client.resource.GroupPoliciesResource;
import org.keycloak.admin.client.resource.GroupsResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.ResourcePermissionsResource;
import org.keycloak.admin.client.resource.ResourcesResource;
import org.keycloak.admin.client.resource.ScopePermissionsResource;
import org.keycloak.admin.client.resource.UserPoliciesResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.admin.client.token.TokenManager;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.IDToken;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.representations.idm.UserSessionRepresentation;
import org.keycloak.representations.idm.authorization.DecisionEffect;
import org.keycloak.representations.idm.authorization.DecisionStrategy;
import org.keycloak.representations.idm.authorization.GroupPolicyRepresentation;
import org.keycloak.representations.idm.authorization.PolicyEvaluationRequest;
import org.keycloak.representations.idm.authorization.PolicyEvaluationResponse;
import org.keycloak.representations.idm.authorization.ResourceOwnerRepresentation;
import org.keycloak.representations.idm.authorization.ResourcePermissionRepresentation;
import org.keycloak.representations.idm.authorization.ResourceRepresentation;
import org.keycloak.representations.idm.authorization.ScopePermissionRepresentation;
//import org.keycloak.representations.idm.authorization.ScopePermissionRepresentation;
import org.keycloak.representations.idm.authorization.ScopeRepresentation;
import org.keycloak.representations.idm.authorization.UserPolicyRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Iterables;

import fi.metatavu.metamind.persistence.models.Session;


@ApplicationScoped
public class AuthenticationController {
  
  private static Logger logger = LoggerFactory.getLogger(AuthenticationController.class);
  
  public Keycloak getAdminClient() {
    String serverUrl = "http://localhost:8280/auth/";
    return KeycloakBuilder.builder().serverUrl(serverUrl).realm("test").clientId("api").clientSecret("1be749de-2023-4ebf-aba0-90679ffab56b").username("admin").password("admin").build();
  }

  private String getRealmName() {
    return "test";
  }

  private String getClientId() {
    return "api";
  }

  private ClientRepresentation getClient(Keycloak keycloak) {
    List<ClientRepresentation> clients = keycloak.realm(getRealmName()).clients().findByClientId(getClientId());
    return clients.isEmpty() ? null : clients.get(0);
  }
   
  /**
   * Creates protected resource into Keycloak
   * 
   * @param ownerId
   * @param name
   * @param uri
   * @param type
   * @param scopes
   * @return
   */
  public UUID createProtectedResource(UUID ownerId, String name, String uri, String type, List<AuthorizationScope> scopes) {
    Keycloak keycloak = getAdminClient();
    ResourceOwnerRepresentation owner = new ResourceOwnerRepresentation();
    owner.setId(getClientId());
    owner.setName(getClientId());
    
    ClientRepresentation client = getClient(keycloak);
    ResourcesResource resources = keycloak.realm(getRealmName()).clients().get(client.getId()).authorization().resources();

    Set<ScopeRepresentation> scopeRepresentations = scopes.stream()
      .map(AuthorizationScope::getName)
      .map(ScopeRepresentation::new)
      .collect(Collectors.toSet());
    
    ResourceRepresentation resource = new ResourceRepresentation(name, scopeRepresentations, uri, type);
    resources.create(resource);
//    System.out.println("!!!CREATED RESOURCE _----- " + resource.getName());
    
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
   * @return
   */
  public UUID findProtectedResource(String name) {
    Keycloak keycloak = getAdminClient();
    ResourceOwnerRepresentation owner = new ResourceOwnerRepresentation();
    ClientRepresentation client = getClient(keycloak);
    ResourcesResource resources = keycloak.realm(getRealmName()).clients().get(client.getId()).authorization().resources();
    List<ResourceRepresentation> foundResources = resources.findByName(name);
    List<String> dsdStrings = foundResources.stream().map(ResourceRepresentation::getId).collect(Collectors.toList());
    if (dsdStrings.isEmpty()) {
      return null;
    }
    UUID foundResourceId = UUID.fromString(dsdStrings.get(0));
    
    return foundResourceId;
  }

  /**
   * Creates new scope permission for resource
   * 
   * @param realmName String realmName
   * @param resourceId UUID resourceId
   * @param scopes AuthorizationScope collection
   * @param name String name
   * @param decisionStrategy decisionStrategy
   * @param policyIds UUID list
   */
  public String upsertResourcePermission(String realmName, UUID resourceId, Collection<AuthorizationScope> scopes,
      String name, DecisionStrategy decisionStrategy, List<UUID> policyIds) {
    String result = new String();
    Keycloak keycloak = getAdminClient();
    ClientRepresentation client = getClient(keycloak);
    
    
    RealmResource realm = keycloak.realm(realmName);    
    ResourcePermissionsResource resourceResource = realm.clients().get(client.getId()).authorization().permissions().resource();    
    ResourcePermissionRepresentation existingPermission = resourceResource.findByName(name);
    
    
    ResourcePermissionRepresentation representation = new ResourcePermissionRepresentation();
    representation.setDecisionStrategy(decisionStrategy);
    representation.setName(name);
    representation.setResources(Collections.singleton(resourceId.toString()));
    representation.setPolicies(policyIds.stream().map(UUID::toString).collect(Collectors.toSet()));
    
    result = representation.getName();
    Response response = resourceResource.create(representation);
    try {
      if (existingPermission == null) {
        int status = response.getStatus();
        if (status != 201) {
          String message = "Unknown error";
          try {
            message = IOUtils.toString((InputStream) response.getEntity(), "UTF-8");
          } catch (IOException e) {
            logger.warn("Failed read error message", e);
          }
          
          logger.warn("Failed to create resource permission for resource {} with message {}", resourceId, message);
        }
      } else {
        realm.clients().get(client.getId()).authorization().permissions().resource().findById(existingPermission.getId()).update(representation);
      }
    } finally {
      response.close();
    }
    return result;
  }
  
  /**
   * Creates new scope permission for resource
   * 
   * @param realmName String realmName
   * @param resourceId UUID resourceId
   * @param scopes AuthorizationScope collection
   * @param name String name
   * @param decisionStrategy decisionStrategy
   * @param policyIds UUID collection
   */
  public String upsertScopePermission(String realmName, UUID resourceId, Collection<AuthorizationScope> scopes, String name, DecisionStrategy decisionStrategy,
      Collection<UUID> policyIds) {
    String result = new String();
    Keycloak keycloak = getAdminClient();
    ClientRepresentation client = getClient(keycloak);

    RealmResource realm = keycloak.realm(realmName);
    ScopePermissionsResource scopeResource = realm.clients().get(client.getId()).authorization().permissions().scope();
    ScopePermissionRepresentation existingPermission = scopeResource.findByName(name);

    ScopePermissionRepresentation representation = new ScopePermissionRepresentation();
    representation.setResources(Collections.singleton(resourceId.toString()));
    representation.setDecisionStrategy(decisionStrategy);
    representation.setName(name);
    representation.setScopes(scopes.stream().map(AuthorizationScope::getName).collect(Collectors.toSet()));
    representation.setPolicies(policyIds.stream().map(UUID::toString).collect(Collectors.toSet()));

    result = representation.getName();
    Response response = scopeResource.create(representation);
    try {
      if (existingPermission == null) {
        int status = response.getStatus();
        if (status != 201) {
          String message = "Unknown error";
          try {
            message = IOUtils.toString((InputStream) response.getEntity(), "UTF-8");
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
   * Updates groups and group policies into Keycloak
   * 
   * @param keycloak admin client
   * @param realmName realm name
   * @param clientId client id
   * @param groupMap groups names
   */
  public List<UUID> updatePermissionGroups(String realmName, List<String> groupNames) {
    List<UUID> result = new ArrayList();
    Keycloak keycloak = getAdminClient();
    RealmResource realm = keycloak.realm(realmName);
    GroupsResource groups = realm.groups();
    ClientRepresentation client = getClient(keycloak);
    GroupPoliciesResource groupPolicies = realm.clients().get(client.getId()).authorization().policies().group();
    
    Map<String, UUID> existingGroups = groups.groups().stream()
      .collect(Collectors.toMap(GroupRepresentation::getName, group -> UUID.fromString(group.getId())));
    
    for (String groupName : groupNames) {
      UUID groupId = existingGroups.get(groupName);
      if (groupId == null) {
        GroupRepresentation groupRepresentation = new GroupRepresentation();
        groupRepresentation.setName(groupName);
        groupId = getCreateResponseId(groups.add(groupRepresentation));  
        
      }
      
      GroupPolicyRepresentation policyRepresentation = groupPolicies.findByName(groupName);
      if (policyRepresentation == null && groupId != null) {
        groupPolicies.create(policyRepresentation);
        policyRepresentation = new GroupPolicyRepresentation();
        policyRepresentation.setName(groupName);
        policyRepresentation.setDecisionStrategy(DecisionStrategy.UNANIMOUS);
        policyRepresentation.addGroup(groupId.toString(), true);
        groupPolicies.create(policyRepresentation);
        
        result.add(groupId);
       
        
      }
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
  public List<UUID> updatePermissionUsers(String realmName, List<String> userNames) {
    List<UUID> result = new ArrayList();
    Keycloak keycloak = getAdminClient();
    RealmResource realm = keycloak.realm(realmName);
    UsersResource users = realm.users();
    ClientRepresentation client = getClient(keycloak);
    UserPoliciesResource userPolicies = realm.clients().get(client.getId()).authorization().policies().user();
    
    List<UserRepresentation> existingUsers = users.list().stream().collect(Collectors.toList());
    
    for (UserRepresentation userRep : existingUsers) {
      UUID userId = UUID.fromString(userRep.getId());
      String userName = userRep.getUsername();
      
      result.add(userId);

      UserPolicyRepresentation policyRepresentation = userPolicies.findByName("user-" + userName);
      if (policyRepresentation == null) {

        policyRepresentation = new UserPolicyRepresentation();
        policyRepresentation.setName("user-" + userName);
        policyRepresentation.setDecisionStrategy(DecisionStrategy.AFFIRMATIVE);
        policyRepresentation.addUser(userName);
        policyRepresentation.setId(userId.toString());
        userPolicies.create(policyRepresentation);
        String policyId = policyRepresentation.getId();

      }
    }
     return result; 
    }
    

  
  /**
   * Returns list of permitted users for a resource with given scopes 
   * 
   * @param keycloak keycloak client instance
   * @param realmName realm name
   * @param clientId client id
   * @param resourceId resource id
   * @param resourceName resource name
   * @param scopes scopes
   * @return set of user ids
   */
  public Set<UUID> getResourcePermittedUsers(String realmName, UUID resourceId, String resourceName, List<AuthorizationScope> scopes) {
    Keycloak keycloak = getAdminClient();
    ClientRepresentation client = getClient(keycloak);
    RealmResource realm = keycloak.realm(realmName);
    System.out.println("Resource permitted users: " + (getPermittedUsers(realm, client, resourceId, resourceName, scopes)));
    return getPermittedUsers(realm, client, resourceId, resourceName, scopes);      
  }
  
  /**
   * Finds a id from Keycloak create response 
   * 
   * @param response response object
   * @return
   */
  private static UUID getCreateResponseId(Response response) {
    if (response.getStatus() != 201) {
      try {
        if (logger.isErrorEnabled()) {
          logger.error("Failed to execute create: {}", IOUtils.toString((InputStream) response.getEntity(), "UTF-8"));
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
  
  private static Set<UUID> getPermittedUsers(RealmResource realm, ClientRepresentation client, UUID resourceId, String resourceName, List<AuthorizationScope> scopes) {
    Map<UUID, DecisionEffect> policies = evaluatePolicies(realm, client, resourceId, resourceName, scopes);
    
    return policies.entrySet().stream()
        .filter(entry -> DecisionEffect.PERMIT.equals(entry.getValue()))
        .map(Entry::getKey)
        .collect(Collectors.toSet());
  }
  
  /**
   * Evaluates policies resource for realm users 
   * 
   * @param realm realm name
   * @param client client
   * @param resourceId resource id
   * @param resourceName resource name
   * @param scopes scopes
   * @return map of results where key is user id and value is decision
   */
  private static Map<UUID, DecisionEffect> evaluatePolicies(RealmResource realm, ClientRepresentation client, UUID resourceId, String resourceName, List<AuthorizationScope> scopes) {
    Map<UUID, DecisionEffect> result = new HashMap<>();
    int firstResult = 0;
    int maxResults = 10;
    
    while (firstResult < 1000) {
      List<UserRepresentation> users = realm.users().list(firstResult, maxResults);
      
      for (UserRepresentation user : users) {
        String userId = user.getId();
        result.put(UUID.fromString(userId), evaluatePolicy(realm, client, resourceId, resourceName, userId, scopes));
      }
      
      if (users.isEmpty() || (users.size() < maxResults)) {
        break;
      }

      firstResult += maxResults;
    }
    
    return result;
  }
  
  /**
   * Evaluates policy for a resource
   * 
   * @param realm realm name
   * @param client client
   * @param resourceId resource id
   * @param resourceName resource name
   * @param userId user's id
   * @param scopes scopes
   * @return decision
   */
  private static DecisionEffect evaluatePolicy(RealmResource realm, ClientRepresentation client, UUID resourceId, String resourceName, String userId, List<AuthorizationScope> scopes) {
    try {
      PolicyEvaluationRequest evaluationRequest = createEvaluationRequest(client, resourceId, resourceName, userId, scopes);
      PolicyEvaluationResponse response = realm.clients().get(client.getId()).authorization().policies().evaluate(evaluationRequest);
      return response.getStatus();
    } catch (InternalServerErrorException e) {
      logger.error("Failed to evaluate resource {} policy for {}", resourceName, userId, e);
      return DecisionEffect.DENY;
    }
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
  private static PolicyEvaluationRequest createEvaluationRequest(ClientRepresentation client, UUID resourceId, String resourceName, String userId, Collection<AuthorizationScope> scopes) {
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
