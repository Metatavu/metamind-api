package fi.metatavu.metamind.email;

/**
 * Interface that describes a single email provider
 * 
 * @author Heikki Kurhinen
 * @author Antti Leppä
 */
public interface EmailProvider {
  
  /**
   * Sends an email
   * 
   * @param toEmail recipient's email address
   * @param subject email's subject
   * @param content email's content
   * @param senderName email sender's name
   * @param senderEmail email sender's email
   */
  public void sendMail(String toEmail, String subject, String content, String senderName, String senderEmail);

}
