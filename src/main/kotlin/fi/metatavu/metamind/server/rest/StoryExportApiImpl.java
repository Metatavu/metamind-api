package fi.metatavu.metamind.server.rest;

import fi.metatavu.metamind.persistence.models.Story;
import fi.metatavu.metamind.api.spec.StoryExportApi;
import fi.metatavu.metamind.api.spec.model.ExportedStory;
import fi.metatavu.metamind.story.StoryController;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.core.Response;
import java.util.UUID;

/**
 * REST - endpoints for exporting and importing stories
 * 
 */
@RequestScoped
@Transactional
public class StoryExportApiImpl extends AbstractRestApi implements StoryExportApi {

  @Inject
  private StoryController storyController;
  
  @Override
  public Response exportStory(UUID storyId) {
    
    Story storyToExport = storyController.findStoryById(storyId);
    
    if (storyToExport == null) {
      return createBadRequest(String.format("Story %s not found", storyId)); 
    }
    
    ExportedStory exportedStory = storyController.exportStory(storyToExport);
    
    return createOk(exportedStory);
  }


}
