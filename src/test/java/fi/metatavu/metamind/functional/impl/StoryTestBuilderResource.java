package fi.metatavu.metamind.functional.impl;

import fi.metatavu.jaxrs.test.functional.builder.auth.AccessTokenProvider;
import fi.metatavu.metamind.api.client.apis.StoriesApi;
import fi.metatavu.metamind.api.client.infrastructure.ApiClient;
import fi.metatavu.metamind.api.client.models.Story;
import fi.metatavu.metamind.functional.AbstractTestBuilderResource;
import fi.metatavu.metamind.functional.TestBuilder;
import fi.metatavu.metamind.functional.TestSettings;

import java.io.IOException;
import java.util.List;

/**
 * Test builder resource for stories
 * 
 * @author Antti Leppä
 */
public class StoryTestBuilderResource extends AbstractTestBuilderResource<Story, StoriesApi> {

  private AccessTokenProvider accessTokenProvider;
  /**
   * Constructor
   * 
   * @param testBuilder test builder
   * @param apiClient initialized API client
   */
  public StoryTestBuilderResource(TestBuilder testBuilder, ApiClient apiClient, AccessTokenProvider accessTokenProvider) {
    super(testBuilder, apiClient);
    this.accessTokenProvider = accessTokenProvider;
  }


  public List<Story> listStories() throws IOException {
    return getApi().listStories();
  }

  /**
   * Builds API client
   *
   * @return API client
   */
  @Override
  protected StoriesApi getApi() throws IOException {
    ApiClient.Companion.setAccessToken(accessTokenProvider.getAccessToken());
    StoriesApi client = new StoriesApi(TestSettings.basePath);
    return client;
  }

  /**
   * Cleans given resource
   *
   * @param story resource
   */
  @Override
  public void clean(Story story) {

  }
  /**
   * Creates new story
   * 
   * @param locale locale
   * @param name name
   * @return created story

  public Story create(String locale, String name, String hintMessage) {
    Story story = new Story();
    story.setLocale(locale);
    story.setName(name);
    story.setDafaultHint(hintMessage);
    return addClosable(getApi().createStory(story));
  }

  /**
   * Finds a story
   * 
   * @param storyId story id
   * @return found story

  public Story findStory(UUID storyId) {
    return getApi().findStory(storyId);
  }

  /**
   * Updates a story into the API
   * 
   * @param body body payload

  public Story updateStory(Story body) {
    return getApi().updateStory(body, body.getId());
  }
  
  /**
   * Deletes a story from the API
   * 
   * @param story story to be deleted

  public void delete(Story story) {
    getApi().deleteStory(story.getId());  
    removeCloseable(closable -> {
      if (closable instanceof Story) {
        return !((Story) closable).getId().equals(story.getId());
      }
      
      return false;
    });
  }

  @Override
  public void clean(Story story) {
   // getApi().deleteStory(story.getId());
  }
*/
}
