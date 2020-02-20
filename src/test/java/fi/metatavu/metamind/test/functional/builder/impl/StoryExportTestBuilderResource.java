package fi.metatavu.metamind.test.functional.builder.impl;

import java.util.UUID;

import fi.metatavu.metamind.ApiClient;
import fi.metatavu.metamind.client.StoryExportApi;
import fi.metatavu.metamind.client.model.ExportedStory;
import fi.metatavu.metamind.client.model.Story;
import fi.metatavu.metamind.test.functional.builder.AbstractTestBuilderResource;
import fi.metatavu.metamind.test.functional.builder.TestBuilder;

/**
 * Test builder resource for importing and exporting stories
 *
 */
public class StoryExportTestBuilderResource extends AbstractTestBuilderResource<ExportedStory, StoryExportApi>{
  public StoryExportTestBuilderResource(TestBuilder testBuilder, ApiClient apiClient) {
    super(testBuilder, apiClient);
  }
  
  /*
   * Exports a story for testing purposes
   * 
   * @param id of the story to be exported
   * @return exported story
   */
  public ExportedStory exportStory(UUID storyId) {
    return getApi().exportStory(storyId);
  }
  
  /**
   * Imports a story for testing purposes
   * 
   * @param a previously exported story
   * 
   * @return imported story
   */
  public Story importStory(ExportedStory story) {
    return getApi().importStory(story);
  }
  
  @Override
  public void clean(ExportedStory t) {
    // TODO Auto-generated method stub
  }
}
