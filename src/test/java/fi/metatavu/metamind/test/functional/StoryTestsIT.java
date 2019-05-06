package fi.metatavu.metamind.test.functional;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

import fi.metatavu.metamind.test.functional.builder.TestBuilder;

public class StoryTestsIT extends AbstractFunctionalTest {
  
  @Test
  public void testCreateStory() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      assertNotNull(builder.admin().stories().create("en", "test story"));
    }
  }
  
}
