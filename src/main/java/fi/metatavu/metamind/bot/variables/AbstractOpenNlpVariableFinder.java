package fi.metatavu.metamind.bot.variables;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import opennlp.tools.namefind.TokenNameFinder;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.util.Span;

/**
 * Abstract base class for OpenNLP variable finders
 * 
 * @author Antti Leppä
 */
public abstract class AbstractOpenNlpVariableFinder implements VariableFinder {

  private Tokenizer tokenizer;
  private TokenNameFinder nameFinder;

  /**
   * Constructor
   * 
   * @param tokenizer tokenizer
   * @param nameFinder name finder instance
   */
  public AbstractOpenNlpVariableFinder(Tokenizer tokenizer, TokenNameFinder nameFinder) {
    super();
    this.tokenizer = tokenizer;
    this.nameFinder = nameFinder;
  }

  @Override
  public Map<UUID, String> findVariables(String text) {
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
