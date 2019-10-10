package fi.metatavu.metamind.bot.script;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import fi.metatavu.metamind.email.EmailProvider;
import fi.metatavu.metamind.email.mailgun.MailgunConsts;
import fi.metatavu.metamind.settings.SystemSettingController;

/**
 * Bot script binding for email service
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class BotEmailServiceScriptBinding {

  @Inject
  private Logger logger;

  @Inject
  private EmailProvider emailProvider;

  @Inject
  private SystemSettingController systemSettingController;
  
  /**
   * Sends an email using default email sender
   * 
   * @param toEmail recipient address
   * @param subject subject
   * @param content content
   */
  public void sendEmail(String toEmail, String subject, String content) {
    String senderName = systemSettingController.getSettingValue(MailgunConsts.SENDER_NAME_SETTING_KEY);
    if (StringUtils.isEmpty(senderName)) {
      logger.error("Sender name setting is missing");
      return;
    }

    String senderEmail = systemSettingController.getSettingValue(MailgunConsts.SENDER_EMAIL_SETTING_KEY);
    if (StringUtils.isEmpty(senderEmail)) {
      logger.error("Sender emaili setting is missing");
      return;
    }
    
    sendEmail(toEmail, subject, content, senderName, senderEmail);
  }

  /**
   * Sends an email
   * 
   * @param toEmail recipient address
   * @param subject subject
   * @param content content
   * @param senderName email sender's name
   * @param senderEmail email sender's email
   */
  public void sendEmail(String toEmail, String subject, String content, String senderName, String senderEmail) {
    emailProvider.sendMail(toEmail, subject, content, senderName, senderEmail);
  }
  
}
