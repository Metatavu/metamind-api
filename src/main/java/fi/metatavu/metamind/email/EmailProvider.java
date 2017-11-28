package fi.metatavu.metamind.email;

public interface EmailProvider {
  
  public void sendMail(String from, String to, String title, String content);

}
