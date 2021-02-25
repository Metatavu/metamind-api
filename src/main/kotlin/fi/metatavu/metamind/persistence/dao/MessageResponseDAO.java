package fi.metatavu.metamind.persistence.dao;

import fi.metatavu.metamind.persistence.models.Message;
import fi.metatavu.metamind.persistence.models.MessageResponse;
import fi.metatavu.metamind.persistence.models.MessageResponse_;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

/**
 * DAO for message responses
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class MessageResponseDAO extends AbstractDAO<MessageResponse> {

  /**
   * Creates new message response
   * 
   * @param message Message that message response is related to
   * @param text message response as a string
   * @return created message response
   */
  public MessageResponse create(Message message, String text) {
    MessageResponse messageResponseEntity = new MessageResponse();
    messageResponseEntity.setMessage(message);
    messageResponseEntity.setText(text);
    return persist(messageResponseEntity);
  }
  
  /**
   * Lists message responses by message
   * 
   * @param message message
   * @return List of message responses
   */
  public List<MessageResponse> listByMessage(Message message) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<MessageResponse> criteria = criteriaBuilder.createQuery(MessageResponse.class);
    Root<MessageResponse> root = criteria.from(MessageResponse.class);

    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(MessageResponse_.message), message));
    
    TypedQuery<MessageResponse> query = entityManager.createQuery(criteria);
    
    return query.getResultList();
  }
  
}
