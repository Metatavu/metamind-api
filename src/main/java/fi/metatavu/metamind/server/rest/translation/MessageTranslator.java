package fi.metatavu.metamind.server.rest.translation;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;

import fi.metatavu.metamind.persistence.models.MessageResponse;
import fi.metatavu.metamind.rest.model.Message;

/**
 * Translator for translating JPA message entities into REST entities
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class MessageTranslator {

  /**
   * Translates JPA message into REST message
   * 
   * @param jpaMessage JPA message
   * @param quickResponses JPA quick responses
   * @param messageResponses JPA message responses
   * @return REST message
   */
  public Message translateMessage(fi.metatavu.metamind.persistence.models.Message jpaMessage, List<String> quickResponses, List<MessageResponse> messageResponses) {
    if (jpaMessage == null) {
      return null;
    }

    Message result = new Message();
    result.setConfidence(jpaMessage.getConfidence());
    result.setContent(jpaMessage.getContent());
    result.setCreatedAt(jpaMessage.getCreatedAt());
    result.setHint(jpaMessage.getHint());
    result.setId(jpaMessage.getId());
    result.setMatchedIntentId(jpaMessage.getMatchedIntent() != null ? jpaMessage.getMatchedIntent().getId() : null);
    result.setModifiedAt(jpaMessage.getModifiedAt());
    result.setQuickResponses(quickResponses);
    result.setResponse(translateMessageResponses(messageResponses));
    result.setSessionId(jpaMessage.getSession() != null ? jpaMessage.getSession().getId() : null);
    result.setSourceKnotId(jpaMessage.getSourceKnot() != null ? jpaMessage.getSourceKnot().getId() : null);
        
    return result;
  }

  /**
   * Translates message responses
   * 
   * @param messageResponses message responses
   * @return translated responses
   */
  private List<String> translateMessageResponses(List<MessageResponse> messageResponses) {
    if (messageResponses == null || messageResponses.isEmpty()) {
      return Collections.emptyList();
    }
    
    return messageResponses.stream().map(MessageResponse::getText).collect(Collectors.toList());
  }
  
}
