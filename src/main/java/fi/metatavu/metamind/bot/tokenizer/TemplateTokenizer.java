  package fi.metatavu.metamind.bot.tokenizer;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import fi.metatavu.metamind.bot.config.TokenizationConfig;

/**
 * Configurable tokenizer for templates
 * 
 * @author Antti Leppä
 * @author Heikki Kurhinen
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
      TokenizationConfig tokenizationConfig = settings.get(entry.getKey());

      if (matcher.matches()) {
        if (tokenizationConfig != null) {
          if (Boolean.TRUE.equals(tokenizationConfig.getStripWhitespace())) {
            text = StringUtils.deleteWhitespace(text);
          }
          
          if (Boolean.TRUE.equals(tokenizationConfig.getUntokenized())) {
            return new String[] { text };
          }
        }
      } else {
        matcher = pattern.matcher(text);
        if (matcher.find() && Boolean.TRUE.equals(tokenizationConfig.getStripWhitespace())) {
          StringBuffer replacementBuffer = new StringBuffer(text.length());
          matcher.appendReplacement(replacementBuffer, StringUtils.deleteWhitespace(matcher.group(0)));
          while (matcher.find()) {
            matcher.appendReplacement(replacementBuffer, StringUtils.deleteWhitespace(matcher.group(0)));
          }
  
          matcher.appendTail(replacementBuffer);
          text = replacementBuffer.toString();
        }
      }
    }
    
    return super.tokenize(text);
  }

}
