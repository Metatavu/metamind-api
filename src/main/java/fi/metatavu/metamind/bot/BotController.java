package fi.metatavu.metamind.bot;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;

import fi.metatavu.metamind.bot.match.IntentMatch;
import fi.metatavu.metamind.bot.match.IntentMatcher;
import fi.metatavu.metamind.bot.match.OpenNPLIntentMatcher;
import fi.metatavu.metamind.persistence.dao.IntentDAO;
import fi.metatavu.metamind.persistence.dao.KnotIntentModelDAO;
import fi.metatavu.metamind.persistence.models.Intent;
import fi.metatavu.metamind.persistence.models.Knot;
import fi.metatavu.metamind.persistence.models.KnotIntentModel;
import fi.metatavu.metamind.persistence.models.Session;
import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.WhitespaceTokenizer;

/**
 * Bot controller
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class BotController {

  @Inject
  private Logger logger;

  @Inject
  private IntentDAO intentDAO;

  @Inject
  private KnotIntentModelDAO knotIntentModelDAO;

  /**
   * Retrieves response from bot
   * 
   * @param session session
   * @param sourceKnot source knot
   * @param message message
   * @param locale locale
   * @param timeZone time zone
   * @return response
   */
  public BotResponse getResponse(Session session, Knot sourceKnot, String message, Locale locale, TimeZone timeZone) {
    List<IntentMatcher> matchers = getIntentMatchers(sourceKnot);
    for (IntentMatcher matcher : matchers) {
      IntentMatch intentMatch = matcher.matchIntents(message);
      if (intentMatch != null) {
        Intent intent = intentDAO.findById(intentMatch.getIntentId());
        if (intent != null) {
          return new BotResponse(intentMatch.getScore(), intent); 
        }
      }
    }
  
    return new BotResponse(0d, null); 
  }
  
  /**
   * Lists intent matchers for given knot
   * 
   * @param sourceKnot source knot
   * @return list of intent matchers
   */
  private List<IntentMatcher> getIntentMatchers(Knot sourceKnot) {
    // TODO: Knot settings: Tokenizer, Strategy, minMatch?
    
    List<IntentMatcher> result = new ArrayList<>();
    Tokenizer tokenizer = WhitespaceTokenizer.INSTANCE;
    KnotIntentModel knotIntentModel = knotIntentModelDAO.findByKnot(sourceKnot);
    if (knotIntentModel != null) {
      try {
        double minMatch = 0.75d;      
        byte[] modelData = knotIntentModel.getData();
        try (InputStream modelStream = new ByteArrayInputStream(modelData)) {
          DoccatModel model = new DoccatModel(modelStream);
          result.add(new OpenNPLIntentMatcher(model, tokenizer, minMatch));      
        }    
      } catch (IOException e) {
        logger.error("Failed to read doccat model", e);
      }
    }
    
    return result;
  }

}
