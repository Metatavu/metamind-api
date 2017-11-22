package fi.metatavu.metamind.bot;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.bladecoder.ink.runtime.Story;
import com.rabidgremlin.mutters.bot.ink.CurrentResponse;
import com.rabidgremlin.mutters.bot.ink.InkBotConfiguration;
import com.rabidgremlin.mutters.bot.ink.InkBotFunction;
import com.rabidgremlin.mutters.bot.ink.SessionUtils;
import com.rabidgremlin.mutters.bot.ink.StoryUtils;
import com.rabidgremlin.mutters.core.Intent;
import com.rabidgremlin.mutters.core.IntentMatch;
import com.rabidgremlin.mutters.core.IntentMatcher;
import com.rabidgremlin.mutters.core.session.Session;
import com.rabidgremlin.mutters.opennlp.intent.OpenNLPIntentMatcher;
import com.rabidgremlin.mutters.opennlp.intent.OpenNLPTokenizer;
import com.rabidgremlin.mutters.opennlp.ner.OpenNLPSlotMatcher;
import com.rabidgremlin.mutters.slots.NumberSlot;

import opennlp.tools.tokenize.WhitespaceTokenizer;

public class MetamindBotConfiguration implements InkBotConfiguration {
  
  @Override
  public IntentMatcher getIntentMatcher() {
    // model was built with OpenNLP whitespace tokenizer
    OpenNLPTokenizer tokenizer = new OpenNLPTokenizer(WhitespaceTokenizer.INSTANCE);

    // use OpenNLP NER for slot matching
    OpenNLPSlotMatcher slotMatcher = new OpenNLPSlotMatcher(tokenizer);
    slotMatcher.addSlotModel("ElectricityConsumption", "models/fi-ner-elecricity-consumption.bin");

    // create intent matcher
    OpenNLPIntentMatcher matcher = new OpenNLPIntentMatcher("models/fi-cat-lumme-intents.bin", tokenizer, slotMatcher);

    Intent orderElectricityIntent = new Intent("OrderElectricity");
    matcher.addIntent(orderElectricityIntent);

    Intent yesIntent = new Intent("YesIntent");
    matcher.addIntent(yesIntent);
    
    Intent noIntent = new Intent("NoIntent");
    matcher.addIntent(noIntent);
    
    Intent gaveElectricyComsumptionIntent = new Intent("GaveElectricityConsumption");
    gaveElectricyComsumptionIntent.addSlot(new NumberSlot("ElectricityConsumption"));
    matcher.addIntent(gaveElectricyComsumptionIntent);

    return matcher;
  }

  @Override
  public String getStoryJson() {
    return StoryUtils.loadStoryJsonFromClassPath("electricity-order.ink.json");
  }

  @Override
  public List<InkBotFunction> getInkFunctions() {
    List<InkBotFunction> functions = new ArrayList<>();

    functions.add(new InkBotFunction() {
      @Override
      public void execute(CurrentResponse currentResponse, Session session, IntentMatch intentMatch, Story story,
          String param) {
        try {
          long electricityConsumption = SessionUtils.getNumberFromSlotOrSession(intentMatch, session, "electricityConsumption", 1000l).longValue();
          double electricityPrice = 5.9d;

          if (electricityConsumption > 6000l) {
            electricityPrice = 35.9d;
          } else if (electricityConsumption > 3000l) {
            electricityPrice = 19.90d;
          } else if (electricityConsumption > 1500l) {
            electricityPrice = 9.90d;
          }
          
          story.getVariablesState().set("electricityPrice", String.format("%.2f", electricityPrice));
        } catch (Exception e) {
          throw new RuntimeException("Unable to set electricityPrice", e);
        }
      }

      @Override
      public String getFunctionName() {
        return "ORDER_ELECTRICITY";
      }

    });

    return functions;
  }

  @Override
  public List<GlobalIntent> getGlobalIntents() {
    List<GlobalIntent> globalIntents = new ArrayList<GlobalIntent>();

    return globalIntents;
  }

  @Override
  public ConfusedKnot getConfusedKnot() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<String> getDefaultResponses() {
    return Arrays.asList("Anteeksi, nyt en ihan ymmärtänyt. Suosittelen teitä ottamaan yhteyttä asiakaspalveluumme.");
  }
}
