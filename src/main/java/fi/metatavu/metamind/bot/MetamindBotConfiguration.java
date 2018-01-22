package fi.metatavu.metamind.bot;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rabidgremlin.mutters.bot.ink.InkBotConfiguration;
import com.rabidgremlin.mutters.bot.ink.InkBotFunction;
import com.rabidgremlin.mutters.core.CompoundIntentMatcher;
import com.rabidgremlin.mutters.core.Intent;
import com.rabidgremlin.mutters.core.IntentMatcher;
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
import fi.metatavu.metamind.persistence.models.IntentModel;
import fi.metatavu.metamind.persistence.models.SlotModel;
import opennlp.tools.tokenize.WhitespaceTokenizer;

public class MetamindBotConfiguration implements InkBotConfiguration {
  
  private static final Logger logger = LoggerFactory.getLogger(MetamindBotConfiguration.class.getName());
  
  private StoryConfig config;
  private List<InkBotFunction> functions;
  private IntentModel intentModel;
  private Map<String, SlotModel> slotModels;
  private String storyJson;
  
  public MetamindBotConfiguration(StoryConfig config, List<InkBotFunction> functions, String storyJson, IntentModel intentModel, Map<String, SlotModel> slotModels) {
    this.config = config;
    this.functions = functions;
    this.storyJson = storyJson;
    this.intentModel = intentModel;
    this.slotModels = slotModels;
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
      SlotModel slotModel = slotModels.get(nerModel);
      if (slotModel != null) {
        slotMatcher.addSlotModel(slotName, slotModel);
      } else {
        if (logger.isWarnEnabled()) {
          logger.warn(String.format("Could not find slotModel %s", nerModel));
        }
      }
    }
    
    OpenNLPIntentMatcher machineLearningIntentMatcher = getIntentMatcher(intentModel, tokenizer, slotMatcher);
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
  
  private OpenNLPIntentMatcher getIntentMatcher(IntentModel intentModel, OpenNLPTokenizer tokenizer, OpenNLPSlotMatcher slotMatcher) {
    return new OpenNLPIntentMatcher(intentModel, tokenizer, slotMatcher, 0.75f, -1);
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
    Map<String, String> globalIntentMap = config.getGlobalIntents();
    if (globalIntentMap == null) {
      return Collections.emptyList();
    }
    
    return globalIntentMap.entrySet().stream()
      .map(entry -> new GlobalIntent(entry.getKey(), entry.getValue()))
      .collect(Collectors.toList());
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
