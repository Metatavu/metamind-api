package fi.metatavu.metamind.bot;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;

import fi.metatavu.metamind.bot.match.IntentMatch;
import fi.metatavu.metamind.bot.match.IntentMatcher;
import fi.metatavu.metamind.bot.match.OpenNlpDoccatIntentMatcher;
import fi.metatavu.metamind.bot.match.RegexIntentMatcher;
import fi.metatavu.metamind.bot.variables.OpenNlpNerVariableFinder;
import fi.metatavu.metamind.bot.variables.OpenNlpRegExVariableFinder;
import fi.metatavu.metamind.bot.variables.VariableFinder;
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
import opennlp.tools.namefind.TokenNameFinderModel;
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
          return new BotResponse(intentMatch.getScore(), intent, getVariables(session, message)); 
        }
      }
    }
  
    return new BotResponse(0d, getConfusedIntent(session), Collections.emptyMap()); 
  }

  /**
   * Resolves variables from message
   * 
   * @param session session
   * @param message message
   * @return variables from message
   */
  private Map<UUID, String> getVariables(Session session, String message) {
    List<VariableFinder> variableFinders = getVariableFinders(session.getStory(), session.getCurrentKnot());
    
    for (VariableFinder variableFinder : variableFinders) {
      Map<UUID, String> variables = variableFinder.findVariables(message);
      if (variables != null) {
        return variables;
      }
    }
    
    return Collections.emptyMap();
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
          result.add(new OpenNlpDoccatIntentMatcher(model, tokenizer, minMatch));      
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
    
    KnotIntentModel knotIntentDoccatModel = knotIntentModelDAO.findByKnotAndType(sourceKnot, TrainingMaterialType.INTENTOPENNLPDOCCAT);
    if (knotIntentDoccatModel != null) {
      try {
        double minMatch = 0.75d;      
        byte[] modelData = knotIntentDoccatModel.getData();
        try (InputStream modelStream = new ByteArrayInputStream(modelData)) {
          DoccatModel model = new DoccatModel(modelStream);
          result.add(new OpenNlpDoccatIntentMatcher(model, tokenizer, minMatch));      
        }    
      } catch (IOException e) {
        logger.error("Failed to read doccat model", e);
      }
    }
    
    KnotIntentModel knotIntentRegexModel = knotIntentModelDAO.findByKnotAndType(sourceKnot, TrainingMaterialType.INTENTREGEX);
    if (knotIntentRegexModel != null) {
      Map<UUID, List<Pattern>> intentModel = readRegexIntentModel(knotIntentRegexModel);
      if (intentModel != null) {
        result.add(new RegexIntentMatcher(intentModel, tokenizer));
      }
    }
    
    return result;
  }
  
  /**
   * Reads Regex intent model into map
   * 
   * @param knotIntentRegexModel model
   * @return Regex intent model
   */
  private Map<UUID, List<Pattern>> readRegexIntentModel(KnotIntentModel knotIntentRegexModel) {
    Map<UUID, List<Pattern>> patterns = new HashMap<>();
    
    try {
      String lineDatas = new String(knotIntentRegexModel.getData(), "UTF-8");
      for (String line : lineDatas.split("\n")) {
        try {
          UUID intentId = UUID.fromString(line.substring(0, 36));
          Pattern pattern = Pattern.compile(line.substring(37));
          
          if (!patterns.containsKey(intentId)) {
            patterns.put(intentId, new ArrayList<>());
          }
          
          patterns.get(intentId).add(pattern);
        } catch (IllegalArgumentException e) {
          logger.warn("Failed to parse regex intent model line {}", line);
        }
      }
      
      return patterns;
    } catch (UnsupportedEncodingException e) {
      logger.error("Failed to parse regex intent model", e);
    }
    
    return null;
  }
  
  /**
   * Lists variable finders for given knot. If knot is null, story global variable finders are returned
   * 
   * @param story story
   * @param sourceKnot source knot
   * @return list of variable finders
   */
  private List<VariableFinder> getVariableFinders(Story story, Knot sourceKnot) {
    if (sourceKnot == null) {
      return getStoryGlobalVariableFinder(story);
    } else {
      return getKnotVariableFinders(sourceKnot);      
    }
  }

  private List<VariableFinder> getStoryGlobalVariableFinder(Story story) {
    // TODO implement
    return Collections.emptyList();
  }

  /**
   * Lists variable finders for given knot
   * 
   * @param sourceKnot source knot
   * @return list of variable finders
   */
  private List<VariableFinder> getKnotVariableFinders(Knot sourceKnot) {
    // TODO: Knot settings: Tokenizer
    
    List<VariableFinder> result = new ArrayList<>();
    Tokenizer tokenizer = WhitespaceTokenizer.INSTANCE;
    KnotIntentModel knotIntentNerModel = knotIntentModelDAO.findByKnotAndType(sourceKnot, TrainingMaterialType.VARIABLEOPENNLPNER);
    if (knotIntentNerModel != null) {
      try {
        byte[] modelData = knotIntentNerModel.getData();
        try (InputStream modelStream = new ByteArrayInputStream(modelData)) {
          TokenNameFinderModel model = new TokenNameFinderModel(modelStream);
          result.add(new OpenNlpNerVariableFinder(model, tokenizer));      
        }    
      } catch (IOException e) {
        logger.error("Failed to read doccat model", e);
      }
    }

    KnotIntentModel knotIntentRegexModel = knotIntentModelDAO.findByKnotAndType(sourceKnot, TrainingMaterialType.VARIABLEOPENNLPREGEX);
    if (knotIntentRegexModel != null) {
      Map<UUID, List<Pattern>> intentModel = readRegexIntentModel(knotIntentRegexModel);
      if (intentModel != null) {
        Map<String, Pattern[]> patterns = new HashMap<>(intentModel.size());
        
        for (Entry<UUID, List<Pattern>> entry : intentModel.entrySet()) {
          patterns.put(entry.getKey().toString(), entry.getValue().toArray(new Pattern[0]));
        }
        
        result.add(new OpenNlpRegExVariableFinder(patterns, tokenizer));
      }
    }
    
    
    return result;
  }

}
