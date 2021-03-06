package fi.metatavu.metamind.server.rest;

import fi.metatavu.metamind.api.spec.V2Api;
import fi.metatavu.metamind.api.spec.model.*;
import fi.metatavu.metamind.bot.BotController;
import fi.metatavu.metamind.bot.BotResponse;
import fi.metatavu.metamind.bot.BotRuntimeContext;
import fi.metatavu.metamind.bot.script.ScriptProcessor;
import fi.metatavu.metamind.messages.MessageController;
import fi.metatavu.metamind.nlp.TrainingMaterialController;
import fi.metatavu.metamind.persistence.models.IntentTrainingMaterial;
import fi.metatavu.metamind.persistence.models.MessageResponse;
import fi.metatavu.metamind.persistence.models.Story;
import fi.metatavu.metamind.persistence.models.TrainingMaterial;
import fi.metatavu.metamind.scripts.ScriptController;
import fi.metatavu.metamind.server.rest.translation.*;
import fi.metatavu.metamind.sessions.SessionController;
import fi.metatavu.metamind.story.StoryController;
import io.quarkus.security.identity.SecurityIdentity;
import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.*;
import java.util.stream.Collectors;

@RequestScoped
@Transactional
public class V2ApiImpl extends AbstractRestApi implements V2Api {

    private static final String UNAUTHORIZED = "Unauthorized";

    private static final int MAX_KNOT_REDIRECTS = 50;

    private static final String STORY_NAME_TEMPLATE = "story-%s";

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

    @Inject
    private ScriptController scriptController;

    @Inject
    private ScriptTranslator scriptTranslator;

    @Inject
    private TrainingMaterialTranslator trainingMaterialTranslator;

    @Inject
    private KeycloakController keycloakController;

    @Override
    public Response importStory(ExportedStory body) {
        UUID loggedUserId = getLoggedUserId();
        if (loggedUserId == null) {
            return createUnauthorized(UNAUTHORIZED);
        }

        Story story = storyController.importStory(body, getLoggedUserId());

        if (!isAdmin() && isDifferentGroups(story.getCreatorId(), loggedUserId)) {
            return createForbidden(STORY_GROUP_ERROR);
        }
        return createOk(storyTranslator.translateStory(story));

    }

    @Override
    public Response createScript(Script body) {
        UUID loggedUserId = getLoggedUserId();
        if (loggedUserId == null) {
            return createUnauthorized(UNAUTHORIZED);
        }

        String name = body.getName();
        String version = body.getVersion();
        String content = body.getContent();
        String language = body.getLanguage();

        if (StringUtils.isEmpty(name) || StringUtils.isEmpty(version)) {
            return createBadRequest("Script name and version are required");
        }

        fi.metatavu.metamind.persistence.models.Script scriptEntity = scriptController.findScriptByNameAndVersion(name, version);
        if (scriptEntity == null) {
            scriptEntity = scriptController.createScript(name, content, version, language, loggedUserId);
        } else {
            if (!isAdmin() && isDifferentGroups(scriptEntity.getCreatorId(), loggedUserId)) {
                return createForbidden(SCRIPT_GROUP_ERROR);
            }

            scriptController.updateScript(scriptEntity, body.getContent(), body.getLanguage(), loggedUserId);
        }

        return createOk(scriptTranslator.translateScript(scriptEntity));
    }

    @Override
    public Response deleteScript(UUID scriptId) {
        UUID loggedUserId = getLoggedUserId();
        if (loggedUserId == null) {
            return createUnauthorized(UNAUTHORIZED);
        }

        fi.metatavu.metamind.persistence.models.Script script = scriptController.findScriptById(scriptId);
        if (script == null) {
            return createNotFound(String.format("Script %s not found", scriptId));
        }

        if (!isAdmin() && isDifferentGroups(script.getCreatorId(), loggedUserId)) {
            return createForbidden(SCRIPT_GROUP_ERROR);
        }

        scriptController.deleteScript(script);

        return createNoContent();
    }

    @Override
    public Response findScript(UUID scriptId) {
        UUID loggedUserId = getLoggedUserId();
        if (loggedUserId == null) {
            return createUnauthorized(UNAUTHORIZED);
        }

        fi.metatavu.metamind.persistence.models.Script script = scriptController.findScriptById(scriptId);
        if (script == null) {
            return createNotFound(String.format("Script %s not found", scriptId));
        }

        if (!isAdmin() && isDifferentGroups(script.getCreatorId(), loggedUserId)) {
            return createForbidden(SCRIPT_GROUP_ERROR);
        }

        return createOk(scriptTranslator.translateScript(script));
    }

    @Override
    public Response listScripts() {
        UUID loggedUserId = getLoggedUserId();
        if (loggedUserId == null) {
            return createUnauthorized(UNAUTHORIZED);
        }

        List<fi.metatavu.metamind.persistence.models.Script> scripts;
        if (isAdmin()) {
            scripts = scriptController.listScripts();
        } else {
            List<UUID> usersFromSameGroup = keycloakController.listSameGroupUsers(loggedUserId);
            scripts = scriptController.listScriptsByCreatorIds(usersFromSameGroup);
        }

        return createOk(scripts.stream().map(scriptTranslator::translateScript).collect(Collectors.toList()));
    }

