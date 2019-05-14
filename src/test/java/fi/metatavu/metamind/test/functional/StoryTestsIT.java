package fi.metatavu.metamind.test.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.UUID;

import org.junit.Test;

import feign.FeignException;
import fi.metatavu.metamind.client.model.Story;
import fi.metatavu.metamind.test.functional.builder.TestBuilder;

public class StoryTestsIT extends AbstractFunctionalTest {

  @Test
  public void testCreateStory() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      assertNotNull(builder.admin().stories().create("en", "test story"));
    }
  }
  // TODO Uncomment test when permissions are done
//  @Test
//  public void testCreateStoryPermissions() throws Exception {
//    try (TestBuilder builder = new TestBuilder()) {
//      builder.invalid().stories().assertCreateFailStatus(403, "en", "test story");
//      builder.anonymous().stories().assertCreateFailStatus(401, "en", "test story");
//    }
//  }

  @Test
  public void testFindStory() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Story createdStory = builder.admin().stories().create("en", "test story");
      builder.admin().stories().assertFindFailStatus(404, UUID.randomUUID());
      Story foundStory = builder.admin().stories().findStory(createdStory.getId());
      builder.admin().stories().assertStoriesEqual(createdStory, foundStory);
    }
  }

  // TODO Uncomment test when permissions are done
//  @Test
//  public void testFindStoryPermissions() throws Exception {
//    try (TestBuilder builder = new TestBuilder()) {
//      Story createdStory = builder.admin().stories().create("en", "test story");
//
//      assertNotNull(builder.admin().stories().findStory(createdStory.getId()));
//
//      builder.invalid().stories().assertFindFailStatus(403, createdStory.getId());
//      builder.anonymous().stories().assertFindFailStatus(401, createdStory.getId());
//
//    }
//  }

  @Test
  public void testUpdateStory() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Story createdStory = builder.admin().stories().create("en", "test story");
      builder.admin().stories().assertStoriesEqual(createdStory, builder.admin().stories().findStory(createdStory.getId()));

      Story updateStory = builder.admin().stories().findStory(createdStory.getId());
      updateStory.setName("updated story");
      Story updatedStory = builder.admin().stories().updateStory(updateStory);
      assertEquals(createdStory.getId(), updatedStory.getId());
      assertEquals(updateStory.getName(), updatedStory.getName());
      Story foundStory = builder.admin().stories().findStory(createdStory.getId());
      assertEquals(createdStory.getId(), foundStory.getId());
      assertEquals(updateStory.getName(), foundStory.getName());
     
    }
  }

  // TODO Uncomment test when permissions are done
//  @Test
//  public void testUpdateStoryPermissions() throws Exception {
//    try (TestBuilder builder = new TestBuilder()) {
//      Story testStory = builder.admin().stories().create("en", "test story");
//
//      builder.anonymous().stories().assertUpdateFailStatus(401, testStory);
//      builder.invalid().stories().assertUpdateFailStatus(403, testStory);
//    }
//  }

  @Test
  public void testDeleteStory() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      Story createdStory = builder.admin().stories().create("en", "test story");
      Story foundStory = builder.admin().stories().findStory(createdStory.getId());
      assertEquals(createdStory.getId(), foundStory.getId());
      builder.admin().stories().delete(createdStory);
      builder.admin().stories().assertDeleteFailStatus(404, createdStory);
    }
  }
  // TODO Uncomment test when permissions are done
//  @Test
//  public void testDeleteStorypermissions() throws Exception {
//    try (TestBuilder builder = new TestBuilder()) {
//      Story createdStory = builder.admin().stories().create("en", "test story");
//      builder.anonymous().stories().assertDeleteFailStatus(401, createdStory);
//      builder.invalid().stories().assertDeleteFailStatus(403, createdStory);
//    }
//  }

}
