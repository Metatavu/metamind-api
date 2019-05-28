package fi.metatavu.metamind.test.functional.builder.auth;

import java.io.IOException;

import fi.metatavu.metamind.ApiClient;
import fi.metatavu.metamind.test.functional.builder.TestBuilder;
import fi.metatavu.metamind.test.functional.builder.impl.IntentTestBuilderResource;
import fi.metatavu.metamind.test.functional.builder.impl.IntentTrainingMaterialTestBuilderResource;
import fi.metatavu.metamind.test.functional.builder.impl.KnotTestBuilderResource;
import fi.metatavu.metamind.test.functional.builder.impl.StoryTestBuilderResource;

/**
 * Abstract base class for all test builder authentication providers
 * 
 * @author Antti Leppä
 */
public abstract class AbstractTestBuilderAuthentication {

  private TestBuilder testBuilder;
  private StoryTestBuilderResource stories;
  private KnotTestBuilderResource knots;
  private IntentTestBuilderResource intents;
  private IntentTrainingMaterialTestBuilderResource trainingMaterials;
  
  protected AbstractTestBuilderAuthentication(TestBuilder testBuilder) {
    this.testBuilder = testBuilder;
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
    
    return new StoryTestBuilderResource(testBuilder, createClient());
  }
  
  /**
   * Returns test builder resource for knots
   * 
   * @return test builder resource for products
   * @throws IOException thrown when authentication fails
   */
  
  public KnotTestBuilderResource knots() throws IOException {
    if (knots != null) {
      return knots;
    }
    
    return new KnotTestBuilderResource(testBuilder, createClient());
  }
  
  /**
   * Returns test builder resource for intents
   * 
   * @return test builder resource for products
   * @throws IOException thrown when authentication fails
   */
  
  public IntentTestBuilderResource intents() throws IOException {
    if (intents != null) {
      return intents;
    }
    
    return new IntentTestBuilderResource(testBuilder, createClient());
  }
  
  /**
   * Returns test builder resource for Training  Material
   * 
   * @return test builder resource for products
   * @throws IOException thrown when authentication fails
   */
  public IntentTrainingMaterialTestBuilderResource trainingMaterial() throws IOException {
    if (trainingMaterials != null) {
      return trainingMaterials;
    }
    
    return new IntentTrainingMaterialTestBuilderResource(testBuilder, createClient());
  }
  
  /**
   * Creates an API client
   * 
   * @return an API client
   * @throws IOException thrown when authentication fails
   */
  protected abstract ApiClient createClient() throws IOException;
  
}