    @Override
    public Response updateScript(UUID scriptId, @Valid Script script) {
        fi.metatavu.metamind.persistence.models.Script foundScript = scriptController.findScriptById(scriptId);
        if (foundScript == null) {
            return createNotFound(String.format("Script %s not found", scriptId));
        }

        UUID loggerUserId = getLoggedUserId();

        return createOk(scriptTranslator.translateScript(scriptController.updateScript(foundScript, script.getContent(), script.getLanguage(), loggerUserId)));

    }

    @Override
    public Response createIntent(UUID storyId, @Valid Intent intent) {
        UUID loggedUserId = getLoggedUserId();
        if (loggedUserId == null) {
            return createUnauthorized(UNAUTHORIZED);
        }

        fi.metatavu.metamind.persistence.models.Knot sourceKnot = intent.getSourceKnotId() != null ? storyController.findKnotById(intent.getSourceKnotId()) : null;
        if (intent.getSourceKnotId() != null && sourceKnot == null) {
            return createBadRequest(String.format("Invalid source knot id %s", intent.getSourceKnotId()));
        }

        fi.metatavu.metamind.persistence.models.Story story = storyController.findStoryById(storyId);
        if (story == null) {
            return createBadRequest(String.format("Story %s not found", storyId));
        }

        if (!isAdmin() && isDifferentGroups(story.getCreatorId(), loggedUserId)) {
            return createForbidden(STORY_GROUP_ERROR);
        }

        fi.metatavu.metamind.persistence.models.Knot targetKnot = storyController.findKnotById(intent.getTargetKnotId());
        if (targetKnot == null) {
            return createBadRequest(String.format("Invalid target knot id %s", intent.getTargetKnotId()));
        }

        if (!storyController.isKnotFromStory(targetKnot, story)) {
            return createBadRequest(String.format("Target knot %s is not from the story %s", targetKnot.getId(), story.getId()));
        }

        if (sourceKnot != null && !storyController.isKnotFromStory(sourceKnot, story)) {
            return createBadRequest(String.format("Source knot %s is not from the story %s", sourceKnot.getId(), story.getId()));
        }

        Boolean global = intent.getGlobal();
        String quickResponse = intent.getQuickResponse();
        Integer quickResponseOrder = intent.getQuickResponseOrder();

        if (quickResponseOrder == null) {
            quickResponseOrder = 0;
        }

        fi.metatavu.metamind.persistence.models.Intent createdIntent = storyController.createIntent(intent.getType(), intent.getName(), sourceKnot, targetKnot, global, quickResponse, quickResponseOrder, loggedUserId);

        IntentTrainingMaterials intentTrainingMaterials = intent.getTrainingMaterials();

        TrainingMaterial intentOpenNlpDoccatMaterial = trainingMaterialController.findTrainingMaterialById(intentTrainingMaterials.getIntentOpenNlpDoccatId());
        if (intentTrainingMaterials.getIntentOpenNlpDoccatId() != null && intentOpenNlpDoccatMaterial == null) {
            return createBadRequest(String.format("Invalid training material id %s", intentTrainingMaterials.getIntentOpenNlpDoccatId()));
        }

        TrainingMaterial variableOpenNlpNerMaterial = trainingMaterialController.findTrainingMaterialById(intentTrainingMaterials.getVariableOpenNlpNerId());
        if (intentTrainingMaterials.getVariableOpenNlpNerId() != null && variableOpenNlpNerMaterial == null) {
            return createBadRequest(String.format("Invalid training material id %s", intentTrainingMaterials.getVariableOpenNlpNerId()));
        }

        TrainingMaterial intentRegexMaterial = trainingMaterialController.findTrainingMaterialById(intentTrainingMaterials.getIntentRegexId());
        if (intentTrainingMaterials.getIntentRegexId() != null && intentRegexMaterial == null) {
            return createBadRequest(String.format("Invalid training material id %s", intentTrainingMaterials.getIntentRegexId()));
        }

        TrainingMaterial variableOpenNlpRegexMaterial = trainingMaterialController.findTrainingMaterialById(intentTrainingMaterials.getVariableOpenNlpRegex());
        if (intentTrainingMaterials.getVariableOpenNlpRegex() != null && variableOpenNlpRegexMaterial == null) {
            return createBadRequest(String.format("Invalid training material id %s", intentTrainingMaterials.getVariableOpenNlpRegex()));
        }

        IntentTrainingMaterial intentOpenNlpDocatTrainingMaterial = trainingMaterialController.setIntentTrainingMaterial(createdIntent, TrainingMaterialType.INTENTOPENNLPDOCCAT, intentOpenNlpDoccatMaterial);
        IntentTrainingMaterial variableOpenNlpNerTrainingMaterial = trainingMaterialController.setIntentTrainingMaterial(createdIntent, TrainingMaterialType.VARIABLEOPENNLPNER, variableOpenNlpNerMaterial);
        IntentTrainingMaterial intentRegexTrainingMaterial = trainingMaterialController.setIntentTrainingMaterial(createdIntent, TrainingMaterialType.INTENTREGEX, intentRegexMaterial);
        IntentTrainingMaterial variableOpenNlpRegexTrainingMaterial = trainingMaterialController.setIntentTrainingMaterial(createdIntent, TrainingMaterialType.VARIABLEOPENNLPREGEX, variableOpenNlpRegexMaterial);

        return createOk(intentTranslator.translateIntent(createdIntent, intentOpenNlpDocatTrainingMaterial, variableOpenNlpNerTrainingMaterial, intentRegexTrainingMaterial, variableOpenNlpRegexTrainingMaterial));
    }

