package fi.metatavu.metamind.functional.auth;

import fi.metatavu.jaxrs.test.functional.builder.AbstractTestBuilder;
import fi.metatavu.jaxrs.test.functional.builder.auth.AccessTokenProvider;

import fi.metatavu.jaxrs.test.functional.builder.auth.AuthorizedTestBuilderAuthentication;
import fi.metatavu.metamind.api.client.infrastructure.ApiClient;
import fi.metatavu.metamind.functional.impl.IntentTestBuilderResource;
import fi.metatavu.metamind.functional.impl.KnotTestBuilderResource;
import fi.metatavu.metamind.functional.impl.StoryTestBuilderResource;
import fi.metatavu.metamind.functional.impl.TrainingMaterialTestBuilderResource;

import java.io.IOException;

import static fi.metatavu.metamind.functional.TestSettings.basePath;

/**
 * Default implementation of test builder authentication provider
 * 
 * @author Antti Leppä
 */
public class TestBuilderAuthentication extends AuthorizedTestBuilderAuthentication<ApiClient> {
  
  private final AbstractTestBuilder<ApiClient> testBuilder;
  private final AccessTokenProvider accessTokenProvider;
  private StoryTestBuilderResource storyTestBuilderResource;
  private KnotTestBuilderResource knotTestBuilderResource;
  private IntentTestBuilderResource intentTestBuilderResource;
  private TrainingMaterialTestBuilderResource trainingMaterialTestBuilderResource;


  /**
   * Constructor
   * 
   * @param testBuilder testBuilder
   * @param accessTokenProvider access token builder
   */
  public TestBuilderAuthentication(AbstractTestBuilder<ApiClient> testBuilder, AccessTokenProvider accessTokenProvider) {
    super(testBuilder, accessTokenProvider);
    this.testBuilder = testBuilder;
    this.accessTokenProvider = accessTokenProvider;
  }


  @Override
  protected ApiClient createClient(String s) {
    ApiClient client = new ApiClient(basePath);
    ApiClient.Companion.setAccessToken(s);
    return client;
  }

  public StoryTestBuilderResource stories() throws IOException {
    if (storyTestBuilderResource == null) {
      storyTestBuilderResource = new StoryTestBuilderResource(testBuilder, accessTokenProvider, createClient());
    }

    return storyTestBuilderResource;
  }

  public KnotTestBuilderResource knots() throws IOException {
    if (knotTestBuilderResource == null) {
      knotTestBuilderResource = new KnotTestBuilderResource(testBuilder, accessTokenProvider, createClient());
    }

    return knotTestBuilderResource;
  }

  public IntentTestBuilderResource intents() throws IOException {
    if (intentTestBuilderResource == null) {
      intentTestBuilderResource = new IntentTestBuilderResource(testBuilder, accessTokenProvider, createClient());
    }

    return intentTestBuilderResource;
  }

  public TrainingMaterialTestBuilderResource trainingMaterial() throws IOException {
    if (trainingMaterialTestBuilderResource == null) {
      trainingMaterialTestBuilderResource = new TrainingMaterialTestBuilderResource(testBuilder, accessTokenProvider, createClient());
    }

    return trainingMaterialTestBuilderResource;
  }
}
