package fi.metatavu.metamind.server.rest.translation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;

import fi.metatavu.metamind.persistence.models.QuickResponse;
import fi.metatavu.metamind.server.rest.model.Message;

@ApplicationScoped
public class MessageTranslator {

  /**
   * Translates JPA message into REST message
   * 
   * @param jpaMessage JPA message
   * @param quickResponses JPA quick responses
   * @return REST message
   */
  public Message translateMessage(fi.metatavu.metamind.persistence.models.Message jpaMessage, List<QuickResponse> quickResponses) {
    if (jpaMessage == null) {
      return null;
    }

    Message result = new Message();
    result.setContent(jpaMessage.getContent());
    result.setCreated(jpaMessage.getCreated());
    result.setHint(jpaMessage.getHint());
    result.setId(UUID.fromString(jpaMessage.getExternalId()));
    result.setQuickResponses(translateQuickResponses(quickResponses));
    result.setResponse(jpaMessage.getResponse());
    result.setSessionId(UUID.fromString(jpaMessage.getSession().getExternalId()));
    
    return result;
  }

  private List<String> translateQuickResponses(List<QuickResponse> quickResponses) {
    if (quickResponses == null || quickResponses.isEmpty()) {
      return Collections.emptyList();
    }

    List<String> result = new ArrayList<>(quickResponses.size());
    for (QuickResponse quickResponse : quickResponses) {
      result.add(quickResponse.getOption());
    }
    
    return result;
  }
  
}
