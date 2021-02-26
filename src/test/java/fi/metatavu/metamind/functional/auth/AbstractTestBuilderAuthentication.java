package fi.metatavu.metamind.functional.auth;

import fi.metatavu.jaxrs.test.functional.builder.auth.AccessTokenProvider;
import fi.metatavu.metamind.api.client.infrastructure.ApiClient;
import fi.metatavu.metamind.functional.TestBuilder;
import fi.metatavu.metamind.functional.impl.*;

import java.io.IOException;

/**
 * Abstract base class for all test builder authentication providers
 * 
 * @author Antti Leppä
 */
public abstract class AbstractTestBuilderAuthentication {

  private AccessTokenProvider tokenProvider;
  private TestBuilder testBuilder;
  private StoryTestBuilderResource stories;
  
  protected AbstractTestBuilderAuthentication(TestBuilder testBuilder, AccessTokenProvider tokenProvider) {
    this.testBuilder = testBuilder;
    this.tokenProvider = tokenProvider;
  }
  
  /**
   * Returns test builder resource for stories
   * 
   * @return test builder resource for products
   * @throws IOException thrown when authentication fails
   */
  public StoryTestBuilderResource stories() throws IOException {
    if (stories != null) {
      return stories;
    }
    
    return new StoryTestBuilderResource(testBuilder, createClient(), this.tokenProvider);
  }
  

  /**
   * Creates an API client
   * 
   * @return an API client
   * @throws IOException thrown when authentication fails
   */
  protected abstract ApiClient createClient() throws IOException;
  
}
