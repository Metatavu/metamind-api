package fi.metatavu.metamind.functional.impl;

import fi.metatavu.jaxrs.test.functional.builder.AbstractTestBuilder;
import fi.metatavu.jaxrs.test.functional.builder.auth.AccessTokenProvider;
import fi.metatavu.metamind.api.client.apis.IntentsApi;
import fi.metatavu.metamind.api.client.apis.StoriesApi;
import fi.metatavu.metamind.api.client.infrastructure.ApiClient;
import fi.metatavu.metamind.api.client.infrastructure.ClientException;
import fi.metatavu.metamind.api.client.models.*;
import fi.metatavu.metamind.functional.TestSettings;
import org.json.JSONException;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Test builder resource for stories
 *
 * @author Antti Leppä
 */
public class IntentTestBuilderResource extends ApiTestBuilderResource<Intent, IntentsApi> {
  private Map<UUID, UUID> intentStoryMap = new HashMap<>();

  private AccessTokenProvider accessTokenProvider;

  /**
   * Constructor for IntentTestBuilderResource
   *
   * @param testBuilder         testBuilder
   * @param accessTokenProvider accessTokenProvider
   * @param apiClient           apiClient
   */
  public IntentTestBuilderResource(
    AbstractTestBuilder<ApiClient> testBuilder,
    AccessTokenProvider accessTokenProvider,
    ApiClient apiClient
  ) {
    super(testBuilder, apiClient);
    this.accessTokenProvider = accessTokenProvider;
  }

  @Override
  protected IntentsApi getApi() {
    try {
      ApiClient.Companion.setAccessToken(accessTokenProvider.getAccessToken());
    } catch (IOException e) {
      e.printStackTrace();
    }
    return new IntentsApi(TestSettings.basePath);
  }

  @Override
  public void clean(Intent intent) {
    getApi().deleteIntent(intentStoryMap.get(intent.getId()), intent.getId());
    intentStoryMap.remove(intent.getId());
  }

  /**
   * Creates a new intent
   *
   * @param storyId                storyId UUID
   * @param sourceKnot             knot
   * @param targetKnot             knot
   * @param name                   String intent name
   * @param type                   intent type
   * @param global                 boolean global
   * @param quickResponse          String quick response
   * @param quickResponseOrder     int response order
   * @param intentOpenNlpDoccatId  UUID
   * @param intentRegexId          UUID
   * @param variableOpenNlpNerId   UUID
   * @param variableOpenNlpRegexId UUID
   * @return created intent
   */
  public Intent create(
    UUID storyId,
    Knot sourceKnot,
    Knot targetKnot,
    String name,
    IntentType type,
    boolean global,
    String quickResponse,
    int quickResponseOrder,
    UUID intentOpenNlpDoccatId,
    UUID intentRegexId,
    UUID variableOpenNlpNerId,
    UUID variableOpenNlpRegexId
  ) {
    IntentTrainingMaterials intentTrainingMaterials = new IntentTrainingMaterials(intentOpenNlpDoccatId, intentRegexId, variableOpenNlpNerId, variableOpenNlpRegexId);
    Intent intent = new Intent(quickResponseOrder, type, targetKnot.getId(), global, intentTrainingMaterials, null, name, quickResponse, sourceKnot.getId(), null, null);

    Intent createdIntent = getApi().createIntent(storyId, intent);
    intentStoryMap.put(createdIntent.getId(), storyId);
    return addClosable(createdIntent);
  }

  /**
   * Finds an intent
   *
   * @param story  story
   * @param intent intent
   * @return found intent
   */
  public Intent findIntent(Story story, Intent intent) {
    return getApi().findIntent(story.getId(), intent.getId());
  }

  /**
   * Lists story knots
   *
   * @param story story
   * @return intents of the story
   */
  public Intent[] listIntents(Story story) {
    return getApi().listIntents(story.getId());
  }

