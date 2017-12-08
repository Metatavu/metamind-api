  package fi.metatavu.metamind.bot.tokenizer;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fi.metatavu.metamind.bot.config.TokenizationConfig;

/**
 * Configurable tokenizer for templates
 * 
 * @author Antti Leppä
 */
public class TemplateTokenizer extends SimpleTokenizer {

  private Map<String, Pattern> patterns;
  private Map<String, TokenizationConfig> settings;
  
  /**
   * Constructor
   * 
   * @param settings settings
   */
  public TemplateTokenizer(Map<String, TokenizationConfig> settings) {
    this.settings = settings != null ? settings : Collections.emptyMap();
    this.patterns = new HashMap<>();
    
    for (String pattern : this.settings.keySet()) {
      patterns.put(pattern, Pattern.compile(pattern));
    }
  }

  @Override
  public String[] tokenize(String originalText) {
    String text = originalText;
    
    for (Entry<String, Pattern> entry : this.patterns.entrySet()) {
      Pattern pattern = entry.getValue();
      Matcher matcher = pattern.matcher(text);
      if (matcher.matches()) {
        TokenizationConfig tokenizationConfig = settings.get(entry.getKey());
        if (tokenizationConfig != null) {
          if (Boolean.TRUE.equals(tokenizationConfig.getStripWhitespace())) {
            text = text.replaceAll(" ", "");
          }
          
          if (Boolean.TRUE.equals(tokenizationConfig.getUntokenized())) {
            return new String[] { text };
          }
        }
      }
    }
    
    return super.tokenize(text);
  }

}
