package fi.metatavu.metamind.functional.impl;

import feign.FeignException;
import fi.metatavu.metamind.ApiClient;
import fi.metatavu.metamind.client.IntentsApi;
import fi.metatavu.metamind.client.model.*;
import fi.metatavu.metamind.functional.AbstractTestBuilderResource;
import fi.metatavu.metamind.functional.TestBuilder;
import org.json.JSONException;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Test builder resource for intents
 * 
 * @author Daniil Smirnov
 */
public class IntentTestBuilderResource extends AbstractTestBuilderResource<Intent, IntentsApi> {

  private Map<UUID, UUID> intentStoryMap = new HashMap<>();

  public IntentTestBuilderResource(TestBuilder testBuilder, ApiClient apiClient) {
    super(testBuilder, apiClient);
  }

  /**
   * Creates a new intent
   * 
   * @param storyId storyId UUID
   * @param sourceKnot knot
   * @param targetKnot knot
   * @param name intent name
   * @return
   */

  /**
   * Creates a new intent
   * 
   * @param storyId storyId UUID
   * @param sourceKnot knot
   * @param targetKnot knot
   * @param name String intent name
   * @param type intent type
   * @param global boolean global
   * @param quickResponse String quick response
   * @param quickResponseOrder int response order
   * @param intentOpenNlpDoccatId UUID 
   * @param intentRegexId UUID
   * @param variableOpenNlpNerId UUID
   * @param variableOpenNlpRegexId UUID
   * @return
   */
  public Intent create(UUID storyId, Knot sourceKnot, Knot targetKnot, String name, IntentType type, boolean global, String quickResponse,
      int quickResponseOrder, UUID intentOpenNlpDoccatId, UUID intentRegexId, UUID variableOpenNlpNerId, UUID variableOpenNlpRegexId) {

    IntentTrainingMaterials intentTrainingMaterials = new IntentTrainingMaterials();
    intentTrainingMaterials.setIntentOpenNlpDoccatId(intentOpenNlpDoccatId);
    Intent intent = new Intent();
    intent.setSourceKnotId(sourceKnot.getId());
    intent.setTargetKnotId(targetKnot.getId());
    intent.setName(name);
    intent.setType(type);
    intent.setGlobal(global);
    intent.setQuickResponse(quickResponse);
    intent.setQuickResponseOrder(quickResponseOrder);
    intent.setTrainingMaterials(intentTrainingMaterials);
    Intent createdIntent = getApi().createIntent(intent, storyId);
    intentStoryMap.put(createdIntent.getId(), storyId);
    return addClosable(createdIntent);

  }

  /**
   * Finds an intent
   * 
   * @param story story
   * @param intent intent
   * @return
   */

  public Intent findIntent(Story story, Intent intent) {
    return getApi().findIntent(story.getId(), intent.getId());
  }
  
  /**
   * Lists story knots
   * 
   * @param story story
   * @return
   */
  public List<Intent> listIntents(Story story) {
    return getApi().listIntents(story.getId());
  }

  /**
   * Updates an intent
   * 
   * @param story story
   * @param intent intent
   * @return
   */

  public Intent updateIntent(Story story, Intent intent) {
    return getApi().updateIntent(intent, story.getId(), intent.getId());
  }

  /**
   * Deletes an intent
   * 
   * @param story story
   * @param intent intent
   */

  public void delete(Story story, Intent intent) {
    getApi().deleteIntent(story.getId(), intent.getId());
    removeCloseable(closable -> {
      if (closable instanceof Intent) {
        return !((Intent) closable).getId().equals(intent.getId());
      }

      return false;
    });
  }

  /**
   * Asserts create status fails with given status code
   * 
   * @param expectedStatus status code
   * @param name intent name
   * @param sourceKnot knot
   * @param targetKnot knot
   * @param story story
   */

  public void assertCreateFailStatus(int expectedStatus, String name, Knot sourceKnot, Knot targetKnot, Story story) {
    try {
      Intent intent = new Intent();
      intent.setName(name);
      intent.setSourceKnotId(sourceKnot.getId());
      intent.setTargetKnotId(targetKnot.getId());
      intent.setType(IntentType.NORMAL);
      getApi().createIntent(intent, story.getId());
      fail(String.format("Expected create to fail with status %d", expectedStatus));
    } catch (FeignException e) {
      assertEquals(expectedStatus, e.status());
    }
  }

  /**
   * Asserts find status fails with given status code
   * 
   * @param storyId storyId
   * @param expectedStatus status code
   * @param intentId intentId
   */

  public void assertFindFailStatus(UUID storyId, int expectedStatus, UUID intentId) {
    try {
      getApi().findIntent(storyId, intentId);
      fail(String.format("Expected find to fail with status %d", expectedStatus));
    } catch (FeignException e) {
      assertEquals(expectedStatus, e.status());
    }
  }

  /**
   * Asserts update status fails with given status code
   * 
   * @param story story
   * @param expectedStatus status code
   * @param intent intent
   */

  public void assertUpdateFailStatus(Story story, int expectedStatus, Intent intent) {
    try {
      getApi().updateIntent(intent, story.getId(), intent.getId());
      fail(String.format("Expected update to fail with status %d", expectedStatus));
    } catch (FeignException e) {
      assertEquals(expectedStatus, e.status());
    }
  }

  /**
   * Asserts delete status fails with given status code
   * 
   * @param story story
   * @param expectedStatus status code
   * @param intent intent
   */

  public void assertDeleteFailStatus(Story story, int expectedStatus, Intent intent) {
    try {
      getApi().deleteIntent(story.getId(), intent.getId());
      fail(String.format("Expected delete to fail with status %d", expectedStatus));
    } catch (FeignException e) {
      assertEquals(expectedStatus, e.status());
    }
  }

  /**
   * Asserts intents are equal
   * 
   * @param expected intent
   * @param actual intent
   * @throws IOException
   * @throws JSONException
   */

  public void assertIntentsEqual(Intent expected, Intent actual) throws IOException, JSONException {
    assertJsonsEqual(expected, actual);
  }

  @Override
  public void clean(Intent intent) {
    getApi().deleteIntent(intentStoryMap.get(intent.getId()), intent.getId());
    intentStoryMap.remove(intent.getId());
  }

}