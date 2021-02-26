package fi.metatavu.metamind.functional.auth;

import fi.metatavu.jaxrs.test.functional.builder.auth.AccessTokenProvider;

import fi.metatavu.metamind.api.client.infrastructure.ApiClient;
import fi.metatavu.metamind.functional.TestBuilder;

import java.io.IOException;

import static fi.metatavu.metamind.functional.TestSettings.basePath;

/**
 * Default implementation of test builder authentication provider
 * 
 * @author Antti Leppä
 */
public class TestBuilderAuthentication extends AbstractTestBuilderAuthentication {
  
  
  private AccessTokenProvider accessTokenProvider;

  /**
   * Constructor
   * 
   * @param testBuilder testBuilder
   * @param accessTokenProvider access token builder
   */
  public TestBuilderAuthentication(TestBuilder testBuilder, AccessTokenProvider accessTokenProvider) {
    super(testBuilder, accessTokenProvider);
    this.accessTokenProvider = accessTokenProvider;
  }
  
  /** 
   * Creates ApiClient authenticated by the given access token
   * 
   * @return ApiClient authenticated by the given access token
   * @throws IOException 
   */
  @Override
  protected ApiClient createClient() throws IOException {
    String accessToken = accessTokenProvider.getAccessToken();
    ApiClient client = new ApiClient(basePath);
    ApiClient.Companion.setAccessToken(accessToken);
    return client;
  }
  
}
