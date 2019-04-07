package fi.metatavu.metamind.bot.variables;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.util.Span;

/**
 * Variable finder using OpenNLP token name finder
 * 
 * @author Antti Leppä
 */
public class OpenNlpVariableFinder implements VariableFinder {

  private TokenNameFinderModel tokenNameFinderModel;
  private Tokenizer tokenizer;

  /**
   * Variable finder using OpenNLP token name finder
   * 
   * @param tokenNameFinderModel model
   * @param tokenizer tokenizer
   */
  public OpenNlpVariableFinder(TokenNameFinderModel tokenNameFinderModel, Tokenizer tokenizer) {
    super();
    this.tokenNameFinderModel = tokenNameFinderModel;
    this.tokenizer = tokenizer;
  }
  
  @Override
  public Map<UUID, String> findVariables(String text) {
    NameFinderME nameFinder = new NameFinderME(tokenNameFinderModel);
    String[] tokens = tokenizer.tokenize(text);
    Span[] spans = nameFinder.find(tokens);
    
    if (spans.length > 0) {
      Map<UUID, String> result = new HashMap<>();
      
      String[] values = Span.spansToStrings(spans, tokens);
      for (int i = 0; i < spans.length; i++) {
        String value = values[i];
        
        if (StringUtils.isNotEmpty(value)) {
          UUID variableId = UUID.fromString(spans[i].getType());
          if (0l != variableId.getLeastSignificantBits() || 0l == variableId.getMostSignificantBits()) {
            result.put(variableId, value);
          }
        }
      }
      
      return result;
    }

    return null;
  }

}
