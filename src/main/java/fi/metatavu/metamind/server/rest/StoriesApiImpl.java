package fi.metatavu.metamind.server.rest;

import java.util.UUID;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import fi.metatavu.metamind.bot.BotController;
import fi.metatavu.metamind.messages.MessageController;
import fi.metatavu.metamind.persistence.models.Session;
import fi.metatavu.metamind.rest.api.StoriesApi;
import fi.metatavu.metamind.rest.model.Intent;
import fi.metatavu.metamind.rest.model.Knot;
import fi.metatavu.metamind.rest.model.Story;
import fi.metatavu.metamind.rest.model.Variable;
import fi.metatavu.metamind.server.rest.translation.MessageTranslator;
import fi.metatavu.metamind.sessions.SessionConsts;
import fi.metatavu.metamind.sessions.SessionController;

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
  private BotController botController;

  @Inject
  private MessageTranslator messageTranslator;
  
  @Override
  public Response createIntent(Intent body, UUID storyId) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Response createKnot(Knot body, UUID storyId) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Response createMessage(fi.metatavu.metamind.rest.model.Message body, UUID storyId) {
    return null;
//
//    Session session = sessionController.findSession(body.getSessionId());
//    if (session == null) {
//      return respondBadRequest("Invalid session id");
//    }
//    
//    String content = StringUtils.trim(body.getContent());
//    if (StringUtils.isBlank(content)) {
//      return respondBadRequest("Missing content");
//    }
//    
//    fi.metatavu.metamind.persistence.models.Message message = messageController.createMessage(content, session);
//    if (message == null) {
//      return respondInternalServerError("Could not create new message");
//    }
//    
//    com.rabidgremlin.mutters.core.session.Session botSession = botController.getBotSession(session);
//    if (botSession == null) {
//      return respondInternalServerError("Could not create bot session");
//    }
//    
//    MetamindBot metamind = botController.getBotInstance(session.getStory());
//    Context context = new Context();
//    context.setLocale(LocaleUtils.toLocale(session.getLocale()));
//    context.setTimeZone(TimeZone.getTimeZone(session.getTimeZone()));
//    
//    try {
//      BotResponse botResponse = metamind.respond(botSession, context, message.getContent());
//      Map<String, Object> debugValues = botResponse.getDebugValues();
//      String matchedIntent = (String) debugValues.get(MetamindBot.DK_MATCHED_INTENT);
//      Double responseScore = getMatchingScore(debugValues);
//      
//      fi.metatavu.metamind.persistence.models.Message updatedMessage = messageController.updateMessage(message, botResponse.getHint(), botResponse.getResponse(), matchedIntent, responseScore);
//      List<QuickResponse> quickResponses = messageController.updateMessageQuickResponses(updatedMessage, botResponse.getQuickReplies());
//      
//      byte[] updatedBotSession = botController.serializeBotSession(botSession);
//      sessionController.updateSessionState(session, updatedBotSession);
//      
//      return respondOk(messageTranslator.translateMessage(updatedMessage, quickResponses));
//
//    } catch (BotException e) {
//      return respondInternalServerError(e);
//    }
  }

  @Override
  public Response createSession(fi.metatavu.metamind.rest.model.Session body, UUID storyId) {
    return null;
//    com.rabidgremlin.mutters.core.session.Session botSession = botController.createBotSession();
//    if (botSession == null) {
//      return respondInternalServerError("Could not initialize bot session");
//    }
//    
//    Story story = storyController.findStoryByName(body.getStory());
//    if (story == null) {
//      return respondBadRequest("Invalid story parameter");
//    }
//    
//    fi.metatavu.metamind.persistence.models.Session session = sessionController.createSession(story, body.getLocale(), body.getTimeZone(), body.getVisitor(), new byte[0]);
//    if (session == null) {
//      return respondInternalServerError("Could not initialize session");
//    }
//    
//    botSession.setLongTermAttribute(SessionConsts.METAMIND_SESSION_ID_ATTRIBUTE, session.getId());
//    
//    byte[] sessionData = botController.serializeBotSession(botSession);
//    if (sessionData == null) {
//      return respondInternalServerError("Failed to serialize bot session");
//    }
//    
//    sessionController.updateSessionState(session, sessionData);
//    
//    return respondOk(sessionTranslator.translateSession(session));
  }

  @Override
  public Response createStory(Story body) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Response createVariable(Variable body, UUID storyId) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Response deleteIntent(UUID storyId, UUID intentId) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Response deleteKnot(UUID storyId, UUID knotId) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Response deleteStory(UUID storyId) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Response deleteVariable(UUID storyId, UUID variableId) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Response findIntent(UUID storyId, UUID intentId) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Response findKnot(UUID storyId, UUID knotId) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Response findStory(UUID storyId) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Response findVariable(UUID storyId, UUID variableId) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Response listIntents(UUID storyId) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Response listKnots(UUID storyId) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Response listStories() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Response listVariables(UUID storyId) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Response updateIntent(Intent body, UUID storyId, UUID intentId) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Response updateKnot(Knot body, UUID storyId, UUID knotId) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Response updateStory(Story body, UUID storyId) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Response updateVariable(Variable body, UUID storyId, UUID variableId) {
    // TODO Auto-generated method stub
    return null;
  }

//  @SuppressWarnings("unchecked")
//  private Double getMatchingScore(Map<String, Object> debugValues) {
//    Double result = 0d;
//    Map<Double, Set<String>> matchingScores = (Map<Double, Set<String>>) debugValues.get(MetamindBot.DK_INTENT_MATCHING_SCORES);
//    if (matchingScores == null) {
//      return debugValues.get(MetamindBot.DK_MATCHED_INTENT) == null ? 0d : 1d;
//    }
//    
//    for (Entry<Double, Set<String>> matchingScore : matchingScores.entrySet()) {
//      result = Math.max(result, matchingScore.getKey());
//    }
//    
//    return result;
//  }

}
