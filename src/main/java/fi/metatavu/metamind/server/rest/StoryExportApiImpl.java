package fi.metatavu.metamind.server.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import fi.metatavu.metamind.nlp.TrainingMaterialController;
import fi.metatavu.metamind.persistence.models.Intent;
import fi.metatavu.metamind.persistence.models.Knot;
import fi.metatavu.metamind.persistence.models.Script;
import fi.metatavu.metamind.persistence.models.Story;
import fi.metatavu.metamind.persistence.models.TrainingMaterial;
import fi.metatavu.metamind.persistence.models.Variable;
import fi.metatavu.metamind.rest.api.StoryExportApi;
import fi.metatavu.metamind.rest.api.StoryImportApi;
import fi.metatavu.metamind.rest.model.Coordinates;
import fi.metatavu.metamind.rest.model.ExportedStory;
import fi.metatavu.metamind.rest.model.ExportedStoryIntent;
import fi.metatavu.metamind.rest.model.ExportedStoryKnot;
import fi.metatavu.metamind.rest.model.ExportedStoryScript;
import fi.metatavu.metamind.rest.model.ExportedStoryTrainingMaterial;
import fi.metatavu.metamind.rest.model.ExportedStoryVariable;
import fi.metatavu.metamind.rest.model.TrainingMaterialType;
import fi.metatavu.metamind.rest.model.TrainingMaterialVisibility;
import fi.metatavu.metamind.scripts.ScriptController;
import fi.metatavu.metamind.server.rest.translation.IntentTranslator;
import fi.metatavu.metamind.story.StoryController;;

/**
 * REST - endpoints for exporting and importing stories
 * 
 */
@RequestScoped
@Stateful
@Consumes({ "application/json;charset=utf-8" })
@Produces({ "application/json;charset=utf-8" })
public class StoryExportApiImpl extends AbstractRestApi implements StoryExportApi, StoryImportApi {

  @Inject
  private StoryController storyController;
  @Inject
  private TrainingMaterialController trainingMaterialController;
  @Inject
  private ScriptController scriptController;
  @Inject
  private IntentTranslator intentTranslator;
  
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
    List<ExportedStoryKnot> storyToExportKnots = new ArrayList<ExportedStoryKnot>();
    
    for (int i = 0; i < knotsToExport.size(); i++) {
      Knot knotToExport = knotsToExport.get(i);
      ExportedStoryKnot exportedKnot = new ExportedStoryKnot();
      
      Coordinates coordinates = new Coordinates();
      coordinates.setX(knotToExport.getCoordinateX());
      coordinates.setY(knotToExport.getCoordinateY());
      
      exportedKnot.setContent(knotToExport.getContent());
      exportedKnot.setCoordinates(coordinates);
      exportedKnot.setId(knotToExport.getId());
      exportedKnot.setType(knotToExport.getType());
      exportedKnot.setTokenizer(knotToExport.getTokenizerType());
      exportedKnot.setName(knotToExport.getName());
      exportedKnot.setHint(knotToExport.getHint());
      
      storyToExportKnots.add(exportedKnot);
    }
    
    exportedStory.setKnots(storyToExportKnots);

    
    List<Intent> intentsToExport = storyController.listIntentsByStory(storyToExport);
    List<ExportedStoryIntent> storyToExportIntents = new ArrayList<ExportedStoryIntent>();
    
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
      List<UUID> intentMaterialIds = new ArrayList<UUID>();
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
    
    List<ExportedStoryTrainingMaterial> storyToExportTrainingMaterials = new ArrayList<ExportedStoryTrainingMaterial>();
    
    for (int i = 0; i < trainingMaterialsToExport.size(); i++) {
      TrainingMaterial materialToExport = trainingMaterialsToExport.get(i);
      ExportedStoryTrainingMaterial exportedMaterial = new ExportedStoryTrainingMaterial();
      
      exportedMaterial.setId(materialToExport.getId());
      exportedMaterial.setType(materialToExport.getType());
      exportedMaterial.setText(materialToExport.getText());
      exportedMaterial.setVisibility(materialToExport.getVisibility().toString());
      exportedMaterial.setName(materialToExport.getName());
      
      storyToExportTrainingMaterials.add(exportedMaterial);
    }
    
    exportedStory.setTrainingMaterials(storyToExportTrainingMaterials);
    
    List<Script> scriptsToExport = scriptController.listScripts();
    List<ExportedStoryScript> storyToExportScripts = new ArrayList<ExportedStoryScript>();
    