    @Override
    public Response createKnot(UUID storyId, @Valid Knot knot) {
        UUID loggedUserId = getLoggedUserId();
        if (loggedUserId == null) {
            return createUnauthorized(UNAUTHORIZED);
        }

        Double coordinateX = knot.getCoordinates() != null ? knot.getCoordinates().getX() : null;
        Double coordinateY = knot.getCoordinates() != null ? knot.getCoordinates().getY() : null;

        fi.metatavu.metamind.persistence.models.Story story = storyController.findStoryById(storyId);
        if (story == null) {
            return createBadRequest(String.format("Story %s not found", storyId));
        }

        if (!isAdmin() && isDifferentGroups(story.getCreatorId(), loggedUserId)) {
            return createForbidden(STORY_GROUP_ERROR);
        }

        return createOk(knotTranslator.translateKnot(storyController.createKnot(knot.getType(), knot.getTokenizer(), knot.getName(), knot.getContent(), knot.getHint(), story, loggedUserId, coordinateX, coordinateY, knot.getScope())));
    }

    @Override
    public Response createMessage(UUID storyId, @Valid Message message) {
        UUID loggedUserId = getLoggedUserId();
        if (loggedUserId == null) {
            return createUnauthorized(UNAUTHORIZED);
        }

        fi.metatavu.metamind.persistence.models.Session session = sessionController.findSessionById(message.getSessionId());
        if (session == null) {
            return createBadRequest("Invalid session id");
        }

        String content = StringUtils.trim(message.getContent());
        if (StringUtils.isBlank(content)) {
            return createBadRequest("Missing content");
        }

        fi.metatavu.metamind.persistence.models.Story story = storyController.findStoryById(storyId);
        if (story == null) {
            return createBadRequest(String.format("Story %s not found", storyId));
        }

        if (!isAdmin() && isDifferentGroups(story.getCreatorId(), loggedUserId)) {
            return createForbidden(STORY_GROUP_ERROR);
        }

        if (!story.getId().equals(session.getStory().getId())) {
            return createBadRequest("Session is not from this story");
        }

        BotResponse botResponse = botController.getResponse(session, content, LocaleUtils.toLocale(session.getLocale()), TimeZone.getTimeZone(session.getTimeZone()));

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

            fi.metatavu.metamind.persistence.models.Message createdMessage = messageController.createMessage(session, content, currentKnot.getHint(), botResponse.getConfidence(), session.getCurrentKnot(), knot, botResponse.getMatchedIntent(), loggedUserId);
            if (createdMessage == null) {
                return createInternalServerError("Could not create new createdMessage");
            }

            for (String response : botRuntimeContext.getResponses()) {
                messageResponses.add(messageController.createMessageResponse(createdMessage, response));
            }

            int redirects = 0;
            fi.metatavu.metamind.persistence.models.Knot redirectKnot;
            while ((redirectKnot = storyController.getSourceKnotRedirectKnot(currentKnot)) != null) {
                messageResponses.add(messageController.createMessageResponse(createdMessage, scriptProcessor.processScripts(redirectKnot.getContent())));
                currentKnot = redirectKnot;
                redirects++;

                if (redirects >= MAX_KNOT_REDIRECTS) {
                    return createInternalServerError("Knot is redirecting incorrectly");
                }
            }

            messageController.updateMessageTargetKnot(createdMessage, currentKnot, loggedUserId);
            sessionController.updateSessionCurrentKnot(session, currentKnot, loggedUserId);

            botResponse.getVariableValues().entrySet().stream().forEach(entry -> {
                fi.metatavu.metamind.persistence.models.Variable variable = storyController.findVariableById(entry.getKey());
                if (variable != null) {
                    sessionController.setSessionVariableValue(session, variable, entry.getValue());
                }
            });

            List<String> quickResponses = storyController.listKnotQuickResponses(currentKnot);
            messageController.updateMessageQuickResponses(createdMessage, quickResponses);

            return createOk(messageTranslator.translateMessage(createdMessage, quickResponses, messageResponses));
        } else {
            return createInternalServerError("Could not resolve intent");
        }
    }

    @Override
    public Response createSession(UUID storyId, fi.metatavu.metamind.api.spec.model.@Valid Session session) {
        UUID loggedUserId = getLoggedUserId();
        if (loggedUserId == null) {
            return createUnauthorized(UNAUTHORIZED);
        }

        fi.metatavu.metamind.persistence.models.Story story = storyController.findStoryById(storyId);
        if (story == null) {
            return createBadRequest("Invalid story parameter");
        }

        if (!isAdmin() && isDifferentGroups(story.getCreatorId(), loggedUserId)) {
            return createForbidden(STORY_GROUP_ERROR);
        }

        String locale = session.getLocale();
        String visitor = session.getVisitor();

        fi.metatavu.metamind.persistence.models.Session createdSession = sessionController.create(story, locale, session.getTimeZone(), visitor, loggedUserId);

        return createOk(sessionTranslator.translateSession(createdSession));
    }

    @Override
    public Response createStory(fi.metatavu.metamind.api.spec.model.Story body) {
        UUID loggedUserId = getLoggedUserId();
        if (loggedUserId == null) {
            return createUnauthorized(UNAUTHORIZED);
        }

        Locale locale = LocaleUtils.toLocale(body.getLocale());
        fi.metatavu.metamind.persistence.models.Story story = storyController.createStory(locale, body.getName(), body.getDafaultHint(), loggedUserId);

        return createOk(storyTranslator.translateStory(story));
    }

    @Override
    public Response createVariable(UUID storyId, @Valid Variable variable) {
        UUID loggedUserId = getLoggedUserId();
        if (loggedUserId == null) {
            return createUnauthorized(UNAUTHORIZED);
        }

        fi.metatavu.metamind.persistence.models.Story story = storyController.findStoryById(storyId);
        if (story == null) {
            return createBadRequest(String.format("Story %s not found", storyId));
        }

        if (!isAdmin() && isDifferentGroups(story.getCreatorId(), loggedUserId)) {
            return createForbidden(STORY_GROUP_ERROR);
        }

        return createOk(variableTranslator.translateVariable(storyController.createVariable(variable.getType(), story, variable.getName(), variable.getValidationScript(), loggedUserId)));
    }

    @Override
    public Response deleteIntent(UUID storyId, UUID intentId) {
        UUID loggedUserId = getLoggedUserId();
        if (loggedUserId == null) {
            return createUnauthorized(UNAUTHORIZED);
        }

        fi.metatavu.metamind.persistence.models.Story story = storyController.findStoryById(storyId);
        if (story == null) {
            return createNotFound(String.format("Story %s not found", storyId));
        }

        if (!isAdmin() && isDifferentGroups(story.getCreatorId(), loggedUserId)) {
            return createForbidden(STORY_GROUP_ERROR);
        }

        fi.metatavu.metamind.persistence.models.Intent intent = storyController.findIntentById(intentId);
        if (intent == null) {
            return createNotFound(String.format("Intent %s not found", intentId));
        }

        if (!storyController.isIntentFromStory(intent, story)) {
            return createNotFound(String.format("Intent %s is not from the story %s", intent.getId(), story.getId()));
        }

        storyController.deleteIntent(intent);

        return createNoContent();
    }

    @Override
    public Response deleteKnot(UUID storyId, UUID knotId) {
        UUID loggedUserId = getLoggedUserId();
        if (loggedUserId == null) {
            return createUnauthorized(UNAUTHORIZED);
        }

        fi.metatavu.metamind.persistence.models.Story story = storyController.findStoryById(storyId);
        if (story == null) {
            return createNotFound(String.format("Story %s not found", storyId));
        }

        if (!isAdmin() && isDifferentGroups(story.getCreatorId(), loggedUserId)) {
            return createForbidden(STORY_GROUP_ERROR);
        }

        fi.metatavu.metamind.persistence.models.Knot knot = storyController.findKnotById(knotId);
        if (knot == null) {
            return createNotFound(String.format("Knot %s not found", knotId));
        }

        if (!storyController.isKnotFromStory(knot, story)) {
            return createNotFound(String.format("Knot %s is not from the story %s", knot.getId(), story.getId()));
        }

        storyController.deleteKnot(knot);

        return createNoContent();
    }

    @Override
    public Response deleteStory(UUID storyId) {
        UUID loggedUserId = getLoggedUserId();
        if (loggedUserId == null) {
            return createUnauthorized(UNAUTHORIZED);
        }

        fi.metatavu.metamind.persistence.models.Story story = storyController.findStoryById(storyId);
        if (story == null) {
            return createNotFound(String.format("Story %s not found", storyId));
        }

        if (!isAdmin() && isDifferentGroups(story.getCreatorId(), loggedUserId)) {
            return createForbidden(STORY_GROUP_ERROR);
        }

        storyController.deleteStory(story);

        return createNoContent();
    }

    @Override
    public Response deleteVariable(UUID storyId, UUID variableId) {
        UUID loggedUserId = getLoggedUserId();
        if (loggedUserId == null) {
            return createUnauthorized(UNAUTHORIZED);
        }

        fi.metatavu.metamind.persistence.models.Variable variable = storyController.findVariableById(variableId);
        if (variable == null) {
            return createBadRequest(String.format("Variable %s not found", variableId));
        }

        fi.metatavu.metamind.persistence.models.Story story = storyController.findStoryById(storyId);
        if (story == null) {
            return createBadRequest(String.format("Story %s not found", storyId));
        }

        if (!storyController.isVariableFromStory(variable, story)) {
            return createBadRequest(String.format("Variable %s is not from the story %s", variable.getId(), story.getId()));
        }

        if (!isAdmin() && isDifferentGroups(story.getCreatorId(), loggedUserId)) {
            return createForbidden(STORY_GROUP_ERROR);
        }

        storyController.deleteVariable(variable);

        return createNoContent();
    }

    @Override
    public Response findIntent(UUID storyId, UUID intentId) {
        UUID loggedUserId = getLoggedUserId();
        if (loggedUserId == null) {
            return createUnauthorized(UNAUTHORIZED);
        }

        fi.metatavu.metamind.persistence.models.Story story = storyController.findStoryById(storyId);
        if (story == null) {
            return createNotFound(String.format("Story %s not found", storyId));
        }

        if (!isAdmin() && isDifferentGroups(story.getCreatorId(), loggedUserId)) {
            return createForbidden(STORY_GROUP_ERROR);
        }

        fi.metatavu.metamind.persistence.models.Intent intent = storyController.findIntentById(intentId);
        if (intent == null) {
            return createNotFound(String.format("Intent %s not found", intentId));
        }

        if (!storyController.isIntentFromStory(intent, story)) {
            return createNotFound(String.format("Intent %s is not from the story %s", intent.getId(), story.getId()));
        }

        return createOk(intentTranslator.translateIntent(intent, trainingMaterialController.listTrainingMaterialByIntent(intent)));
    }

    @Override
    public Response findKnot(UUID storyId, UUID knotId) {
        UUID loggedUserId = getLoggedUserId();
        if (loggedUserId == null) {
            return createUnauthorized(UNAUTHORIZED);
        }

        fi.metatavu.metamind.persistence.models.Story story = storyController.findStoryById(storyId);
        if (story == null) {
            return createNotFound(String.format("Story %s not found", storyId));
        }

        if (!isAdmin() && isDifferentGroups(story.getCreatorId(), loggedUserId)) {
            return createForbidden(STORY_GROUP_ERROR);
        }

        fi.metatavu.metamind.persistence.models.Knot knot = storyController.findKnotById(knotId);
        if (knot == null) {
            return createNotFound(String.format("Knot %s not found", knotId));
        }

        if (!storyController.isKnotFromStory(knot, story)) {
            return createNotFound(String.format("Knot %s is not from the story %s", knot.getId(), story.getId()));
        }

        return createOk(knotTranslator.translateKnot(knot));
    }

    @Override
    public Response findStory(UUID storyId) {
        UUID loggedUserId = getLoggedUserId();
        if (loggedUserId == null) {
          return createUnauthorized(UNAUTHORIZED);
        }

        fi.metatavu.metamind.persistence.models.Story story = storyController.findStoryById(storyId);
        if (story == null) {
            return createNotFound(String.format("Story %s not found", storyId));
        }

        if (!isAdmin() && isDifferentGroups(story.getCreatorId(), loggedUserId)) {
            return createForbidden(STORY_GROUP_ERROR);
        }

        return createOk(storyTranslator.translateStory(story));
    }

    @Override
    public Response findVariable(UUID storyId, UUID variableId) {
        UUID loggedUserId = getLoggedUserId();
        if (loggedUserId == null) {
            return createUnauthorized(UNAUTHORIZED);
        }

        fi.metatavu.metamind.persistence.models.Variable variable = storyController.findVariableById(variableId);
        if (variable == null) {
            return createBadRequest(String.format("Variable %s not found", variableId));
        }

        fi.metatavu.metamind.persistence.models.Story story = storyController.findStoryById(storyId);
        if (story == null) {
            return createBadRequest(String.format("Story %s not found", storyId));
        }

        if (!isAdmin() && isDifferentGroups(story.getCreatorId(), loggedUserId)) {
            return createForbidden(STORY_GROUP_ERROR);
        }

        if (!storyController.isVariableFromStory(variable, story)) {
            return createBadRequest(String.format("Variable %s is not from the story %s", variable.getId(), story.getId()));
        }

        return createOk(variableTranslator.translateVariable(variable));
    }

    @Override
    public Response listIntents(UUID storyId) {
        UUID loggedUserId = getLoggedUserId();
        if (loggedUserId == null) {
            return createUnauthorized(UNAUTHORIZED);
        }

        fi.metatavu.metamind.persistence.models.Story story = storyController.findStoryById(storyId);
        if (story == null) {
            return createBadRequest(String.format("Story %s not found", storyId));
        }

        if (!isAdmin() && isDifferentGroups(story.getCreatorId(), loggedUserId)) {
            return createForbidden(STORY_GROUP_ERROR);
        }

        return createOk(storyController.listIntentsByStory(story).stream()
                .map(intent -> intentTranslator.translateIntent(intent, trainingMaterialController.listTrainingMaterialByIntent(intent)))
                .collect(Collectors.toList()));
    }

    @Override
    public Response listKnots(UUID storyId) {
        UUID loggedUserId = getLoggedUserId();
        if (loggedUserId == null) {
            return createUnauthorized(UNAUTHORIZED);
        }

        fi.metatavu.metamind.persistence.models.Story story = storyController.findStoryById(storyId);
        if (story == null) {
            return createBadRequest(String.format("Story %s not found", storyId));
        }

        if (!isAdmin() && isDifferentGroups(story.getCreatorId(), loggedUserId)) {
            return createForbidden(STORY_GROUP_ERROR);
        }

        return createOk(storyController.listKnotsByStory(story).stream()
                .map(knotTranslator::translateKnot)
                .collect(Collectors.toList()));
    }

    @Override
    public Response listStories() {
        UUID loggedUserId = getLoggedUserId();
        if (loggedUserId == null) {
            return createUnauthorized(UNAUTHORIZED);
        }

        List<Story> foundStories;
        if (isAdmin()) {
            foundStories = storyController.listStories();
        } else {
            List<UUID> usersFromSameGroup = keycloakController.listSameGroupUsers(loggedUserId);
            foundStories = storyController.listStoriesByCreatorIds(usersFromSameGroup);
        }

        return createOk(foundStories.stream().map(storyTranslator::translateStory).collect(Collectors.toList()));
    }

    @Override
    public Response listVariables(UUID storyId) {
        UUID loggedUserId = getLoggedUserId();
        if (loggedUserId == null) {
            return createUnauthorized(UNAUTHORIZED);
        }

        fi.metatavu.metamind.persistence.models.Story story = storyController.findStoryById(storyId);
        if (story == null) {
            return createBadRequest(String.format("Story %s not found", storyId));
        }

        if (!isAdmin() && isDifferentGroups(story.getCreatorId(), loggedUserId)) {
            return createForbidden(STORY_GROUP_ERROR);
        }

        return createOk(storyController.listVariablesByStory(story).stream()
                .map(variableTranslator::translateVariable)
                .collect(Collectors.toList()));
    }

    @SuppressWarnings("squid:S3776")
    @Override
    public Response updateIntent(UUID storyId, UUID intentId, @Valid Intent intent) {
        UUID loggedUserId = getLoggedUserId();
        if (loggedUserId == null) {
            return createUnauthorized(UNAUTHORIZED);
        }

        fi.metatavu.metamind.persistence.models.Story story = storyController.findStoryById(storyId);
        if (story == null) {
            return createBadRequest(String.format("Story %s not found", storyId));
        }
        if (!isAdmin() && isDifferentGroups(story.getCreatorId(), loggedUserId)) {
            return createForbidden(STORY_GROUP_ERROR);
        }

        fi.metatavu.metamind.persistence.models.Knot sourceKnot = intent.getSourceKnotId() != null ? storyController.findKnotById(intent.getSourceKnotId()) : null;
        if (intent.getSourceKnotId() != null && sourceKnot == null) {
            return createBadRequest(String.format("Invalid source knot id %s", intent.getSourceKnotId()));
        }

        fi.metatavu.metamind.persistence.models.Knot targetKnot = storyController.findKnotById(intent.getTargetKnotId());
        if (targetKnot == null) {
            return createBadRequest(String.format("Invalid target knot id %s", intent.getTargetKnotId()));
        }

        fi.metatavu.metamind.persistence.models.Intent foundIntent = storyController.findIntentById(intentId);

        Boolean global = intent.getGlobal();
        String quickResponse = intent.getQuickResponse();
        Integer quickResponseOrder = intent.getQuickResponseOrder();
        if (quickResponseOrder == null) {
            quickResponseOrder = 0;
        }

        IntentTrainingMaterials intentTrainingMaterials = intent.getTrainingMaterials();

        TrainingMaterial intentOpenNlpDoccatMaterial = trainingMaterialController.findTrainingMaterialById(intentTrainingMaterials.getIntentOpenNlpDoccatId());
        if (intentTrainingMaterials.getIntentOpenNlpDoccatId() != null && intentOpenNlpDoccatMaterial == null) {
            return createBadRequest(String.format("Invalid training material id %s", intentTrainingMaterials.getIntentOpenNlpDoccatId()));
        }

        TrainingMaterial variableOpenNlpNerMaterial = trainingMaterialController.findTrainingMaterialById(intentTrainingMaterials.getVariableOpenNlpNerId());
        if (intentTrainingMaterials.getVariableOpenNlpNerId() != null && variableOpenNlpNerMaterial == null) {
            return createBadRequest(String.format("Invalid training material id %s", intentTrainingMaterials.getVariableOpenNlpNerId()));
        }

        TrainingMaterial intentRegexMaterial = trainingMaterialController.findTrainingMaterialById(intentTrainingMaterials.getIntentRegexId());
        if (intentTrainingMaterials.getIntentRegexId() != null && intentRegexMaterial == null) {
            return createBadRequest(String.format("Invalid training material id %s", intentTrainingMaterials.getIntentRegexId()));
        }

        TrainingMaterial variableOpenNlpRegexMaterial = trainingMaterialController.findTrainingMaterialById(intentTrainingMaterials.getVariableOpenNlpRegex());
        if (intentTrainingMaterials.getVariableOpenNlpRegex() != null && variableOpenNlpRegexMaterial == null) {
            return createBadRequest(String.format("Invalid training material id %s", intentTrainingMaterials.getVariableOpenNlpRegex()));
        }

        IntentTrainingMaterial intentOpenNlpDocatTrainingMaterial = trainingMaterialController.setIntentTrainingMaterial(foundIntent, TrainingMaterialType.INTENTOPENNLPDOCCAT, intentOpenNlpDoccatMaterial);
        IntentTrainingMaterial variableOpenNlpNerTrainingMaterial = trainingMaterialController.setIntentTrainingMaterial(foundIntent, TrainingMaterialType.VARIABLEOPENNLPNER, variableOpenNlpNerMaterial);
        IntentTrainingMaterial intentRegexTrainingMaterial = trainingMaterialController.setIntentTrainingMaterial(foundIntent, TrainingMaterialType.INTENTREGEX, intentRegexMaterial);
        IntentTrainingMaterial variableOpenNlpRegexTrainingMaterial = trainingMaterialController.setIntentTrainingMaterial(foundIntent, TrainingMaterialType.VARIABLEOPENNLPREGEX, variableOpenNlpRegexMaterial);

        return createOk(intentTranslator.translateIntent(storyController.updateIntent(foundIntent, intent.getType(), intent.getName(), sourceKnot, targetKnot, global, quickResponse, quickResponseOrder, loggedUserId),
                intentOpenNlpDocatTrainingMaterial,
                variableOpenNlpNerTrainingMaterial,
                intentRegexTrainingMaterial,
                variableOpenNlpRegexTrainingMaterial
        ));
    }

    @Override
    public Response updateKnot(UUID storyId, UUID knotId, @Valid Knot knot) {
        UUID loggedUserId = getLoggedUserId();
        if (loggedUserId == null) {
            return createUnauthorized(UNAUTHORIZED);
        }

        Double coordinateX = knot.getCoordinates() != null ? knot.getCoordinates().getX() : null;
        Double coordinateY = knot.getCoordinates() != null ? knot.getCoordinates().getY() : null;
        fi.metatavu.metamind.persistence.models.Story story = storyController.findStoryById(storyId);
        if (story == null) {
            return createBadRequest(String.format("Story %s not found", storyId));
        }

        if (!isAdmin() && isDifferentGroups(story.getCreatorId(), loggedUserId)) {
            return createForbidden(STORY_GROUP_ERROR);
        }

        fi.metatavu.metamind.persistence.models.Knot foundKnot = storyController.findKnotById(knotId);
        if (foundKnot == null) {
            return createBadRequest(String.format("Knot %s not found", knotId));
        }

        if (!storyController.isKnotFromStory(foundKnot, story)) {
            return createBadRequest(String.format("Knot %s is not from the story %s", foundKnot.getId(), story.getId()));
        }

        return createOk(knotTranslator.translateKnot(storyController.updateKnot(foundKnot, knot.getType(), knot.getTokenizer(), knot.getName(), knot.getContent(), knot.getHint(), loggedUserId, coordinateX, coordinateY, knot.getScope())));
    }

    @Override
    public Response updateStory(UUID storyId, @Valid fi.metatavu.metamind.api.spec.model.Story story) {
        UUID loggedUserId = getLoggedUserId();
        if (loggedUserId == null) {
            return createUnauthorized(UNAUTHORIZED);
        }

        Locale locale = LocaleUtils.toLocale(story.getLocale());

        fi.metatavu.metamind.persistence.models.Story foundStory = storyController.findStoryById(storyId);
        if (foundStory == null) {
            return createBadRequest(String.format("Story %s not found", storyId));
        }

        if (!isAdmin() && isDifferentGroups(foundStory.getCreatorId(), loggedUserId)) {
            return createForbidden(STORY_GROUP_ERROR);
        }

        return createOk(storyTranslator.translateStory(storyController.updateStory(foundStory, locale, story.getName(), story.getDafaultHint(), loggedUserId)));
    }

    @Override
    public Response updateVariable(UUID storyId, UUID variableId, @Valid Variable variable) {
        UUID loggedUserId = getLoggedUserId();
        if (loggedUserId == null) {
            return createUnauthorized(UNAUTHORIZED);
        }

        fi.metatavu.metamind.persistence.models.Variable foundVasriable = storyController.findVariableById(variableId);
        if (foundVasriable == null) {
            return createBadRequest(String.format("Variable %s not found", variableId));
        }

        fi.metatavu.metamind.persistence.models.Story story = storyController.findStoryById(storyId);
        if (story == null) {
            return createBadRequest(String.format("Story %s not found", storyId));
        }

        if (!isAdmin() && isDifferentGroups(story.getCreatorId(), loggedUserId)) {
            return createForbidden(STORY_GROUP_ERROR);
        }

        if (!storyController.isVariableFromStory(foundVasriable, story)) {
            return createBadRequest(String.format("Variable %s is not from the story %s", foundVasriable.getId(), story.getId()));
        }

        return createOk(variableTranslator.translateVariable(storyController.updateVariable(foundVasriable, variable.getName(), variable.getType(), variable.getValidationScript(), loggedUserId)));
    }

    @Override
    public Response exportStory(UUID storyId) {
        UUID loggedUserId = getLoggedUserId();
        if (loggedUserId == null) {
            return createUnauthorized(UNAUTHORIZED);
        }

        Story storyToExport = storyController.findStoryById(storyId);

        if (storyToExport == null) {
            return createBadRequest(String.format("Story %s not found", storyId));
        }

        if (!isAdmin() && isDifferentGroups(storyToExport.getCreatorId(), loggedUserId)) {
            return createForbidden(STORY_GROUP_ERROR);
        }

        ExportedStory exportedStory = storyController.exportStory(storyToExport);

        return createOk(exportedStory);
    }


    @Override
    public Response createTrainingMaterial(fi.metatavu.metamind.api.spec.model.TrainingMaterial body) {
        UUID loggedUserId = getLoggedUserId();
        if (loggedUserId == null) {
            return createUnauthorized(UNAUTHORIZED);
        }

        UUID storyId = body.getStoryId();
        TrainingMaterialType type = body.getType();
        TrainingMaterialVisibility visibility = body.getVisibility();

        fi.metatavu.metamind.persistence.models.Story story = storyId != null ? storyController.findStoryById(storyId) : null;
        if (storyId != null) {
            if (story == null) {
                return createBadRequest(String.format("Story %s not found", storyId));
            }

            if (!isAdmin() && isDifferentGroups(story.getCreatorId(), loggedUserId)){
                return createForbidden(SCRIPT_GROUP_ERROR);
            }
        }
        TrainingMaterial createdMaterial = trainingMaterialController.createTrainingMaterial(type, body.getName(), body.getText(), story, loggedUserId, visibility);
        return createOk(trainingMaterialTranslator.translateTrainingMaterial(createdMaterial));
    }

    @Override
    public Response deleteTrainingMaterial(UUID trainingMaterialId) {
        UUID loggedUserId = getLoggedUserId();
        if (loggedUserId == null) {
            return createUnauthorized(UNAUTHORIZED);
        }

        fi.metatavu.metamind.persistence.models.TrainingMaterial trainingMaterial = trainingMaterialController.findTrainingMaterialById(trainingMaterialId);
        if (trainingMaterial == null) {
            return createNotFound(String.format("Training material %s not found", trainingMaterialId));
        }

        if (!isAdmin() && isDifferentGroups(trainingMaterial.getCreatorId(), loggedUserId)) {
            return createForbidden(TRAINING_MATERIAL_GROUP_ERROR);
        }

        trainingMaterialController.deleteTrainingMaterial(trainingMaterial);

        return createNoContent();
    }

    @Override
    public Response findTrainingMaterial(UUID trainingMaterialId) {
        UUID loggedUserId = getLoggedUserId();
        if (loggedUserId == null) {
            return createUnauthorized(UNAUTHORIZED);
        }
        fi.metatavu.metamind.persistence.models.TrainingMaterial trainingMaterial = trainingMaterialController.findTrainingMaterialById(trainingMaterialId);
        if (trainingMaterial == null) {
            return createNotFound(String.format("Training material %s not found", trainingMaterialId));
        }

        if (!isAdmin() && isDifferentGroups(trainingMaterial.getCreatorId(), loggedUserId)) {
            return createForbidden(TRAINING_MATERIAL_GROUP_ERROR);
        }

        return createOk(trainingMaterialTranslator.translateTrainingMaterial(trainingMaterial));
    }

    @Override
    public Response listTrainingMaterials(UUID storyId, TrainingMaterialType type, TrainingMaterialVisibility visibility) {
        UUID loggedUserId = getLoggedUserId();
        if (loggedUserId == null) {
            return createUnauthorized(UNAUTHORIZED);
        }

        fi.metatavu.metamind.persistence.models.Story story = storyId != null ? storyController.findStoryById(storyId) : null;
        if (storyId != null) {
            if (story == null) {
                return createBadRequest(String.format("Story %s not found", storyId));
            }

            if (!isAdmin() && isDifferentGroups(story.getCreatorId(), loggedUserId)){
                return createForbidden(STORY_GROUP_ERROR);
            }
        }

        List<UUID> allowedCreatorIdsFilter = null;
        if (!isAdmin()) {
            allowedCreatorIdsFilter = keycloakController.listSameGroupUsers(loggedUserId);
        }

        return createOk(trainingMaterialController.listTrainingMaterials(story, type, visibility, allowedCreatorIdsFilter).stream()
                .map(trainingMaterialTranslator::translateTrainingMaterial)
                .collect(Collectors.toList()));
    }

    @Override
    public Response updateTrainingMaterial(UUID trainingMaterialId, @Valid fi.metatavu.metamind.api.spec.model.TrainingMaterial trainingMaterial) {
        UUID loggedUserId = getLoggedUserId();
        if (loggedUserId == null) {
            return createUnauthorized(UNAUTHORIZED);
        }

        fi.metatavu.metamind.persistence.models.TrainingMaterial foundTrainingMaterial = trainingMaterialController.findTrainingMaterialById(trainingMaterialId);
        if (foundTrainingMaterial == null) {
            return createNotFound(String.format("Training material %s not found", trainingMaterialId));
        }

        if (!isAdmin() && isDifferentGroups(foundTrainingMaterial.getCreatorId(), loggedUserId)) {
            return createForbidden(TRAINING_MATERIAL_GROUP_ERROR);
        }

        return createOk(trainingMaterialTranslator.translateTrainingMaterial(trainingMaterialController.updateTrainingMaterial(foundTrainingMaterial, trainingMaterial.getName(), trainingMaterial.getText(), loggedUserId, trainingMaterial.getVisibility())));
    }

}
