package fi.metatavu.metamind.server.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
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
import org.keycloak.representations.idm.authorization.DecisionStrategy;
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
import fi.metatavu.metamind.rest.model.TokenizerType;
import fi.metatavu.metamind.rest.model.TrainingMaterialType;
import fi.metatavu.metamind.rest.model.Variable;
import fi.metatavu.metamind.rest.model.VariableType;
import fi.metatavu.metamind.server.keycloak.AuthenticationController;
import fi.metatavu.metamind.server.keycloak.AuthorizationScope;
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

  private static final int MAX_KNOT_REDIRECTS = 50;
  
  @Inject
  private AuthenticationController authenticationController;
  
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
    
    Boolean global = body.isisGlobal();
    String quickResponse = body.getQuickResponse();
    Integer quickResponseOrder = body.getQuickResponseOrder();
    String realmName = "test";
    List<AuthorizationScope> scopes = Arrays.asList(AuthorizationScope.STORY_ACCESS, AuthorizationScope.STORY_MANAGE);
    List<String> userNames = Arrays.asList("admin", "manager");
    if (quickResponseOrder == null) {
      quickResponseOrder = 0;
    }
    
    UUID loggedUserId = getLoggerUserId();
    fi.metatavu.metamind.persistence.models.Intent intent = storyController.createIntent(body.getType(), body.getName(), sourceKnot, targetKnot, global, quickResponse, quickResponseOrder, loggedUserId);
    
    IntentTrainingMaterials intentTrainingMaterials = body.getTrainingMaterials();
    
    TrainingMaterial intentOpenNlpDoccatMaterial = findTrainingMaterialById(intentTrainingMaterials.getIntentOpenNlpDoccatId());
    if (intentTrainingMaterials.getIntentOpenNlpDoccatId() != null && intentOpenNlpDoccatMaterial == null) {
      return createBadRequest(String.format("Invalid training material id %s", intentTrainingMaterials.getIntentOpenNlpDoccatId()));
    }
    
    TrainingMaterial variableOpenNlpNerMaterial = findTrainingMaterialById(intentTrainingMaterials.getVariableOpenNlpNerId());
    if (intentTrainingMaterials.getVariableOpenNlpNerId() != null && variableOpenNlpNerMaterial == null) {
      return createBadRequest(String.format("Invalid training material id %s", intentTrainingMaterials.getVariableOpenNlpNerId()));
    }
    
    TrainingMaterial intentRegexMaterial = findTrainingMaterialById(intentTrainingMaterials.getIntentRegexId());
    if (intentTrainingMaterials.getIntentRegexId() != null && intentRegexMaterial == null) {
      return createBadRequest(String.format("Invalid training material id %s", intentTrainingMaterials.getIntentRegexId()));
    }
    
    TrainingMaterial variableOpenNlpRegexMaterial = findTrainingMaterialById(intentTrainingMaterials.getVariableOpenNlpRegex());
    if (intentTrainingMaterials.getVariableOpenNlpRegex() != null && variableOpenNlpRegexMaterial == null) {
      return createBadRequest(String.format("Invalid training material id %s", intentTrainingMaterials.getVariableOpenNlpRegex()));
    }

    IntentTrainingMaterial intentOpenNlpDocatTrainingMaterial = trainingMaterialController.setIntentTrainingMaterial(intent, TrainingMaterialType.INTENTOPENNLPDOCCAT, intentOpenNlpDoccatMaterial);
    IntentTrainingMaterial variableOpenNlpNerTrainingMaterial = trainingMaterialController.setIntentTrainingMaterial(intent, TrainingMaterialType.VARIABLEOPENNLPNER, variableOpenNlpNerMaterial);
    IntentTrainingMaterial intentRegexTrainingMaterial = trainingMaterialController.setIntentTrainingMaterial(intent, TrainingMaterialType.INTENTREGEX, intentRegexMaterial);
    IntentTrainingMaterial variableOpenNlpRegexTrainingMaterial = trainingMaterialController.setIntentTrainingMaterial(intent, TrainingMaterialType.VARIABLEOPENNLPREGEX, variableOpenNlpRegexMaterial);
    
    try {
      System.out.println("First insttance Logged user is >>> " + loggedUserId);
      UUID createdResourceId = authenticationController.createProtectedResource(loggedUserId, String.format("Intent-%s", intent.getId()), String.format("/v2/stories/intents/%s", intent.getId()), "intent", scopes);
      List<UUID> policyIds = authenticationController.updatePermissionUsers(realmName, userNames);
      authenticationController.upsertScopePermission(realmName, createdResourceId, scopes, String.format("Permission for intent-%s", intent.getId()), DecisionStrategy.AFFIRMATIVE, policyIds);
      Set<UUID> permittedUsersId = authenticationController.getResourcePermittedUsers(realmName, createdResourceId, String.format("Intent-%s", intent.getId()), scopes);
      if (permittedUsersId.contains(loggedUserId) && loggedUserId != null) {
       System.out.println("User is allowed");
      } else {
        return createBadRequest("NO USER FOUND: Intents");
      }
    } catch (Exception e) {
      System.out.println("Exception caught: " + e);
    }
    
    return createOk(intentTranslator.translateIntent(intent, intentOpenNlpDocatTrainingMaterial, variableOpenNlpNerTrainingMaterial, intentRegexTrainingMaterial, variableOpenNlpRegexTrainingMaterial));
  }

  @Override
  public Response createKnot(Knot body, UUID storyId) {
    UUID loggedUserId = getLoggerUserId();
    TokenizerType tokenizerType = body.getTokenizer();
    String content = body.getContent();
    String hint = body.getHint();
    
    String realmName = "test";
    List<AuthorizationScope> scopes = Arrays.asList(AuthorizationScope.STORY_ACCESS, AuthorizationScope.STORY_MANAGE);
    List<String> userNames = Arrays.asList("admin", "manager");
    
    fi.metatavu.metamind.persistence.models.Story story = storyController.findStoryById(storyId);
    if (story == null) {
      return createBadRequest(String.format("Story %s not found", storyId)); 
    }
    
    fi.metatavu.metamind.persistence.models.Knot knot = storyController.createKnot(body.getType(), tokenizerType, body.getName(), content, hint, story, loggedUserId); 
    
    try {
      System.out.println("First insttance Logged user is >>> " + loggedUserId);
      UUID createdResourceId = authenticationController.createProtectedResource(loggedUserId, String.format("Knot-%s", knot.getId()), String.format("/v2/stories/knots/%s", knot.getId()), "knot", scopes);
      List<UUID> policyIds = authenticationController.updatePermissionUsers(realmName, userNames);
      authenticationController.upsertScopePermission(realmName, createdResourceId, scopes, String.format("Permission for knot-%s", knot.getId()), DecisionStrategy.AFFIRMATIVE, policyIds);
      Set<UUID> permittedUsersId = authenticationController.getResourcePermittedUsers(realmName, createdResourceId, String.format("Knot-%s", knot.getId()), scopes);
      if (!permittedUsersId.contains(loggedUserId)) {
        if (loggedUserId != null) {
          return createForbidden("Forbidden: 403");
        } else {
          return createUnauthorized("Unauthorized: 401");
        }
        
      }
    } catch (Exception e) {
      System.out.println("Exception caught: " + e);
    }
    System.out.println("User is allowed");
    return createOk(knotTranslator.translateKnot(storyController.createKnot(body.getType(), body.getTokenizer(), body.getName(), body.getContent(), body.getHint(), story, loggedUserId)));
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
      botRuntimeContext.setCurrentMessageContent(content);  
      
      scriptProcessor.processScripts();
      
      fi.metatavu.metamind.persistence.models.Knot currentKnot = botRuntimeContext.getCurrentKnot();

      fi.metatavu.metamind.persistence.models.Message message = messageController.createMessage(session, content, currentKnot.getHint(), botResponse.getConfidence(), session.getCurrentKnot(), knot, botResponse.getMatchedIntent(), loggedUserId);
      if (message == null) {
        return createInternalServerError("Could not create new message");
      }
      
      for (String response : botRuntimeContext.getResponses()) {
        messageResponses.add(messageController.createMessageResponse(message, response));
      }
      
      int redirects = 0;
      fi.metatavu.metamind.persistence.models.Knot redirectKnot;
      while ((redirectKnot = storyController.getSourceKnotRedirectKnot(currentKnot)) != null) {
        messageResponses.add(messageController.createMessageResponse(message, scriptProcessor.processScripts(redirectKnot.getContent())));
        currentKnot = redirectKnot;
        redirects++;
        
        if (redirects >= MAX_KNOT_REDIRECTS) {
          return createInternalServerError("Knot is redirecting incorrectly");
        }
      }
 
      messageController.updateMessageTargetKnot(message, currentKnot, loggedUserId);
      sessionController.updateSessionCurrentKnot(session, currentKnot, loggedUserId);
      
      botResponse.getVariableValues().entrySet().stream().forEach(entry -> {
        fi.metatavu.metamind.persistence.models.Variable variable = storyController.findVariableById(entry.getKey());
        if (variable != null) {
          sessionController.setSessionVariableValue(session, variable, entry.getValue());
        }
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
    String realmName = "test";
    List<AuthorizationScope> scopes = Arrays.asList(AuthorizationScope.STORY_ACCESS, AuthorizationScope.STORY_MANAGE);
    List<String> userNames = Arrays.asList("admin", "manager");
    
    Session session = sessionController.create(story, locale, body.getTimeZone(), visitor, loggedUserId);
    
    try {
      System.out.println("First insttance Logged user is >>> " + loggedUserId);
      UUID createdResourceId = authenticationController.createProtectedResource(loggedUserId, String.format("Session-%s", session.getId()), String.format("/v2/stories/sessions/%s", session.getId()), "session", scopes);
      List<UUID> policyIds = authenticationController.updatePermissionUsers(realmName, userNames);
      authenticationController.upsertScopePermission(realmName, createdResourceId, scopes, String.format("Permission for session-%s", session.getId()), DecisionStrategy.AFFIRMATIVE, policyIds);
      Set<UUID> permittedUsersId = authenticationController.getResourcePermittedUsers(realmName, createdResourceId, String.format("Session-%s", session.getId()), scopes);
      if (permittedUsersId.contains(loggedUserId) && loggedUserId != null) {
       System.out.println("User is allowed");
      } else {
        return createBadRequest("NO USER FOUND: Sessions");
      }
    } catch (Exception e) {
      System.out.println("Exception caught: " + e);
    }
    
    return createOk(sessionTranslator.translateSession(session));
  }

  @Override
  public Response createStory(Story body) {
//    System.out.println("Requested ID is: " + getLoggerUserId());
//    UUID requestedUserId = getLoggerUserId();
    UUID loggedUserId = getLoggerUserId();
    String realmName = "test";
    Locale locale = LocaleUtils.toLocale(body.getLocale());
    List<AuthorizationScope> scopes = Arrays.asList(AuthorizationScope.STORY_ACCESS, AuthorizationScope.STORY_MANAGE);
    List<String> userNames = Arrays.asList("admin", "manager");
    
    fi.metatavu.metamind.persistence.models.Story story = storyController.createStory(locale, body.getName(), body.getDafaultHint(), loggedUserId);
//    fi.metatavu.metamind.persistence.models.Story story = storyController.findStoryById(body.getId());
    System.out.println("DEBUG API IMPL: CREATED STORY's ID IS: " + story.getId());
    try {
      System.out.println("First insttance Logged user is >>> " + loggedUserId);
      UUID createdResourceId = authenticationController.createProtectedResource(loggedUserId, String.format("story-%s", story.getId()), String.format("/v2/stories/%s", story.getId()), "story", scopes);
      List<UUID> policyIds = authenticationController.updatePermissionUsers(realmName, userNames);
      authenticationController.upsertScopePermission(realmName, createdResourceId, scopes, String.format("Permission for story-%s", story.getId()), DecisionStrategy.AFFIRMATIVE, policyIds);
      Set<UUID> permittedUsersId = authenticationController.getResourcePermittedUsers(realmName, createdResourceId, String.format("story-%s", story.getId()), scopes);
      if (permittedUsersId.contains(loggedUserId) && loggedUserId != null) {
       System.out.println("User is allowed");
      } else {
        return createBadRequest("NO USER FOUND: Stories");
      }
    } catch (Exception e) {
      System.out.println("Exception caught: " + e);
    }
//    UUID userNameSet = authenticationController.getUserFromSession();
//    System.out.println("ID's EXTRACTED FROM SESSION ARE ... " + userNameSet);
    System.out.println("ACCESS GRANTED, ACCOUNT WITH ID - " + loggedUserId + " IS PERMITTED");
    return createOk(storyTranslator.translateStory(story));
  }

  @Override
  public Response createVariable(Variable body, UUID storyId) {
    UUID loggedUserId = getLoggerUserId();
    VariableType type = body.getType();
    String validationScript = body.getValidationScript();
    
    String realmName = "test";
    List<AuthorizationScope> scopes = Arrays.asList(AuthorizationScope.STORY_ACCESS, AuthorizationScope.STORY_MANAGE);
    List<String> userNames = Arrays.asList("admin", "manager");
    
    fi.metatavu.metamind.persistence.models.Story story = storyController.findStoryById(storyId);
    if (story == null) {
      return createBadRequest(String.format("Story %s not found", storyId)); 
    }
    
    fi.metatavu.metamind.persistence.models.Variable variable = storyController.createVariable(type, story, body.getName(), validationScript, loggedUserId);
    
    try {
      System.out.println("First insttance Logged user is >>> " + loggedUserId);
      UUID createdResourceId = authenticationController.createProtectedResource(loggedUserId, String.format("Variable-%s", variable.getId()), String.format("/v2/stories/variables/%s", variable.getId()), "variable", scopes);
      List<UUID> policyIds = authenticationController.updatePermissionUsers(realmName, userNames);
      authenticationController.upsertScopePermission(realmName, createdResourceId, scopes, String.format("Permission for variable-%s", variable.getId()), DecisionStrategy.AFFIRMATIVE, policyIds);
      Set<UUID> permittedUsersId = authenticationController.getResourcePermittedUsers(realmName, createdResourceId, String.format("Variable-%s", variable.getId()), scopes);
      if (permittedUsersId.contains(loggedUserId) && loggedUserId != null) {
       System.out.println("User is allowed");
      } else {
        return createBadRequest("NO USER FOUND: Variables");
      }
    } catch (Exception e) {
      System.out.println("Exception caught: " + e);
    }
    
    return createOk(variableTranslator.translateVariable(storyController.createVariable(body.getType(), story, body.getName(), body.getValidationScript(), loggedUserId)));
  }

  @Override
  public Response deleteIntent(UUID storyId, UUID intentId) {
    UUID loggedUserId = getLoggerUserId();
    String realmName = "test";
    List<AuthorizationScope> scopes = Arrays.asList(AuthorizationScope.STORY_ACCESS, AuthorizationScope.STORY_MANAGE);
    Arrays.asList("admin", "manager");
    
    fi.metatavu.metamind.persistence.models.Story story = storyController.findStoryById(storyId);
    if (story == null) {
      return createNotFound(String.format("Story %s not found", storyId)); 
    }
    
    fi.metatavu.metamind.persistence.models.Intent intent = storyController.findIntentById(intentId);
    if (intent == null) {
      return createNotFound(String.format("Intent %s not found", intentId)); 
    }
    
    if (!isIntentFromStory(intent, story)) {
      return createNotFound(String.format("Intent %s is not from the story %s", intent.getId(), story.getId()));
    }
    UUID foundResourceId = authenticationController.findProtectedResource(String.format("Intent-%s", intent.getId()));
//    if (foundResourceId == null) {
//      return createNotFound(String.format("Resource not found", intentId)); 
//    }
    try {
      
      Set<UUID> permittedUsersId = authenticationController.getResourcePermittedUsers(realmName, foundResourceId, String.format("Intent-%s", intent.getId()), scopes);
      System.out.println("test permitted users " + permittedUsersId);
      if (permittedUsersId.contains(loggedUserId) && loggedUserId != null) {
        System.out.println("User is allowed");
      } else {
        return createBadRequest("NO USER FOUND: Intents");
      }
    } catch (Exception e) {
      System.out.println("Exception caught: " + e);
    }
    
    storyController.deleteIntent(intent);

    return createNoContent();
  }

  @Override
  public Response deleteKnot(UUID storyId, UUID knotId) {
    UUID loggedUserId = getLoggerUserId();
    String realmName = "test";
    List<AuthorizationScope> scopes = Arrays.asList(AuthorizationScope.STORY_ACCESS, AuthorizationScope.STORY_MANAGE);
    Arrays.asList("admin", "manager");
    
    fi.metatavu.metamind.persistence.models.Story story = storyController.findStoryById(storyId);
    if (story == null) {
      return createNotFound(String.format("Story %s not found", storyId)); 
    }
    
    fi.metatavu.metamind.persistence.models.Knot knot = storyController.findKnotById(knotId);
    if (knot == null) {
      return createNotFound(String.format("Knot %s not found", knotId)); 
    }
    
    if (!isKnotFromStory(knot, story)) {
      return createNotFound(String.format("Knot %s is not from the story %s", knot.getId(), story.getId()));
    }
    UUID foundResourceId = authenticationController.findProtectedResource(String.format("Knot-%s", knot.getId()));
    try {
      
      Set<UUID> permittedUsersId = authenticationController.getResourcePermittedUsers(realmName, foundResourceId, String.format("Knot-%s", knot.getId()), scopes);
      if (permittedUsersId.contains(loggedUserId) && loggedUserId != null) {
        System.out.println("User is allowed");
      } else {
        return createBadRequest("NO USER FOUND: Knots");
      }
    } catch (Exception e) {
      System.out.println("Exception caught: " + e);
    }
    
    storyController.deleteKnot(knot);

    return createNoContent();
  }

  @Override
  public Response deleteStory(UUID storyId) {
    
    UUID loggedUserId = getLoggerUserId();
    String realmName = "test";
    List<AuthorizationScope> scopes = Arrays.asList(AuthorizationScope.STORY_ACCESS, AuthorizationScope.STORY_MANAGE);
    Arrays.asList("admin", "manager");
    
    fi.metatavu.metamind.persistence.models.Story story = storyController.findStoryById(storyId);
    if (story == null) {
      return createNotFound(String.format("Story %s not found", storyId)); 
    }
    UUID foundResourceId = authenticationController.findProtectedResource(String.format("story-%s", story.getId()));
    try {
      
      Set<UUID> permittedUsersId = authenticationController.getResourcePermittedUsers(realmName, foundResourceId, String.format("story-%s", story.getId()), scopes);
      if (permittedUsersId.contains(loggedUserId) && loggedUserId != null) {
        System.out.println("User is allowed");
      } else {
        return createBadRequest("NO USER FOUND: Stories");
      }
    } catch (Exception e) {
      System.out.println("Exception caught: " + e);
    }
    
    storyController.deleteStory(story);

    return createNoContent();
  }

  @Override
  public Response deleteVariable(UUID storyId, UUID variableId) {
    UUID loggedUserId = getLoggerUserId();
    String realmName = "test";
    List<AuthorizationScope> scopes = Arrays.asList(AuthorizationScope.STORY_ACCESS, AuthorizationScope.STORY_MANAGE);
    Arrays.asList("admin", "manager");
    
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
    UUID foundResourceId = authenticationController.findProtectedResource(String.format("Variable-%s", variable.getId()));
    try {
      
      Set<UUID> permittedUsersId = authenticationController.getResourcePermittedUsers(realmName, foundResourceId, String.format("Variable-%s", variable.getId()), scopes);
      if (permittedUsersId.contains(loggedUserId) && loggedUserId != null) {
        System.out.println("User is allowed");
      } else {
        return createBadRequest("NO USER FOUND: Variables");
      }
    } catch (Exception e) {
      System.out.println("Exception caught: " + e);
    }
    
    storyController.deleteVariable(variable);
    
    return createNoContent();
  }

  @Override
  public Response findIntent(UUID storyId, UUID intentId) {
    UUID loggedUserId = getLoggerUserId();
    String realmName = "test";
    List<AuthorizationScope> scopes = Arrays.asList(AuthorizationScope.STORY_ACCESS, AuthorizationScope.STORY_MANAGE);
    Arrays.asList("admin", "manager");
    
    fi.metatavu.metamind.persistence.models.Story story = storyController.findStoryById(storyId);
    if (story == null) {
      return createNotFound(String.format("Story %s not found", storyId)); 
    }
    
    fi.metatavu.metamind.persistence.models.Intent intent = storyController.findIntentById(intentId);
    if (intent == null) {
      return createNotFound(String.format("Intent %s not found", intentId)); 
    }
    
    if (!isIntentFromStory(intent, story)) {
      return createNotFound(String.format("Intent %s is not from the story %s", intent.getId(), story.getId()));
    }
    UUID foundResourceId = authenticationController.findProtectedResource(String.format("Intent-%s", intent.getId()));
    try {
      
      Set<UUID> permittedUsersId = authenticationController.getResourcePermittedUsers(realmName, foundResourceId, String.format("Intent-%s", intent.getId()), scopes);
      if (permittedUsersId.contains(loggedUserId) && loggedUserId != null) {
        System.out.println("User is allowed");
      } else {
        return createBadRequest("NO USER FOUND: Intents");
      }
    } catch (Exception e) {
      System.out.println("Exception caught: " + e);
    }
    
    return createOk(intentTranslator.translateIntent(intent, trainingMaterialController.listTrainingMaterialByIntent(intent)));
  }

  @Override
  public Response findKnot(UUID storyId, UUID knotId) {
    UUID loggedUserId = getLoggerUserId();
    String realmName = "test";
    List<AuthorizationScope> scopes = Arrays.asList(AuthorizationScope.STORY_ACCESS, AuthorizationScope.STORY_MANAGE);
    Arrays.asList("admin", "manager");
    
    fi.metatavu.metamind.persistence.models.Story story = storyController.findStoryById(storyId);
    if (story == null) {
      return createNotFound(String.format("Story %s not found", storyId)); 
    }
    
    fi.metatavu.metamind.persistence.models.Knot knot = storyController.findKnotById(knotId);
    if (knot == null) {
      return createNotFound(String.format("Knot %s not found", knotId)); 
    }

    if (!isKnotFromStory(knot, story)) {
      return createNotFound(String.format("Knot %s is not from the story %s", knot.getId(), story.getId()));
    }
    UUID foundResourceId = authenticationController.findProtectedResource(String.format("Knot-%s", knot.getId()));
    try {
      
      Set<UUID> permittedUsersId = authenticationController.getResourcePermittedUsers(realmName, foundResourceId, String.format("Knot-%s", knot.getId()), scopes);
      if (permittedUsersId.contains(loggedUserId) && loggedUserId != null) {
        System.out.println("User is allowed");
      } else {
        return createBadRequest("NO USER FOUND: Knots");
      }
    } catch (Exception e) {
      System.out.println("Exception caught: " + e);
    }
    
    return createOk(knotTranslator.translateKnot(knot));
  }

  @Override
  public Response findStory(UUID storyId) {
    UUID loggedUserId = getLoggerUserId();
    String realmName = "test";
    List<AuthorizationScope> scopes = Arrays.asList(AuthorizationScope.STORY_ACCESS, AuthorizationScope.STORY_MANAGE);
    Arrays.asList("admin", "manager");
    
    fi.metatavu.metamind.persistence.models.Story story = storyController.findStoryById(storyId);
    if (story == null) {
      return createNotFound(String.format("Story %s not found", storyId)); 
    }
    UUID foundResourceId = authenticationController.findProtectedResource(String.format("story-%s", story.getId()));
    try {
      
      Set<UUID> permittedUsersId = authenticationController.getResourcePermittedUsers(realmName, foundResourceId, String.format("story-%s", story.getId()), scopes);
      if (permittedUsersId.contains(loggedUserId) && loggedUserId != null) {
        System.out.println("User is allowed");
      } else {
        return createBadRequest("NO USER FOUND: Stories");
      }
    } catch (Exception e) {
      System.out.println("Exception caught: " + e);
    }
    
    return createOk(storyTranslator.translateStory(story));
  }

  @Override
  public Response findVariable(UUID storyId, UUID variableId) {
    UUID loggedUserId = getLoggerUserId();
    String realmName = "test";
    List<AuthorizationScope> scopes = Arrays.asList(AuthorizationScope.STORY_ACCESS, AuthorizationScope.STORY_MANAGE);
    Arrays.asList("admin", "manager");
    
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
    UUID foundResourceId = authenticationController.findProtectedResource(String.format("Variable-%s", variable.getId()));
    try {
      
      Set<UUID> permittedUsersId = authenticationController.getResourcePermittedUsers(realmName, foundResourceId, String.format("Variable-%s", variable.getId()), scopes);
      if (permittedUsersId.contains(loggedUserId) && loggedUserId != null) {
        System.out.println("User is allowed");
      } else {
        return createBadRequest("NO USER FOUND: Variables");
      }
    } catch (Exception e) {
      System.out.println("Exception caught: " + e);
    }
    
    return createOk(variableTranslator.translateVariable(variable));
  }

  @Override
  public Response listIntents(UUID storyId) {
    String realmName = "test";
    List<AuthorizationScope> scopes = Arrays.asList(AuthorizationScope.STORY_ACCESS, AuthorizationScope.STORY_MANAGE);
    Arrays.asList("admin", "manager");
    
    fi.metatavu.metamind.persistence.models.Story story = storyController.findStoryById(storyId);
    if (story == null) {
      return createBadRequest(String.format("Story %s not found", storyId)); 
    }
    
    List<fi.metatavu.metamind.persistence.models.Intent> intents = storyController.listIntentsByStory(story);
    if (intents.isEmpty()) {
      return createNotFound(String.format("Intents %s not found", storyId)); 
    }
    
    try {
      for (fi.metatavu.metamind.persistence.models.Intent intent : intents) {
        UUID foundResourceId = authenticationController.findProtectedResource(String.format("Intent-%s", intent.getId()));
        Set<UUID> permittedUsersId = authenticationController.getResourcePermittedUsers(realmName, foundResourceId, String.format("Intent-%s", intent.getId()), scopes);
        UUID userNameSet = getLoggerUserId();
        System.out.println("ID's EXTRACTED FROM SESSION ARE ... " + userNameSet);
        if (permittedUsersId.contains(userNameSet) && userNameSet != null) {
        } else {
          return createBadRequest("NO USER FOUND: Intents");
        }
      }
      
    } catch (Exception e) {
      System.out.println("Exception caught: " + e);
    }
    
    return createOk(storyController.listIntentsByStory(story).stream()
      .map(intent -> intentTranslator.translateIntent(intent, trainingMaterialController.listTrainingMaterialByIntent(intent)))
      .collect(Collectors.toList()));
  }

  @Override
  public Response listKnots(UUID storyId) {
    String realmName = "test";
    List<AuthorizationScope> scopes = Arrays.asList(AuthorizationScope.STORY_ACCESS, AuthorizationScope.STORY_MANAGE);
    Arrays.asList("admin", "manager");
    
    fi.metatavu.metamind.persistence.models.Story story = storyController.findStoryById(storyId);
    if (story == null) {
      return createBadRequest(String.format("Story %s not found", storyId)); 
    }
    
    List<fi.metatavu.metamind.persistence.models.Knot> knots = storyController.listKnotsByStory(story);
    if (knots.isEmpty()) {
      return createNotFound(String.format("Knots %s not found", storyId)); 
    }
    
    try {
      for (fi.metatavu.metamind.persistence.models.Knot knot : knots) {
        UUID foundResourceId = authenticationController.findProtectedResource(String.format("Knot-%s", knot.getId()));
        Set<UUID> permittedUsersId = authenticationController.getResourcePermittedUsers(realmName, foundResourceId, String.format("Knot-%s", knot.getId()), scopes);
        UUID userNameSet = getLoggerUserId();
        System.out.println("ID's EXTRACTED FROM SESSION ARE ... " + userNameSet);
        if (permittedUsersId.contains(userNameSet) && userNameSet != null) {
        } else {
          return createBadRequest("NO USER FOUND: Knots");
        }
      }
      
    } catch (Exception e) {
      System.out.println("Exception caught: " + e);
    }
    
    return createOk(storyController.listKnotsByStory(story).stream()
      .map(knotTranslator::translateKnot)
      .collect(Collectors.toList()));
  }

  @Override
  public Response listStories() {
    String realmName = "test";
    List<AuthorizationScope> scopes = Arrays.asList(AuthorizationScope.STORY_ACCESS, AuthorizationScope.STORY_MANAGE);
    Arrays.asList("admin", "manager");
    
    List<fi.metatavu.metamind.persistence.models.Story> stories = storyController.listStories();
    if (stories == null) {
      return createBadRequest(String.format("Stories %s not found")); 
    }
    
    try {
      for (fi.metatavu.metamind.persistence.models.Story story : stories) {
        UUID foundResourceId = authenticationController.findProtectedResource(String.format("story-%s", story.getId()));
        Set<UUID> permittedUsersId = authenticationController.getResourcePermittedUsers(realmName, foundResourceId, String.format("story-%s", story.getId()), scopes);
        UUID userNameSet = getLoggerUserId();
        System.out.println("ID's EXTRACTED FROM SESSION ARE ... " + userNameSet);
        if (permittedUsersId.contains(userNameSet) && userNameSet != null) {
        } else {
          return createBadRequest("NO USER FOUND: Stories");
        }
      }
      
    } catch (Exception e) {
      System.out.println("Exception caught: " + e);
    }
    
    return createOk(storyController.listStories().stream()
      .map(storyTranslator::translateStory)
      .collect(Collectors.toList()));
  }

  @Override
  public Response listVariables(UUID storyId) {
    String realmName = "test";
    List<AuthorizationScope> scopes = Arrays.asList(AuthorizationScope.STORY_ACCESS, AuthorizationScope.STORY_MANAGE);
    Arrays.asList("admin", "manager");
    
    fi.metatavu.metamind.persistence.models.Story story = storyController.findStoryById(storyId);
    if (story == null) {
      return createBadRequest(String.format("Story %s not found", storyId)); 
    }
    
    List<fi.metatavu.metamind.persistence.models.Variable> variables = storyController.listVariablesByStory(story);
    if (variables == null) {
      return createBadRequest(String.format("Variables %s not found", storyId)); 
    }
    
    try {
      for (fi.metatavu.metamind.persistence.models.Variable variable : variables) {
        UUID foundResourceId = authenticationController.findProtectedResource(String.format("Variable-%s", variable.getId()));
        Set<UUID> permittedUsersId = authenticationController.getResourcePermittedUsers(realmName, foundResourceId, String.format("Variable-%s", variable.getId()), scopes);
        UUID userNameSet = getLoggerUserId();
        System.out.println("ID's EXTRACTED FROM SESSION ARE ... " + userNameSet);
        if (permittedUsersId.contains(userNameSet) && userNameSet != null) {
        } else {
          return createBadRequest("NO USER FOUND: Variables");
        }
      }
      
    } catch (Exception e) {
      System.out.println("Exception caught: " + e);
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
    
    Boolean global = body.isisGlobal();
    String quickResponse = body.getQuickResponse();
    Integer quickResponseOrder = body.getQuickResponseOrder();
    if (quickResponseOrder == null) {
      quickResponseOrder = 0;
    }
    
    UUID loggedUserId = getLoggerUserId();
    String realmName = "test";
    List<AuthorizationScope> scopes = Arrays.asList(AuthorizationScope.STORY_ACCESS, AuthorizationScope.STORY_MANAGE);
    Arrays.asList("admin", "manager");
    
    IntentTrainingMaterials intentTrainingMaterials = body.getTrainingMaterials();
    
    TrainingMaterial intentOpenNlpDoccatMaterial = findTrainingMaterialById(intentTrainingMaterials.getIntentOpenNlpDoccatId());
    if (intentTrainingMaterials.getIntentOpenNlpDoccatId() != null && intentOpenNlpDoccatMaterial == null) {
      return createBadRequest(String.format("Invalid training material id %s", intentTrainingMaterials.getIntentOpenNlpDoccatId()));
    }
    
    TrainingMaterial variableOpenNlpNerMaterial = findTrainingMaterialById(intentTrainingMaterials.getVariableOpenNlpNerId());
    if (intentTrainingMaterials.getVariableOpenNlpNerId() != null && variableOpenNlpNerMaterial == null) {
      return createBadRequest(String.format("Invalid training material id %s", intentTrainingMaterials.getVariableOpenNlpNerId()));
    }
    
    TrainingMaterial intentRegexMaterial = findTrainingMaterialById(intentTrainingMaterials.getIntentRegexId());
    if (intentTrainingMaterials.getIntentRegexId() != null && intentRegexMaterial == null) {
      return createBadRequest(String.format("Invalid training material id %s", intentTrainingMaterials.getIntentRegexId()));
    }
    
    TrainingMaterial variableOpenNlpRegexMaterial = findTrainingMaterialById(intentTrainingMaterials.getVariableOpenNlpRegex());
    if (intentTrainingMaterials.getVariableOpenNlpRegex() != null && variableOpenNlpRegexMaterial == null) {
      return createBadRequest(String.format("Invalid training material id %s", intentTrainingMaterials.getVariableOpenNlpRegex()));
    }

    IntentTrainingMaterial intentOpenNlpDocatTrainingMaterial = trainingMaterialController.setIntentTrainingMaterial(intent, TrainingMaterialType.INTENTOPENNLPDOCCAT, intentOpenNlpDoccatMaterial);
    IntentTrainingMaterial variableOpenNlpNerTrainingMaterial = trainingMaterialController.setIntentTrainingMaterial(intent, TrainingMaterialType.VARIABLEOPENNLPNER, variableOpenNlpNerMaterial);
    IntentTrainingMaterial intentRegexTrainingMaterial = trainingMaterialController.setIntentTrainingMaterial(intent, TrainingMaterialType.INTENTREGEX, intentRegexMaterial);
    IntentTrainingMaterial variableOpenNlpRegexTrainingMaterial = trainingMaterialController.setIntentTrainingMaterial(intent, TrainingMaterialType.VARIABLEOPENNLPREGEX, variableOpenNlpRegexMaterial);
    
    UUID foundResourceId = authenticationController.findProtectedResource(String.format("Intent-%s", intent.getId()));
    try {
      
      Set<UUID> permittedUsersId = authenticationController.getResourcePermittedUsers(realmName, foundResourceId, String.format("Intent-%s", intent.getId()), scopes);
      if (permittedUsersId.contains(loggedUserId) && loggedUserId != null) {
       System.out.println("User is allowed");
      } else {
        return createBadRequest("NO USER FOUND: Intents");
      }
    } catch (Exception e) {
      System.out.println("Exception caught: " + e);
    }
    
    return createOk(intentTranslator.translateIntent(storyController.updateIntent(intent, body.getType(), body.getName(), sourceKnot, targetKnot, global, quickResponse, quickResponseOrder, loggedUserId), 
      intentOpenNlpDocatTrainingMaterial, 
      variableOpenNlpNerTrainingMaterial, 
      intentRegexTrainingMaterial, 
      variableOpenNlpRegexTrainingMaterial
    ));
  }

  @Override
  public Response updateKnot(Knot body, UUID storyId, UUID knotId) {
    UUID loggedUserId = getLoggerUserId();
    String realmName = "test";
    List<AuthorizationScope> scopes = Arrays.asList(AuthorizationScope.STORY_ACCESS, AuthorizationScope.STORY_MANAGE);
    Arrays.asList("admin", "manager");
    
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
    UUID foundResourceId = authenticationController.findProtectedResource(String.format("Knot-%s", knot.getId()));
    try {
     
      Set<UUID> permittedUsersId = authenticationController.getResourcePermittedUsers(realmName, foundResourceId, String.format("Knot-%s", knot.getId()), scopes);
      if (permittedUsersId.contains(loggedUserId) && loggedUserId != null) {
       System.out.println("User is allowed");
      } else {
        return createBadRequest("NO USER FOUND: Knots");
      }
    } catch (Exception e) {
      System.out.println("Exception caught: " + e);
    }
    
    return createOk(knotTranslator.translateKnot(storyController.updateKnot(knot, body.getType(), body.getTokenizer(), body.getName(), body.getContent(), body.getHint(), loggedUserId)));
  }

  @Override
  public Response updateStory(Story body, UUID storyId) {
    System.out.println("reached updateStory");
    UUID loggedUserId = getLoggerUserId();
    Locale locale = LocaleUtils.toLocale(body.getLocale());
    String realmName = "test";
    List<AuthorizationScope> scopes = Arrays.asList(AuthorizationScope.STORY_ACCESS, AuthorizationScope.STORY_MANAGE);
    Arrays.asList("admin", "manager");

    fi.metatavu.metamind.persistence.models.Story story = storyController.findStoryById(storyId);
    if (story == null) {
      return createBadRequest(String.format("Story %s not found", storyId)); 
    }
    UUID foundResourceId = authenticationController.findProtectedResource(String.format("story-%s", story.getId()));
    System.out.println("Found resource id is: " + foundResourceId);
    try {
      
      Set<UUID> permittedUsersId = authenticationController.getResourcePermittedUsers(realmName, foundResourceId, String.format("story-%s", story.getId()), scopes);
      if (permittedUsersId.contains(loggedUserId) && loggedUserId != null) {
       System.out.println("User is allowed");
      } else {
        return createBadRequest("NO USER FOUND: Stories");
      }
    } catch (Exception e) {
      System.out.println("Exception caught: " + e);
    }
    
    return createOk(storyTranslator.translateStory(storyController.updateStory(story, locale, body.getName(), body.getDafaultHint(), loggedUserId)));
  }

  @Override
  public Response updateVariable(Variable body, UUID storyId, UUID variableId) {
    UUID loggedUserId = getLoggerUserId();
    String realmName = "test";
    List<AuthorizationScope> scopes = Arrays.asList(AuthorizationScope.STORY_ACCESS, AuthorizationScope.STORY_MANAGE);
    Arrays.asList("admin", "manager");
    
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
    UUID foundResourceId = authenticationController.findProtectedResource(String.format("Variable-%s", variable.getId()));
    try {
      
      Set<UUID> permittedUsersId = authenticationController.getResourcePermittedUsers(realmName, foundResourceId, String.format("Variable-%s", variable.getId()), scopes);
      if (permittedUsersId.contains(loggedUserId) && loggedUserId != null) {
       System.out.println("User is allowed");
      } else {
        return createBadRequest("NO USER FOUND: Variables");
      }
    } catch (Exception e) {
      System.out.println("Exception caught: " + e);
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