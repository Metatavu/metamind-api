package fi.metatavu.metamind.bot;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rabidgremlin.mutters.bot.ink.InkBotConfiguration;
import com.rabidgremlin.mutters.bot.ink.InkBotFunction;
import com.rabidgremlin.mutters.core.CompoundIntentMatcher;
import com.rabidgremlin.mutters.core.Intent;
import com.rabidgremlin.mutters.core.IntentMatcher;
import com.rabidgremlin.mutters.opennlp.intent.OpenNLPIntentMatcher;
import com.rabidgremlin.mutters.opennlp.intent.OpenNLPTokenizer;
import com.rabidgremlin.mutters.slots.LiteralSlot;
import com.rabidgremlin.mutters.slots.NumberSlot;
import com.rabidgremlin.mutters.templated.TemplatedIntent;
import com.rabidgremlin.mutters.templated.TemplatedIntentMatcher;

import fi.metatavu.metamind.bot.config.AbstractIntentConfig;
import fi.metatavu.metamind.bot.config.MachineLearningConfig;
import fi.metatavu.metamind.bot.config.MachineLearningIntentConfig;
import fi.metatavu.metamind.bot.config.RegexSlot;
import fi.metatavu.metamind.bot.config.StoryConfig;
import fi.metatavu.metamind.bot.config.TemplateConfig;
import fi.metatavu.metamind.bot.config.TemplatedIntentConfig;
import fi.metatavu.metamind.bot.slots.RegExSlot;
import fi.metatavu.metamind.bot.tokenizer.TemplateTokenizer;
import opennlp.tools.tokenize.WhitespaceTokenizer;

public class MetamindBotConfiguration implements InkBotConfiguration {
  
  private static final Logger logger = LoggerFactory.getLogger(MetamindBotConfiguration.class.getName());
  
  private StoryConfig config;
  private List<InkBotFunction> functions;
  private String intentModelUrl;
  private String slotModelUrl;
  private String storyJson;
  
  public MetamindBotConfiguration(StoryConfig config, List<InkBotFunction> functions, String storyJson, String intentModelUrl, String slotModelUrl) {
    this.config = config;
    this.functions = functions;
    this.storyJson = storyJson;
    this.intentModelUrl = intentModelUrl;
    this.slotModelUrl = slotModelUrl;
  }
  
  @Override
  public IntentMatcher getIntentMatcher() {
    MachineLearningConfig machineLearning = config.getMachineLearning();
    TemplateConfig templateConfig = config.getTemplate();
    TemplatedIntentMatcher templatedIntentMatcher = new TemplatedIntentMatcher(new TemplateTokenizer(templateConfig.getTokenization()));
    
    OpenNLPTokenizer tokenizer = new OpenNLPTokenizer(WhitespaceTokenizer.INSTANCE);
    OpenNLPSlotMatcher slotMatcher = new OpenNLPSlotMatcher(tokenizer);
    
    for (Entry<String, String> slotModelEntry : machineLearning.getSlotModels().entrySet()) {
      String slotName = slotModelEntry.getKey();
      String nerModel = slotModelEntry.getValue();
      try {
        slotMatcher.addSlotModel(slotName, new URL(String.format(slotModelUrl,nerModel)));
      } catch (MalformedURLException e) {
        logger.error("Failed to construct slot model URL", e);
      }
    }
    
    OpenNLPIntentMatcher machineLearningIntentMatcher = getIntentMatcher(machineLearning.getIntentModel(), tokenizer, slotMatcher);
    if (machineLearningIntentMatcher == null) {
      logger.error("Failed to construct intent matcher");
      return null;
    }
    
    createTemplateIntents(templateConfig, templatedIntentMatcher);
    createMachineLearningIntents(machineLearning, machineLearningIntentMatcher);

    return new CompoundIntentMatcher(templatedIntentMatcher, machineLearningIntentMatcher);
  }

  private void createTemplateIntents(TemplateConfig templateConfig, TemplatedIntentMatcher templatedIntentMatcher) {
    for (Entry<String, TemplatedIntentConfig> templatedIntentEntry : templateConfig.getIntents().entrySet()) {
      String intentName = templatedIntentEntry.getKey();
      TemplatedIntentConfig intentConfig = templatedIntentEntry.getValue();
      TemplatedIntent intent = templatedIntentMatcher.addIntent(intentName);
      intent.addUtterances(intentConfig.getUtterances());
      addIntentSlots(intent, intentConfig);
    }
  }

  private void createMachineLearningIntents(MachineLearningConfig machineLearning,
      OpenNLPIntentMatcher machineLearningIntentMatcher) {
    for (Entry<String, MachineLearningIntentConfig> machineLearningIntentEntry : machineLearning.getIntents().entrySet()) {
      String intentName = machineLearningIntentEntry.getKey();
      MachineLearningIntentConfig intentConfig = machineLearningIntentEntry.getValue();
      
      Intent intent = new Intent(intentName);
      addIntentSlots(intent, intentConfig);
      machineLearningIntentMatcher.addIntent(intent);
    }
  }
  
  private void addIntentSlots(Intent intent, AbstractIntentConfig intentConfig) {
    if (intentConfig.getNumberSlots() != null) {
      for (String numberSlot : intentConfig.getNumberSlots()) {
        intent.addSlot(new NumberSlot(numberSlot));
      }
    }
    
    if (intentConfig.getRegExSlots() != null) {
      for (RegexSlot regexSlot : intentConfig.getRegExSlots()) {
        intent.addSlot(new RegExSlot(regexSlot.getName(), regexSlot.getPattern()));
      }
    }
    
    if (intentConfig.getTextSlots() != null) {
      for (String textSlot : intentConfig.getTextSlots()) {
        intent.addSlot(new LiteralSlot(textSlot));
      }
    } 
  }
  
  private OpenNLPIntentMatcher getIntentMatcher(String intentModel, OpenNLPTokenizer tokenizer, OpenNLPSlotMatcher slotMatcher) {
    URL intentModelURL;
    try {
      intentModelURL = new URL(String.format(intentModelUrl, intentModel));
    } catch (MalformedURLException e) {
      logger.error("Failed to construct intent model URL", e);
      return null;
    }
    
    return new OpenNLPIntentMatcher(intentModelURL, tokenizer, slotMatcher, 0.75f, -1);
  }

  @Override
  public String getStoryJson() {
    return storyJson;
  }

  @Override
  public List<InkBotFunction> getInkFunctions() {
    return functions;
  }

  @Override
  public List<GlobalIntent> getGlobalIntents() {
    return Collections.emptyList();
  }

  @Override
  public ConfusedKnot getConfusedKnot() {
    String confusedKnotName = config.getConfusedKnotName();
    Integer maxAttemptsBeforeConfused = config.getMaxAttemptsBeforeConfused();
    
    if (confusedKnotName != null && maxAttemptsBeforeConfused != null) {
      return new ConfusedKnot(maxAttemptsBeforeConfused, confusedKnotName);
    }
    
    return null;
  }

  @Override
  public List<String> getDefaultResponses() {
    return Arrays.asList("Anteeksi, nyt en ihan ymmärtänyt. Suosittelen teitä ottamaan yhteyttä asiakaspalveluumme.");
  }
}
