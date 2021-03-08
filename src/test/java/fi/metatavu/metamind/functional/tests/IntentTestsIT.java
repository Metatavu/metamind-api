package fi.metatavu.metamind.functional.tests;

import fi.metatavu.metamind.api.client.models.*;
import fi.metatavu.metamind.functional.impl.TestBuilder;
import fi.metatavu.metamind.functional.resources.KeycloakResource;
import fi.metatavu.metamind.functional.resources.MysqlResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import liquibase.pro.packaged.U;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
@QuarkusTestResource.List(value = {
        @QuarkusTestResource(MysqlResource.class),
        @QuarkusTestResource(KeycloakResource.class)
})
public class IntentTestsIT {

  @Test  
  public void testCreateIntent() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Story story = builder.admin().stories().create("en", "test story", "Enter your answer");
      Knot sourceKnot = builder.admin().knots().create(story, KnotType.tEXT, "Test1", "Content", 10.0, 20.0);
      Knot targetKnot = builder.admin().knots().create(story, KnotType.tEXT, "Test2", "Content", 20.0, 40.0);

      assertNotNull(builder.admin().intents().create(story.getId(), sourceKnot, targetKnot, "Test Intent", IntentType.dEFAULT, false, "quickresponse", 1, null, null, null, null));
    }
  }
  
 /*
  @Test
  public void testCreateIntentPermissions() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Story story = builder.admin().stories().create("en", "test story", "Enter your answer");
      Knot sourceKnot = builder.admin().knots().create(story, KnotType.tEXT, "Test1", "Content", 10.0, 20.0);
      Knot targetKnot = builder.admin().knots().create(story, KnotType.tEXT, "Test2", "Content", 20.0, 40.0);
      builder.invalid().intents().assertCreateFailStatus(403, "Test Intent", sourceKnot, targetKnot, story);
      builder.anonymous().intents().assertCreateFailStatus(401, "Test Intent", sourceKnot, targetKnot, story);
    }
  }
*/
  @Test
  public void testFindIntent() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Story story = builder.admin().stories().create("en", "test story", "Enter your answer");
      Knot sourceKnot = builder.admin().knots().create(story, KnotType.tEXT, "Test1", "Content", 10.0, 20.0);
      Knot targetKnot = builder.admin().knots().create(story, KnotType.tEXT, "Test2", "Content", 20.0, 40.0);
      Intent createdIntent = builder.admin().intents().create(story.getId(), sourceKnot, targetKnot, "Test Intent", IntentType.dEFAULT, false, "quickresponse", 1, null, null, null, null);
      
      builder.admin().intents().assertFindFailStatus(story.getId(), 404, UUID.randomUUID());
      builder.admin().intents().assertFindFailStatus(UUID.randomUUID(), 404, createdIntent.getId());
      Intent foundIntent = builder.admin().intents().findIntent(story, createdIntent);
      builder.admin().intents().assertIntentsEqual(createdIntent, foundIntent);
      
    }
  }

  /*
  @Test
  public void testFindIntentPermissions() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Story story = builder.admin().stories().create("en", "test story", "Enter your answer");
      Knot sourceKnot = builder.admin().knots().create(story, KnotType.tEXT, "Test1", "Content", 10.0, 20.0);
      Knot targetKnot = builder.admin().knots().create(story, KnotType.tEXT, "Test2", "Content", 20.0, 40.0);
      Intent createdIntent = builder.admin().intents().create(story.getId(), sourceKnot, targetKnot, "Test Intent", IntentType.dEFAULT, false, "quickresponse", 1,  null, null, null, null);
      
      assertNotNull(builder.admin().intents().findIntent(story, createdIntent));
      builder.invalid().intents().assertFindFailStatus(story.getId(), 403, createdIntent.getId());
      builder.anonymous().intents().assertFindFailStatus(story.getId(), 401, createdIntent.getId());
    }
  }*/

  //@Test
  public void testUpdateIntent() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Story story = builder.admin().stories().create("en", "test story", "Enter your answer");
      Knot sourceKnot = builder.admin().knots().create(story, KnotType.tEXT, "Test1", "Content", 10.0, 20.0);
      Knot targetKnot = builder.admin().knots().create(story, KnotType.tEXT, "Test2", "Content", 20.0, 40.0);
      Intent createdIntent = builder.admin().intents().create(story.getId(), sourceKnot, targetKnot, "Test Intent", IntentType.dEFAULT, false, "quickresponse", 1, null, null, null, null);

      Intent updateIntent = new Intent(2, IntentType.cONFUSED, null, false,
              null, null, "Updated intent", "veryquickresponse", null, null,
              null);

      Intent updatedIntent = builder.admin().intents().updateIntent(story, updateIntent);
      assertEquals(createdIntent.getId(), updatedIntent.getId());
      assertEquals(updateIntent.getName(), updatedIntent.getName());
      assertEquals(updateIntent.getType(), updatedIntent.getType());
      assertEquals(updateIntent.getQuickResponse(), updatedIntent.getQuickResponse());
      assertEquals(updateIntent.getQuickResponseOrder(), updatedIntent.getQuickResponseOrder());
      Intent foundIntent = builder.admin().intents().findIntent(story, createdIntent);
      assertEquals(createdIntent.getId(), foundIntent.getId());
      assertEquals(updateIntent.getName(), foundIntent.getName());
    }
  }

