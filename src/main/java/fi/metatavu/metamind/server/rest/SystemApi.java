package fi.metatavu.metamind.server.rest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

import fi.metatavu.metamind.models.ModelsContoller;
import fi.metatavu.metamind.persistence.models.IntentModel;
import fi.metatavu.metamind.persistence.models.SlotModel;
import fi.metatavu.metamind.settings.SystemConsts;
import fi.metatavu.metamind.settings.SystemSettingController;
import fi.metatavu.metamind.story.StoryController;

/**
 * REST system endpoints
 * 
 * @author Antti Leppä
 *
 */
@Path("/system")
@Consumes({ "application/json;charset=utf-8" })
@Produces({ "application/json;charset=utf-8" })
@RequestScoped
@Stateful
public class SystemApi extends AbstractRestApi {

  @Inject
  private Logger logger;

  @Inject
  private StoryController storyController;
  
  @Inject
  private ModelsContoller modelsContoller;

  @Inject
  private SystemSettingController systemSettingController;
  
  @GET
  @Path ("/reload")
  public Response reload() {
    List<File> intentModels = listModelFiles(systemSettingController.getSettingValues(SystemConsts.INTENT_MODEL_PATHS));
    List<File> slotModels = listModelFiles(systemSettingController.getSettingValues(SystemConsts.SLOT_MODEL_PATHS));
    List<File> storyFiles = listStoryFiles(systemSettingController.getSettingValues(SystemConsts.STORY_PATHS));
    
    try {
      for (File intentModel : intentModels) {
        byte[] data = readFileBytes(intentModel); 
        modelsContoller.setIntentModelData(intentModel.getName(), data);
      }
      
      for (File slotModel : slotModels) {
        byte[] data = readFileBytes(slotModel); 
        modelsContoller.setSlotModelData(slotModel.getName(), data);
      }
    
      for (File storyFile : storyFiles) {
        String fileName = storyFile.getName();
        String storyName = fileName.substring(0, fileName.length() - 9);
        File configFile = new File(storyFile.getParentFile(), String.format("%s.config.json", storyName));
        String configJson = readFileString(configFile);
        String storyJson = readFileString(storyFile).replace('\uFEFF', ' ');
        storyController.setStoryData(storyName, configJson, storyJson);
      }
      
    } catch (IOException e) {
      logger.error("Failed to reload intent model", e);
      return Response.status(Status.INTERNAL_SERVER_ERROR).build();
    }
    
    return Response.noContent().build();
  }

  @GET
  @Path ("/slotModel/{name}")
  @Produces({ "application/octet-stream" })
  public Response getSlotModelData(@PathParam ("name") String name) {
    SlotModel slotModel = modelsContoller.findSlotModelByName(name);
    if (slotModel == null) {
      return respondNotFound();
    }
    
    return streamResponse(slotModel.getData(), "application/octet-stream");
  }
  
  @GET
  @Path ("/intentModel/{name}")
  @Produces({ "application/octet-stream" })
  public Response getIntentModelData(@PathParam ("name") String name) {
    IntentModel intentModel = modelsContoller.findIntentModelByName(name);
    if (intentModel == null) {
      return respondNotFound();
    }
    
    return streamResponse(intentModel.getData(), "application/octet-stream");
  }
  
  private List<File> listStoryFiles(String[] paths) {
    List<File> result = new ArrayList<>();
    
    for (String path : paths) {
      File parentFolder = new File(path);
      File[] files = parentFolder.listFiles((File dir, String name) -> name.endsWith(".ink.json") );
      for (File file : files) {
        result.add(file);
      }
    }
    
    return result;
  }

  private List<File> listModelFiles(String[] paths) {
    List<File> result = new ArrayList<>();
    
    for (String path : paths) {
      File parentFolder = new File(path);
      File[] files = parentFolder.listFiles();
      for (File file : files) {
        result.add(file);
      }
    }
    
    return result;
  }
  
  private byte[] readFileBytes(File file) throws IOException {
    try (InputStream inputStream = new FileInputStream(file)) {
      return IOUtils.toByteArray(inputStream);
    }
  }
  
  private String readFileString(File file) throws IOException {
    try (FileReader reader = new FileReader(file)) {
      return IOUtils.toString(reader);
    }
  }
}
