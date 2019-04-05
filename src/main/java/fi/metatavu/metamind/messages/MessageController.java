package fi.metatavu.metamind.messages;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.metamind.persistence.dao.MessageDAO;
import fi.metatavu.metamind.persistence.dao.MessageResponseDAO;
import fi.metatavu.metamind.persistence.dao.QuickResponseDAO;
import fi.metatavu.metamind.persistence.models.Intent;
import fi.metatavu.metamind.persistence.models.Knot;
import fi.metatavu.metamind.persistence.models.Message;
import fi.metatavu.metamind.persistence.models.MessageResponse;
import fi.metatavu.metamind.persistence.models.QuickResponse;
import fi.metatavu.metamind.persistence.models.Session;

/**
 * Controller for messages
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class MessageController {

  @Inject
  private MessageDAO messageDAO;

  @Inject
  private QuickResponseDAO quickResponseDAO;

  @Inject
  private MessageResponseDAO messageResponseDAO;
  
  /**
   * Creates new message
   *
   * @param session session
   * @param content content
   * @param hint hint
   * @param confidence confidence
   * @param sourceKnot sourceKnot
   * @param matchedIntent matchedIntent
   * @param creatorId creator's id
   * @return created message
   */
  public Message createMessage(Session session, String content, String hint, Double confidence, Knot sourceKnot, Intent matchedIntent, UUID creatorId) {
    return messageDAO.create(UUID.randomUUID(), session, content, hint, confidence, sourceKnot, matchedIntent, creatorId, creatorId);
  }

  /**
   * Creates new message response
   * 
   * @param message message
   * @param text response text
   * @return created message response
   */
  public MessageResponse createMessageResponse(Message message, String text) {
    return messageResponseDAO.create(message, text);
  }
  
  /**
   * Lists messages by a session
   * 
   * @param session session
   * @return messages
   */
  public List<Message> listSessionMessages(Session session) {
    return messageDAO.listBySession(session);
  }
  
  /**
   * Lists message responses
   * 
   * @param message message
   * @return responses
   */
  public List<MessageResponse> listMessageResponses(Message message) {
    return messageResponseDAO.listByMessage(message);
  }
  
  /**
   * Returns message responses as a string
   * 
   * @param message message
   * @return message responses as a string
   */
  public String getMessageResponse(Message message) {
    return listMessageResponses(message).stream().map(MessageResponse::getText).collect(Collectors.joining("\n"));
  }
  
  /**
   * Updates message's quick responses
   * 
   * @param message message
   * @param quickReplies quick replies
   * @return
   */
  public List<QuickResponse> updateMessageQuickResponses(fi.metatavu.metamind.persistence.models.Message message, List<String> quickReplies) {
    if (quickReplies == null || quickReplies.isEmpty()) {
      return Collections.emptyList();
    }
    
    List<QuickResponse> result = new ArrayList<>(quickReplies.size());
    
    for (QuickResponse quickResponse : quickResponseDAO.listByMessage(message)) {
      quickResponseDAO.delete(quickResponse);
    }
    
    for (String quickReply : quickReplies) {
      result.add(quickResponseDAO.create(message, quickReply));
    }
    
    return result;
  }

}
