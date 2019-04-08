package fi.metatavu.metamind.server.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;

import fi.metatavu.metamind.bot.BotController;
import fi.metatavu.metamind.bot.BotResponse;
import fi.metatavu.metamind.bot.BotRuntimeContext;
import fi.metatavu.metamind.bot.script.ScriptProcessor;
import fi.metatavu.metamind.messages.MessageController;
import fi.metatavu.metamind.nlp.TrainingMaterialController;
import fi.metatavu.metamind.persistence.models.IntentTrainingMaterial;
import fi.metatavu.metamind.persistence.models.MessageResponse;
import fi.metatavu.metamind.persistence.models.Session;
import fi.metatavu.metamind.persistence.models.TrainingMaterial;
import fi.metatavu.metamind.rest.api.StoriesApi;
import fi.metatavu.metamind.rest.model.Intent;
import fi.metatavu.metamind.rest.model.IntentTrainingMaterials;
import fi.metatavu.metamind.rest.model.Knot;
import fi.metatavu.metamind.rest.model.Story;
import fi.metatavu.metamind.rest.model.TrainingMaterialType;
import fi.metatavu.metamind.rest.model.Variable;
import fi.metatavu.metamind.server.rest.translation.IntentTranslator;
import fi.metatavu.metamind.server.rest.translation.KnotTranslator;
import fi.metatavu.metamind.server.rest.translation.MessageTranslator;
import fi.metatavu.metamind.server.rest.translation.SessionTranslator;
import fi.metatavu.metamind.server.rest.translation.StoryTranslator;
import fi.metatavu.metamind.server.rest.translation.VariableTranslator;
import fi.metatavu.metamind.sessions.SessionController;
import fi.metatavu.metamind.story.StoryController;

/**
 * REST - endpoints for stories
 * 
 * @author Heikki Kurhinen
 * @author Antti Leppä
 */
@RequestScoped
@Stateful
@Consumes({ "application/json;charset=utf-8" })
@Produces({ "application/json;charset=utf-8" })
public class StoriesApiImpl extends AbstractRestApi implements StoriesApi {

  @Inject
  private SessionController sessionController;

  @Inject
  private MessageController messageController;

  @Inject
  private StoryController storyController;

  @Inject
  private BotController botController;

  @Inject
  private TrainingMaterialController trainingMaterialController;
  
  @Inject
  private ScriptProcessor scriptProcessor;

  @Inject
  private MessageTranslator messageTranslator;

  @Inject
  private StoryTranslator storyTranslator;

  @Inject
  private IntentTranslator intentTranslator;

  @Inject
  private KnotTranslator knotTranslator;

  @Inject
  private SessionTranslator sessionTranslator;
  
  @Inject
  private VariableTranslator variableTranslator;

  @Inject
  private BotRuntimeContext botRuntimeContext;
  
