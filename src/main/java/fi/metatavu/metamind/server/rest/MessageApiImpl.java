package fi.metatavu.metamind.server.rest;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;

import com.rabidgremlin.mutters.core.Context;
import com.rabidgremlin.mutters.core.bot.BotResponse;

import fi.metatavu.metamind.bot.BotController;
import fi.metatavu.metamind.bot.MetamindBot;
import fi.metatavu.metamind.messages.MessageController;
import fi.metatavu.metamind.persistence.models.QuickResponse;
import fi.metatavu.metamind.persistence.models.Session;
import fi.metatavu.metamind.server.rest.model.Message;
import fi.metatavu.metamind.server.rest.translation.MessageTranslator;
import fi.metatavu.metamind.sessions.SessionController;

/**
 * REST - endpoints for messages
 * 
 * @author Heikki Kurhinen
 *
 */
@Path("/messages")
@Consumes({ "application/json;charset=utf-8" })
@Produces({ "application/json;charset=utf-8" })
@RequestScoped
@Stateful
public class MessageApiImpl extends AbstractRestApi implements MessagesApi {

  @Inject
  private SessionController sessionController;

  @Inject
  private MessageController messageController;

  @Inject
  private BotController botController;

  @Inject
  private MessageTranslator messageTranslator;
  
  @Override
  public Response createMessage(Message body) throws Exception {
    Session session = sessionController.findSession(body.getSessionId());
    if (session == null) {
      return respondBadRequest("Invalid session id");
    }
    
    if (StringUtils.isBlank(body.getContent())) {
      return respondBadRequest("Missing content");
    }
    
    fi.metatavu.metamind.persistence.models.Message message = messageController.createMessage(body.getContent(), session);
    if (message == null) {
      return respondInternalServerError("Could not create new message");
    }
    
    com.rabidgremlin.mutters.core.session.Session botSession = botController.getBotSession(session);
    if (botSession == null) {
      return respondInternalServerError("Could not create bot session");
    }
    
    MetamindBot metamind = botController.getBotInstance(session.getStory());
    Context context = new Context();
    context.setLocale(LocaleUtils.toLocale(session.getLocale()));
    context.setTimeZone(TimeZone.getTimeZone(session.getTimeZone()));
    
    BotResponse botResponse = metamind.respond(botSession, context, message.getContent());
    Map<String, Object> debugValues = botResponse.getDebugValues();
    String matchedIntent = (String) debugValues.get(MetamindBot.DK_MATCHED_INTENT);
    Double responseScore = getMatchingScore(debugValues);
    
    fi.metatavu.metamind.persistence.models.Message updatedMessage = messageController.updateMessage(message, botResponse.getHint(), botResponse.getResponse(), matchedIntent, responseScore);
    List<QuickResponse> quickResponses = messageController.updateMessageQuickResponses(updatedMessage, botResponse.getQuickReplies());
    
    byte[] updatedBotSession = botController.serializeBotSession(botSession);
    sessionController.updateSessionState(session, updatedBotSession);
    
    return respondOk(messageTranslator.translateMessage(updatedMessage, quickResponses));
  }
  
  @SuppressWarnings("unchecked")
  private Double getMatchingScore(Map<String, Object> debugValues) {
    Double result = 0d;
    Map<Double, Set<String>> matchingScores = (Map<Double, Set<String>>) debugValues.get(MetamindBot.DK_INTENT_MATCHING_SCORES);
    if (matchingScores == null) {
      return debugValues.get(MetamindBot.DK_MATCHED_INTENT) == null ? 0d : 1d;
    }
    
    for (Entry<Double, Set<String>> matchingScore : matchingScores.entrySet()) {
      result = Math.max(result, matchingScore.getKey());
    }
    
    return result;
  }

}
