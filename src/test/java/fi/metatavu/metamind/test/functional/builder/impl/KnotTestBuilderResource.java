package fi.metatavu.metamind.test.functional.builder.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.UUID;

import org.hibernate.search.hcore.impl.HibernateSearchIntegrator;
import org.jboss.wsf.stack.cxf.interceptor.HandlerAuthInterceptor;
import org.json.JSONException;

import com.fasterxml.jackson.databind.deser.impl.ExternalTypeHandler.Builder;
import com.oracle.truffle.js.nodes.cast.JSToPrimitiveNode.Hint;

import feign.Body;
import feign.FeignException;
import fi.metatavu.metamind.ApiClient;
import fi.metatavu.metamind.client.KnotsApi;
import fi.metatavu.metamind.client.StoriesApi;
import fi.metatavu.metamind.client.model.Knot;
import fi.metatavu.metamind.client.model.KnotType;
import fi.metatavu.metamind.client.model.Story;
import fi.metatavu.metamind.client.model.TokenizerType;
import fi.metatavu.metamind.test.functional.builder.AbstractTestBuilderResource;
import fi.metatavu.metamind.test.functional.builder.TestBuilder;

public class KnotTestBuilderResource extends AbstractTestBuilderResource<Knot, KnotsApi> {

  public KnotTestBuilderResource(TestBuilder testBuilder, ApiClient apiClient) {
    super(testBuilder, apiClient);
  }

  /**
   * Creates a new Knot
   * 
   * @param type
   * @param name
   * @param body
   * @return
   */
  public Knot create(Story story, KnotType type, String name, String content) {
    Knot knot = new Knot();
    knot.setType(type);
    knot.setName(name);
    knot.setContent(content);
    knot.setTokenizer(TokenizerType.WHITESPACE);
    knot.setHint("hint");
    return addClosable(getApi().createKnot(knot, story.getId()));
  }

  /**
   * Finds a Knot
   * 
   * @param type
   * @param name
   * @param knotId
   * @return
   */
  public Knot findKnot(Story story, Knot knot) {
    return getApi().findKnot(story.getId(), knot.getId());
  }

  /**
   * Updates knot
   * 
   * @param knotId
   * @param body
   * @return
   */

  public Knot updateKnot(Story story, Knot knot) {
    return getApi().updateKnot(knot, story.getId(), knot.getId());
  }

  public void delete(Story story, Knot knot) {
    getApi().deleteKnot(story.getId(), knot.getId());
    removeCloseable(closable -> !closable.getId().equals(knot.getId()));
  }

  public void assertCreateFailStatus(int expectedStatus, String content, String name) {
    try {
      Knot knot = new Knot();
      Story story = new Story();
      knot.setContent(content);
      knot.setName(name);
      getApi().createKnot(knot, story.getId());
      fail(String.format("Expected create to fail with status %d", expectedStatus));
    } catch (FeignException e) {
      assertEquals(expectedStatus, e.status());
    }

  }

  /**
   * Asserts find status fails with given status code
   * 
   * @param expectedStatus
   * @param knotId
   */
  public void assertFindFailStatus(Story story, int expectedStatus, UUID knotId) {
    try {
      //Story story = new Story();
      getApi().findKnot(story.getId(), knotId);
      fail(String.format("Expected find to fail with status %d", expectedStatus));
    } catch (FeignException e) {
      assertEquals(expectedStatus, e.status());
    }
  }

  public void assertUpdateFailStatus(Story story, int expectedStatus, Knot knot) {
    try {
      getApi().updateKnot(knot, story.getId(), knot.getId());
      fail(String.format("Expected update to fail with status %d", expectedStatus));
    } catch (FeignException e) {
      assertEquals(expectedStatus, e.status());
    }
  }

  public void assertKnotsEqual(Knot expected, Knot actual) throws IOException, JSONException {
    assertJsonsEqual(expected, actual);
  }

  public void assertDeleteFailStatus(Story story, int expectedStatus, Knot knot) {
    try {
      getApi().deleteKnot(story.getId(), knot.getId());
      fail(String.format("Expected delete to fail with status %d", expectedStatus));
    } catch (FeignException e) {
      assertEquals(expectedStatus, e.status());
    }
  }

  @Override
  public void clean(Knot knot) {
    getApi().deleteKnot(knot.getStoryId(), knot.getId());

  }

}
