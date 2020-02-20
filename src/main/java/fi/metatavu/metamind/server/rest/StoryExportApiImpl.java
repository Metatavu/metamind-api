package fi.metatavu.metamind.server.rest;

import java.util.UUID;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import fi.metatavu.metamind.persistence.models.Story;
import fi.metatavu.metamind.rest.api.StoryExportApi;
import fi.metatavu.metamind.rest.model.ExportedStory;
import fi.metatavu.metamind.story.StoryController;

/**
 * REST - endpoints for exporting and importing stories
 * 
 */
@RequestScoped
@Stateful
@Consumes({ "application/json;charset=utf-8" })
@Produces({ "application/json;charset=utf-8" })
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
