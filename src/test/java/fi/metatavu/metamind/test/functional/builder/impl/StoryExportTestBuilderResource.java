package fi.metatavu.metamind.test.functional.builder.impl;

import fi.metatavu.metamind.ApiClient;
import fi.metatavu.metamind.test.functional.builder.AbstractTestBuilderResource;
import fi.metatavu.metamind.test.functional.builder.TestBuilder;

public class StoryExportTestBuilderResource extends AbstractTestBuilderResource<ExportedStory, StoryExportApi>{
  public StoryExportTestBuilderResource(TestBuilder testBuilder, ApiClient apiClient) {
    super(testBuilder, apiClient);
  }

  public ExportedStory exportStory(UUID storyId) {
    return getApi().exportStory(storyId);
  }
  
  public Story importStory(ExportedStory story) {
    return getApi().importStory(story);
  }
  
  @Override
  public void clean(ExportedStory t) {
    // TODO Auto-generated method stub
  }
}
