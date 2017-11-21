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

  public Message create(String content, String externalId, String hint, Session session) {
    Message message = new Message();
    message.setContent(content);
    message.setExternalId(externalId);
    message.setHint(hint);
    message.setSession(session);
    return persist(message);
  }
  
}
