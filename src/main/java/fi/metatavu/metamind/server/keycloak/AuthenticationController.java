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
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.jboss.security.authorization.AuthorizationException;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.ResourcesResource;
import org.keycloak.admin.client.resource.ScopePermissionsResource;
import org.keycloak.admin.client.resource.UserPoliciesResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.ClientRepresentation;
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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import fi.metatavu.metamind.persistence.models.Story;

/**
 * Class providing authentication keycloak system to evaluate permissions
 *  
 * @author DaniilSmirnov1
 *
 */
@ApplicationScoped
public class AuthenticationController {
  private static final String REALM = getRealm();
  private static final String CLIENT_ID = getClientId();
  private static final String ADMIN_USER = getAdminUser();
  private static final String ADMIN_PASSWORD = getAdminPassword();
  private static final String SERVER_URL = getServerUrl();
  private static final String CLIENT_SECRET = getClientSecret();
  private static final String AUTHORIZATION_EXCEPTION_MESSAGE = "Client representation is NULL";
  
  @Inject 
  private static Logger logger;
  
  private static final String getRealm() {
    return System.getenv("KEYCLOAK_REALM") != null ? System.getenv("KEYCLOAK_REALM") : System.getProperty("keycloak-admin-realm");
  }
  
  private static final String getClientId() {
    return System.getenv("KEYCLOAK_ADMIN_CLIENT_ID") != null ? System.getenv("KEYCLOAK_ADMIN_CLIENT_ID") : System.getProperty("keycloak-admin-client-id");
  }
  
  private static final String getAdminUser() {
    return System.getenv("KEYCLOAK_ADMIN_USER") != null ? System.getenv("KEYCLOAK_ADMIN_USER") : System.getProperty("keycloak-admin-user");
  }
  
  private static final String getAdminPassword() {
    return System.getenv("KEYCLOAK_ADMIN_PASS") != null ? System.getenv("KEYCLOAK_ADMIN_PASS") : System.getProperty("keycloak-admin-password");
  }
  
  private static final String getServerUrl() {
    return System.getenv("KEYCLOAK_URL") != null ? System.getenv("KEYCLOAK_URL") : System.getProperty("keycloak-admin-server-url");
  }
  
  private static final String getClientSecret() {
    return System.getenv("KEYCLOAK_ADMIN_CLIENT_SECRET") != null ? System.getenv("KEYCLOAK_ADMIN_CLIENT_SECRET") : System.getProperty("keycloak-admin-client-secret");
  }
  
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
    
    System.out.println("--------------------------------");
    System.out.println("1");
    
    ResourceRepresentation resource = new ResourceRepresentation(name, scopeRepresentations, uri, type);
    System.out.println("2");
    
    Response createResponse = resources.create(resource);
    System.out.println("3");
    System.out.println(createResponse.getStatus());
    System.out.println("4");
    
    System.out.println("--------------------------------");
    
