package fi.metatavu.metamind.server.rest;

import java.util.UUID;
import java.util.stream.Collectors;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import fi.metatavu.metamind.nlp.TrainingMaterialController;
import fi.metatavu.metamind.rest.api.TrainingMaterialsApi;
import fi.metatavu.metamind.rest.model.TrainingMaterial;
import fi.metatavu.metamind.rest.model.TrainingMaterialType;
import fi.metatavu.metamind.rest.model.TrainingMaterialVisibility;
import fi.metatavu.metamind.server.rest.translation.TrainingMaterialTranslator;
import fi.metatavu.metamind.story.StoryController;

/**
 * REST - endpoints for training materials
 * 
 * @author Antti Leppä
 */
@RequestScoped
@Stateful
@Consumes({ "application/json;charset=utf-8" })
@Produces({ "application/json;charset=utf-8" })
public class TrainingMaterialsApiImpl extends AbstractRestApi implements TrainingMaterialsApi {

  @Inject
  private TrainingMaterialTranslator trainingMaterialTranslator;

  @Inject
  private TrainingMaterialController trainingMaterialController;

  @Inject
  private StoryController storyController;

  @Override
  public Response createTrainingMaterial(TrainingMaterial body) {
    // TODO: Permission check
    
    UUID loggedUserId = getLoggerUserId();
    UUID storyId = body.getStoryId();
    TrainingMaterialType type = body.getType();
    TrainingMaterialVisibility visibility = body.getVisibility();
    
    fi.metatavu.metamind.persistence.models.Story story = storyId != null ? storyController.findStoryById(storyId) : null;
    if (storyId != null && story == null) {
      return createBadRequest(String.format("Story %s not found", storyId)); 
    }
    
    return createOk(trainingMaterialTranslator.translateTrainingMaterial(trainingMaterialController.createTrainingMaterial(type, body.getName(), body.getText(), story, loggedUserId, visibility)));
  }

  @Override
  public Response deleteTrainingMaterial(UUID trainingMaterialId) {
    // TODO: Check permissions
    
    fi.metatavu.metamind.persistence.models.TrainingMaterial trainingMaterial = trainingMaterialController.findTrainingMaterialById(trainingMaterialId);
    if (trainingMaterial == null) {
      return createNotFound(String.format("Training material %s not found", trainingMaterialId));
    }
    
    trainingMaterialController.deleteTrainingMaterial(trainingMaterial);
    
    return createNoContent();
  }

  @Override
  public Response findTrainingMaterial(UUID trainingMaterialId) {
    // TODO: Check permissions
    
    fi.metatavu.metamind.persistence.models.TrainingMaterial trainingMaterial = trainingMaterialController.findTrainingMaterialById(trainingMaterialId);
    if (trainingMaterial == null) {
      return createNotFound(String.format("Training material %s not found", trainingMaterialId));
    }
    
    return createOk(trainingMaterialTranslator.translateTrainingMaterial(trainingMaterial));
  }
  
  @Override
  public Response listTrainingMaterials(UUID storyId, TrainingMaterialType type, TrainingMaterialVisibility visibility) {
    // TODO: Check permissions
    
    fi.metatavu.metamind.persistence.models.Story story = storyId != null ? storyController.findStoryById(storyId) : null;
    if (storyId != null && story == null) {
      return createBadRequest(String.format("Story %s not found", storyId)); 
    }
    
    return createOk(trainingMaterialController.listTrainingMaterials(story, type, visibility).stream()
      .map(trainingMaterialTranslator::translateTrainingMaterial)
      .collect(Collectors.toList()));
  }

  @Override
  public Response updateTrainingMaterial(TrainingMaterial body, UUID trainingMaterialId) {
    // TODO: Check permissions
    
    fi.metatavu.metamind.persistence.models.TrainingMaterial trainingMaterial = trainingMaterialController.findTrainingMaterialById(trainingMaterialId);
    if (trainingMaterial == null) {
      return createNotFound(String.format("Training material %s not found", trainingMaterialId));
    }
    
    UUID loggedUserId = getLoggerUserId();
    
    return createOk(trainingMaterialTranslator.translateTrainingMaterial(trainingMaterialController.updateTrainingMaterial(trainingMaterial, body.getName(), body.getText(), loggedUserId, body.getVisibility())));
  }

}
