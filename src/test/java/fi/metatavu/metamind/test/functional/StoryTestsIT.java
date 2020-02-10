package fi.metatavu.metamind.test.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.UUID;

import org.junit.Test;

import fi.metatavu.metamind.client.model.Intent;
import fi.metatavu.metamind.client.model.IntentType;
import fi.metatavu.metamind.client.model.Knot;
import fi.metatavu.metamind.client.model.KnotType;
import fi.metatavu.metamind.client.model.Script;
import fi.metatavu.metamind.client.model.Story;
import fi.metatavu.metamind.client.model.TrainingMaterial;
import fi.metatavu.metamind.client.model.TrainingMaterialType;
import fi.metatavu.metamind.client.model.TrainingMaterialVisibility;
import fi.metatavu.metamind.test.functional.builder.TestBuilder;

public class StoryTestsIT extends AbstractFunctionalTest {

  @Test
  public void testCreateStory() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      assertNotNull(builder.admin().stories().create("en", "test story", "Enter your answer"));
    }
  }
  

  @Test
  public void testCreateStoryPermissions() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      builder.invalid().stories().assertCreateFailStatus(403, "en", "test story", "Enter your answer");
      builder.anonymous().stories().assertCreateFailStatus(401, "en", "test story", "Enter your answer");
    }
  }

  @Test
  public void testFindStory() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Story createdStory = builder.admin().stories().create("en", "test story", "Enter your answer");
      builder.admin().stories().assertFindFailStatus(404, UUID.randomUUID());
      Story foundStory = builder.admin().stories().findStory(createdStory.getId());
      builder.admin().stories().assertStoriesEqual(createdStory, foundStory);
    }
  }

  @Test
  public void testFindStoryPermissions() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Story createdStory = builder.admin().stories().create("en", "test story", "Enter your answer");

      assertNotNull(builder.admin().stories().findStory(createdStory.getId()));

      builder.invalid().stories().assertFindFailStatus(403, createdStory.getId());
      builder.anonymous().stories().assertFindFailStatus(401, createdStory.getId());

    }
  }

  @Test
  public void testUpdateStory() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Story createdStory = builder.admin().stories().create("en", "test story", "Enter your answer");
      builder.admin().stories().assertStoriesEqual(createdStory, builder.admin().stories().findStory(createdStory.getId()));

      Story updateStory = builder.admin().stories().findStory(createdStory.getId());
      updateStory.setName("updated story");
      updateStory.setDafaultHint("Enter your phone number here");
      Story updatedStory = builder.admin().stories().updateStory(updateStory);
      assertEquals(createdStory.getId(), updatedStory.getId());
      assertEquals(updateStory.getName(), updatedStory.getName());
      assertEquals(updateStory.getDafaultHint(), updatedStory.getDafaultHint());
      Story foundStory = builder.admin().stories().findStory(createdStory.getId());
      assertEquals(createdStory.getId(), foundStory.getId());
      assertEquals(updateStory.getName(), foundStory.getName());
      assertEquals(updateStory.getDafaultHint(), foundStory.getDafaultHint());
     
    }
  }


  @Test
  public void testUpdateStoryPermissions() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Story testStory = builder.admin().stories().create("en", "test story", "Enter your answer");
      builder.anonymous().stories().assertUpdateFailStatus(401, testStory);
      builder.invalid().stories().assertUpdateFailStatus(403, testStory);
      
    }
  }

  @Test
  public void testDeleteStory() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Story createdStory = builder.admin().stories().create("en", "test story", "Enter your answer");
      Story foundStory = builder.admin().stories().findStory(createdStory.getId());
      assertEquals(createdStory.getId(), foundStory.getId());
      builder.admin().stories().delete(createdStory);
      builder.admin().stories().assertDeleteFailStatus(404, createdStory);
    }
  }

  @Test
  public void testDeleteStorypermissions() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Story createdStory = builder.admin().stories().create("en", "test story", "Enter your answer");
      builder.anonymous().stories().assertDeleteFailStatus(401, createdStory);
      builder.invalid().stories().assertDeleteFailStatus(403, createdStory);
    }
  }
  
  @Test
  public void testExportImportStory() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Story story = builder.admin().stories().create("en", "test story", "Enter your answer");
      Knot knot1 = builder.admin().knots().create(story, KnotType.TEXT, "Test knot", "Content", 10.0, 20.0);
      Knot knot2 = builder.admin().knots().create(story, KnotType.TEXT, "Test knot 2", "Content 2", 10.0, 50.0);
      Intent intent = builder.admin().intents().create(story.getId(), knot1, knot2, "Test Intent", IntentType.DEFAULT, false, "quickresponse", 1, null, null, null, null);
      TrainingMaterial material = builder.admin().trainingMaterial().create(story.getId(), TrainingMaterialType.INTENTOPENNLPDOCCAT, "Test material", "Test", TrainingMaterialVisibility.STORY);
      Script script = builder.admin().scripts().create("Test content", "English", "Test script", "0.1");
      
    }
  }

}
