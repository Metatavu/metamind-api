package fi.metatavu.metamind.test.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.UUID;

import org.junit.Test;

import fi.metatavu.metamind.client.model.Story;
import fi.metatavu.metamind.client.model.TrainingMaterial;
import fi.metatavu.metamind.client.model.TrainingMaterialType;
import fi.metatavu.metamind.test.functional.builder.TestBuilder;

public class TrainingMaterialTestsIT extends AbstractFunctionalTest {

  @Test
  public void testCreateTrainingMaterial() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Story story = builder.admin().stories().create("en", "test story", "Enter your answer");
      
      assertNotNull(builder.admin().trainingMaterial().create(story.getId(), TrainingMaterialType.INTENTOPENNLPDOCCAT, "Test material", "Test"));
    }
  }
  
 
  @Test
  public void testCreateTrainingMaterialPermissions() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Story story = builder.admin().stories().create("en", "test story", "Enter your answer");
      
      builder.invalid().trainingMaterial().assertCreateFailStatus(403, story.getId(), TrainingMaterialType.INTENTOPENNLPDOCCAT, "Test material", "Test");
      builder.anonymous().trainingMaterial().assertCreateFailStatus(401, story.getId(), TrainingMaterialType.INTENTOPENNLPDOCCAT, "Test material", "Test");
    }
  }

  @Test
  public void testFindTrainingMaterial() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Story story = builder.admin().stories().create("en", "test story", "Enter your answer");
      TrainingMaterial createdTrainingMaterial = builder.admin().trainingMaterial().create(story.getId(), TrainingMaterialType.INTENTOPENNLPDOCCAT, "Test material", "Test");
      
      builder.admin().trainingMaterial().assertFindFailStatus(404, UUID.randomUUID());
      TrainingMaterial foundTrainingMaterial = builder.admin().trainingMaterial().findTrainingMaterial(createdTrainingMaterial);
      builder.admin().trainingMaterial().assertTrainingMaterialEqual(createdTrainingMaterial, foundTrainingMaterial);
      
    }
  }

  
  @Test
  public void testFindTrainingMaterialPermissions() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Story story = builder.admin().stories().create("en", "test story", "Enter your answer");
      TrainingMaterial createdTrainingMaterial = builder.admin().trainingMaterial().create(story.getId(), TrainingMaterialType.INTENTOPENNLPDOCCAT, "Test material", "Test");
      
      assertNotNull(builder.admin().trainingMaterial().findTrainingMaterial(createdTrainingMaterial));
      builder.invalid().trainingMaterial().assertFindFailStatus(403, createdTrainingMaterial.getId());
      builder.anonymous().trainingMaterial().assertFindFailStatus(401, createdTrainingMaterial.getId());
    }
  }

  @Test
  public void testUpdateTrainingMaterial() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Story story = builder.admin().stories().create("en", "test story", "Enter your answer");
      TrainingMaterial createdTrainingMaterial = builder.admin().trainingMaterial().create(story.getId(), TrainingMaterialType.INTENTOPENNLPDOCCAT, "Test material", "Test");

      TrainingMaterial updateTrainingMaterial = builder.admin().trainingMaterial().findTrainingMaterial(createdTrainingMaterial);
      updateTrainingMaterial.setName("Updated training material");
      updateTrainingMaterial.setType(TrainingMaterialType.INTENTOPENNLPDOCCAT);
      updateTrainingMaterial.setText("value");
      
      TrainingMaterial updatedTrainingMaterial = builder.admin().trainingMaterial().updateTrainingMaterial(updateTrainingMaterial);
      assertEquals(createdTrainingMaterial.getId(), updatedTrainingMaterial.getId());
      assertEquals(updateTrainingMaterial.getName(), updatedTrainingMaterial.getName());
      assertEquals(updateTrainingMaterial.getType(), updatedTrainingMaterial.getType());
      assertEquals(updateTrainingMaterial.getText(), updatedTrainingMaterial.getText());
      TrainingMaterial foundTrainingMaterial = builder.admin().trainingMaterial().findTrainingMaterial(createdTrainingMaterial);
      assertEquals(createdTrainingMaterial.getId(), foundTrainingMaterial.getId());
      assertEquals(updateTrainingMaterial.getType(), foundTrainingMaterial.getType());
      assertEquals(updateTrainingMaterial.getText(), foundTrainingMaterial.getText());
      assertEquals(updateTrainingMaterial.getName(), foundTrainingMaterial.getName());
    }
  }


  @Test
  public void testUpdateIntentPermissions() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Story story = builder.admin().stories().create("en", "test story", "Enter your answer");
      TrainingMaterial createdTrainingMaterial = builder.admin().trainingMaterial().create(story.getId(), TrainingMaterialType.INTENTOPENNLPDOCCAT, "Test material", "Test");

      builder.anonymous().trainingMaterial().assertUpdateFailStatus(401, createdTrainingMaterial);
      builder.invalid().trainingMaterial().assertUpdateFailStatus(403, createdTrainingMaterial);
    }
  }

  @Test
  public void testDeleteTrainingMaterial() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Story story = builder.admin().stories().create("en", "test story", "Enter your answer");
 
      TrainingMaterial createdTrainingMaterial = builder.admin().trainingMaterial().create(story.getId(), TrainingMaterialType.INTENTOPENNLPDOCCAT, "Test material", "Test");
      TrainingMaterial foundTrainingMaterial = builder.admin().trainingMaterial().findTrainingMaterial(createdTrainingMaterial);
      assertEquals(createdTrainingMaterial.getId(), foundTrainingMaterial.getId());
      builder.admin().trainingMaterial().delete(createdTrainingMaterial);
      builder.admin().trainingMaterial().assertDeleteFailStatus(404, createdTrainingMaterial);

    }
  }
 

  @Test
  public void testDeleteIntentPermissions() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Story story = builder.admin().stories().create("en", "test story", "Enter your answer");
      TrainingMaterial createdTrainingMaterial = builder.admin().trainingMaterial().create(story.getId(), TrainingMaterialType.INTENTOPENNLPDOCCAT, "Test material", "Test");
      
      builder.anonymous().trainingMaterial().assertDeleteFailStatus(401, createdTrainingMaterial);
      builder.invalid().trainingMaterial().assertDeleteFailStatus(403, createdTrainingMaterial);
    }
  }


}
