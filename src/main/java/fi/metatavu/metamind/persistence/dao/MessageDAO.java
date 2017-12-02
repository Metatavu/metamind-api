package fi.metatavu.metamind.persistence.dao;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.metatavu.metamind.persistence.models.Message;
import fi.metatavu.metamind.persistence.models.Message_;
import fi.metatavu.metamind.persistence.models.Session;

/**
 * DAO for messages
 * 
 * @author Heikki Kurhinen
 * @author Antti Leppä
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
   * Lists messages by session
   * 
   * @param session session
   * @return List of messages
   */
  public List<Message> listByMessage(Session session) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Message> criteria = criteriaBuilder.createQuery(Message.class);
    Root<Message> root = criteria.from(Message.class);

    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(Message_.session), session));
    
    TypedQuery<Message> query = entityManager.createQuery(criteria);
    
    return query.getResultList();
  }
  
  /**
   * Updates response
   *
   * @param message message
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
   * @param message message
   * @param hint hint
   * @return updated message
   */
  public Message updateHint(Message message, String hint) {
    message.setHint(hint);
    return persist(message);
  }

}
