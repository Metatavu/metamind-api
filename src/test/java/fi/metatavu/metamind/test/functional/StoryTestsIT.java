package fi.metatavu.metamind.test.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.UUID;

import org.junit.Test;

import fi.metatavu.metamind.client.model.ExportedStory;
import fi.metatavu.metamind.client.model.Intent;
import fi.metatavu.metamind.client.model.IntentTrainingMaterials;
import fi.metatavu.metamind.client.model.IntentType;
import fi.metatavu.metamind.client.model.Knot;
import fi.metatavu.metamind.client.model.KnotType;
import fi.metatavu.metamind.client.model.Script;
import fi.metatavu.metamind.client.model.Story;
import fi.metatavu.metamind.client.model.TrainingMaterial;
import fi.metatavu.metamind.client.model.TrainingMaterialType;
import fi.metatavu.metamind.client.model.TrainingMaterialVisibility;
import fi.metatavu.metamind.client.model.Variable;
import fi.metatavu.metamind.client.model.VariableType;
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
      Knot knot1 = builder.admin().knots().create(story, KnotType.TEXT, "Test1", "Content", 10.0, 20.0);
      Knot knot2 = builder.admin().knots().create(story, KnotType.TEXT, "Test2", "Content", 10.0, 50.0);
      TrainingMaterial material = builder.admin().trainingMaterial().create(story.getId(), TrainingMaterialType.INTENTOPENNLPDOCCAT, "Test material", "Test", TrainingMaterialVisibility.STORY);
      Intent intent = builder.admin().intents().create(story.getId(), knot1, knot2, "Test Intent", IntentType.DEFAULT, false, "quickresponse", 1, null, null, null, null);
      IntentTrainingMaterials materials = new IntentTrainingMaterials();
      materials.setIntentOpenNlpDoccatId(material.getId());
      intent.setTrainingMaterials(materials);
      builder.admin().intents().updateIntent(story, intent);
      Script script = builder.admin().scripts().create("Test content", "English", "Test script", "0.1");
      Variable variable = builder.admin().variables().create(story.getId(), "Test variable", VariableType.STRING, "");
      
      ExportedStory exportedStory = builder.admin().storyExport().exportStory(story.getId());
      assertNotNull(exportedStory);
      Story importedStory = builder.admin().storyExport().importStory(exportedStory);
      assertNotNull(importedStory);
      assertNotNull(builder.admin().stories().findStory(importedStory.getId()));     
      assertEquals(story.getName(), importedStory.getName());
      assertEquals(story.getDafaultHint(), importedStory.getDafaultHint());
      assertEquals(story.getLocale(), importedStory.getLocale());
      
      Knot importedKnot = builder.admin().knots().listKnots(importedStory).get(0);
      assertNotNull(importedKnot);
      assertEquals(knot1.getName(), importedKnot.getName());
      assertEquals(knot1.getType(), importedKnot.getType());
      assertEquals(knot1.getContent(), importedKnot.getContent());
      assertEquals(knot1.getCoordinates(), importedKnot.getCoordinates());
      
      Intent importedIntent = builder.admin().intents().listIntents(importedStory).get(0);
      assertNotNull(importedIntent);
      assertEquals(intent.getName(), importedIntent.getName());
      assertEquals(intent.getQuickResponse(), importedIntent.getQuickResponse());
      assertEquals(intent.getQuickResponseOrder(), importedIntent.getQuickResponseOrder());
      assertEquals(intent.getSourceKnotId(), importedIntent.getSourceKnotId());
      assertEquals(intent.getTargetKnotId(), importedIntent.getTargetKnotId());
      assertEquals(intent.isGlobal(), importedIntent.isGlobal());
      
      TrainingMaterial importedMaterial = builder.admin().trainingMaterial().listTrainingMaterial(importedStory, material.getType(), material.getVisibility()).get(0);
      assertNotNull(importedMaterial);
      assertEquals(material.getType(), importedMaterial.getType());
      assertEquals(material.getText(), importedMaterial.getText());
      assertEquals(material.getName(), importedMaterial.getName());
      assertEquals(material.getVisibility(), importedMaterial.getVisibility());
      
      Script importedScript = builder.admin().scripts().listScripts().get(1);
      assertNotNull(importedScript);
      assertEquals(script.getContent(), importedScript.getContent());
      assertEquals(script.getVersion(), importedScript.getVersion());
      assertEquals(script.getName(), importedScript.getName());
      assertEquals(script.getLanguage(), importedScript.getLanguage());
      
      Variable importedVariable = builder.admin().variables().listVariables(importedStory).get(0);
      assertNotNull(importedVariable);
      assertEquals(variable.getName(), importedVariable.getName());
      assertEquals(variable.getType(), importedVariable.getType());
      assertEquals(variable.getValidationScript(), importedVariable.getValidationScript());
   }
  }

}
