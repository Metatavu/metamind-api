package fi.metatavu.metamind.functional.tests;

import fi.metatavu.metamind.api.client.models.Story;
import fi.metatavu.metamind.api.client.models.TrainingMaterial;
import fi.metatavu.metamind.api.client.models.TrainingMaterialType;
import fi.metatavu.metamind.api.client.models.TrainingMaterialVisibility;
import fi.metatavu.metamind.functional.impl.TestBuilder;
import fi.metatavu.metamind.functional.resources.KeycloakResource;
import fi.metatavu.metamind.functional.resources.MysqlResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
@QuarkusTestResource.List(value = {
        @QuarkusTestResource(MysqlResource.class),
        @QuarkusTestResource(KeycloakResource.class)
})
public class TrainingMaterialTestsIT {

  @Test
  public void testCreateTrainingMaterial() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Story story = builder.admin().stories().create("en", "test story", "Enter your answer");
      
      assertNotNull(builder.admin().trainingMaterial().create(story.getId(), TrainingMaterialType.iNTENTOPENNLPDOCCAT, "Test material", "Test", TrainingMaterialVisibility.sTORY));
    }
  }
  
 /*
  @Test
  public void testCreateTrainingMaterialPermissions() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Story story = builder.admin().stories().create("en", "test story", "Enter your answer");
      
      builder.invalid().trainingMaterial().assertCreateFailStatus(403, story.getId(), TrainingMaterialType.iNTENTOPENNLPDOCCAT, "Test material", "Test");
      builder.anonymous().trainingMaterial().assertCreateFailStatus(401, story.getId(), TrainingMaterialType.iNTENTOPENNLPDOCCAT, "Test material", "Test");
    }
  }*/

  @Test
  public void testFindTrainingMaterial() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Story story = builder.admin().stories().create("en", "test story", "Enter your answer");
      TrainingMaterial createdTrainingMaterial = builder.admin().trainingMaterial().create(story.getId(), TrainingMaterialType.iNTENTOPENNLPDOCCAT, "Test material", "Test", TrainingMaterialVisibility.sTORY);
      
      builder.admin().trainingMaterial().assertFindFailStatus(404, UUID.randomUUID());
      TrainingMaterial foundTrainingMaterial = builder.admin().trainingMaterial().findTrainingMaterial(createdTrainingMaterial);
      builder.admin().trainingMaterial().assertTrainingMaterialEqual(createdTrainingMaterial, foundTrainingMaterial);
      
    }
  }

  /*
  @Test
  public void testFindTrainingMaterialPermissions() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Story story = builder.admin().stories().create("en", "test story", "Enter your answer");
      TrainingMaterial createdTrainingMaterial = builder.admin().trainingMaterial().create(story.getId(), TrainingMaterialType.iNTENTOPENNLPDOCCAT, "Test material", "Test", TrainingMaterialVisibility.sTORY);
      
      assertNotNull(builder.admin().trainingMaterial().findTrainingMaterial(createdTrainingMaterial));
      builder.invalid().trainingMaterial().assertFindFailStatus(403, createdTrainingMaterial.getId());
      builder.anonymous().trainingMaterial().assertFindFailStatus(401, createdTrainingMaterial.getId());
    }
  }*/

  @Test
  public void testUpdateTrainingMaterial() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Story story = builder.admin().stories().create("en", "test story", "Enter your answer");
      TrainingMaterial createdTrainingMaterial = builder.admin().trainingMaterial().create(story.getId(), TrainingMaterialType.iNTENTOPENNLPDOCCAT, "Test material", "Test", TrainingMaterialVisibility.sTORY);

      TrainingMaterial firstFoundTrainingMaterial = builder.admin().trainingMaterial().findTrainingMaterial(createdTrainingMaterial);
      TrainingMaterial updateTrainingMaterial = new TrainingMaterial("value", "Updated training material", firstFoundTrainingMaterial.getId(),
              TrainingMaterialType.iNTENTOPENNLPDOCCAT, firstFoundTrainingMaterial.getStoryId(), firstFoundTrainingMaterial.getVisibility(), firstFoundTrainingMaterial.getCreatedAt(),
              firstFoundTrainingMaterial.getModifiedAt());
      
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

/*
 @Test
  public void testUpdateIntentPermissions() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Story story = builder.admin().stories().create("en", "test story", "Enter your answer");
      TrainingMaterial createdTrainingMaterial = builder.admin().trainingMaterial().create(story.getId(), TrainingMaterialType.iNTENTOPENNLPDOCCAT, "Test material", "Test", TrainingMaterialVisibility.sTORY);

      builder.anonymous().trainingMaterial().assertUpdateFailStatus(401, createdTrainingMaterial);
      builder.invalid().trainingMaterial().assertUpdateFailStatus(403, createdTrainingMaterial);
    }
  }
*/
  @Test
  public void testDeleteTrainingMaterial() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Story story = builder.admin().stories().create("en", "test story", "Enter your answer");
 
      TrainingMaterial createdTrainingMaterial = builder.admin().trainingMaterial().create(story.getId(), TrainingMaterialType.iNTENTOPENNLPDOCCAT, "Test material", "Test", TrainingMaterialVisibility.sTORY);
      TrainingMaterial foundTrainingMaterial = builder.admin().trainingMaterial().findTrainingMaterial(createdTrainingMaterial);
      assertEquals(createdTrainingMaterial.getId(), foundTrainingMaterial.getId());
      builder.admin().trainingMaterial().delete(createdTrainingMaterial);
      builder.admin().trainingMaterial().assertDeleteFailStatus(404, createdTrainingMaterial);

    }
  }

/*
  @Test
  public void testDeleteIntentPermissions() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Story story = builder.admin().stories().create("en", "test story", "Enter your answer");
      TrainingMaterial createdTrainingMaterial = builder.admin().trainingMaterial().create(story.getId(), TrainingMaterialType.iNTENTOPENNLPDOCCAT, "Test material", "Test", TrainingMaterialVisibility.sTORY);
      
      builder.anonymous().trainingMaterial().assertDeleteFailStatus(401, createdTrainingMaterial);
      builder.invalid().trainingMaterial().assertDeleteFailStatus(403, createdTrainingMaterial);
    }
  }
*/

}
