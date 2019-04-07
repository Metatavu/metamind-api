package fi.metatavu.metamind.bot;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;

import fi.metatavu.metamind.bot.match.IntentMatch;
import fi.metatavu.metamind.bot.match.IntentMatcher;
import fi.metatavu.metamind.bot.match.OpenNPLIntentMatcher;
import fi.metatavu.metamind.persistence.dao.IntentDAO;
import fi.metatavu.metamind.persistence.dao.KnotIntentModelDAO;
import fi.metatavu.metamind.persistence.dao.StoryGlobalIntentModelDAO;
import fi.metatavu.metamind.persistence.models.Intent;
import fi.metatavu.metamind.persistence.models.Knot;
import fi.metatavu.metamind.persistence.models.KnotIntentModel;
import fi.metatavu.metamind.persistence.models.Session;
import fi.metatavu.metamind.persistence.models.Story;
import fi.metatavu.metamind.persistence.models.StoryGlobalIntentModel;
import fi.metatavu.metamind.rest.model.IntentType;
import fi.metatavu.metamind.rest.model.TrainingMaterialType;
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

  @Inject
  private StoryGlobalIntentModelDAO storyGlobalIntentModelDAO;

  /**
   * Retrieves response from bot
   * 
   * @param session session
   * @param message message
   * @param locale locale
   * @param timeZone time zone
   * @return response
   */
  public BotResponse getResponse(Session session, String message, Locale locale, TimeZone timeZone) {
    List<IntentMatcher> matchers = getIntentMatchers(session.getStory(), session.getCurrentKnot());
    for (IntentMatcher matcher : matchers) {
      IntentMatch intentMatch = matcher.matchIntents(message);
      if (intentMatch != null) {
        Intent intent = intentDAO.findById(intentMatch.getIntentId());
        if (intent != null) {
          // TODO: variables
          return new BotResponse(intentMatch.getScore(), intent, Collections.emptyMap()); 
        }
      }
    }
  
    return new BotResponse(0d, getConfusedIntent(session), Collections.emptyMap()); 
  }
  
  /**
   * Returns confused intent for a session
   * 
   * @param session session
   * @return confused intent
   */
  private Intent getConfusedIntent(Session session) {
    Knot currentKnot = session.getCurrentKnot();
    if (currentKnot != null) {
      List<Intent> knotConfusedIntents = intentDAO.listBySourceKnotAndType(currentKnot, IntentType.CONFUSED);
      if (!knotConfusedIntents.isEmpty()) {
        return knotConfusedIntents.get(RandomUtils.nextInt(0, knotConfusedIntents.size()));
      }
    }
    
    List<Intent> globalConfusedIntents = intentDAO.listByStoryAndGlobalAndType(session.getStory(), Boolean.TRUE, IntentType.CONFUSED);
    if (!globalConfusedIntents.isEmpty()) {
      return globalConfusedIntents.get(RandomUtils.nextInt(0, globalConfusedIntents.size()));
    }
    
    return null;
  }

  /**
   * Lists intent matchers for given knot. If knot is null, story global intent matchers are returned
   * 
   * @param story story
   * @param sourceKnot source knot
   * @return list of intent matchers
   */
  private List<IntentMatcher> getIntentMatchers(Story story, Knot sourceKnot) {
    
    if (sourceKnot == null) {
      return getStoryGlobalIntentMatches(story);
    } else {
      return getKnotIntentMatchers(sourceKnot);      
    }
  }

  /**
   * Lists intent matchers for story
   * 
   * @param sourceKnot source knot
   * @return list of intent matchers
   */
  private List<IntentMatcher> getStoryGlobalIntentMatches(Story story) {
    // TODO: Story settings: Tokenizer, Strategy, minMatch?
    
    List<IntentMatcher> result = new ArrayList<>();
    Tokenizer tokenizer = WhitespaceTokenizer.INSTANCE;
    
    StoryGlobalIntentModel intentModel = storyGlobalIntentModelDAO.findByStory(story);
    if (intentModel != null) {
      try {
        double minMatch = 0.75d;      
        byte[] modelData = intentModel.getData();
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

  /**
   * Lists intent matchers for given knot
   * 
   * @param sourceKnot source knot
   * @return list of intent matchers
   */
  private List<IntentMatcher> getKnotIntentMatchers(Knot sourceKnot) {
    // TODO: Knot settings: Tokenizer, Strategy, minMatch?
    
    List<IntentMatcher> result = new ArrayList<>();
    Tokenizer tokenizer = WhitespaceTokenizer.INSTANCE;
    KnotIntentModel knotIntentModel = knotIntentModelDAO.findByKnotAndType(sourceKnot, TrainingMaterialType.OPENNLPDOCCAT);
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
