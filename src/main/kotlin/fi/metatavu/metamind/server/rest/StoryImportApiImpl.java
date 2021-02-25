package fi.metatavu.metamind.server.rest;

import com.sun.xml.ws.developer.Stateful;
import fi.metatavu.metamind.persistence.models.Story;
import fi.metatavu.metamind.api.spec.StoryImportApi;
import fi.metatavu.metamind.api.spec.model.ExportedStory;
import fi.metatavu.metamind.server.rest.translation.StoryTranslator;
import fi.metatavu.metamind.story.StoryController;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

;

/**
 * REST - endpoints for exporting and importing stories
 * 
 */
@Stateful
public class StoryImportApiImpl extends AbstractRestApi implements StoryImportApi {

  @Inject
  private StoryController storyController;
  
  @Inject
  private StoryTranslator storyTranslator;
  
  @Override
  public Response importStory(ExportedStory body) {

    Story story = storyController.importStory(body, getLoggerUserId());
    return createOk(storyTranslator.translateStory(story));
   
  }

}