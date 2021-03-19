package fi.metatavu.metamind.functional.impl;

import fi.metatavu.jaxrs.test.functional.builder.AbstractTestBuilder;
import fi.metatavu.jaxrs.test.functional.builder.auth.AccessTokenProvider;
import fi.metatavu.metamind.api.client.apis.StoriesApi;
import fi.metatavu.metamind.api.client.infrastructure.ApiClient;
import fi.metatavu.metamind.api.client.infrastructure.ClientException;
import fi.metatavu.metamind.api.client.models.Story;
import fi.metatavu.metamind.functional.TestSettings;
import org.apache.http.HttpException;
import org.json.JSONException;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Test builder resource for stories
 *
 * @author Antti Leppä
 */
public class StoryTestBuilderResource extends ApiTestBuilderResource<Story, StoriesApi> {

  private AccessTokenProvider accessTokenProvider;

  /**
   * Constructor for StoryTestBuilderResource
   *
   * @param testBuilder         testBuilder
   * @param accessTokenProvider accessTokenProvider
   * @param apiClient           apiClient
   */
  public StoryTestBuilderResource(
    AbstractTestBuilder<ApiClient> testBuilder,
    AccessTokenProvider accessTokenProvider,
    ApiClient apiClient) {
    super(testBuilder, apiClient);
    this.accessTokenProvider = accessTokenProvider;
  }

  /**
   * Builds API client
   *
   * @return API client
   */
  @Override
  protected StoriesApi getApi() {
    try {
      ApiClient.Companion.setAccessToken(accessTokenProvider.getAccessToken());
    } catch (IOException e) {
      e.printStackTrace();
    }
    return new StoriesApi(TestSettings.basePath);
  }

  /**
   * Creates new story
   *
   * @param locale locale
   * @param name   name
   * @return created story
   */
  public Story create(String locale, String name, String hintMessage) throws IOException {
    Story story = new Story(name, locale, null, hintMessage, null, null, null);
    return getApi().createStory(story);
  }

  /**
   * Finds a story
   *
   * @param storyId story id
   * @return found story
   */
  public Story findStory(UUID storyId) {
    return getApi().findStory(storyId);
  }

  /**
   * Updates a story into the API
   *
   * @param body body payload
   * @return updated story
   */
  public Story updateStory(Story body) {
    return getApi().updateStory(Objects.requireNonNull(body.getId()), body);
  }

  /**
   * Deletes a story from the API
   *
   * @param story story to be deleted
   */
  public void delete(Story story) throws IOException {
    getApi().deleteStory(story.getId());
    removeCloseable(closable -> {
      if (closable instanceof Story) {
        return ((Story) closable).getId().equals(story.getId());
      }

      return false;
    });
  }

  /**
   * Cleans given resource
   *
   * @param story resource
   */
  @Override
  public void clean(Story story) {
    getApi().deleteStory(Objects.requireNonNull(story.getId()));
  }

  /**
   * Asserts find status fails with given status code
   *
   * @param expectedStatus expected status code
   * @param storyId        story id
   */
  public void assertFindFailStatus(int expectedStatus, UUID storyId) {
    try {
      getApi().findStory(storyId);
      fail(String.format("Expected find to fail with status %d", expectedStatus));
    } catch (ClientException e) {
      assertEquals(expectedStatus, e.getStatusCode());
    }
  }

  /**
   * Asserts that actual story equals expected story when both are serialized into JSON
   *
   * @param expected expected story
   * @param actual   actual story
   * @throws JSONException thrown when JSON serialization error occurs
   * @throws IOException   thrown when IO Exception occurs
   */
  public void assertStoriesEqual(Story expected, Story actual) throws IOException, JSONException {
    assertJsonsEqual(expected, actual);
  }

  /**
   * Asserts create status fails with given status code
   *
   * @param expectedStatus expected status code
   * @param locale         locale
   * @param name           name
   */
  public void assertCreateFailStatus(int expectedStatus, String locale, String name, String hintMessage) {
    try {
      Story story = new Story(name, locale, null, hintMessage, null, null, null);
      getApi().createStory(story);
      fail(String.format("Expected create to fail with status %d", expectedStatus));
    } catch (ClientException e) {
      assertEquals(expectedStatus, e.getStatusCode());
    }
  }

  /**
   * Asserts update status fails with given status code
   *
   * @param expectedStatus expected status code
   * @param story          story
   */
  public void assertUpdateFailStatus(int expectedStatus, Story story) {
    try {
      getApi().updateStory(story.getId(), story);
      fail(String.format("Expected update to fail with status %d", expectedStatus));
    } catch (ClientException e) {
      assertEquals(expectedStatus, e.getStatusCode());
    }
  }

  /**
   * Asserts delete status fails with given status code
   *
   * @param expectedStatus expected status code
   * @param story          story
   */
  public void assertDeleteFailStatus(int expectedStatus, Story story) {
    try {
      getApi().deleteStory(story.getId());
      fail(String.format("Expected delete to fail with status %d", expectedStatus));
    } catch (ClientException e) {
      assertEquals(expectedStatus, e.getStatusCode());
    }
  }

}
