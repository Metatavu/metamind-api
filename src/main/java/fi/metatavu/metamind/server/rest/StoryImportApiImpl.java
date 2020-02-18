package fi.metatavu.metamind.server.rest;

import java.util.List;
import java.util.Locale;

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
import fi.metatavu.metamind.rest.api.StoryImportApi;
import fi.metatavu.metamind.rest.model.ExportedStory;
import fi.metatavu.metamind.rest.model.ExportedStoryIntent;
import fi.metatavu.metamind.rest.model.ExportedStoryKnot;
import fi.metatavu.metamind.rest.model.ExportedStoryTrainingMaterial;
import fi.metatavu.metamind.rest.model.ExportedStoryVariable;
import fi.metatavu.metamind.rest.model.TrainingMaterialVisibility;
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
  private TrainingMaterialController trainingMaterialController;
  @Inject
  private StoryTranslator storyTranslator;
  
  @Override
  public Response importStory(ExportedStory body) {
    Story story = storyController.createStory(Locale.forLanguageTag(body.getLocale()), body.getName(), body.getDefaultHint(), getLoggerUserId());
    
    List<ExportedStoryKnot> knotsToCreate = body.getKnots();
    List<ExportedStoryTrainingMaterial> trainingMaterialsToCreate = body.getTrainingMaterials();
    
    for (int i = 0; i < body.getIntents().size(); i++) {
      Knot sourceKnot = null;
      if (body.getIntents().get(i).getSourceKnotId() != null) {
        for (int j = 0; j < knotsToCreate.size(); j++) {
          if (body.getIntents().get(i).getSourceKnotId().equals(knotsToCreate.get(j).getId())) {
            ExportedStoryKnot knotToCreate = knotsToCreate.get(j);
            sourceKnot = storyController.createKnot(knotToCreate.getType(), knotToCreate.getTokenizer(), knotToCreate.getName(), knotToCreate.getContent(), knotToCreate.getHint(), story, getLoggerUserId(), knotToCreate.getCoordinates().getX(), knotToCreate.getCoordinates().getY());
            knotsToCreate.remove(knotToCreate);
          }
        }
      }
      ExportedStoryKnot targetKnotToCreate = null;
      for (int j = 0; j < knotsToCreate.size(); j++) {
        if (body.getIntents().get(i).getTargetKnotId().equals(knotsToCreate.get(j).getId())) {
          targetKnotToCreate = knotsToCreate.get(j);
          knotsToCreate.remove(knotsToCreate.get(j));
        }
      }
      
      if (targetKnotToCreate == null) {
        return createBadRequest("Target knot not found!");
      }
      Knot targetKnot = storyController.createKnot(targetKnotToCreate.getType(), targetKnotToCreate.getTokenizer(), targetKnotToCreate.getName(), targetKnotToCreate.getContent(), targetKnotToCreate.getHint(), story, getLoggerUserId(), targetKnotToCreate.getCoordinates().getX(), targetKnotToCreate.getCoordinates().getY());
      
      ExportedStoryIntent intentToCreate = body.getIntents().get(i);
      Intent intent = storyController.createIntent(intentToCreate.getType(), intentToCreate.getName(), sourceKnot, targetKnot, intentToCreate.isisGlobal(), intentToCreate.getQuickResponse(), intentToCreate.getQuickResponseOrder(), getLoggerUserId());      
      for (int x = 0; x < trainingMaterialsToCreate.size(); x++) {
        for (int y = 0; y < intentToCreate.getTrainingMaterialIds().size(); y++) {
          if (trainingMaterialsToCreate.get(x).getId().equals(intentToCreate.getTrainingMaterialIds().get(y))) {
            ExportedStoryTrainingMaterial materialToCreate = trainingMaterialsToCreate.get(x);
            TrainingMaterial material = trainingMaterialController.createTrainingMaterial(materialToCreate.getType(), materialToCreate.getName(), materialToCreate.getText(), story, getLoggerUserId(), TrainingMaterialVisibility.fromValue(materialToCreate.getVisibility()));
            trainingMaterialController.setIntentTrainingMaterial(intent, material.getType(), material);
            trainingMaterialsToCreate.remove(materialToCreate);
          }
        }
      }
    }
    for (int i = 0; i < trainingMaterialsToCreate.size(); i++) {
      ExportedStoryTrainingMaterial materialToCreate = trainingMaterialsToCreate.get(i);
      trainingMaterialController.createTrainingMaterial(materialToCreate.getType(), materialToCreate.getName(), materialToCreate.getText(), story, getLoggerUserId(), TrainingMaterialVisibility.fromValue(materialToCreate.getVisibility()));
    }
    for (int i = 0; i < knotsToCreate.size(); i++) {
      ExportedStoryKnot knotToCreate = knotsToCreate.get(i);
      storyController.createKnot(knotToCreate.getType(), knotToCreate.getTokenizer(), knotToCreate.getName(), knotToCreate.getContent(), knotToCreate.getHint(), story, getLoggerUserId(), knotToCreate.getCoordinates().getX(), knotToCreate.getCoordinates().getY());
    }
    
    for (int i  = 0; i < body.getVariables().size(); i++) {
      ExportedStoryVariable variableToCreate = body.getVariables().get(i);
      storyController.createVariable(variableToCreate.getType(), story, variableToCreate.getName(), variableToCreate.getValidationScript(), getLoggerUserId());
    }
    
    return createOk(storyTranslator.translateStory(story));
  }

}