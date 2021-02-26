package fi.metatavu.metamind.bot.match;

import fi.metatavu.metamind.bot.tokenization.Tokenizer;
import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.doccat.DocumentCategorizerME;

import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Intent matcher that users OpenNPL
 * 
 * @author Antti Leppä
 */
public class OpenNlpDoccatIntentMatcher implements IntentMatcher {
  
  private double minMatch;
  private DoccatModel model;
  private Tokenizer tokenizer;

  /**
   * Constructor
   * 
   * @param model A model for document categorization
   * @param minMatch minimum confidence for a match
   */
  public OpenNlpDoccatIntentMatcher(DoccatModel model, Tokenizer tokenizer, double minMatch) {
    super();
    this.minMatch = minMatch;
    this.model = model;
    this.tokenizer = tokenizer;
  }
  
  @Override
  public IntentMatch matchIntents(String text) {
    DocumentCategorizerME intentCategorizer = new DocumentCategorizerME(model);
    SortedMap<Double, Set<String>> scoreMap = intentCategorizer.sortedScoreMap(tokenizer.tokenize(text));
    
    return scoreMap.entrySet().stream()
      .filter(entry -> entry.getKey() > minMatch)
      .map(entry -> {
        Double score = entry.getKey();
        return entry.getValue().stream()
          .map(UUID::fromString)
          .map(intentId -> new IntentMatch(intentId, score))
          .collect(Collectors.toList());
      })
      .flatMap(List::stream)
      .reduce((first, second) -> second)
      .orElse(null);
  }


}
