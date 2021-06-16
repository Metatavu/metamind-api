package fi.metatavu.metamind.story;

import fi.metatavu.metamind.bot.KnotTrainingMaterialUpdateRequestEvent;
import fi.metatavu.metamind.messages.MessageController;
import fi.metatavu.metamind.nlp.TrainingMaterialController;
import fi.metatavu.metamind.persistence.dao.*;
import fi.metatavu.metamind.persistence.models.*;
import fi.metatavu.metamind.api.spec.model.*;
import fi.metatavu.metamind.persistence.models.Intent;
import fi.metatavu.metamind.persistence.models.Knot;
import fi.metatavu.metamind.persistence.models.Story;
import fi.metatavu.metamind.persistence.models.TrainingMaterial;
import fi.metatavu.metamind.persistence.models.Variable;
import jersey.repackaged.com.google.common.base.Objects;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Story controller
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class StoryController {

  @Inject
  private KnotIntentModelDAO knotIntentModelDAO;

  @Inject
  private StoryDAO storyDAO;

  @Inject
  private KnotDAO knotDAO;

  @Inject
  private IntentDAO intentDAO;

  @Inject
  private VariableDAO variableDAO;

  @Inject
  private IntentTrainingMaterialDAO intentTrainingMaterialDAO;

  @Inject
  private MessageController messageController; 

  @Inject
  private Event<KnotTrainingMaterialUpdateRequestEvent> knotTrainingMaterialUpdateRequestEvent;
  
  @Inject
  private TrainingMaterialController trainingMaterialController;

  /**
   * Finds story by id
   * 
   * @param id id
   * @return story or null if not found
   */
  public Story findStoryById(UUID id) {
    return storyDAO.findById(id);
  }

  /**
   * Finds a knot by id
   * 
   * @param id id
   * @return knot or null if not found
   */
  public Knot findKnotById(UUID id) {
    return knotDAO.findById(id);
  }
  
  /**
   * Creates new story
   *
   * @param locale locale
   * @param name name
   * @param creatorId creator's id
   * @return created story
   */
  public Story createStory(Locale locale, String name, String defaultHint, UUID creatorId) {
    return storyDAO.create(UUID.randomUUID(), locale, name, defaultHint, creatorId, creatorId);
  }

  /**
   * Lists stories
   * 
   * @return stories
   */
  public List<Story> listStories() {
    return storyDAO.listAll();
  }

  /**
   * Lists stories based on permitted creator IDs
   *
   * @param creatorIds all the permitted ids
   * @return stories
   */
  public List<Story> listStoriesByCreatorIds(List<UUID> creatorIds) {
    return storyDAO.listStoriesByCreatorIds(creatorIds);
  }

  /**
   * Creates new Knot
   * 
   * @param type type
   * @param tokenizerType type of tokenizer
   * @param name name
   * @param content content
   * @param hint hint
   * @param story story
   * @param creatorId creator's id
   * @param coordinateX Double coordinate
   * @param coordinateY Double coordinate
   * @param scope scope
   * @return created knot
   */
  public Knot createKnot(KnotType type, TokenizerType tokenizerType, String name, String content, String hint, Story story, UUID creatorId, Double coordinateX, Double coordinateY, KnotScope scope) {
    return knotDAO.create(UUID.randomUUID(), type, tokenizerType, name, content, hint, story, creatorId, creatorId, coordinateX, coordinateY, scope);
  }
  
  /**
   * Creates new Knot
   * 
   * @param type type
   * @param name name
   * @param sourceKnot sourceKnot
   * @param targetKnot targetKnot
   * @param global global
   * @param creatorId creator's id
   * @return created intent
   */
  public Intent createIntent(IntentType type, String name, Knot sourceKnot, Knot targetKnot, Boolean global, String quickResponse, Integer quickResponseOrder, UUID creatorId) {
    return intentDAO.create(UUID.randomUUID(), type, name, sourceKnot, targetKnot, global, quickResponse, quickResponseOrder, creatorId, creatorId);
  }
  
  /**
   * Creates new variable
   *
   * @param type type
   * @param story story
   * @param name name
   * @param validationScript validationScript
   * @param creatorId creator's id
   * @return created variable
   */
  public Variable createVariable(VariableType type, Story story, String name, String validationScript, UUID creatorId) {
    return variableDAO.create(UUID.randomUUID(), type, story, name, validationScript, creatorId, creatorId);
  }

  /**
   * Finds an intent by id
   * 
   * @param intentId intent id
   * @return found intent or null if not found
   */
  public Intent findIntentById(UUID intentId) {
    return intentDAO.findById(intentId);
  }
  
  /**
   * Finds a variable by id
   * 
   * @param variableId variable id
   * @return found variable or null if not found
   */
  public Variable findVariableById(UUID variableId) {
    return variableDAO.findById(variableId);
  }

  /**
   * Finds a variable by story and variable name
   * 
   * @param story story
   * @param variableName variable name
   * @return variable
   */
  public Variable findVariableByStoryAndName(Story story, String variableName) {
    return variableDAO.findByStoryNameName(story, variableName);
  }

  /**
   * Lists intents by story
   * 
   * @param story story
   * @return intents list
   */
  public List<Intent> listIntentsByStory(Story story) {
    return intentDAO.listByStory(story);
  }
  
  /**
   * Lists knots by story
   * 
   * @param story story
   * @return knots list
   */
  public List<Knot> listKnotsByStory(Story story) {
    return knotDAO.listByStory(story);
  }
  
  /**
   * List variables by story
   * 
   * @param story story
   * @return variables list
   */
  public List<Variable> listVariablesByStory(Story story) {
    return variableDAO.listByStory(story);
  }

  /**
   * Lists quick responses by source knot
   * 
   * @param sourceKnot source knot
   * @return quick responses list
   */
  public List<String> listKnotQuickResponses(Knot sourceKnot) {
    return intentDAO.listQuickResponsesBySourceKnot(sourceKnot);
  }

  /**
   * Lists story global quick responses
   * @param story story
   * @return story global quick responses
   */
  public List<String> listStoryGlobalQuickResponses(Story story) {
    return intentDAO.listQuickResponsesByStoryAndGlobal(story, Boolean.TRUE);
  }

  /**
   * Returns redirect target knot for a source knot if redirect intent is defined
   * 
   * @param sourceKnot source knot
   * @return target redirect knot or null if knot should not be redirected
   */
  public Knot getSourceKnotRedirectKnot(Knot sourceKnot) {
    List<Intent> redirectKnots = intentDAO.listBySourceKnotAndType(sourceKnot, IntentType.REDIRECT);
    return redirectKnots.isEmpty() ? null : redirectKnots.get(0).getTargetKnot();
  }

  /**
   * Updates an intent
   *   
   * @param intent intent
   * @param type type
   * @param name name
   * @param sourceKnot source knot
   * @param targetKnot target knot
   * @param global whether intent is a global one
   * @param lastModifierId last modifier's id
   * @return updated intent
   */
  public Intent updateIntent(Intent intent, IntentType type, String name, Knot sourceKnot, Knot targetKnot, Boolean global, String quickResponse, Integer quickResponseOrder, UUID lastModifierId) {
    UUID oldSourceKnotId = intent.getSourceKnot() != null ? intent.getSourceKnot().getId() : null;
    UUID newSourceKnotId = sourceKnot != null ? sourceKnot.getId() : null;
    
    boolean sourceKnotChanged = !Objects.equal(newSourceKnotId, oldSourceKnotId);
    
    intentDAO.updateGlobal(intent, global, lastModifierId);
    intentDAO.updateSourceKnot(intent, sourceKnot, lastModifierId);
    intentDAO.updateTargetKnot(intent, targetKnot, lastModifierId);
    intentDAO.updateType(intent, type, lastModifierId);
    intentDAO.updateType(intent, type, lastModifierId);
    intentDAO.updateName(intent, name, lastModifierId);
    intentDAO.updateQuickResponse(intent, quickResponse, lastModifierId);
    intentDAO.updateQuickResponseOrder(intent, quickResponseOrder, lastModifierId);
    
    if (sourceKnot != null && sourceKnotChanged) {
      knotTrainingMaterialUpdateRequestEvent.fire(new KnotTrainingMaterialUpdateRequestEvent(sourceKnot.getId()));
    }
    
    return intent;
  }

  /**
   * Updates knot
   * 
   * @param knot knot
   * @param type type
   * @param tokenizerType type of tokenizer
   * @param name name
   * @param content content
   * @param hint hint
   * @param lastModifierId last modifier's id
   * @param coordinateX Double coordinate
   * @param coordinateY Double coordinate
   * @param scope scope
   * @return updated knot
   */
  public Knot updateKnot(Knot knot, KnotType type, TokenizerType tokenizerType, String name, String content, String hint, UUID lastModifierId, Double coordinateX, Double coordinateY, KnotScope scope) {
    knot = knotDAO.updateContent(knot, content, lastModifierId);
    knot = knotDAO.updateName(knot, name, lastModifierId);
    knot = knotDAO.updateType(knot, type, lastModifierId);
    knot = knotDAO.updateTokenizerType(knot, tokenizerType, lastModifierId);
    knot = knotDAO.updateHint(knot, hint, lastModifierId);
    knot = knotDAO.updateCoordinates(knot, coordinateX, coordinateY);
    knot = knotDAO.updateScope(knot, scope, lastModifierId);
    return knot;
  }

  /**
   * Updates story
   * 
   * @param story story
   * @param locale locale
   * @param name name
   * @param lastModifierId last modifier's id
   * @return updated story
   */
  public Story updateStory(Story story, Locale locale, String name, String defaultHint, UUID lastModifierId) {
    story = storyDAO.updateLocale(story, locale, lastModifierId);
    story = storyDAO.updateName(story, name, lastModifierId);
    story = storyDAO.updateDefaultHint(story, defaultHint, lastModifierId);
    return story;
  }
  
  /**
   * Updates variable
   * 
   * @param variable variable
   * @param name name
   * @param type type
   * @param validationScript validation script
   * @param lastModifierId last modifier's id
   * @return updated variable
   */
  public Variable updateVariable(Variable variable, String name, VariableType type, String validationScript, UUID lastModifierId) {
    variable = variableDAO.updateName(variable, name, lastModifierId);
    variable = variableDAO.updateType(variable, type, lastModifierId);
    variable = variableDAO.updateValidationScript(variable, validationScript, lastModifierId);
    return variable;
  }

  /**
   * Deletes a story
   * 
   * @param story story
   */
  public void deleteStory(Story story) {
    storyDAO.delete(story);
  }

  /**
   * Deletes a knot
   * 
   * @param knot knot
   */
  public void deleteKnot(Knot knot) {
    KnotIntentModel modelKnot = knotIntentModelDAO.findByKnot(knot); 
    if (modelKnot != null) {
      knotIntentModelDAO.delete(modelKnot);
    }
   
    knotDAO.delete(knot);
  }

  /**
   * Deletes a intent
   * 
   * @param intent intent
   */
  public void deleteIntent(Intent intent) {
    messageController.listMessagedByMatchedIntent(intent).stream().forEach(messageController::deleteMessage);
    intentTrainingMaterialDAO.listByIntent(intent).stream().forEach(intentTrainingMaterialDAO::delete);
    intentDAO.delete(intent);
  }
  
  /**
   * Deletes a variable
   * 
   * @param variable variable
   */
  public void deleteVariable(Variable variable) {
    variableDAO.delete(variable);
  }
  
  /**
   * Exports a story
   * 
   * @param story to be exported
   * @return exported story
   */
  public ExportedStory exportStory(Story story) {
    ExportedStory exportedStory = new ExportedStory();
    exportedStory.setName(story.getName());
    exportedStory.setLocale(story.getLocale().toLanguageTag());
    exportedStory.setDefaultHint(story.getDefaultHint());
    
    List<Knot> knotsToExport = listKnotsByStory(story);
    List<Intent> intentsToExport = listIntentsByStory(story);
    List<TrainingMaterial> trainingMaterialsToExport = trainingMaterialController.listTrainingMaterials(story, null, null, null);
    List<Variable> variablesToExport = listVariablesByStory(story);
    
    exportedStory.setKnots(knotsToExport.stream().map(knot -> exportKnot(knot)).collect(Collectors.toList()));
    exportedStory.setIntents(intentsToExport.stream().map(intent -> exportIntent(intent)).collect(Collectors.toList()));
    exportedStory.setTrainingMaterials(trainingMaterialsToExport.stream().map(trainingMaterial -> trainingMaterialController.exportTrainingMaterial(trainingMaterial)).collect(Collectors.toList()));
    exportedStory.setVariables(variablesToExport.stream().map((variable) -> exportVariable(variable)).collect(Collectors.toList()));
    
    return exportedStory;
  }
  
  /**
   * Imports a story
   * 
   * @param body that was previously exported
   * @param userId of user doing the import
   * @return imported story
   */
  public Story importStory(ExportedStory body, UUID userId) {
    Story story = createStory(Locale.forLanguageTag(body.getLocale()), body.getName(), body.getDefaultHint(), userId);
    
    List<ExportedStoryKnot> knotsToCreate = body.getKnots();
    List<ExportedStoryTrainingMaterial> trainingMaterialsToCreate = body.getTrainingMaterials();
    List<ExportedStoryIntent> intentsToCreate = body.getIntents();
    List<ExportedStoryVariable> variablesToCreate = body.getVariables();
    
    variablesToCreate.forEach(variable -> {
      createVariable(variable.getType(), story, variable.getName(), variable.getValidationScript(), userId);
    });
    
    Map <UUID, Knot> originalKnotIds = new HashMap<>();
    knotsToCreate.forEach(knotToCreate -> {
      System.out.println("Creating knot " + knotToCreate + " with scope "+knotToCreate.getScope());
      Knot knot = createKnot(knotToCreate.getType(), knotToCreate.getTokenizer(), knotToCreate.getName(), knotToCreate.getContent(), knotToCreate.getHint(), story, userId, knotToCreate.getCoordinates().getX(), knotToCreate.getCoordinates().getY(), knotToCreate.getScope());
      originalKnotIds.put(knotToCreate.getId(), knot);
    });
    
    Map <UUID, TrainingMaterial> originalTrainingMaterialIds = new HashMap<>();
    trainingMaterialsToCreate.forEach(trainingMaterialToCreate -> {
      TrainingMaterial trainingMaterial = trainingMaterialController.createTrainingMaterial(trainingMaterialToCreate.getType(), trainingMaterialToCreate.getName(), trainingMaterialToCreate.getText(), story, userId, TrainingMaterialVisibility.fromValue(trainingMaterialToCreate.getVisibility()));
      originalTrainingMaterialIds.put(trainingMaterialToCreate.getId(), trainingMaterial);
    });

    intentsToCreate.forEach(intentToCreate -> importIntent(intentToCreate, userId, originalKnotIds, originalTrainingMaterialIds));

    return story;
  }
  
  /**
   * Exports a knot
   * 
   * @param knot to be exported
   * @return exported knot
   */
  private ExportedStoryKnot exportKnot(Knot knot) {
    ExportedStoryKnot exportedKnot = new ExportedStoryKnot();
    Coordinates coordinates = new Coordinates();
    coordinates.setX(knot.getCoordinateX());
    coordinates.setY(knot.getCoordinateY());
    exportedKnot.setContent(knot.getContent());
    exportedKnot.setCoordinates(coordinates);
    exportedKnot.setId(knot.getId());
    exportedKnot.setType(knot.getType());
    exportedKnot.setTokenizer(knot.getTokenizerType());
    exportedKnot.setName(knot.getName());
    exportedKnot.setHint(knot.getHint());
    exportedKnot.setScope(knot.getScope());
    return exportedKnot;
  }
  
  /**
   * Exports an intent
   * 
   * @param intent to be exported
   * @return exported intent
   */
  private ExportedStoryIntent exportIntent(Intent intent) {
    ExportedStoryIntent exportedIntent = new ExportedStoryIntent();
    
    exportedIntent.setId(intent.getId());
    exportedIntent.setName(intent.getName());
    exportedIntent.setQuickResponse(intent.getQuickResponse());
    exportedIntent.setQuickResponseOrder(intent.getQuickResponseOrder());
    exportedIntent.setType(intent.getType());
    exportedIntent.setGlobal(intent.getGlobal());
    
    if (intent.getSourceKnot() != null) {
    	exportedIntent.setSourceKnotId(intent.getSourceKnot().getId());
    }
    
    if (intent.getTargetKnot() != null) {
    	exportedIntent.setTargetKnotId(intent.getTargetKnot().getId());
    }
    
    List<TrainingMaterial> intentMaterial = trainingMaterialController.listTrainingMaterialByIntent(intent);
    exportedIntent.setTrainingMaterialIds(intentMaterial.stream().map(trainingMaterial -> {
      return trainingMaterial.getId();
    }).collect(Collectors.toList()));
    
    return exportedIntent;
  }
  
  /**
   * Exports a variable
   * 
   * @param variable to be exported
   * @return exported variable
   */
  private ExportedStoryVariable exportVariable(Variable variable) {
    ExportedStoryVariable exportedVariable = new ExportedStoryVariable();
    exportedVariable.setName(variable.getName());
    exportedVariable.setType(variable.getType());
    exportedVariable.setValidationScript(variable.getValidationScript());
    return exportedVariable;
  }
  
  /**
   * Imports an intent from a previously exported story
   * 
   * @param intentToCreate intent from previously exported story
   * @param userId of the user doing the import
   * @param originalKnotIds with original knot ids and imported knots
   * @param originalTrainingMaterialIds with original training material ids and imported training materials
   */
  private void importIntent(ExportedStoryIntent intentToCreate, UUID userId, Map<UUID, Knot> originalKnotIds, Map<UUID, TrainingMaterial> originalTrainingMaterialIds) {
    Knot sourceKnot = null;
    Knot targetKnot = null;
    if (intentToCreate.getTargetKnotId() != null) {
      targetKnot = originalKnotIds.get(intentToCreate.getTargetKnotId());
    } 
    
    if (intentToCreate.getSourceKnotId() != null) {
      sourceKnot = originalKnotIds.get(intentToCreate.getSourceKnotId());
    }
    
    if (targetKnot != null) {
      Intent intent = createIntent(intentToCreate.getType(), intentToCreate.getName(), sourceKnot, targetKnot, intentToCreate.getGlobal(), intentToCreate.getQuickResponse(), intentToCreate.getQuickResponseOrder(), userId);
      
      List<TrainingMaterial> intentTrainingMaterials = intentToCreate.getTrainingMaterialIds().stream().map(id -> {
        return originalTrainingMaterialIds.get(id);
      }).collect(Collectors.toList());
      
      intentTrainingMaterials.forEach(trainingMaterial -> {
        if (trainingMaterial != null) {
          trainingMaterialController.setIntentTrainingMaterial(intent, trainingMaterial.getType(), trainingMaterial);
        }
      });
    }
  }

  /**
   * Returns whether intent is from given story
   *
   * @param intent intent
   * @param story story
   * @return whether intent is from given story
   */
  public boolean isIntentFromStory(fi.metatavu.metamind.persistence.models.Intent intent, fi.metatavu.metamind.persistence.models.Story story) {
    if (intent == null) {
      return false;
    }

    return isKnotFromStory(intent.getTargetKnot(), story);
  }

  /**
   * Returns whether knot is from given story
   *
   * @param knot knot
   * @param story story
   * @return whether knot is from given story
   */
  public boolean isKnotFromStory(fi.metatavu.metamind.persistence.models.Knot knot, fi.metatavu.metamind.persistence.models.Story story) {
    if (knot == null || story == null) {
      return false;
    }

    return story.getId().equals(knot.getStory().getId());
  }

  /**
   * Returns whether variable is from given story
   *
   * @param variable variable
   * @param story story
   * @return whether variable is from given story
   */
  public boolean isVariableFromStory(fi.metatavu.metamind.persistence.models.Variable variable, fi.metatavu.metamind.persistence.models.Story story) {
    if (variable == null || story == null) {
      return false;
    }

    return story.getId().equals(variable.getStory().getId());
  }

}