    List<ResourceRepresentation> foundResources = resources.findByName(name);
    if (foundResources.isEmpty()) {
      return null;
    }
    System.out.println("we are getting resources");
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
  public String upsertScopePermission(UUID resourceId, Collection<AuthorizationScope> scopes, String name, DecisionStrategy decisionStrategy, UUID policyId) throws AuthorizationException {
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
    representation.setPolicies(Collections.singleton(policyId.toString()));

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
   * Assures that logged user has an existing policy
   * 
   * @param keycloak admin client
   * @param realmName realm name
   * @param clientId client id
   * @return UUID user policy id
   */
  public UUID ensureUserPolicyExists(UUID userId) throws AuthorizationException {
    String realmName = getRealmName();
    Keycloak keycloak = getAdminClient();
    RealmResource realm = keycloak.realm(realmName);
    UsersResource users = realm.users();
    ClientRepresentation client = getClient(keycloak);
    String userIdString = userId.toString();
    
    if (client == null) {
      throw new AuthorizationException(AUTHORIZATION_EXCEPTION_MESSAGE);
    }
    
    UserPoliciesResource userPolicies = realm.clients().get(client.getId()).authorization().policies().user();
    
    UserResource loggedUserResource = users.get(userIdString);

    String userName = loggedUserResource.toRepresentation().getUsername();
    String policyNameTemplate = String.format("user-%s", userName);
    UserPolicyRepresentation policyRepresentation = userPolicies.findByName(policyNameTemplate);

    if (policyRepresentation == null) {

      UserPolicyRepresentation createdPolicyRepresentation = new UserPolicyRepresentation();
      createdPolicyRepresentation.setName(policyNameTemplate);
      createdPolicyRepresentation.setDecisionStrategy(DecisionStrategy.AFFIRMATIVE);
      createdPolicyRepresentation.addUser(userName);
      userPolicies.create(createdPolicyRepresentation);
      createdPolicyRepresentation = userPolicies.findByName(policyNameTemplate);

      return UUID.fromString(createdPolicyRepresentation.getId());
    } else {

      return UUID.fromString(policyRepresentation.getId());
    }
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
   * Evaluates eligibility of a user to list stories
   * 
   * @param loggedUserId UUID user id
   * @param storiesList List<Story> of all stories
   * @return List<story> stories that resources refer to
   */
  public List<Story> getPermittedStories(UUID loggedUserId, List<Story> storiesList) {
    try {
      List<AuthorizationScope> scopes = Collections.singletonList(AuthorizationScope.STORY_ACCESS);
      Keycloak keycloak = getAdminClient();
      RealmResource realm = keycloak.realm(REALM);
      ClientRepresentation client = getClient(keycloak);
      String userId = loggedUserId.toString();
      Map<Story, DecisionEffect> result = new HashMap<>();
      
      for (Story listedStory : storiesList) {
        UUID listedStoryId = listedStory.getId();
        String foundResourceName = String.format("story-%s", listedStoryId.toString());
        UUID foundResourceId = findProtectedResource(foundResourceName); 
        PolicyEvaluationRequest evaluationRequest = createEvaluationRequest(client, foundResourceId, foundResourceName, userId, scopes);
        PolicyEvaluationResponse response = realm.clients().get(client.getId()).authorization().policies().evaluate(evaluationRequest);
        result.put(listedStory, response.getStatus());
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
   * Creates admin client for config
   * 
   * @param configuration configuration
   * @return admin client
   */
  private Keycloak getAdminClient() {
   
    String token = getAccessToken(SERVER_URL, REALM, CLIENT_ID, CLIENT_SECRET, ADMIN_USER, ADMIN_PASSWORD);
    
    return KeycloakBuilder.builder()
      .serverUrl(SERVER_URL)
      .realm(REALM)
      .grantType(OAuth2Constants.PASSWORD)
      .clientId(CLIENT_ID)
      .clientSecret(CLIENT_SECRET)
      .username(ADMIN_USER)
      .password(ADMIN_PASSWORD)
      .authorization(String.format("Bearer %s", token))
      .build();
  }
  
  /**
   * Resolves an access token for realm, client, username and password
   * 
   * @param realm realm
   * @param clientId clientId
   * @param username username
   * @param password password
   * @return an access token
   * @throws IOException thrown on communication failure
   */
  private String getAccessToken(String serverUrl, String realm, String clientId, String clientSecret, String username, String password) {
    String uri = String.format("%s/realms/%s/protocol/openid-connect/token", serverUrl, realm);
    
    try (CloseableHttpClient client = HttpClients.createDefault()) {
      HttpPost httpPost = new HttpPost(uri);
      List<NameValuePair> params = new ArrayList<>();
      params.add(new BasicNameValuePair("client_id", clientId));
      params.add(new BasicNameValuePair("grant_type", "password"));
      params.add(new BasicNameValuePair("username", username));
      params.add(new BasicNameValuePair("password", password));
      params.add(new BasicNameValuePair("client_secret", clientSecret));
      httpPost.setEntity(new UrlEncodedFormEntity(params));
      
      try (CloseableHttpResponse response = client.execute(httpPost)) {
        try (InputStream inputStream = response.getEntity().getContent()) {
          Map<String, Object> responseMap = readJsonMap(inputStream);
          return (String) responseMap.get("access_token");
        }
      }
    } catch (IOException e) {
      logger.debug("Failed to retrieve access token", e);
    }
    
    return null;
  }
  
  /**
   * Reads JSON src into Map
   * 
   * @param src input
   * @return map
   * @throws IOException throws IOException when there is error when reading the input 
   */
  private Map<String, Object> readJsonMap(InputStream src) throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    return objectMapper.readValue(src, new TypeReference<Map<String, Object>>() {});
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