/*
  @Test
  public void testUpdateIntentPermissions() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Story story = builder.admin().stories().create("en", "test story", "Enter your answer");
      Knot sourceKnot = builder.admin().knots().create(story, KnotType.tEXT, "Test1", "Content", 10.0, 20.0);
      Knot targetKnot = builder.admin().knots().create(story, KnotType.tEXT, "Test2", "Content", 20.0, 40.0);
      Intent createdIntent = builder.admin().intents().create(story.getId(), sourceKnot, targetKnot, "Test Intent", IntentType.dEFAULT, false, "quickresponse", 1,  null, null, null, null);

      builder.anonymous().intents().assertUpdateFailStatus(story, 401, createdIntent);
      builder.invalid().intents().assertUpdateFailStatus(story, 403, createdIntent);
    }
  }
*/
  @Test
  public void testDeleteIntent() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Story story = builder.admin().stories().create("en", "test story", "Enter your answer");
      Knot sourceKnot = builder.admin().knots().create(story, KnotType.tEXT, "Test1", "Content", 10.0, 20.0);
      Knot targetKnot = builder.admin().knots().create(story, KnotType.tEXT, "Test2", "Content", 20.0, 40.0);
      Intent createdIntent = builder.admin().intents().create(story.getId(), sourceKnot, targetKnot, "Test Intent", IntentType.dEFAULT, false, "quickresponse", 1, null, null, null, null);
      Intent foundIntent = builder.admin().intents().findIntent(story, createdIntent);
      assertEquals(createdIntent.getId(), foundIntent.getId());
      builder.admin().intents().delete(story, createdIntent);
      builder.admin().intents().assertDeleteFailStatus(story, 404, createdIntent);

    }
  }
 
/*
  @Test
  public void testDeleteIntentPermissions() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Story story = builder.admin().stories().create("en", "test story", "Enter your answer");
      Knot sourceKnot = builder.admin().knots().create(story, KnotType.tEXT, "Test1", "Content", 10.0, 20.0);
      Knot targetKnot = builder.admin().knots().create(story, KnotType.tEXT, "Test2", "Content", 20.0, 40.0);
      Intent createdIntent = builder.admin().intents().create(story.getId(), sourceKnot, targetKnot, "Test Intent", IntentType.dEFAULT, false, "quickresponse", 1, null, null, null, null);

      builder.anonymous().intents().assertDeleteFailStatus(story, 401, createdIntent);
      builder.invalid().intents().assertDeleteFailStatus(story, 403, createdIntent);
    }
  }*/

}