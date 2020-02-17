package fi.metatavu.metamind.server.rest;

import java.util.UUID;

import javax.ws.rs.core.Response;

import fi.metatavu.metamind.rest.api.StoryExportApi;
import fi.metatavu.metamind.rest.api.StoryImportApi;
import fi.metatavu.metamind.rest.model.ExportedStory;;

public class StoryExportApiImpl extends AbstractRestApi implements StoryExportApi, StoryImportApi{

  @Override
  public Response exportStory(UUID storyId) {
    // TODO Auto-generated method stub
    return null;
  }
  
  @Override
  public Response importStory(ExportedStory body) {
    // TODO Auto-generated method stub
    return null;
  }

}
