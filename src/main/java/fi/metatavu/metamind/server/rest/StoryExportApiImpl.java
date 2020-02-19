package fi.metatavu.metamind.server.rest;

import java.util.ArrayList;
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
import fi.metatavu.metamind.rest.model.Coordinates;
import fi.metatavu.metamind.rest.model.ExportedStory;
import fi.metatavu.metamind.rest.model.ExportedStoryIntent;
import fi.metatavu.metamind.rest.model.ExportedStoryKnot;
import fi.metatavu.metamind.rest.model.ExportedStoryTrainingMaterial;
import fi.metatavu.metamind.rest.model.ExportedStoryVariable;
import fi.metatavu.metamind.rest.model.TrainingMaterialType;
import fi.metatavu.metamind.rest.model.TrainingMaterialVisibility;
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
    ExportedStory exportedStory = new ExportedStory();
    Story storyToExport = storyController.findStoryById(storyId);
    
    if (storyToExport == null) {
      return createBadRequest(String.format("Story %s not found", storyId)); 
    }
    
    exportedStory.setName(storyToExport.getName());
    exportedStory.setLocale(storyToExport.getLocale().toLanguageTag());
    exportedStory.setDefaultHint(storyToExport.getDefaultHint());
    
    List<Knot> knotsToExport = storyController.listKnotsByStory(storyToExport);

    exportedStory.setKnots(knotsToExport.stream().map(var -> {
      ExportedStoryKnot exportedKnot = new ExportedStoryKnot();
      Coordinates coordinates = new Coordinates();
      coordinates.setX(var.getCoordinateX());
      coordinates.setY(var.getCoordinateY());
      exportedKnot.setContent(var.getContent());
      exportedKnot.setCoordinates(coordinates);
      exportedKnot.setId(var.getId());
      exportedKnot.setType(var.getType());
      exportedKnot.setTokenizer(var.getTokenizerType());
      exportedKnot.setName(var.getName());
      exportedKnot.setHint(var.getHint());
      
      return exportedKnot;
    }).collect(Collectors.toList()));

    
    List<Intent> intentsToExport = storyController.listIntentsByStory(storyToExport);
    List<ExportedStoryIntent> storyToExportIntents = new ArrayList<>();
    
    for (int i = 0; i < intentsToExport.size(); i++) {
      Intent intentToExport = intentsToExport.get(i);
      ExportedStoryIntent exportedIntent = new ExportedStoryIntent();
      
      exportedIntent.setId(intentToExport.getId());
      exportedIntent.setName(intentToExport.getName());
      exportedIntent.setQuickResponse(intentToExport.getQuickResponse());
      exportedIntent.setQuickResponseOrder(intentToExport.getQuickResponseOrder());
      exportedIntent.setType(intentToExport.getType());
      exportedIntent.setSourceKnotId(intentToExport.getSourceKnot().getId());
      exportedIntent.setTargetKnotId(intentToExport.getTargetKnot().getId());
      exportedIntent.setGlobal(intentToExport.getGlobal());
      
      List<TrainingMaterial> intentMaterial = trainingMaterialController.listTrainingMaterialByIntent(intentToExport);
      List<UUID> intentMaterialIds = new ArrayList<>();
      for (int j = 0; j < intentMaterial.size(); j++) {
        intentMaterialIds.add(intentMaterial.get(j).getId());
      }
      
      exportedIntent.setTrainingMaterialIds(intentMaterialIds);
      
      storyToExportIntents.add(exportedIntent);
    }
    
    exportedStory.setIntents(storyToExportIntents);
    
    List<TrainingMaterial> trainingMaterialsToExport = trainingMaterialController.listTrainingMaterials(storyToExport, TrainingMaterialType.INTENTOPENNLPDOCCAT, TrainingMaterialVisibility.LOCAL);
    trainingMaterialsToExport.addAll(trainingMaterialController.listTrainingMaterials(storyToExport, TrainingMaterialType.INTENTOPENNLPDOCCAT, TrainingMaterialVisibility.STORY));
    
    trainingMaterialsToExport.addAll(trainingMaterialController.listTrainingMaterials(storyToExport, TrainingMaterialType.INTENTREGEX, TrainingMaterialVisibility.STORY));
    trainingMaterialsToExport.addAll(trainingMaterialController.listTrainingMaterials(storyToExport, TrainingMaterialType.INTENTREGEX, TrainingMaterialVisibility.LOCAL));
    
    trainingMaterialsToExport.addAll(trainingMaterialController.listTrainingMaterials(storyToExport, TrainingMaterialType.VARIABLEOPENNLPNER, TrainingMaterialVisibility.STORY));
    trainingMaterialsToExport.addAll(trainingMaterialController.listTrainingMaterials(storyToExport, TrainingMaterialType.VARIABLEOPENNLPNER, TrainingMaterialVisibility.LOCAL));
    
    trainingMaterialsToExport.addAll(trainingMaterialController.listTrainingMaterials(storyToExport, TrainingMaterialType.VARIABLEOPENNLPREGEX, TrainingMaterialVisibility.STORY));
    trainingMaterialsToExport.addAll(trainingMaterialController.listTrainingMaterials(storyToExport, TrainingMaterialType.VARIABLEOPENNLPREGEX, TrainingMaterialVisibility.LOCAL));
 
    exportedStory.setTrainingMaterials(trainingMaterialsToExport.stream().map(var -> {
      ExportedStoryTrainingMaterial exportedMaterial = new ExportedStoryTrainingMaterial();
      exportedMaterial.setId(var.getId());
      exportedMaterial.setType(var.getType());
      exportedMaterial.setText(var.getText());
      exportedMaterial.setVisibility(var.getVisibility().toString());
      exportedMaterial.setName(var.getName());
      return exportedMaterial;
    }).collect(Collectors.toList()));
    
    List<Variable> variablesToExport = storyController.listVariablesByStory(storyToExport);

    exportedStory.setVariables(variablesToExport
        .stream()
        .map((var) -> {
            ExportedStoryVariable exportedVariable = new ExportedStoryVariable();
            exportedVariable.setName(var.getName());
            exportedVariable.setType(var.getType());
            exportedVariable.setValidationScript(var.getValidationScript());
            return exportedVariable;
        }).collect(Collectors.toList()));
    
    return createOk(exportedStory);
  }


}
