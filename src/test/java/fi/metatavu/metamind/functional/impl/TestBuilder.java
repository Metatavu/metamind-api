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
import java.util.Optional;
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
   * @throws IOException ioexception
   */
  public TestBuilderAuthentication admin() throws IOException {
    if (admin != null) {
      return admin;
    }

    Optional<String> serverUrl = ConfigProvider.getConfig().getOptionalValue("metamind.keycloak.admin.host", String.class);
    Optional<String> REALM = ConfigProvider.getConfig().getOptionalValue("metamind.keycloak.admin.realm", String.class);
    Optional<String> CLIENT_ID = ConfigProvider.getConfig().getOptionalValue("quarkus.oidc.client-id", String.class);
    Optional<String> ADMIN_USER = ConfigProvider.getConfig().getOptionalValue("metamind.keycloak.admin.user", String.class);
    Optional<String> ADMIN_PASSWORD = ConfigProvider.getConfig().getOptionalValue("metamind.keycloak.admin.password", String.class);
    Optional<String> CLIENT_SECRET = ConfigProvider.getConfig().getOptionalValue("metamind.keycloak.admin.secret", String.class);

    return admin = new TestBuilderAuthentication(this, new KeycloakAccessTokenProvider(getOptional(serverUrl), getOptional(REALM),
      getOptional(CLIENT_ID), getOptional(ADMIN_USER), getOptional(ADMIN_PASSWORD), getOptional(CLIENT_SECRET)));
  }

  /**
   * Gets value or null if empty
   *
   * @param value value
   * @return value optional value
   */
  private String getOptional(Optional<String> value) {
    return value.orElse(null);
  }

  @Override
  public AuthorizedTestBuilderAuthentication<ApiClient> createTestBuilderAuthentication(
    AbstractTestBuilder<ApiClient> abstractTestBuilder,
    AccessTokenProvider accessTokenProvider
  ) {
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