  @Override
  public Response createIntent(Intent body, UUID storyId) {
    fi.metatavu.metamind.persistence.models.Knot sourceKnot = body.getSourceKnotId() != null ? storyController.findKnotById(body.getSourceKnotId()) : null;
    if (body.getSourceKnotId() != null && sourceKnot == null) {
      return createBadRequest(String.format("Invalid source knot id %s", body.getSourceKnotId()));
    }
    
    fi.metatavu.metamind.persistence.models.Story story = storyController.findStoryById(storyId);
    if (story == null) {
      return createBadRequest(String.format("Story %s not found", storyId)); 
    }

    fi.metatavu.metamind.persistence.models.Knot targetKnot = storyController.findKnotById(body.getTargetKnotId());
    if (targetKnot == null) {
      return createBadRequest(String.format("Invalid target knot id %s", body.getTargetKnotId()));
    }

    if (!isKnotFromStory(targetKnot, story)) {
      return createBadRequest(String.format("Target knot %s is not from the story %s", targetKnot.getId(), story.getId()));
    }

    if (sourceKnot != null && !isKnotFromStory(sourceKnot, story)) {
      return createBadRequest(String.format("Source knot %s is not from the story %s", sourceKnot.getId(), story.getId()));
    }
    
    // TODO: Permission check
    
    Boolean global = body.isisGlobal();
    String quickResponse = body.getQuickResponse();
    
    UUID loggedUserId = getLoggerUserId();
    fi.metatavu.metamind.persistence.models.Intent intent = storyController.createIntent(body.getType(), body.getName(), sourceKnot, targetKnot, global, quickResponse, loggedUserId);
    
    IntentTrainingMaterials intentTrainingMaterials = body.getTrainingMaterials();
    
    TrainingMaterial openNlpDoccatMaterial = findTrainingMaterialById(intentTrainingMaterials.getOpenNlpDoccatId());
    if (intentTrainingMaterials.getOpenNlpDoccatId() != null && openNlpDoccatMaterial == null) {
      return createBadRequest(String.format("Invalid training material id %s", intentTrainingMaterials.getOpenNlpDoccatId()));
    }
    
    TrainingMaterial openNlpNerMaterial = findTrainingMaterialById(intentTrainingMaterials.getOpenNlpNerId());
    if (intentTrainingMaterials.getOpenNlpNerId() != null && openNlpNerMaterial == null) {
      return createBadRequest(String.format("Invalid training material id %s", intentTrainingMaterials.getOpenNlpNerId()));
    }

    IntentTrainingMaterial openNlpDocatTrainingMaterial = trainingMaterialController.setIntentTrainingMaterial(intent, TrainingMaterialType.OPENNLPDOCCAT, openNlpDoccatMaterial);
    IntentTrainingMaterial openNlpNerTrainingMaterial = trainingMaterialController.setIntentTrainingMaterial(intent, TrainingMaterialType.OPENNLPNER, openNlpNerMaterial);
    
    return createOk(intentTranslator.translateIntent(intent, openNlpDocatTrainingMaterial, openNlpNerTrainingMaterial));
  }

  @Override
  public Response createKnot(Knot body, UUID storyId) {
    UUID loggedUserId = getLoggerUserId();
    
    // TODO: Permission check
    
    fi.metatavu.metamind.persistence.models.Story story = storyController.findStoryById(storyId);
    if (story == null) {
      return createBadRequest(String.format("Story %s not found", storyId)); 
    }
    
    return createOk(knotTranslator.translateKnot(storyController.createKnot(body.getType(), body.getName(), body.getContent(), body.getHint(), story, loggedUserId)));
  }

  @Override
  public Response createMessage(fi.metatavu.metamind.rest.model.Message body, UUID storyId) {
    Session session = sessionController.findSessionById(body.getSessionId());
    if (session == null) {
      return createBadRequest("Invalid session id");
    }
    
    String content = StringUtils.trim(body.getContent());
    if (StringUtils.isBlank(content)) {
      return createBadRequest("Missing content");
    }
    
    fi.metatavu.metamind.persistence.models.Story story = storyController.findStoryById(storyId);
    if (story == null) {
      return createBadRequest(String.format("Story %s not found", storyId)); 
    }
    
    if (!story.getId().equals(session.getStory().getId())) {
      return createBadRequest("Session is not from this story");
    }
    
    BotResponse botResponse = botController.getResponse(session, content, LocaleUtils.toLocale(session.getLocale()), TimeZone.getTimeZone(session.getTimeZone()));
    
    UUID loggedUserId = getLoggerUserId();
    
    List<MessageResponse> messageResponses = new ArrayList<>();
    
    fi.metatavu.metamind.persistence.models.Intent matchedIntent = botResponse.getMatchedIntent();
    if (matchedIntent != null) {
      fi.metatavu.metamind.persistence.models.Knot knot = matchedIntent.getTargetKnot();
      
      botRuntimeContext.setSession(session);    
      botRuntimeContext.setLoggedUserId(loggedUserId);
      botRuntimeContext.setCurrentKnot(knot);
      botRuntimeContext.setMatchedIntent(matchedIntent);
      botRuntimeContext.setVariableValues(botResponse.getVariableValues());
        
      scriptProcessor.processScripts();
      
      fi.metatavu.metamind.persistence.models.Knot currentKnot = botRuntimeContext.getCurrentKnot();

      fi.metatavu.metamind.persistence.models.Message message = messageController.createMessage(session, content, currentKnot.getHint(), botResponse.getConfidence(), session.getCurrentKnot(), knot, botResponse.getMatchedIntent(), loggedUserId);
      if (message == null) {
        return createInternalServerError("Could not create new message");
      }

      for (String response : botRuntimeContext.getResponses()) {
        messageResponses.add(messageController.createMessageResponse(message, response));
      }
 
      messageController.updateMessageTargetKnot(message, currentKnot, loggedUserId);
      sessionController.updateSessionCurrentKnot(session, currentKnot, loggedUserId);
      
      botResponse.getVariableValues().entrySet().stream().forEach(entry -> {
        fi.metatavu.metamind.persistence.models.Variable variable = storyController.findVariableById(entry.getKey());
        sessionController.setSessionVariableValue(session, variable, entry.getValue());
      });
      
      List<String> quickResponses = storyController.listKnotQuickResponses(currentKnot);
      messageController.updateMessageQuickResponses(message, quickResponses);

      return createOk(messageTranslator.translateMessage(message, quickResponses, messageResponses));
    } else {
      return createInternalServerError("Could not resolve intent");
    }    
  }

