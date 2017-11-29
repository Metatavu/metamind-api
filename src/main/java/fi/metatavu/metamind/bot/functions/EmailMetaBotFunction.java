package fi.metatavu.metamind.bot.functions;

import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import com.bladecoder.ink.runtime.Story;
import com.rabidgremlin.mutters.bot.ink.CurrentResponse;
import com.rabidgremlin.mutters.core.IntentMatch;
import com.rabidgremlin.mutters.core.session.Session;

import fi.metatavu.metamind.email.EmailProvider;

/**
 * Metabot function for sending emails
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class EmailMetaBotFunction extends AbstractFreemarkerMetaBotFunction {

  @Inject
  private Logger logger;

  @Inject
  private EmailProvider emailProvider;

  @Override
  public String getFunctionName() {
    return "SEND_EMAIL";
  }
  
  @Override
  public void execute(CurrentResponse currentResponse, Session session, IntentMatch intentMatch, Story story, String paramString) {
    Map<String, String> params = getParsedParam(paramString);
    
    String contentString = params.get("content");
    String contentTemplate = params.get("content-template");
    String subjectString = params.get("subject");
    String subjectTemplate = params.get("subject-template");
    String toEmail = params.get("address");
    String content = getOptionallyTemplatedString(contentString, contentTemplate, session, intentMatch);
    String subject = getOptionallyTemplatedString(subjectString, subjectTemplate, session, intentMatch);
    
    if (StringUtils.isBlank(toEmail)) {
      logger.error("Could not send mail without recipient email");
      return;
    }

    if (StringUtils.isBlank(subject)) {
      logger.error("Could not send mail without subject");
      return;
    }

    if (StringUtils.isBlank(content)) {
      logger.error("Could not send mail without content");
      return;
    }
    
    emailProvider.sendMail(toEmail, subject, content);
  }

  private String getOptionallyTemplatedString(String string, String template, Session session, IntentMatch intentMatch) {
    if (StringUtils.isNotBlank(string)) {
      return string;
    }
    
    if (StringUtils.isNotBlank(template)) {
      return getRenderedText(template, session, intentMatch);
    }
    
    return null;
  }

}
