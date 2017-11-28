package fi.metatavu.metamind.email.mailgun;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import fi.metatavu.metamind.email.EmailProvider;
import fi.metatavu.metamind.settings.SystemSettingController;
import net.sargue.mailgun.Configuration;
import net.sargue.mailgun.Mail;

@ApplicationScoped
public class MailgunEmailProviderImpl implements EmailProvider {
  
  @Inject
  private SystemSettingController systemSettingController;

  @Inject
  private Logger logger;
  
  @Override
  public void sendMail(String from, String to, String title, String content) {
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

    String senderAddress = systemSettingController.getSettingValue(MailgunConsts.SENDER_SETTING_KEY);
    if (StringUtils.isEmpty(senderAddress)) {
      logger.error("Sender address setting is missing");
      return;
    }
    
    Configuration configuration = new Configuration()
        .domain(domain)
        .apiKey(apiKey)
        .from(from, senderAddress);
    
    Mail.using(configuration)
      .to(to)
      .subject(title)
      .text(content)
      .build()
      .send();
  }

}
