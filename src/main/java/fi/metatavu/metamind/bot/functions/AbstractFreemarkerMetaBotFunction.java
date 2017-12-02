package fi.metatavu.metamind.bot.functions;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;

import com.rabidgremlin.mutters.bot.ink.SessionUtils;
import com.rabidgremlin.mutters.core.IntentMatch;
import com.rabidgremlin.mutters.core.Slot;
import com.rabidgremlin.mutters.core.SlotMatch;
import com.rabidgremlin.mutters.core.session.Session;

import fi.metatavu.metamind.freemarker.FreemarkerRenderer;
import fi.metatavu.metamind.messages.MessageController;
import fi.metatavu.metamind.persistence.models.Message;
import fi.metatavu.metamind.sessions.SessionController;

/**
 * Abstract base class for freemarker metabot functions
 * 
 * @author Antti Leppä
 */
public abstract class AbstractFreemarkerMetaBotFunction extends AbstractMetaBotFunction {

  @Inject
  private MessageController messageController;

  @Inject
  private SessionController sessionController;
  
  @Inject
  private FreemarkerRenderer freemarkerRenderer;
  
  /**
   * Renders a freemarker template with session data
   * 
   * @param templateName name of the template
   * @param session session
   * @param intentMatch intent match
   * @return rendered template
   */
  protected String getRenderedText(String templateName, Session session, IntentMatch intentMatch, boolean includeSessionAttributes, boolean includeMessages) {
    return freemarkerRenderer.render(templateName, getModel(session, intentMatch, includeSessionAttributes, includeMessages), new Locale("fi"));
  }

  private FreemarkerModel getModel(Session botSession, IntentMatch intentMatch, boolean includeSessionAttributes, boolean includeMessages) {
    fi.metatavu.metamind.persistence.models.Session metamindSession = sessionController.findSessionFromBotSession(botSession);
    Map<String, Object> sessionAttributes = includeSessionAttributes ? getSessionAttributes(botSession, intentMatch) : Collections.emptyMap();
    List<Message> sessionMessages = includeMessages ? getSessionMessages(metamindSession) : Collections.emptyList();
    return new FreemarkerModel(metamindSession, sessionAttributes, sessionMessages);
  }

  private Map<String, Object> getSessionAttributes(Session session, IntentMatch intentMatch) {
    Map<String, Object> result = new HashMap<>();
    
    Map<Slot, SlotMatch> slotMatches = intentMatch.getSlotMatches();
    for (Slot slot : slotMatches.keySet()) {
      String slotName = slot.getName();
      Object value = SessionUtils.getStringFromSlotOrSession(intentMatch, session, slotName, null);
      result.put(slotName.toLowerCase(), value);
    }
    
    return result;
  }
  
  private List<Message> getSessionMessages(fi.metatavu.metamind.persistence.models.Session metamindSession) {
    return messageController.listSessionMessages(metamindSession);
  }
  
  public class FreemarkerModel {
    
    private Map<String, Object> sessionAttributes;
    private List<Message> sessionMessages;
    public fi.metatavu.metamind.persistence.models.Session metamindSession;
    
    public FreemarkerModel(fi.metatavu.metamind.persistence.models.Session metamindSession, Map<String, Object> sessionAttributes, List<Message> sessionMessages) {
      super();
      this.metamindSession = metamindSession;
      this.sessionAttributes = sessionAttributes;
      this.sessionMessages = sessionMessages;
    }
    
    public fi.metatavu.metamind.persistence.models.Session getMetamindSession() {
      return metamindSession;
    }

    public Map<String, Object> getSessionAttributes() {
      return sessionAttributes;
    }
    
    public List<Message> getSessionMessages() {
      return sessionMessages;
    }
    
  }

}