  @Override
  public Response createSession(fi.metatavu.metamind.rest.model.Session body, UUID storyId) {
    fi.metatavu.metamind.persistence.models.Story story = storyController.findStoryById(storyId);
    if (story == null) {
      return createBadRequest("Invalid story parameter");
    }
    
    UUID loggedUserId = getLoggerUserId();
    String locale = body.getLocale();
    String visitor = body.getVisitor();
    
    // TODO: Permission check
    
    Session session = sessionController.create(story, locale, body.getTimeZone(), visitor, loggedUserId);
    
    return createOk(sessionTranslator.translateSession(session));
  }

  @Override
  public Response createStory(Story body) {
    UUID loggedUserId = getLoggerUserId();
    Locale locale = LocaleUtils.toLocale(body.getLocale());
    
    // TODO: Permission check

    return createOk(storyTranslator.translateStory(storyController.createStory(locale, body.getName(), loggedUserId)));
  }

  @Override
  public Response createVariable(Variable body, UUID storyId) {
    UUID loggedUserId = getLoggerUserId();
    
    // TODO: Permission check
    
    fi.metatavu.metamind.persistence.models.Story story = storyController.findStoryById(storyId);
    if (story == null) {
      return createBadRequest(String.format("Story %s not found", storyId)); 
    }
    
    return createOk(variableTranslator.translateVariable(storyController.createVariable(body.getType(), story, body.getName(), body.getValidationScript(), loggedUserId)));
  }

  @Override
  public Response deleteIntent(UUID storyId, UUID intentId) {
    // TODO: Permission check
    
    fi.metatavu.metamind.persistence.models.Story story = storyController.findStoryById(storyId);
    if (story == null) {
      return createBadRequest(String.format("Story %s not found", storyId)); 
    }
    
    fi.metatavu.metamind.persistence.models.Intent intent = storyController.findIntentById(intentId);
    if (intent == null) {
      return createBadRequest(String.format("Intent %s not found", intentId)); 
    }
    
    if (!isIntentFromStory(intent, story)) {
      return createBadRequest(String.format("Intent %s is not from the story %s", intent.getId(), story.getId()));
    }
    
    storyController.deleteIntent(intent);

    return createNoContent();
  }

