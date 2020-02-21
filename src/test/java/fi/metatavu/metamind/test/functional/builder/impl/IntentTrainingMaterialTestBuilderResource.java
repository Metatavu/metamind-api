package fi.metatavu.metamind.test.functional.builder.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.json.JSONException;

import feign.FeignException;
import fi.metatavu.metamind.ApiClient;
import fi.metatavu.metamind.client.model.TrainingMaterial;
import fi.metatavu.metamind.client.model.TrainingMaterialType;
import fi.metatavu.metamind.client.model.TrainingMaterialVisibility;
import fi.metatavu.metamind.client.model.Story;
import fi.metatavu.metamind.client.TrainingMaterialsApi;
import fi.metatavu.metamind.test.functional.builder.AbstractTestBuilderResource;
import fi.metatavu.metamind.test.functional.builder.TestBuilder;

/**
 * Test builder resource for intent training material
 * 
 * @author Daniil Smirnov
 */
public class IntentTrainingMaterialTestBuilderResource extends AbstractTestBuilderResource<TrainingMaterial, TrainingMaterialsApi> {

  public IntentTrainingMaterialTestBuilderResource(TestBuilder testBuilder, ApiClient apiClient) {
    super(testBuilder, apiClient);
  }
  /**
   * Creates new training material
   * 
   * @param storyId StoryId UUID
   * @param type type
   * @param name training material name
   * @param text text
   * @return
   */
  public TrainingMaterial create(UUID storyId, TrainingMaterialType type, String name, String text, TrainingMaterialVisibility visibility) {
    TrainingMaterial trainingMaterial = new TrainingMaterial();
    trainingMaterial.setName(name);
    trainingMaterial.setStoryId(storyId);
    trainingMaterial.setText(text);
    trainingMaterial.setType(type);
    trainingMaterial.setVisibility(visibility);
    TrainingMaterial createdTrainingMaterial = getApi().createTrainingMaterial(trainingMaterial);
    return addClosable(createdTrainingMaterial);
  }
  
  /**
   * Finds training material
   * 
   * @param trainingMaterial trainingMaterial
   * @return
   */
  public TrainingMaterial findTrainingMaterial(TrainingMaterial trainingMaterial) {
    return getApi().findTrainingMaterial(trainingMaterial.getId());
  }
  
  /**
   * List training material
   * 
   * @return
   */
  public List<TrainingMaterial> listTrainingMaterial(Story story, TrainingMaterialType type, TrainingMaterialVisibility visibility) {
    return getApi().listTrainingMaterials(story.getId(), type, visibility);
  }
  /**
   * Updates training material
   * 
   * @param trainingMaterial trainingMaterial
   * @return
   */
  public TrainingMaterial updateTrainingMaterial(TrainingMaterial trainingMaterial) {
    return getApi().updateTrainingMaterial(trainingMaterial, trainingMaterial.getId());
  }
  
  /**
   * Deletes training material
   * 
   * @param trainingMaterial trainingMaterial
   */
  public void delete(TrainingMaterial trainingMaterial) {
    getApi().deleteTrainingMaterial(trainingMaterial.getId());
    removeCloseable(closable -> {
      if (closable instanceof TrainingMaterial) {
        return !((TrainingMaterial) closable).getId().equals(trainingMaterial.getId());
      }
      
      return false;
    });
  }
  
  /**
   * Asserts create status fails with given status code
   * 
   * @param expectedStatus
   * @param storyId
   * @param type
   * @param name
   * @param text
   */
  public void assertCreateFailStatus (int expectedStatus, UUID storyId, TrainingMaterialType type, String name, String text) {
    try {
      TrainingMaterial trainingMaterial = new TrainingMaterial();
      trainingMaterial.setName(name);
      trainingMaterial.setStoryId(storyId);
      trainingMaterial.setText(text);
      trainingMaterial.setType(type);
      getApi().createTrainingMaterial(trainingMaterial);
      fail(String.format("Expected find to fail with status %d", expectedStatus));
    } catch (FeignException e) {
      assertEquals(expectedStatus, e.status());
    }
  }
  
  /**
   * Asserts find status fails with given status code
   * 
   * @param expectedStatus
   * @param trainingMaterialId UUID trainingMaterialId
   */
  public void assertFindFailStatus(int expectedStatus, UUID trainingMaterialId) {
    try {
      getApi().findTrainingMaterial(trainingMaterialId);
      fail(String.format("Expected find to fail with status %d", expectedStatus));
    } catch (FeignException e) {
      assertEquals(expectedStatus, e.status());
    }
  }
  
  /**
   * Asserts update status fails with given status code
   * 
   * @param expectedStatus
   * @param trainingMaterial
   */
  public void assertUpdateFailStatus(int expectedStatus, TrainingMaterial trainingMaterial) {
    try {
      getApi().updateTrainingMaterial(trainingMaterial, trainingMaterial.getId());
      fail(String.format("Expected find to fail with status %d", expectedStatus));
    } catch (FeignException e) {
      assertEquals(expectedStatus, e.status());
    }
  }
  
  /**
   * Asserts delete status fails with given status code
   * 
   * @param expectedStatus
   * @param trainingMaterial
   */
  public void assertDeleteFailStatus(int expectedStatus, TrainingMaterial trainingMaterial) {
    try {
      getApi().deleteTrainingMaterial(trainingMaterial.getId());
      fail(String.format("Expected find to fail with status %d", expectedStatus));
    } catch (FeignException e) {
      assertEquals(expectedStatus, e.status());
    }
  }
  
  /**
   * Asserts Training materials are equal
   * 
   * @param expected Training material
   * @param actual Training material
   * @throws IOException
   * @throws JSONException
   */
  public void assertTrainingMaterialEqual(TrainingMaterial expected, TrainingMaterial actual) throws IOException, JSONException {
    assertJsonsEqual(expected, actual);
  }

  @Override
  public void clean(TrainingMaterial trainingMaterial) {
    getApi().deleteTrainingMaterial(trainingMaterial.getId());
    
    
  }

}
