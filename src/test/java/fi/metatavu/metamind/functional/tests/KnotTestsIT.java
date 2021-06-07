package fi.metatavu.metamind.functional.tests;

import fi.metatavu.metamind.api.client.models.Coordinates;
import fi.metatavu.metamind.api.client.models.Knot;
import fi.metatavu.metamind.api.client.models.KnotType;
import fi.metatavu.metamind.api.client.models.Story;
import fi.metatavu.metamind.functional.impl.TestBuilder;
import fi.metatavu.metamind.functional.resources.KeycloakResource;
import fi.metatavu.metamind.functional.resources.MysqlResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Tests for knots API
 */
@QuarkusTest
@QuarkusTestResource.List(value = {
  @QuarkusTestResource(MysqlResource.class),
  @QuarkusTestResource(KeycloakResource.class)
})
public class KnotTestsIT {

  /**
   * Tests knot creation
   * @throws Exception exception
   */
  @Test
  public void testCreateKnot() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Story story = builder.admin().stories().create("en", "test story", "Enter your answer");
      assertNotNull(builder.admin().knots().create(story, KnotType.tEXT, "Test", "Content", 10.0, 20.0));
      assertNotNull(builder.admin().knots().create(story, KnotType.tEXT, "Test Null", "Content", null, null));
    }
  }

  /**
   * Test that not logged in users cannot create knots
   */
  @Test
  public void testCreateKnotPermissions() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Story story = builder.admin().stories().create("en", "test story", "Enter your answer");
      builder.invalid().knots().assertCreateFailStatus(401, "Content", "Test", story);
      builder.anonymous().knots().assertCreateFailStatus(401, "Content", "Test", story);
    }
  }

  /**
   * Tests searching for knot by id
   * @throws Exception exception
   */
  @Test
  public void testFindKnot() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Story story = builder.admin().stories().create("en", "test story", "Enter your answer");
      assertNotNull(story);

      Knot createdKnot = builder.admin().knots().create(story, KnotType.tEXT, "Test", "Content", 10.0, 20.0);
      builder.admin().knots().assertFindFailStatus(story.getId(), 404, UUID.randomUUID());
      builder.admin().knots().assertFindFailStatus(UUID.randomUUID(), 404, createdKnot.getId());
      Knot foundKnot = builder.admin().knots().findKnot(story, createdKnot);
      builder.admin().knots().assertKnotsEqual(createdKnot, foundKnot);

    }
  }

  @Test
  public void testFindKnotPermissions() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Story story = builder.admin().stories().create("en", "test story", "Enter your answer");
      Knot createdKnot = builder.admin().knots().create(story, KnotType.tEXT, "Test", "Content", 10.0, 20.0);
      assertNotNull(builder.admin().knots().findKnot(story, createdKnot));
      builder.invalid().knots().assertFindFailStatus(story.getId(), 401, createdKnot.getId());
      builder.anonymous().knots().assertFindFailStatus(story.getId(), 401, createdKnot.getId());
    }
  }

  /**
   * Tests intent updates
   * @throws Exception exception
   */
  @Test
  public void testUpdateKnot() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Story story = builder.admin().stories().create("en", "test story", "Enter your answer");
      Knot createdKnot = builder.admin().knots().create(story, KnotType.tEXT, "Test", "Content", 10.0, 20.0);
      Coordinates coordinates = new Coordinates(20.0, 40.0);
      builder.admin().knots().assertKnotsEqual(createdKnot, builder.admin().knots().findKnot(story, createdKnot));

      Knot foundKnotToUpdate = builder.admin().knots().findKnot(story, createdKnot);
      Knot updateKnot = new Knot(foundKnotToUpdate.getType(), foundKnotToUpdate.getTokenizer(), "updated knot", foundKnotToUpdate.getContent(),
        foundKnotToUpdate.getId(), foundKnotToUpdate.getHint(), foundKnotToUpdate.getStoryId(), coordinates, foundKnotToUpdate.getCreatedAt(), foundKnotToUpdate.getModifiedAt());

      Knot updatedKnot = builder.admin().knots().updateKnot(story, updateKnot);
      assertEquals(createdKnot.getId(), updatedKnot.getId());
      assertEquals(updateKnot.getName(), updatedKnot.getName());
      assertEquals(updateKnot.getCoordinates(), updatedKnot.getCoordinates());
      Knot foundKnot = builder.admin().knots().findKnot(story, createdKnot);
      assertEquals(createdKnot.getId(), foundKnot.getId());
      assertEquals(updateKnot.getName(), foundKnot.getName());
      assertEquals(updateKnot.getCoordinates(), foundKnot.getCoordinates());
    }
  }

  /**
   * Tests that not logged in users cannot update knots
   * @throws Exception
   */
  @Test
  public void testUpdateKnotPermissions() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Story story = builder.admin().stories().create("en", "test story", "Enter your answer");
      Knot createdKnot = builder.admin().knots().create(story, KnotType.tEXT, "Test", "Content", 10.0, 20.0);

      builder.anonymous().knots().assertUpdateFailStatus(story, 401, createdKnot);
      builder.invalid().knots().assertUpdateFailStatus(story, 401, createdKnot);
    }
  }

  /**
   * Tests intent deletion
   * @throws Exception exception
   */
  @Test
  public void testDeleteKnot() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Story story = builder.admin().stories().create("en", "test story", "Enter your answer");
      assertNotNull(story);
      Knot createdKnot = builder.admin().knots().create(story, KnotType.tEXT, "Test", "Content", 10.0, 20.0);
      Knot foundKnot = builder.admin().knots().findKnot(story, createdKnot);
      assertEquals(createdKnot.getId(), foundKnot.getId());
      builder.admin().knots().delete(story, createdKnot);
      builder.admin().knots().assertDeleteFailStatus(story, 404, createdKnot);
    }
  }


  @Test
  public void testDeleteKnotPermissions() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Story story = builder.admin().stories().create("en", "test story", "Enter your answer");
      Knot createdKnot = builder.admin().knots().create(story, KnotType.tEXT, "Test", "Content", 10.0, 20.0);
      builder.anonymous().knots().assertDeleteFailStatus(story, 401, createdKnot);
      builder.invalid().knots().assertDeleteFailStatus(story, 401, createdKnot);
    }
  }

}
