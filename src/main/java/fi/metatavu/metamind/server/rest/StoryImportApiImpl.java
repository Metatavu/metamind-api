package fi.metatavu.metamind.server.rest;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import fi.metatavu.metamind.persistence.models.Story;
import fi.metatavu.metamind.rest.api.StoryImportApi;
import fi.metatavu.metamind.rest.model.ExportedStory;
import fi.metatavu.metamind.server.rest.translation.StoryTranslator;
import fi.metatavu.metamind.story.StoryController;;

/**
 * REST - endpoints for exporting and importing stories
 * 
 */
@RequestScoped
@Stateful
@Consumes({ "application/json;charset=utf-8" })
@Produces({ "application/json;charset=utf-8" })
public class StoryImportApiImpl extends AbstractRestApi implements StoryImportApi {

  @Inject
  private StoryController storyController;
  
  @Inject
  private StoryTranslator storyTranslator;
  
  @Override
  public Response importStory(ExportedStory body) {
    try {
      Story story = storyController.importStory(body, getLoggerUserId());
      return createOk(storyTranslator.translateStory(story));
    } catch (Exception e) {
      return createBadRequest(e.getMessage());
    }
  }

}