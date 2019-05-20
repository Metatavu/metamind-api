package fi.metatavu.metamind.test.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.UUID;

import org.junit.Test;

import com.fasterxml.jackson.databind.deser.impl.ExternalTypeHandler.Builder;

import fi.metatavu.metamind.client.model.Intent;
import fi.metatavu.metamind.client.model.Knot;
import fi.metatavu.metamind.client.model.KnotType;
import fi.metatavu.metamind.client.model.Story;
import fi.metatavu.metamind.rest.model.IntentType;
import fi.metatavu.metamind.test.functional.builder.TestBuilder;

public class IntentTestsIT extends AbstractFunctionalTest {

  @Test
  public void testCreateIntent() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Story story = builder.admin().stories().create("en", "test story");
      Knot sourceKnot = builder.admin().knots().create(story, KnotType.TEXT, "Test1", "Content");
      Knot targetKnot = builder.admin().knots().create(story, KnotType.TEXT, "Test2", "Content");

      assertNotNull(builder.admin().intents().create(story.getId(), sourceKnot, targetKnot, "Test Intent"));
    }
  }
  
//TODO uncomment when permissions are added 
//  @Test
//  public void testCreateIntentPermissions() throws Exception {
//    try (TestBuilder builder = new TestBuilder()) {
//      Story story = builder.admin().stories().create("en", "test story");
//      Knot sourceKnot = builder.admin().knots().create(story, KnotType.TEXT, "Test1", "Content");
//      Knot targetKnot = builder.admin().knots().create(story, KnotType.TEXT, "Test2", "Content");
//      
//      builder.invalid().intents().assertCreateFailStatus(403, "Test Intent", sourceKnot, targetKnot, story);
//      builder.anonymous().intents().assertCreateFailStatus(401, "Test Intent", sourceKnot, targetKnot, story);
//    }
//  }

  @Test
  public void testFindIntent() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Story story = builder.admin().stories().create("en", "test story");
      Knot sourceKnot = builder.admin().knots().create(story, KnotType.TEXT, "Test1", "Content");
      Knot targetKnot = builder.admin().knots().create(story, KnotType.TEXT, "Test2", "Content");
      Intent createdIntent = builder.admin().intents().create(story.getId(), sourceKnot, targetKnot, "Test Intent");
      
      builder.admin().intents().assertFindFailStatus(story.getId(), 404, UUID.randomUUID());
      builder.admin().intents().assertFindFailStatus(UUID.randomUUID(), 404, createdIntent.getId());
      Intent foundIntent = builder.admin().intents().findIntent(story, createdIntent);
      builder.admin().intents().assertIntentsEqual(createdIntent, foundIntent);
      
    }
  }

//TODO uncomment when permissions are added  
//  @Test
//  public void testFindIntentPermissions() throws Exception {
//    try (TestBuilder builder = new TestBuilder()) {
//      Story story = builder.admin().stories().create("en", "test story");
//      Knot sourceKnot = builder.admin().knots().create(story, KnotType.TEXT, "Test1", "Content");
//      Knot targetKnot = builder.admin().knots().create(story, KnotType.TEXT, "Test2", "Content");
//      Intent createdIntent = builder.admin().intents().create(story.getId(), sourceKnot, targetKnot, "Test Intent");
//      
//      assertNotNull(builder.admin().intents().findIntent(story, createdIntent));
//      builder.invalid().intents().assertFindFailStatus(story.getId(), 403, createdIntent.getId());
//      builder.anonymous().knots().assertFindFailStatus(story.getId(), 401, createdIntent.getId());
//    }
//  }

  @Test
  public void testUpdateIntent() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Story story = builder.admin().stories().create("en", "test story");
      Knot sourceKnot = builder.admin().knots().create(story, KnotType.TEXT, "Test1", "Content");
      Knot targetKnot = builder.admin().knots().create(story, KnotType.TEXT, "Test2", "Content");
      Intent createdIntent = builder.admin().intents().create(story.getId(), sourceKnot, targetKnot, "Test Intent");

      Intent updateIntent = builder.admin().intents().findIntent(story, createdIntent);
      updateIntent.setName("Updated intent");
      Intent updatedIntent = builder.admin().intents().updateIntent(story, updateIntent);
      assertEquals(createdIntent.getId(), updatedIntent.getId());
      assertEquals(updateIntent.getName(), updatedIntent.getName());
      Intent foundIntent = builder.admin().intents().findIntent(story, createdIntent);
      assertEquals(createdIntent.getId(), foundIntent.getId());
      assertEquals(updateIntent.getName(), foundIntent.getName());
    }
  }

//TODO uncomment when permissions are added
//  @Test
//  public void testUpdateIntentPermissions() throws Exception {
//    try (TestBuilder builder = new TestBuilder()) {
//      Story story = builder.admin().stories().create("en", "test story");
//      Knot sourceKnot = builder.admin().knots().create(story, KnotType.TEXT, "Test1", "Content");
//      Knot targetKnot = builder.admin().knots().create(story, KnotType.TEXT, "Test2", "Content");
//      Intent createdIntent = builder.admin().intents().create(story.getId(), sourceKnot, targetKnot, "Test Intent");
//
//      builder.anonymous().intents().assertUpdateFailStatus(story, 401, createdIntent);
//      builder.invalid().intents().assertUpdateFailStatus(story, 403, createdIntent);
//    }
//  }

  @Test
  public void testDeleteIntent() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Story story = builder.admin().stories().create("en", "test story");
      Knot sourceKnot = builder.admin().knots().create(story, KnotType.TEXT, "Test1", "Content");
      Knot targetKnot = builder.admin().knots().create(story, KnotType.TEXT, "Test2", "Content");
      Intent createdIntent = builder.admin().intents().create(story.getId(), sourceKnot, targetKnot, "Test Intent");
      Intent foundIntent = builder.admin().intents().findIntent(story, createdIntent);
      assertEquals(createdIntent.getId(), foundIntent.getId());
      builder.admin().intents().delete(story, createdIntent);
      builder.admin().intents().assertDeleteFailStatus(story, 404, createdIntent);

    }
  }
 
//TODO uncomment when permissions are added
//  @Test
//  public void testDeleteIntentPermissions() throws Exception {
//    try (TestBuilder builder = new TestBuilder()) {
//      Story story = builder.admin().stories().create("en", "test story");
//      Knot sourceKnot = builder.admin().knots().create(story, KnotType.TEXT, "Test1", "Content");
//      Knot targetKnot = builder.admin().knots().create(story, KnotType.TEXT, "Test2", "Content");
//      Intent createdIntent = builder.admin().intents().create(story.getId(), sourceKnot, targetKnot, "Test Intent");
//
//      builder.anonymous().intents().assertDeleteFailStatus(story, 401, createdIntent);
//      builder.invalid().intents().assertDeleteFailStatus(story, 403, createdIntent);
//    }
//  }

}