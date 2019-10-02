package fi.metatavu.metamind.test.functional.builder.auth;

import java.io.IOException;

import fi.metatavu.metamind.ApiClient;
import fi.metatavu.metamind.test.functional.builder.TestBuilder;
import fi.metatavu.metamind.test.functional.settings.TestSettings;

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
    super(testBuilder);
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
    String authorization = accessToken != null ? String.format("Bearer %s", accessToken) : null;
    ApiClient apiClient = authorization != null ? new ApiClient("BearerAuth", authorization) : new ApiClient();
    String basePath = String.format("http://%s:%d/v2", TestSettings.getHost(), TestSettings.getPort());
    apiClient.setBasePath(basePath);
    return apiClient;
  }
  
}