  @Override
  public Response deleteKnot(UUID storyId, UUID knotId) {
    // TODO: Permission check
    
    fi.metatavu.metamind.persistence.models.Story story = storyController.findStoryById(storyId);
    if (story == null) {
      return createBadRequest(String.format("Story %s not found", storyId)); 
    }
    
    fi.metatavu.metamind.persistence.models.Knot knot = storyController.findKnotById(knotId);
    if (knot == null) {
      return createBadRequest(String.format("Knot %s not found", knotId)); 
    }
    
    if (!isKnotFromStory(knot, story)) {
      return createBadRequest(String.format("Knot %s is not from the story %s", knot.getId(), story.getId()));
    }
    
    storyController.deleteKnot(knot);

    return createNoContent();
  }

  @Override
  public Response deleteStory(UUID storyId) {
    // TODO: Permission check
    
    fi.metatavu.metamind.persistence.models.Story story = storyController.findStoryById(storyId);
    if (story == null) {
      return createBadRequest(String.format("Story %s not found", storyId)); 
    }
    
    storyController.deleteStory(story);

    return createNoContent();
  }

  @Override
  public Response deleteVariable(UUID storyId, UUID variableId) {
    // TODO: Permission check
    
    fi.metatavu.metamind.persistence.models.Variable variable = storyController.findVariableById(variableId);
    if (variable == null) {
      return createBadRequest(String.format("Variable %s not found", variableId)); 
    }
    
    fi.metatavu.metamind.persistence.models.Story story = storyController.findStoryById(storyId);
    if (story == null) {
      return createBadRequest(String.format("Story %s not found", storyId)); 
    }
    
    if (!isVariableFromStory(variable, story)) {
      return createBadRequest(String.format("Variable %s is not from the story %s", variable.getId(), story.getId()));
    }
    
    storyController.deleteVariable(variable);
    
    return createNoContent();
  }

  @Override
  public Response findIntent(UUID storyId, UUID intentId) {
    // TODO: Permission check
    
    fi.metatavu.metamind.persistence.models.Story story = storyController.findStoryById(storyId);
    if (story == null) {
      return createBadRequest(String.format("Story %s not found", storyId)); 
    }
    
    fi.metatavu.metamind.persistence.models.Intent intent = storyController.findIntentById(intentId);
    if (intent == null) {
      return createBadRequest(String.format("Intent %s not found", intentId)); 
    }
    
    if (!isIntentFromStory(intent, story)) {
      return createBadRequest(String.format("Intent %s is not from the story %s", intent.getId(), story.getId()));
    }
    
    return createOk(intentTranslator.translateIntent(intent, trainingMaterialController.listTrainingMaterialByIntent(intent)));
  }

  @Override
  public Response findKnot(UUID storyId, UUID knotId) {
    // TODO: Permission check
    
    fi.metatavu.metamind.persistence.models.Story story = storyController.findStoryById(storyId);
    if (story == null) {
      return createBadRequest(String.format("Story %s not found", storyId)); 
    }
    
    fi.metatavu.metamind.persistence.models.Knot knot = storyController.findKnotById(knotId);
    if (knot == null) {
      return createBadRequest(String.format("Knot %s not found", knotId)); 
    }

    if (!isKnotFromStory(knot, story)) {
      return createBadRequest(String.format("Knot %s is not from the story %s", knot.getId(), story.getId()));
    }
    
    return createOk(knotTranslator.translateKnot(knot));
  }

  @Override
  public Response findStory(UUID storyId) {
    // TODO: Permission check
    
    fi.metatavu.metamind.persistence.models.Story story = storyController.findStoryById(storyId);
    if (story == null) {
      return createBadRequest(String.format("Story %s not found", storyId)); 
    }
    
    return createOk(storyTranslator.translateStory(story));
  }

  @Override
  public Response findVariable(UUID storyId, UUID variableId) {
    // TODO: Permission check
    
    fi.metatavu.metamind.persistence.models.Variable variable = storyController.findVariableById(variableId);
    if (variable == null) {
      return createBadRequest(String.format("Variable %s not found", variableId)); 
    }
    
    fi.metatavu.metamind.persistence.models.Story story = storyController.findStoryById(storyId);
    if (story == null) {
      return createBadRequest(String.format("Story %s not found", storyId)); 
    }
    
    if (!isVariableFromStory(variable, story)) {
      return createBadRequest(String.format("Variable %s is not from the story %s", variable.getId(), story.getId()));
    }
    
    return createOk(variableTranslator.translateVariable(variable));
  }

