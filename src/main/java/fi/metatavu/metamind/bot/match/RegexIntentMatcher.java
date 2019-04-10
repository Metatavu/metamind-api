package fi.metatavu.metamind.bot.match;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.regex.Pattern;

import fi.metatavu.metamind.bot.tokenization.Tokenizer;

/**
 * Intent matcher that uses Regex patterns for matching
 * 
 * @author Antti Leppä
 */
public class RegexIntentMatcher implements IntentMatcher {
  
  private Map<UUID, List<Pattern>> patterns;
  private Tokenizer tokenizer;

  /**
   * Constructor
   * 
   * @param model A pattern for matching
   */
  public RegexIntentMatcher(Map<UUID, List<Pattern>> patterns, Tokenizer tokenizer) {
    super();
    this.patterns = patterns;
    this.tokenizer = tokenizer;
  }
  
  @Override
  public IntentMatch matchIntents(String text) {
    String[] tokens = tokenizer.tokenize(text);
    for (String token : tokens) {
      for (Entry<UUID, List<Pattern>> entry : patterns.entrySet()) {
        for (Pattern pattern : entry.getValue()) {
          if (pattern.matcher(token).matches()) {
            return new IntentMatch(entry.getKey(), 1d);            
          }
        }
      }
    }
    
    return null;
  }


}
