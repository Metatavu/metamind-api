package fi.metatavu.metamind.functional.impl;

import fi.metatavu.jaxrs.test.functional.builder.AbstractTestBuilder;
import fi.metatavu.jaxrs.test.functional.builder.auth.AccessTokenProvider;
import fi.metatavu.metamind.api.client.apis.KnotsApi;
import fi.metatavu.metamind.api.client.apis.StoriesApi;
import fi.metatavu.metamind.api.client.infrastructure.ApiClient;
import fi.metatavu.metamind.api.client.infrastructure.ClientException;
import fi.metatavu.metamind.api.client.models.Coordinates;
import fi.metatavu.metamind.api.client.models.Knot;
import fi.metatavu.metamind.api.client.models.KnotType;
import fi.metatavu.metamind.api.client.models.Story;
import fi.metatavu.metamind.api.spec.model.TokenizerType;
import fi.metatavu.metamind.functional.TestSettings;
import org.json.JSONException;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Test builder resource for stories
 * 
 * @author Antti Leppä
 */
public class KnotTestBuilderResource extends ApiTestBuilderResource<Knot, KnotsApi> {

  private AccessTokenProvider accessTokenProvider;

  /**
   * Constructor for KnotTestBuilderResource
   *
   * @param testBuilder testBuilder
   * @param accessTokenProvider accessTokenProvider
   * @param apiClient apiClient
   */
  public KnotTestBuilderResource(
          AbstractTestBuilder<ApiClient> testBuilder,
          AccessTokenProvider accessTokenProvider,
          ApiClient apiClient
  ) {
    super(testBuilder, apiClient);
    this.accessTokenProvider = accessTokenProvider;
  }

  /**
   * Builds API client
   *
   * @return API client
   */
  @Override
  protected KnotsApi getApi() {
    try {
      ApiClient.Companion.setAccessToken(accessTokenProvider.getAccessToken());
    } catch (IOException e) {
      e.printStackTrace();
    }
    return new KnotsApi(TestSettings.basePath);
  }

  /**
   * Creates a new knot
   *
   * @param story story
   * @param type knot type
   * @param name knot name
   * @param content knot content
   * @param coordinateX Double coordinate
   * @param coordinateY Double coordinate
   * @return
   */
  public Knot create(Story story, KnotType type, String name, String content, Double coordinateX, Double coordinateY) {
    Coordinates coordinates = new Coordinates(coordinateX, coordinateY);

    Knot knot = new Knot(type,  fi.metatavu.metamind.api.client.models.TokenizerType.wHITESPACE, name,
            content, null, "hint", null, coordinates, null, null);

    return addClosable(getApi().createKnot(story.getId(), knot));
  }

  /**
   * Finds a story knot
   *
   * @param story story
   * @param knot knot content
   * @return
   */
  public Knot findKnot(Story story, Knot knot) {
    return getApi().findKnot(story.getId(), knot.getId());
  }

  /**
   * Lists story knots
   *
   * @param story story
   * @return all knots from the given story
   */
  public Knot[] listKnots(Story story) {
    return getApi().listKnots(story.getId());
  }

  /**
   * Updates a story knot
   *
   * @param story story
   * @param knot knot content
   * @return
   */
  public Knot updateKnot(Story story, Knot knot) {
    return getApi().updateKnot(story.getId(), knot.getId(), knot);
  }

  /**
   * Deletes a story knot
   *
   * @param story story
   * @param knot knot
   */
  public void delete(Story story, Knot knot) {
    getApi().deleteKnot(story.getId(), knot.getId());
    removeCloseable(closable -> {
      if (closable instanceof Knot) {
        return ((Knot) closable).getId().equals(knot.getId());
      }

      return false;
    });
  }

  /**
   * Asserts create status fails with given status code
   *
   * @param expectedStatus status code
   * @param content knot content
   * @param name knot name
   * @param story story
   */
  public void assertCreateFailStatus(int expectedStatus, String content, String name, Story story) {
    try {
      Knot knot = new Knot(null, null, name, content, null, null, null, null, null, null);
      getApi().createKnot(Objects.requireNonNull(story.getId()), knot);
      fail(String.format("Expected create to fail with status %d", expectedStatus));
    } catch (ClientException e) {
      assertEquals(expectedStatus, e.getStatusCode());
    }

  }

  /**
   * Asserts find status fails with given status code
   *
   * @param storyId story UUID
   * @param expectedStatus status code
   * @param knotId knotId UUID
   */
  public void assertFindFailStatus(UUID storyId, int expectedStatus, UUID knotId) {
    try {
      getApi().findKnot(storyId, knotId);
      fail(String.format("Expected find to fail with status %d", expectedStatus));
    } catch (ClientException e) {
      assertEquals(expectedStatus, e.getStatusCode());
    }
  }


  /**
   * Asserts update status fails with given status code
   *
   * @param story story
   * @param expectedStatus status code
   * @param knot knot
   */
  public void assertUpdateFailStatus(Story story, int expectedStatus, Knot knot) {
    try {
      getApi().updateKnot(Objects.requireNonNull(story.getId()), Objects.requireNonNull(knot.getId()), knot);
      fail(String.format("Expected update to fail with status %d", expectedStatus));
    } catch (ClientException e) {
      assertEquals(expectedStatus, e.getStatusCode());
    }
  }

  /**
   * Asserts knots are equal
   *
   * @param expected knot
   * @param actual knot
   * @throws IOException
   * @throws JSONException
   */
  public void assertKnotsEqual(Knot expected, Knot actual) throws IOException, JSONException {
    assertJsonsEqual(expected, actual);
  }

  /**
   * Asserts delete status fails with given status code
   *
   * @param story story
   * @param expectedStatus status code
   * @param knot knot
   */
  public void assertDeleteFailStatus(Story story, int expectedStatus, Knot knot) {
    try {
      getApi().deleteKnot(Objects.requireNonNull(story.getId()), Objects.requireNonNull(knot.getId()));
      fail(String.format("Expected delete to fail with status %d", expectedStatus));
    } catch (ClientException e) {
      assertEquals(expectedStatus, e.getStatusCode());
    }
  }

  @Override
  public void clean(Knot knot) {
    getApi().deleteKnot(knot.getStoryId(), knot.getId());
  }
}
