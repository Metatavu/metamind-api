package fi.metatavu.metamind.functional.impl;

import fi.metatavu.jaxrs.test.functional.builder.AbstractTestBuilder;
import fi.metatavu.jaxrs.test.functional.builder.auth.*;
import fi.metatavu.metamind.api.client.infrastructure.ApiClient;
import fi.metatavu.metamind.functional.auth.TestBuilderAuthentication;
import org.eclipse.microprofile.config.ConfigProvider;
import org.keycloak.Config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Test builder class
 * 
 * @author Antti Leppä
 */
public class TestBuilder extends AbstractTestBuilder<ApiClient> {

  private TestBuilderAuthentication admin;
  private TestBuilderAuthentication invalid;
  private TestBuilderAuthentication anonymous;


  /**
   * Returns admin authenticated authentication resource
   * 
   * @return admin authenticated authentication resource
   * @throws IOException 
   */
  public TestBuilderAuthentication admin() throws IOException {
    if (admin != null) {
      return admin;
    }

    String serverUrl = ConfigProvider.getConfig().getValue("metamind.keycloak.host", String.class);
    String REALM = ConfigProvider.getConfig().getValue("metamind.keycloak.realm", String.class);
    String CLIENT_ID = ConfigProvider.getConfig().getValue("quarkus.oidc.client-id", String.class);
    String ADMIN_USER = ConfigProvider.getConfig().getValue("metamind.keycloak.user", String.class);
    String ADMIN_PASSWORD = ConfigProvider.getConfig().getValue("metamind.keycloak.password", String.class);
    String CLIENT_SECRET = null;


    return admin = new TestBuilderAuthentication(this,
            new KeycloakAccessTokenProvider(serverUrl, REALM, CLIENT_ID, ADMIN_USER, ADMIN_PASSWORD, CLIENT_SECRET));
  }

  /**
   * Creates new authorized test builder authentication
   *
   * @param abstractTestBuilder this instance
   * @param accessTokenProvider access token provider
   * @return initialized test builder authentication
   */
  @Override
  public AuthorizedTestBuilderAuthentication<ApiClient> createTestBuilderAuthentication(AbstractTestBuilder<ApiClient> abstractTestBuilder, AccessTokenProvider accessTokenProvider) {
    return new TestBuilderAuthentication(abstractTestBuilder, accessTokenProvider);
  }

  /**
   * Returns authentication resource with invalid token
   * 
   * @return authentication resource with invalid token
   */
  public TestBuilderAuthentication invalid() {
    if (invalid != null) {
      return invalid;
    }

    return invalid = new TestBuilderAuthentication(this, new InvalidAccessTokenProvider());
  }

  /**
   * Returns authentication resource without token
   * 
   * @return authentication resource without token
   */
  public TestBuilderAuthentication anonymous() {
    if (anonymous != null) {
      return anonymous;
    }

    return anonymous = new TestBuilderAuthentication(this, new NullAccessTokenProvider());
  }

}