    for (int i = 0; i < scriptsToExport.size(); i++) {
      Script scriptToExport = scriptsToExport.get(i);
      ExportedStoryScript exportedScript = new ExportedStoryScript();
      
      exportedScript.setContent(scriptToExport.getContent());
      exportedScript.setLanguage(scriptToExport.getLanguage());
      exportedScript.setName(scriptToExport.getName());
      exportedScript.setVersion(scriptToExport.getVersion());
      
      storyToExportScripts.add(exportedScript);
    }
    
    exportedStory.setScripts(storyToExportScripts);
    
    List<Variable> variablesToExport = storyController.listVariablesByStory(storyToExport);
    List<ExportedStoryVariable> exportedStoryVariables = new ArrayList<ExportedStoryVariable>();
    
    for (int i = 0; i < variablesToExport.size(); i++) {
      Variable variableToExport = variablesToExport.get(i);
      ExportedStoryVariable exportedVariable = new ExportedStoryVariable();
      
      exportedVariable.setName(variableToExport.getName());
      exportedVariable.setType(variableToExport.getType());
      exportedVariable.setValidationScript(variableToExport.getValidationScript());
      
      exportedStoryVariables.add(exportedVariable);
    }
    exportedStory.setVariables(exportedStoryVariables);
    
    return createOk(exportedStory);
  }
  
  @Override
  public Response importStory(ExportedStory body) {
    Story story = storyController.createStory(Locale.forLanguageTag(body.getLocale()), body.getName(), body.getDefaultHint(), getLoggerUserId());
    
    List<ExportedStoryKnot> knotsToCreate = body.getKnots();
    List<ExportedStoryTrainingMaterial> trainingMaterialsToCreate = body.getTrainingMaterials();
    
    for (int i = 0; i < body.getIntents().size(); i++) {
      Knot sourceKnot = null;
      Knot targetKnot = null;
      
      if (body.getIntents().get(i).getSourceKnotId() != null) {
        for (int j = 0; j < knotsToCreate.size(); j++) {
          if (body.getIntents().get(i).getSourceKnotId() == knotsToCreate.get(j).getId()) {
            ExportedStoryKnot knotToCreate = knotsToCreate.get(j);
            sourceKnot = storyController.createKnot(knotToCreate.getType(), knotToCreate.getTokenizer(), knotToCreate.getName(), knotToCreate.getContent(), knotToCreate.getHint(), story, getLoggerUserId(), knotToCreate.getCoordinates().getX(), knotToCreate.getCoordinates().getY());
            knotsToCreate.remove(knotToCreate);
          }
        }
      }
      
      if (body.getIntents().get(i).getTargetKnotId() != null) {
        for (int j = 0; j < knotsToCreate.size(); j++) {
          if (body.getIntents().get(i).getTargetKnotId() == knotsToCreate.get(j).getId()) {
            ExportedStoryKnot knotToCreate = knotsToCreate.get(j);
            targetKnot = storyController.createKnot(knotToCreate.getType(), knotToCreate.getTokenizer(), knotToCreate.getName(), knotToCreate.getContent(), knotToCreate.getHint(), story, getLoggerUserId(), knotToCreate.getCoordinates().getX(), knotToCreate.getCoordinates().getY());
            knotsToCreate.remove(knotToCreate);
          }
        }
      }
      
      ExportedStoryIntent intentToCreate = body.getIntents().get(i);
      Intent intent = storyController.createIntent(intentToCreate.getType(), intentToCreate.getName(), sourceKnot, targetKnot, intentToCreate.isisGlobal(), intentToCreate.getQuickResponse(), intentToCreate.getQuickResponseOrder(), getLoggerUserId());      
      for (int x = 0; x < trainingMaterialsToCreate.size(); x++) {
        for (int y = 0; y < intentToCreate.getTrainingMaterialIds().size(); y++) {
          if (trainingMaterialsToCreate.get(x).getId() == intentToCreate.getTrainingMaterialIds().get(y)) {
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
    
    for (int i = 0; i < body.getScripts().size(); i++) {
      ExportedStoryScript scriptToCreate = body.getScripts().get(i);
      scriptController.createScript(scriptToCreate.getName(), scriptToCreate.getContent(), scriptToCreate.getVersion(), scriptToCreate.getLanguage(), getLoggerUserId());
    }
    
    return createOk(story);
  }

}
