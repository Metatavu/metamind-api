package fi.metatavu.metamind.functional.tests;

import fi.metatavu.metamind.api.client.models.*;
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
 * Tests for story API
 */
@QuarkusTest
@QuarkusTestResource.List(value = {
  @QuarkusTestResource(MysqlResource.class),
  @QuarkusTestResource(KeycloakResource.class)
})
public class StoryTestsIT {

  /**
   * Tests story creation
   * @throws Exception exception
   */
  @Test
  public void testCreateStory() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      assertNotNull(builder.admin().stories().create("en", "test story", "Enter your answer"));
    }
  }

  @Test
  public void testCreateStoryPermissions() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      builder.invalid().stories().assertCreateFailStatus(401, "en", "test story", "Enter your answer");
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

      builder.invalid().stories().assertFindFailStatus(401, createdStory.getId());
      builder.anonymous().stories().assertFindFailStatus(401, createdStory.getId());

      Story testStoryGroup1 = builder.admin().stories().create("en", "test story", "Enter your answer");
      builder.test2().stories().assertFindFailStatus(403, testStoryGroup1.getId());
    }
  }

  /**
   * Tests story update
   * @throws Exception exception
   */
  @Test
  public void testUpdateStory() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Story createdStory = builder.admin().stories().create("en", "test story", "Enter your answer");
      builder.admin().stories().assertStoriesEqual(createdStory, builder.admin().stories().findStory(createdStory.getId()));
      Story updateStory = new Story("updated story", createdStory.getLocale(), createdStory.getId(), "Enter your phone number here", null, null, null);
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
      builder.invalid().stories().assertUpdateFailStatus(401, testStory);

      Story testStoryGroup1 = builder.admin().stories().create("en", "test story", "Enter your answer");
      builder.test2().stories().assertUpdateFailStatus(403, testStoryGroup1);
    }
  }

  /**
   * Tests story deletion
   * @throws Exception exception
   */
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
  public void testDeleteStoryPermissions() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Story createdStory = builder.admin().stories().create("en", "test story", "Enter your answer");
      builder.anonymous().stories().assertDeleteFailStatus(401, createdStory);
      builder.invalid().stories().assertDeleteFailStatus(401, createdStory);
      builder.admin().stories().delete(createdStory);

      Story createdStoryGroup1 = builder.test1().stories().create("en", "test story1", "Enter your answer");
      builder.test2().stories().assertDeleteFailStatus(403, createdStoryGroup1);
      builder.test1().stories().delete(createdStoryGroup1);
    }
  }

  /**
   * Tests listing stories with various access rights
   */
  @Test
  public void testListStoriesPermissions() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      builder.admin().stories().create("en", "test story1", "Enter your answer");
      builder.test1().stories().create("en", "test story1", "Enter your answer");
      builder.test2().stories().create("en", "test story2", "Enter your answer");
      builder.test2().stories().create("en", "test story3", "Enter your answer");

      assertEquals(1, builder.test1().stories().listStories().length);
      assertEquals(2, builder.test2().stories().listStories().length);
      assertEquals(4, builder.admin().stories().listStories().length);
    }
  }

  /**
   * Tests story exporting and importing
   * @throws Exception exception
   */
  @Test
  public void testExportImportStory() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Story story = builder.admin().stories().create("en", "test story", "Enter your answer");
      Knot knot1 = builder.admin().knots().createBasicKnot(story, KnotType.tEXT, "Test1", "Content", 10.0, 20.0);
      Knot knot2 = builder.admin().knots().createBasicKnot(story, KnotType.tEXT, "Test1", "Content", 10.0, 20.0);
      TrainingMaterial material = builder.admin().trainingMaterial().create(story.getId(), TrainingMaterialType.iNTENTOPENNLPDOCCAT, "Test material", "Test", TrainingMaterialVisibility.sTORY);
      Intent intent = builder.admin().intents().create(story.getId(), knot1, knot2, "Test Intent", IntentType.dEFAULT, false, "quickresponse", 1, material.getId(), null, null, null);
      fi.metatavu.metamind.api.client.models.Variable variable = builder.admin().variables().create(story.getId(), "Test variable", VariableType.sTRING, "");

      ExportedStory exportedStory = builder.admin().storyExport().exportStory(story.getId());
      assertNotNull(exportedStory);
      Story importedStory = builder.admin().stories().addClosable(builder.admin().storyExport().importStory(exportedStory));
      assertNotNull(importedStory);
      assertNotNull(builder.admin().stories().findStory(importedStory.getId()));
      assertEquals(story.getName(), importedStory.getName());
      assertEquals(story.getDafaultHint(), importedStory.getDafaultHint());
      assertEquals(story.getLocale(), importedStory.getLocale());

      Knot[] importedKnots = builder.admin().knots().listKnots(importedStory);
      assertEquals(2, importedKnots.length);
      Knot importedKnot = importedKnots[0];
      assertNotNull(importedKnot);
      assertEquals(knot1.getName(), importedKnot.getName());
      assertEquals(knot1.getType(), importedKnot.getType());
      assertEquals(knot1.getContent(), importedKnot.getContent());
      assertEquals(knot1.getCoordinates().getX(), importedKnot.getCoordinates().getX());
      assertEquals(knot1.getCoordinates().getY(), importedKnot.getCoordinates().getY());

      Intent[] importedIntents = builder.admin().intents().listIntents(importedStory);
      assertEquals(1, importedIntents.length);
      Intent importedIntent = importedIntents[0];
      assertNotNull(importedIntent);
      assertEquals(intent.getName(), importedIntent.getName());
      assertEquals(intent.getQuickResponse(), importedIntent.getQuickResponse());
      assertEquals(intent.getQuickResponseOrder(), importedIntent.getQuickResponseOrder());
      assertEquals(intent.getGlobal(), importedIntent.getGlobal());

      TrainingMaterial[] importedMaterials = builder.admin().trainingMaterial().listTrainingMaterial(importedStory, material.getType(), material.getVisibility());
      assertEquals(1, importedMaterials.length);
      TrainingMaterial importedMaterial = importedMaterials[0];
      assertNotNull(importedMaterial);
      assertEquals(material.getType(), importedMaterial.getType());
      assertEquals(material.getText(), importedMaterial.getText());
      assertEquals(material.getName(), importedMaterial.getName());
      assertEquals(material.getVisibility(), importedMaterial.getVisibility());

      assertEquals(importedIntent.getTrainingMaterials().getIntentOpenNlpDoccatId(), importedMaterial.getId());

      Variable[] importedVariables = builder.admin().variables().listVariables(importedStory);
      assertEquals(1, importedVariables.length);
      Variable importedVariable = importedVariables[0];
      assertNotNull(importedVariable);
      assertEquals(variable.getName(), importedVariable.getName());
      assertEquals(variable.getType(), importedVariable.getType());
      assertEquals(variable.getValidationScript(), importedVariable.getValidationScript());

      //delete knots and intents dependable on imported story
      builder.admin().intents().delete(importedStory, importedIntents[0]);
      builder.admin().knots().delete(importedStory, importedKnots[0]);
      builder.admin().knots().delete(importedStory, importedKnots[1]);
      builder.admin().variables().delete(importedStory, importedVariables[0]);
      builder.admin().trainingMaterial().delete(importedMaterials[0]);
    }
  }
}