  @Override
  public Response listIntents(UUID storyId) {
    // TODO: Permission check
    
    fi.metatavu.metamind.persistence.models.Story story = storyController.findStoryById(storyId);
    if (story == null) {
      return createBadRequest(String.format("Story %s not found", storyId)); 
    }
    
    return createOk(storyController.listIntentsByStory(story).stream()
      .map(intent -> intentTranslator.translateIntent(intent, trainingMaterialController.listTrainingMaterialByIntent(intent)))
      .collect(Collectors.toList()));
  }

  @Override
  public Response listKnots(UUID storyId) {
    // TODO: Permission check
    
    fi.metatavu.metamind.persistence.models.Story story = storyController.findStoryById(storyId);
    if (story == null) {
      return createBadRequest(String.format("Story %s not found", storyId)); 
    }
    
    return createOk(storyController.listKnotsByStory(story).stream()
      .map(knotTranslator::translateKnot)
      .collect(Collectors.toList()));
  }

  @Override
  public Response listStories() {
    // TODO: Permission check
    
    return createOk(storyController.listStories().stream()
      .map(storyTranslator::translateStory)
      .collect(Collectors.toList()));
  }

  @Override
  public Response listVariables(UUID storyId) {
    // TODO: Permission check
    
    fi.metatavu.metamind.persistence.models.Story story = storyController.findStoryById(storyId);
    if (story == null) {
      return createBadRequest(String.format("Story %s not found", storyId)); 
    }
    
    return createOk(storyController.listVariablesByStory(story).stream()
      .map(variableTranslator::translateVariable)
      .collect(Collectors.toList()));
  }

  @Override
  public Response updateIntent(Intent body, UUID storyId, UUID intentId) {
    fi.metatavu.metamind.persistence.models.Knot sourceKnot = body.getSourceKnotId() != null ? storyController.findKnotById(body.getSourceKnotId()) : null;
    if (body.getSourceKnotId() != null && sourceKnot == null) {
      return createBadRequest(String.format("Invalid source knot id %s", body.getSourceKnotId()));
    }

    fi.metatavu.metamind.persistence.models.Knot targetKnot = storyController.findKnotById(body.getTargetKnotId());
    if (targetKnot == null) {
      return createBadRequest(String.format("Invalid target knot id %s", body.getTargetKnotId()));
    }
    
    fi.metatavu.metamind.persistence.models.Intent intent = storyController.findIntentById(intentId);
    
    // TODO: Permission check
    // TODO: Story check
    
    Boolean global = body.isisGlobal();
    String quickResponse = body.getQuickResponse();
    UUID loggedUserId = getLoggerUserId();
    
    IntentTrainingMaterials intentTrainingMaterials = body.getTrainingMaterials();
    
    TrainingMaterial openNlpDoccatMaterial = findTrainingMaterialById(intentTrainingMaterials.getOpenNlpDoccatId());
    if (intentTrainingMaterials.getOpenNlpDoccatId() != null && openNlpDoccatMaterial == null) {
      return createBadRequest(String.format("Invalid training material id %s", intentTrainingMaterials.getOpenNlpDoccatId()));
    }
    
    TrainingMaterial openNlpNerMaterial = findTrainingMaterialById(intentTrainingMaterials.getOpenNlpNerId());
    if (intentTrainingMaterials.getOpenNlpNerId() != null && openNlpNerMaterial == null) {
      return createBadRequest(String.format("Invalid training material id %s", intentTrainingMaterials.getOpenNlpNerId()));
    }

    IntentTrainingMaterial openNlpDocatTrainingMaterial = trainingMaterialController.setIntentTrainingMaterial(intent, TrainingMaterialType.OPENNLPDOCCAT, openNlpDoccatMaterial);
    IntentTrainingMaterial openNlpNerTrainingMaterial = trainingMaterialController.setIntentTrainingMaterial(intent, TrainingMaterialType.OPENNLPNER, openNlpNerMaterial);
    
    return createOk(intentTranslator.translateIntent(storyController.updateIntent(intent, body.getType(), body.getName(), sourceKnot, targetKnot, global, quickResponse, loggedUserId), openNlpDocatTrainingMaterial, openNlpNerTrainingMaterial));
  }