  /**
   * Updates an intent
   *
   * @param story  story
   * @param intent intent
   * @return updated intent
   */
  public Intent updateIntent(Story story, Intent intent) {
    return getApi().updateIntent(story.getId(), intent.getId(), intent);
  }

  /**
   * Deletes an intent
   *
   * @param story  story
   * @param intent intent
   */
  public void delete(Story story, Intent intent) {
    getApi().deleteIntent(story.getId(), intent.getId());
    removeCloseable(closable -> {
      if (closable instanceof Intent) {
        return ((Intent) closable).getId().equals(intent.getId());
      }

      return false;
    });
  }

  /**
   * Asserts create status fails with given status code
   *
   * @param expectedStatus status
   * @param storyId story id
   * @param sourceKnot source knot
   * @param targetKnot target knot
   * @param name intent name
   * @param type intent type
   * @param global global intent
   * @param quickResponse quick response
   * @param quickResponseOrder quick response order
   * @param intentOpenNlpDoccatId intent training material openNlp doccatId
   * @param intentRegexId intent training material regex id
   * @param variableOpenNlpNerId intent training material openNlp NerId
   * @param variableOpenNlpRegexId intent training material openNlp RegexId
   */
  public void assertCreateFailStatus(
    int expectedStatus,
    UUID storyId,
    Knot sourceKnot,
    Knot targetKnot,
    String name,
    IntentType type,
    boolean global,
    String quickResponse,
    int quickResponseOrder,
    UUID intentOpenNlpDoccatId,
    UUID intentRegexId,
    UUID variableOpenNlpNerId,
    UUID variableOpenNlpRegexId
  ) {
    try {
      IntentTrainingMaterials intentTrainingMaterials = new IntentTrainingMaterials(intentOpenNlpDoccatId, intentRegexId, variableOpenNlpNerId, variableOpenNlpRegexId);
      Intent intent = new Intent(quickResponseOrder, type, targetKnot.getId(), global, intentTrainingMaterials, null, name, quickResponse, sourceKnot.getId(), null, null);

      getApi().createIntent(storyId, intent);
      fail(String.format("Expected create to fail with status %d", expectedStatus));
    } catch (ClientException e) {
      assertEquals(expectedStatus, e.getStatusCode());
    }
  }

  /**
   * Asserts find status fails with given status code
   *
   * @param storyId        storyId
   * @param expectedStatus status code
   * @param intentId       intentId
   */
  public void assertFindFailStatus(UUID storyId, int expectedStatus, UUID intentId) {
    try {
      getApi().findIntent(storyId, intentId);
      fail(String.format("Expected find to fail with status %d", expectedStatus));
    } catch (ClientException e) {
      assertEquals(expectedStatus, e.getStatusCode());
    }
  }

  /**
   * Asserts update status fails with given status code
   *
   * @param story          story
   * @param expectedStatus status code
   * @param intent         intent
   */
  public void assertUpdateFailStatus(Story story, int expectedStatus, Intent intent) {
    try {
      getApi().updateIntent(story.getId(), intent.getId(), intent);
      fail(String.format("Expected update to fail with status %d", expectedStatus));
    } catch (ClientException e) {
      assertEquals(expectedStatus, e.getStatusCode());
    }
  }

  /**
   * Asserts delete status fails with given status code
   *
   * @param story          story
   * @param expectedStatus status code
   * @param intent         intent
   */
  public void assertDeleteFailStatus(Story story, int expectedStatus, Intent intent) {
    try {
      getApi().deleteIntent(story.getId(), intent.getId());
      fail(String.format("Expected delete to fail with status %d", expectedStatus));
    } catch (ClientException e) {
      assertEquals(expectedStatus, e.getStatusCode());
    }
  }

  /**
   * Asserts intents are equal
   *
   * @param expected intent
   * @param actual   intent
   * @throws IOException
   * @throws JSONException
   */
  public void assertIntentsEqual(Intent expected, Intent actual) throws IOException, JSONException {
    assertJsonsEqual(expected, actual);
  }

}
