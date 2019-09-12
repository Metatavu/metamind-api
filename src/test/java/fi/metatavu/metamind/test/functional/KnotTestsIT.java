package fi.metatavu.metamind.test.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.UUID;

import org.junit.Test;

import fi.metatavu.metamind.client.model.Coordinates;
import fi.metatavu.metamind.client.model.Knot;
import fi.metatavu.metamind.client.model.KnotType;
import fi.metatavu.metamind.client.model.Story;
import fi.metatavu.metamind.test.functional.builder.TestBuilder;

public class KnotTestsIT extends AbstractFunctionalTest {

  @Test
  public void testCreateKnot() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Story story = builder.admin().stories().create("en", "test story", "Enter your answer");
      assertNotNull(builder.admin().knots().create(story, KnotType.TEXT, "Test", "Content", 10.0, 20.0));
      assertNotNull(builder.admin().knots().create(story, KnotType.TEXT, "Test Null", "Content", null, null));
    }
  }
  

  @Test
  public void testCreateKnotPermissions() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Story story = builder.admin().stories().create("en", "test story", "Enter your answer");
      builder.invalid().knots().assertCreateFailStatus(403, "Content", "Test", story);
      builder.anonymous().knots().assertCreateFailStatus(401, "Content", "Test", story);
    }
  }

  @Test
  public void testFindKnot() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Story story = builder.admin().stories().create("en", "test story", "Enter your answer");
      assertNotNull(story);
      
      Knot createdKnot = builder.admin().knots().create(story, KnotType.TEXT, "Test", "Content", 10.0, 20.0);
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
      Knot createdKnot = builder.admin().knots().create(story, KnotType.TEXT, "Test", "Content", 10.0, 20.0);
      assertNotNull(builder.admin().knots().findKnot(story, createdKnot));
      builder.invalid().knots().assertFindFailStatus(story.getId(), 403, createdKnot.getId());
      builder.anonymous().knots().assertFindFailStatus(story.getId(), 401, createdKnot.getId());
    }
  }

  @Test
  public void testUpdateKnot() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Story story = builder.admin().stories().create("en", "test story", "Enter your answer");
      Knot createdKnot = builder.admin().knots().create(story, KnotType.TEXT, "Test", "Content", 10.0, 20.0);
      Coordinates coordinates = new Coordinates();
      coordinates.setX(20.0);
      coordinates.setY(40.0);
      builder.admin().knots().assertKnotsEqual(createdKnot, builder.admin().knots().findKnot(story, createdKnot));

      Knot updateKnot = builder.admin().knots().findKnot(story, createdKnot);
      updateKnot.setName("updated knot");
      updateKnot.setCoordinates(coordinates);
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
  

  @Test
  public void testUpdateKnotPermissions() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Story story = builder.admin().stories().create("en", "test story", "Enter your answer");
      Knot createdKnot = builder.admin().knots().create(story, KnotType.TEXT, "Test", "Content", 10.0, 20.0);

      builder.anonymous().knots().assertUpdateFailStatus(story, 401, createdKnot);
      builder.invalid().knots().assertUpdateFailStatus(story, 403, createdKnot);
    }
  }

  @Test
  public void testDeleteKnot() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Story story = builder.admin().stories().create("en", "test story", "Enter your answer");
      assertNotNull(story);
      Knot createdKnot = builder.admin().knots().create(story, KnotType.TEXT, "Test", "Content", 10.0, 20.0);
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
      Knot createdKnot = builder.admin().knots().create(story, KnotType.TEXT, "Test", "Content", 10.0, 20.0);
      builder.anonymous().knots().assertDeleteFailStatus(story, 401, createdKnot);
      builder.invalid().knots().assertDeleteFailStatus(story, 403, createdKnot);
    }
  }

}
