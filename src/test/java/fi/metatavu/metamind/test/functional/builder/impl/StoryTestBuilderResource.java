package fi.metatavu.metamind.test.functional.builder.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.UUID;

import org.json.JSONException;

import feign.FeignException;
import fi.metatavu.metamind.ApiClient;
import fi.metatavu.metamind.client.StoriesApi;
import fi.metatavu.metamind.client.model.Story;
import fi.metatavu.metamind.test.functional.builder.AbstractTestBuilderResource;
import fi.metatavu.metamind.test.functional.builder.TestBuilder;

/**
 * Test builder resource for stories
 * 
 * @author Antti Leppä
 */
public class StoryTestBuilderResource extends AbstractTestBuilderResource<Story, StoriesApi> {
  
  /**
   * Constructor
   * 
   * @param testBuilder test builder
   * @param apiClient initialized API client
   */
  public StoryTestBuilderResource(TestBuilder testBuilder, ApiClient apiClient) {
    super(testBuilder, apiClient);
  }
  
  /**
   * Creates new story
   * 
   * @param locale locale
   * @param name name
   * @return created story
   */
  public Story create(String locale, String name) {
    Story story = new Story();
    story.setLocale(locale);
    story.setName(name);
    return addClosable(getApi().createStory(story));
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
   */
  public Story updateStory(Story body) {
    return getApi().updateStory(body, body.getId());
  }
  
  /**
   * Deletes a story from the API
   * 
   * @param story story to be deleted
   */
  public void delete(Story story) {
    getApi().deleteStory(story.getId());  
    removeCloseable(closable -> !closable.getId().equals(story.getId()));
  }
  
  /**
   * Asserts story count within the system
   * 
   * @param expected expected count
   */
  public void assertCount(int expected) {
    assertEquals(expected, getApi().listStories().size());
  }
  
  /**
   * Asserts find status fails with given status code
   * 
   * @param expectedStatus expected status code
   * @param storyId story id
   */
  public void assertFindFailStatus(int expectedStatus, UUID storyId) {
    try {
      getApi().findStory(storyId);
      fail(String.format("Expected find to fail with status %d", expectedStatus));
    } catch (FeignException e) {
      assertEquals(expectedStatus, e.status());
    }
  }

  /**
   * Asserts create status fails with given status code
   * 
   * @param expectedStatus expected status code
   * @param locale locale
   * @param name name
   */
  public void assertCreateFailStatus(int expectedStatus, String locale, String name) {
    try {
      Story story = new Story();
      story.setLocale(locale);
      story.setName(name);
      getApi().createStory(story);
      fail(String.format("Expected create to fail with status %d", expectedStatus));
    } catch (FeignException e) {
      assertEquals(expectedStatus, e.status());
    }
  }

  /**
   * Asserts update status fails with given status code
   * 
   * @param expectedStatus expected status code
   * @param story story
   */
  public void assertUpdateFailStatus(int expectedStatus, Story story) {
    try {
      getApi().updateStory(story, story.getId());
      fail(String.format("Expected update to fail with status %d", expectedStatus));
    } catch (FeignException e) {
      assertEquals(expectedStatus, e.status());
    }
  }
  
  /**
   * Asserts delete status fails with given status code
   * 
   * @param expectedStatus expected status code
   * @param story story
   */
  public void assertDeleteFailStatus(int expectedStatus, Story story) {
    try {
      getApi().deleteStory(story.getId());
      fail(String.format("Expected delete to fail with status %d", expectedStatus));
    } catch (FeignException e) {
      assertEquals(expectedStatus, e.status());
    }
  }
  
  /**
   * Asserts list status fails with given status code
   * 
   * @param expectedStatus expected status code
   */
  public void assertListFailStatus(int expectedStatus) {
    try {
      getApi().listStories();
      fail(String.format("Expected list to fail with status %d", expectedStatus));
    } catch (FeignException e) {
      assertEquals(expectedStatus, e.status());
    }
  }

  /**
   * Asserts that actual story equals expected story when both are serialized into JSON
   * 
   * @param expected expected story
   * @param actual actual story
   * @throws JSONException thrown when JSON serialization error occurs
   * @throws IOException thrown when IO Exception occurs
   */
  public void assertStoriesEqual(Story expected, Story actual) throws IOException, JSONException {
    assertJsonsEqual(expected, actual);
  }

  @Override
  public void clean(Story story) {
    getApi().deleteStory(story.getId());  
  }

}
