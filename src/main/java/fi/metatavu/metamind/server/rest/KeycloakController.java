package fi.metatavu.metamind.server.rest;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.*;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Client for keycloak admin
 */
@ApplicationScoped
public class KeycloakController {

  @Inject
  @ConfigProperty(name = "metamind.keycloak.admin.host")
  private String authServerUrl;

  @Inject
  @ConfigProperty(name = "metamind.keycloak.admin.realm")
  private String realm;

  @Inject
  @ConfigProperty(name = "metamind.keycloak.admin.secret")
  private String clientSecret;

  @Inject
  @ConfigProperty(name = "metamind.keycloak.admin.admin_client_id")
  private String clientId;

  @Inject
  @ConfigProperty(name = "metamind.keycloak.admin.user")
  private String apiAdminUser;

  @Inject
  @ConfigProperty(name = "metamind.keycloak.admin.password")
  private String apiAdminPassword;

  /**
   * Checks if two users don't to common groups
   *
   * @param user1 user 1 ID
   * @param user2 user 2 ID
   * @return if they have no common groups or if one of the users is missing
   */
  public boolean usersShareNoGroups(UUID user1, UUID user2) {
    UserResource userResource1 = getUserResource(user1);
    UserResource userResource2 = getUserResource(user2);

    if (userResource1 == null || userResource2 == null) {
      return true;
    }

    List<String> user1Groups = userResource1.groups().stream().map(GroupRepresentation::getId).collect(Collectors.toList());
    List<String> user2Groups = userResource2.groups().stream().map(GroupRepresentation::getId).collect(Collectors.toList());
    return Collections.disjoint(user1Groups, user2Groups);
  }

  /**
   * Lists ids of users sharing with groups with provided user
   *
   * @param userId user id
   * @return list of user IDs in same groups as userId
   */
  public List<UUID> listSameGroupUsers(UUID userId) {
    List<GroupRepresentation> user1Groups = getUserResource(userId).groups();
    Keycloak adminClient = getAdminClient();
    RealmResource realm = adminClient.realms().realm(this.realm);
    List<UUID> userIds = new ArrayList<>();
    for (GroupRepresentation group : user1Groups) {
      GroupResource groupResource = realm.groups().group(group.getId());
      groupResource.members().forEach(member ->
        userIds.add(UUID.fromString(member.getId())));
    }
    userIds.add(userId);
    return userIds;
  }

  /**
   * Gets user resources object from keycloak
   *
   * @param userId UUID
   * @return found UserResource or null
   */
  private UserResource getUserResource(UUID userId) {
    Keycloak adminClient = getAdminClient();
    RealmResource realm = adminClient.realms().realm(this.realm);
    UserResource userResource = realm.users().get(userId.toString());
    try {
      userResource.toRepresentation();
    } catch (NotFoundException ex) {
      return null;
    }

    return userResource;
  }

  /**
   * Gets list of groups of the user
   *
   * @param userId user id
   * @return groups it belongs to
   */
  private List<GroupRepresentation> getGroups(UUID userId) {
    UserResource userResource = getUserResource(userId);
    if (userResource == null) {
      return null;
    }
    return userResource.groups();
  }

  /**
   * Creates admin client for config
   *
   * @return admin client
   */
  private Keycloak getAdminClient() {
    return KeycloakBuilder.builder()
      .serverUrl(authServerUrl)
      .realm(realm)
      .username(apiAdminUser)
      .password(apiAdminPassword)
      .clientId(clientId)
      .clientSecret(clientSecret)
      .build();
  }
}
