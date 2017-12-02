package fi.metatavu.metamind.messages;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.metamind.persistence.dao.MessageDAO;
import fi.metatavu.metamind.persistence.dao.QuickResponseDAO;
import fi.metatavu.metamind.persistence.models.Message;
import fi.metatavu.metamind.persistence.models.QuickResponse;
import fi.metatavu.metamind.persistence.models.Session;

@ApplicationScoped
public class MessageController {

  @Inject
  private MessageDAO messageDAO;

  @Inject
  private QuickResponseDAO quickResponseDAO;
  
  /**
   * Creates new message
   * 
   * @param content message content
   * @param session message session
   * @return created message
   */
  public Message createMessage(String content, Session session) {
    return messageDAO.create(content, UUID.randomUUID().toString(), session);
  }

  /**
   * Lists all messages within a session
   * 
   * @param session session
   * @return all messages within a session
   */
  public List<Message> listSessionMessages(fi.metatavu.metamind.persistence.models.Session session) {
    return messageDAO.listByMessage(session); 
  }
  
  /**
   * Updates message with bot's responses
   * 
   * @param message message
   * @param hint hint
   * @param response response
   * @return updated message
   */
  public Message updateMessage(fi.metatavu.metamind.persistence.models.Message message, String hint, String response) {
    messageDAO.updateHint(message, hint);
    messageDAO.updateResponse(message, response);
    return message;
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
