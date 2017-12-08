package fi.metatavu.metamind.email.mailgun;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import fi.metatavu.metamind.email.EmailProvider;
import fi.metatavu.metamind.settings.SystemSettingController;
import net.sargue.mailgun.Configuration;
import net.sargue.mailgun.Mail;

/**
 * Mailgun email provider implementation
 * 
 * @author Heikki Kurhinen
 */
@ApplicationScoped
public class MailgunEmailProviderImpl implements EmailProvider {
  
  @Inject
  private SystemSettingController systemSettingController;

  @Inject
  private Logger logger;
  
  @Override
  public void sendMail(String toEmail, String subject, String content) {
    String domain = systemSettingController.getSettingValue(MailgunConsts.DOMAIN_SETTING_KEY);
    if (StringUtils.isEmpty(domain)) {
      logger.error("Domain setting is missing");
      return;
    }

    String apiKey = systemSettingController.getSettingValue(MailgunConsts.APIKEY_SETTING_KEY);
    if (StringUtils.isEmpty(apiKey)) {
      logger.error("API key setting is missing");
      return;
    }

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
    
    Configuration configuration = new Configuration()
        .domain(domain)
        .apiKey(apiKey)
        .from(senderName, senderEmail);
    
    Mail.using(configuration)
      .to(toEmail)
      .subject(subject)
      .text(content)
      .build()
      .send();
  }

}
