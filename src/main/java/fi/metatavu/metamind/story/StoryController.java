package fi.metatavu.metamind.story;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import fi.metatavu.metamind.bot.KnotTrainingMaterialUpdateRequestEvent;
import fi.metatavu.metamind.messages.MessageController;
import fi.metatavu.metamind.persistence.dao.IntentDAO;
import fi.metatavu.metamind.persistence.dao.IntentTrainingMaterialDAO;
import fi.metatavu.metamind.persistence.dao.KnotDAO;
import fi.metatavu.metamind.persistence.dao.StoryDAO;
import fi.metatavu.metamind.persistence.dao.VariableDAO;
import fi.metatavu.metamind.persistence.models.Intent;
import fi.metatavu.metamind.persistence.models.Knot;
import fi.metatavu.metamind.persistence.models.Story;
import fi.metatavu.metamind.persistence.models.Variable;
import fi.metatavu.metamind.rest.model.IntentType;
import fi.metatavu.metamind.rest.model.KnotType;
import fi.metatavu.metamind.rest.model.TokenizerType;
import fi.metatavu.metamind.rest.model.VariableType;
import jersey.repackaged.com.google.common.base.Objects;

/**
 * Story controller
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class StoryController {

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
  public Story createStory(Locale locale, String name, UUID creatorId) {
    return storyDAO.create(UUID.randomUUID(), locale, name, creatorId, creatorId);
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
   * Creates new Knot
   * 
   * @param type type
   * @param tokenizerType type of tokenizer
   * @param name name
   * @param content content
   * @param hint hint
   * @param story story
   * @param creatorId creator's id
   * @return created knot
   */
  public Knot createKnot(KnotType type, TokenizerType tokenizerType, String name, String content, String hint, Story story, UUID creatorId) {
    return knotDAO.create(UUID.randomUUID(), type, tokenizerType, name, content, hint, story, creatorId, creatorId);
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
   * @return intents
   */
  public List<Intent> listIntentsByStory(Story story) {
    return intentDAO.listByStory(story);
  }
  
  /**
   * Lists knots by story
   * 
   * @param story story
   * @return knots
   */
  public List<Knot> listKnotsByStory(Story story) {
    return knotDAO.listByStory(story);
  }

  /**
   * Lists knots by given parameters
   * 
   * @param story story
   * @param nameLike filter by knots which's name matches like query. Ignored if null given
   * @return matching knots
   */
  public List<Knot> listKnots(Story story, String nameLike) {
    return knotDAO.list(story, nameLike);
  }
  
  /**
   * List variables by story
   * 
   * @param story story
   * @return variables
   */
  public List<Variable> listVariablesByStory(Story story) {
    return variableDAO.listByStory(story);
  }

  /**
   * Lists quick responses by source knot
   * 
   * @param sourceKnot source knot
   * @return quick responses
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
   * @param trainingMaterial training material
   * @param global wheter intent is a global one
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
   * @return updated knot
   */
  public Knot updateKnot(Knot knot, KnotType type, TokenizerType tokenizerType, String name, String content, String hint, UUID lastModifierId) {
    knot = knotDAO.updateContent(knot, content, lastModifierId);
    knot = knotDAO.updateName(knot, name, lastModifierId);
    knot = knotDAO.updateType(knot, type, lastModifierId);
    knot = knotDAO.updateTokenizerType(knot, tokenizerType, lastModifierId);
    knot = knotDAO.updateHint(knot, hint, lastModifierId);
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
  public Story updateStory(Story story, Locale locale, String name, UUID lastModifierId) {
    story = storyDAO.updateLocale(story, locale, lastModifierId);
    story = storyDAO.updateName(story, name, lastModifierId);
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

}
