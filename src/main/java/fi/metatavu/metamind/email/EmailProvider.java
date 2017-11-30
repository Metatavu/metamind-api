package fi.metatavu.metamind.email;

/**
 * Interface that describes a single email provider
 * 
 * @author Heikki Kurhinen
 */
public interface EmailProvider {
  
  /**
   * Sends an email
   * 
   * @param toEmail recipient's email address
   * @param subject email's subject
   * @param content email's content
   */
  public void sendMail(String toEmail, String subject, String content);

}
