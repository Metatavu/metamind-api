package fi.metatavu.metamind.persistence.dao;

import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.metatavu.metamind.persistence.models.*;

/**
 * DAO class for Message
 * 
 * @author Antti Leppä
 */
public class MessageDAO extends AbstractDAO<Message> {

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
     * @param lastModifierId last modifier's id
     * @return created message
     */
    public Message create(UUID id, Session session, String content, String hint, Double confidence, Knot sourceKnot, Intent matchedIntent, UUID creatorId, UUID lastModifierId) {
      Message message = new Message();
      message.setSession(session);
      message.setContent(content);
      message.setHint(hint);
      message.setConfidence(confidence);
      message.setSourceKnot(sourceKnot);
      message.setMatchedIntent(matchedIntent);
      message.setId(id);
      message.setCreatorId(creatorId);
      message.setLastModifierId(lastModifierId);
      return persist(message);
    }

    /**
     * Lists messages by session
     * 
     * @param session session
     * @return List of messages
     */
    public List<Message> listBySession(Session session) {
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
     * Updates session
     *
     * @param session session
     * @param lastModifierId last modifier's id
     * @return updated message
     */
    public Message updateSession(Message message, Session session, UUID lastModifierId) {
      message.setLastModifierId(lastModifierId);
      message.setSession(session);
      return persist(message);
    }

    /**
     * Updates content
     *
     * @param content content
     * @param lastModifierId last modifier's id
     * @return updated message
     */
    public Message updateContent(Message message, String content, UUID lastModifierId) {
      message.setLastModifierId(lastModifierId);
      message.setContent(content);
      return persist(message);
    }

    /**
     * Updates hint
     *
     * @param hint hint
     * @param lastModifierId last modifier's id
     * @return updated message
     */
    public Message updateHint(Message message, String hint, UUID lastModifierId) {
      message.setLastModifierId(lastModifierId);
      message.setHint(hint);
      return persist(message);
    }

    /**
     * Updates confidence
     *
     * @param confidence confidence
     * @param lastModifierId last modifier's id
     * @return updated message
     */
    public Message updateConfidence(Message message, Double confidence, UUID lastModifierId) {
      message.setLastModifierId(lastModifierId);
      message.setConfidence(confidence);
      return persist(message);
    }

    /**
     * Updates sourceKnot
     *
     * @param sourceKnot sourceKnot
     * @param lastModifierId last modifier's id
     * @return updated message
     */
    public Message updateSourceKnot(Message message, Knot sourceKnot, UUID lastModifierId) {
      message.setLastModifierId(lastModifierId);
      message.setSourceKnot(sourceKnot);
      return persist(message);
    }

    /**
     * Updates matchedIntent
     *
     * @param matchedIntent matchedIntent
     * @param lastModifierId last modifier's id
     * @return updated message
     */
    public Message updateMatchedIntent(Message message, Intent matchedIntent, UUID lastModifierId) {
      message.setLastModifierId(lastModifierId);
      message.setMatchedIntent(matchedIntent);
      return persist(message);
    }

}