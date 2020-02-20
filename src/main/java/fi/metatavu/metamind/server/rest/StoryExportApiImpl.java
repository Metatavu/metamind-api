package fi.metatavu.metamind.server.rest;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import fi.metatavu.metamind.nlp.TrainingMaterialController;
import fi.metatavu.metamind.persistence.models.Intent;
import fi.metatavu.metamind.persistence.models.Knot;
import fi.metatavu.metamind.persistence.models.Story;
import fi.metatavu.metamind.persistence.models.TrainingMaterial;
import fi.metatavu.metamind.persistence.models.Variable;
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
  
  @Inject
  private TrainingMaterialController trainingMaterialController;

  
  @Override
  public Response exportStory(UUID storyId) {

    Story storyToExport = storyController.findStoryById(storyId);
    
    if (storyToExport == null) {
      return createBadRequest(String.format("Story %s not found", storyId)); 
    }
    
    List<Knot> knotsToExport = storyController.listKnotsByStory(storyToExport);
    List<Intent> intentsToExport = storyController.listIntentsByStory(storyToExport);
    List<TrainingMaterial> trainingMaterialsToExport = trainingMaterialController.listTrainingMaterials(storyToExport);
    List<Variable> variablesToExport = storyController.listVariablesByStory(storyToExport);

    ExportedStory exportedStory = storyController.exportStory(storyToExport);
    exportedStory.setKnots(knotsToExport.stream().map(knot -> storyController.exportKnot(knot)).collect(Collectors.toList()));
    exportedStory.setIntents(intentsToExport.stream().map(intent -> storyController.exportIntent(intent)).collect(Collectors.toList()));
    exportedStory.setTrainingMaterials(trainingMaterialsToExport.stream().map(trainingMaterial -> trainingMaterialController.exportTrainingMaterial(trainingMaterial)).collect(Collectors.toList()));
    exportedStory.setVariables(variablesToExport.stream().map((variable) -> storyController.exportVariable(variable)).collect(Collectors.toList()));
    
    return createOk(exportedStory);
  }


}