  @Override
  public Response updateKnot(Knot body, UUID storyId, UUID knotId) {
    UUID loggedUserId = getLoggerUserId();
    
    // TODO: Permission check
    
    fi.metatavu.metamind.persistence.models.Story story = storyController.findStoryById(storyId);
    if (story == null) {
      return createBadRequest(String.format("Story %s not found", storyId)); 
    }
    
    fi.metatavu.metamind.persistence.models.Knot knot = storyController.findKnotById(knotId);
    if (knot == null) {
      return createBadRequest(String.format("Knot %s not found", knotId)); 
    }

    if (!isKnotFromStory(knot, story)) {
      return createBadRequest(String.format("Knot %s is not from the story %s", knot.getId(), story.getId()));
    }
    
    return createOk(knotTranslator.translateKnot(storyController.updateKnot(knot, body.getType(), body.getName(), body.getContent(), body.getHint(), loggedUserId)));
  }

  @Override
  public Response updateStory(Story body, UUID storyId) {
    UUID loggedUserId = getLoggerUserId();
    Locale locale = LocaleUtils.toLocale(body.getLocale());
    
    // TODO: Permission check

    fi.metatavu.metamind.persistence.models.Story story = storyController.findStoryById(storyId);
    if (story == null) {
      return createBadRequest(String.format("Story %s not found", storyId)); 
    }
    
    return createOk(storyTranslator.translateStory(storyController.updateStory(story, locale, body.getName(), loggedUserId)));
  }

  @Override
  public Response updateVariable(Variable body, UUID storyId, UUID variableId) {
    UUID loggedUserId = getLoggerUserId();
    // TODO: Permission check
    
    fi.metatavu.metamind.persistence.models.Variable variable = storyController.findVariableById(variableId);
    if (variable == null) {
      return createBadRequest(String.format("Variable %s not found", variableId)); 
    }
    
    fi.metatavu.metamind.persistence.models.Story story = storyController.findStoryById(storyId);
    if (story == null) {
      return createBadRequest(String.format("Story %s not found", storyId)); 
    }
    
    if (!isVariableFromStory(variable, story)) {
      return createBadRequest(String.format("Variable %s is not from the story %s", variable.getId(), story.getId()));
    }
    
    return createOk(variableTranslator.translateVariable(storyController.updateVariable(variable, body.getName(), body.getType(), body.getValidationScript(), loggedUserId)));
  }

  /**
   * Returns whether intent is from given story
   * 
   * @param intent intent
   * @param story story
   * @return  whether intent is from given story
   */
  private boolean isIntentFromStory(fi.metatavu.metamind.persistence.models.Intent intent, fi.metatavu.metamind.persistence.models.Story story) {
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
   * @return  whether knot is from given story
   */
  private boolean isKnotFromStory(fi.metatavu.metamind.persistence.models.Knot knot, fi.metatavu.metamind.persistence.models.Story story) {
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
   * @return  whether variable is from given story
   */
  private boolean isVariableFromStory(fi.metatavu.metamind.persistence.models.Variable variable, fi.metatavu.metamind.persistence.models.Story story) {
    if (variable == null || story == null) {
      return false;
    }
    
    return story.getId().equals(variable.getStory().getId());
  }

  /**
   * Finds training material by id
   * 
   * @param id id
   * @return training material or null if not found
   */
  private TrainingMaterial findTrainingMaterialById(UUID id) {
    if (id == null) {
      return null;
    }
    
    return trainingMaterialController.findTrainingMaterialById(id);
  }
  
}