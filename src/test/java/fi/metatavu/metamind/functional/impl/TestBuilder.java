package fi.metatavu.metamind.functional.impl;

import fi.metatavu.jaxrs.test.functional.builder.AbstractTestBuilder;
import fi.metatavu.jaxrs.test.functional.builder.auth.*;
import fi.metatavu.metamind.api.client.infrastructure.ApiClient;
import fi.metatavu.metamind.functional.auth.TestBuilderAuthentication;
import org.eclipse.microprofile.config.ConfigProvider;

import java.io.IOException;
import java.util.Optional;

/**
 * Test builder class
 *
 * @author Antti Leppä
 */
public class TestBuilder extends AbstractTestBuilder<ApiClient> {

  private TestBuilderAuthentication admin;
  private TestBuilderAuthentication test1;
  private TestBuilderAuthentication test2;
  private TestBuilderAuthentication invalid;
  private TestBuilderAuthentication anonymous;

  private String serverUrl = ConfigProvider.getConfig().getValue("metamind.keycloak.admin.host", String.class);
  private String realm = ConfigProvider.getConfig().getValue("metamind.keycloak.admin.realm", String.class);
  private String api_client_id = "api";
  private String apiSecret = "1be749de-2023-4ebf-aba0-90679ffab56b";


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

    return admin = new TestBuilderAuthentication(this, new KeycloakAccessTokenProvider(serverUrl, realm,
      api_client_id, "admin@example.com", "admin", apiSecret));
  }

  /**
   * Gets authenticated test1 user (belongs to group1)
   *
   * @return authenticated test1 resource
   * @throws IOException io exception
   */
  public TestBuilderAuthentication test1() throws IOException {
    if (test1 != null) {
      return test1;
    }

    KeycloakAccessTokenProvider keycloakAccessTokenProvider = new KeycloakAccessTokenProvider(serverUrl, "metamind", api_client_id,
      "test1", "test", apiSecret);
    return test1 = new TestBuilderAuthentication(this, keycloakAccessTokenProvider);
  }

  /**
   * Gets authenticated test2 user (belongs to group2)
   *
   * @return authenticated test2 resource
   * @throws IOException io exception
   */
  public TestBuilderAuthentication test2() throws IOException {
    if (test2 != null) {
      return test2;
    }

    return test2 = new TestBuilderAuthentication(this, new KeycloakAccessTokenProvider(serverUrl, realm, api_client_id,
      "test2", "test", "1be749de-2023-4ebf-aba0-90679ffab56b"));
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
