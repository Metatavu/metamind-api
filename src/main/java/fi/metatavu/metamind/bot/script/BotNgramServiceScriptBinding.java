package fi.metatavu.metamind.bot.script;

import java.io.IOException;

import javax.enterprise.context.ApplicationScoped;

import org.slf4j.Logger;
import javax.inject.Inject;

import fi.metatavu.metamind.bot.utils.TextLanguageDetector;

/**
 * Bot script binding for ngram service
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class BotNgramServiceScriptBinding {
  
  @Inject
  private Logger logger;

  /**
   * Detects language from text
   * 
   * @param text text
   * @return language or null if language could not be detected
   */
  public String detectLanguage(String text) {
    try {
      return TextLanguageDetector.getInstance().getLanguage(text);
    } catch (IOException e) {
      logger.warn("Failed to detect language", e);
    }
    
    return null;
  }
  
}
