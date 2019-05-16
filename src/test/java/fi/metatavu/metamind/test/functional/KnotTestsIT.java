package fi.metatavu.metamind.test.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.UUID;

import org.junit.Test;

import fi.metatavu.metamind.client.model.Knot;
import fi.metatavu.metamind.client.model.KnotType;
import fi.metatavu.metamind.client.model.Story;
import fi.metatavu.metamind.test.functional.builder.TestBuilder;

public class KnotTestsIT extends AbstractFunctionalTest {

  @Test
  public void testCreateKnot() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Story story = builder.admin().stories().create("en", "test story");

      assertNotNull(builder.admin().knots().create(story, KnotType.TEXT, "Test", "Content"));
    }
  }

//  @Test
//  public void testCreateKnotPermissions() throws Exception {
//    try (TestBuilder builder = new TestBuilder()) {
//      builder.invalid().knots().assertCreateFailStatus(403, "Content", "Test");
//      builder.anonymous().knots().assertCreateFailStatus(401, "Content", "Test");
//    }
//  }

  @Test
  public void testFindKnot() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Story story = builder.admin().stories().create("en", "test story");
      assertNotNull(story);
      
      Knot createdKnot = builder.admin().knots().create(story, KnotType.TEXT, "Test", "Content");
      builder.admin().knots().assertFindFailStatus(story, 404, UUID.randomUUID());
      Knot foundKnot = builder.admin().knots().findKnot(story, createdKnot);
      builder.admin().knots().assertKnotsEqual(createdKnot, foundKnot);
      
    }
  }

//  @Test
//  public void testFindKnotPermissions() throws Exception {
//    try (TestBuilder builder = new TestBuilder()) {
//      Knot createdKnot = builder.admin().knots().create(KnotType.TEXT, "Test", "Content");
//      assertNotNull(builder.admin().knots().findKnot(createdKnot.getId()));
//      builder.invalid().knots().assertFindFailStatus(403, createdKnot.getId());
//      builder.anonymous().knots().assertFindFailStatus(401, createdKnot.getId());
//    }
//  }

//  @Test
//  public void testUpdateKnot() throws Exception {
//    try (TestBuilder builder = new TestBuilder()) {
//      Story story = builder.admin().stories().create("en", "test story");
//      Knot createdKnot = builder.admin().knots().create(story, KnotType.TEXT, "Test", "Content");
//      builder.admin().knots().assertKnotsEqual(createdKnot, builder.admin().knots().findKnot(story, createdKnot));
//
//      Knot updateKnot = builder.admin().knots().findKnot(story, createdKnot);
//      updateKnot.setName("updated knot");
//      Knot updatedKnot = builder.admin().knots().updateKnot(story, updateKnot);
//      assertEquals(createdKnot.getId(), updatedKnot.getId());
//      assertEquals(updateKnot.getName(), updatedKnot.getName());
//      Knot foundKnot = builder.admin().knots().findKnot(story, createdKnot);
//      assertEquals(createdKnot.getId(), foundKnot.getId());
//      assertEquals(updateKnot.getName(), foundKnot.getName());
//    }
//  }

//  @Test
//  public void testUpdateKnotPermissions() throws Exception {
//    try (TestBuilder builder = new TestBuilder()) {
//      Knot createdKnot = builder.admin().knots().create(KnotType.TEXT, "Test", "Content");
//
//      builder.anonymous().knots().assertUpdateFailStatus(401, createdKnot);
//      builder.invalid().knots().assertUpdateFailStatus(403, createdKnot);
//    }
//  }

//  @Test
//  public void testDeleteKnot() throws Exception {
//    try (TestBuilder builder = new TestBuilder()) {
//      Story story = builder.admin().stories().create("en", "test story");
//      Knot createdKnot = builder.admin().knots().create(story, KnotType.TEXT, "Test", "Content");
//      Knot foundKnot = builder.admin().knots().findKnot(story, createdKnot);
//      assertEquals(createdKnot.getId(), foundKnot.getId());
//      builder.admin().knots().delete(story, createdKnot);
//      builder.admin().knots().assertDeleteFailStatus(404, createdKnot);
//    }
//  }

//  @Test
//  public void testDeleteKnotPermissions() throws Exception {
//    try (TestBuilder builder = new TestBuilder()) {
//      Knot createdKnot = builder.admin().knots().create(KnotType.TEXT, "Test", "Content");
//      builder.anonymous().knots().assertDeleteFailStatus(401, createdKnot);
//      builder.invalid().knots().assertDeleteFailStatus(403, createdKnot);
//    }
//  }

}
