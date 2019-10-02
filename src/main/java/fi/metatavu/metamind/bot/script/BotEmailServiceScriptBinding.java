package fi.metatavu.metamind.bot.script;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.metamind.email.EmailProvider;

/**
 * Bot script binding for email service
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class BotEmailServiceScriptBinding {

  @Inject
  private EmailProvider emailProvider;
  
  /**
   * Sends an email
   * 
   * @param toEmail recipient address
   * @param subject subject
   * @param content content
   */
  public void sendEmail(String toEmail, String subject, String content) {
    emailProvider.sendMail(toEmail, subject, content);
  }
  
}
