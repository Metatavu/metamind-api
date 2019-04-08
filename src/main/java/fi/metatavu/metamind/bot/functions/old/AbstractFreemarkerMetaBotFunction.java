package fi.metatavu.metamind.bot.functions.old;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;

import org.jsoup.Jsoup;
import org.jsoup.examples.HtmlToPlainText;

import com.bladecoder.ink.runtime.Story;
import com.bladecoder.ink.runtime.VariablesState;
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
   * @param story story
   * @return rendered template
   */
  protected String getRenderedText(String templateName, Session session, Story story, boolean includeSessionAttributes, boolean includeMessages) {
    return freemarkerRenderer.render(templateName, getModel(session, story, includeSessionAttributes, includeMessages), new Locale("fi"));
  }

  private FreemarkerModel getModel(Session botSession, Story story, boolean includeSessionAttributes, boolean includeMessages) {
    fi.metatavu.metamind.persistence.models.Session metamindSession = sessionController.findSessionFromBotSession(botSession);
    Map<String, Object> sessionAttributes = includeSessionAttributes ? getSessionAttributes(story) : Collections.emptyMap();
    List<FreemarkerMessageModel> sessionMessages = includeMessages ? getSessionMessages(metamindSession) : Collections.emptyList();
    return new FreemarkerModel(metamindSession, sessionAttributes, sessionMessages);
  }

  private Map<String, Object> getSessionAttributes(Story story) {
    Map<String, Object> result = new HashMap<>();
    
    VariablesState variablesState = story.getVariablesState();
    for (String variableName : variablesState) {
      Object value = variablesState.get(variableName);
      result.put(variableName.toLowerCase(), value);
    }

    return result;
  }
  
  private List<FreemarkerMessageModel> getSessionMessages(fi.metatavu.metamind.persistence.models.Session metamindSession) {
    List<Message> sessionMessages = messageController.listSessionMessages(metamindSession);
    
    List<FreemarkerMessageModel> result = new ArrayList<>(sessionMessages.size());
    for (Message sessionMessage : sessionMessages) {
      String response = messageController.getMessageResponse(sessionMessage);
      
      result.add(new FreemarkerMessageModel(getDate(sessionMessage.getCreatedAt()), 
        stripHtml(sessionMessage.getContent()), 
        stripHtml(response)));
    }
    
    return result;
  }
  
  private String stripHtml(String content) {
    if (content == null) {
      return null;
    }
    
    return new HtmlToPlainText().getPlainText(Jsoup.parse(content));    
  }

  private Date getDate(OffsetDateTime offsetDateTime) {
    return Date.from(offsetDateTime.toInstant());
  }

  public class FreemarkerModel {
    
    private Map<String, Object> sessionAttributes;
    private List<FreemarkerMessageModel> sessionMessages;
    private fi.metatavu.metamind.persistence.models.Session metamindSession;
    
    public FreemarkerModel(fi.metatavu.metamind.persistence.models.Session metamindSession, Map<String, Object> sessionAttributes, List<FreemarkerMessageModel> sessionMessages) {
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
    
    public List<FreemarkerMessageModel> getSessionMessages() {
      return sessionMessages;
    }
    
  }
  
  public class FreemarkerMessageModel {
    
    private Date created;
    private String content;
    private String response;
    
    public FreemarkerMessageModel(Date created, String content, String response) {
      super();
      this.created = created;
      this.content = content;
      this.response = response;
    }

    public String getContent() {
      return content;
    }
    
    public Date getCreated() {
      return created;
    }
    
    public String getResponse() {
      return response;
    }
    
  }

}