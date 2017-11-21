package fi.metatavu.metamind.persistence.dao;

import javax.enterprise.context.ApplicationScoped;

import fi.metatavu.metamind.persistence.models.Message;
import fi.metatavu.metamind.persistence.models.Session;

/**
 * DAO for messages
 * 
 * @author Heikki Kurhinen
 *
 */
@ApplicationScoped
public class MessageDAO extends AbstractDAO<Message> {

  /**
   * Creates new message
   * 
   * @param content user's message content
   * @param externalId external id
   * @param session session
   * @return created message
   */
  public Message create(String content, String externalId, Session session) {
    Message message = new Message();
    message.setContent(content);
    message.setExternalId(externalId);
    message.setSession(session);
    return persist(message);
  }

  /**
   * Updates response
   *
   * @param response response
   * @return updated message
   */
  public Message updateResponse(Message message, String response) {
    message.setResponse(response);
    return persist(message);
  }

  /**
   * Updates hint
   *
   * @param hint hint
   * @return updated message
   */
  public Message updateHint(Message message, String hint) {
    message.setHint(hint);
    return persist(message);
  }

}
