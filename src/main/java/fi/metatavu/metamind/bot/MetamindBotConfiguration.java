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
import com.rabidgremlin.mutters.core.CompoundIntentMatcher;
import com.rabidgremlin.mutters.core.Intent;
import com.rabidgremlin.mutters.core.IntentMatch;
import com.rabidgremlin.mutters.core.IntentMatcher;
import com.rabidgremlin.mutters.core.session.Session;
import com.rabidgremlin.mutters.opennlp.intent.OpenNLPIntentMatcher;
import com.rabidgremlin.mutters.opennlp.intent.OpenNLPTokenizer;
import com.rabidgremlin.mutters.opennlp.ner.OpenNLPSlotMatcher;
import com.rabidgremlin.mutters.slots.LiteralSlot;
import com.rabidgremlin.mutters.slots.NumberSlot;
import com.rabidgremlin.mutters.templated.SimpleTokenizer;
import com.rabidgremlin.mutters.templated.TemplatedIntent;
import com.rabidgremlin.mutters.templated.TemplatedIntentMatcher;

import opennlp.tools.tokenize.WhitespaceTokenizer;

public class MetamindBotConfiguration implements InkBotConfiguration {
  
  @Override
  public IntentMatcher getIntentMatcher() {

    TemplatedIntentMatcher templatedIntentMatcher = new TemplatedIntentMatcher(new SimpleTokenizer());

    TemplatedIntent templatedElectricyComsumptionIntent = templatedIntentMatcher.addIntent("GaveElectricityConsumption");
    templatedElectricyComsumptionIntent.addUtterance("{ElectricityConsumption}");
    templatedElectricyComsumptionIntent.addUtterance("{ElectricityConsumption} kwh");
    templatedElectricyComsumptionIntent.addUtterance("{ElectricityConsumption}kwh");
    templatedElectricyComsumptionIntent.addUtterance("{ElectricityConsumption} kilowattituntia");
    templatedElectricyComsumptionIntent.addSlot(new NumberSlot("ElectricityConsumption"));
    
    TemplatedIntent templatedResidentCountIntent = templatedIntentMatcher.addIntent("GaveResidentCount");
    templatedResidentCountIntent.addUtterance("{ResidentCount}");
    templatedResidentCountIntent.addUtterance("{ResidentCount} henkilöä");
    templatedResidentCountIntent.addUtterance("{ResidentCount} hlö");
    templatedResidentCountIntent.addUtterance("{ResidentCount} hlöä");
    templatedResidentCountIntent.addSlot(new NumberSlot("ResidentCount"));
    
    TemplatedIntent templatedLivingSpaceIntent = templatedIntentMatcher.addIntent("GaveLivingSpaceSize");
    templatedLivingSpaceIntent.addUtterance("{LivingSpaceSize}");
    templatedLivingSpaceIntent.addUtterance("{LivingSpaceSize} neliö");
    templatedLivingSpaceIntent.addUtterance("{LivingSpaceSize} neliömetriä");
    templatedLivingSpaceIntent.addUtterance("{LivingSpaceSize} m2");
    templatedLivingSpaceIntent.addSlot(new NumberSlot("LivingSpaceSize"));

    OpenNLPTokenizer tokenizer = new OpenNLPTokenizer(WhitespaceTokenizer.INSTANCE);

    OpenNLPSlotMatcher slotMatcher = new OpenNLPSlotMatcher(tokenizer);
    slotMatcher.addSlotModel("ElectricityConsumption", "models/fi-ner-elecricity-consumption.bin");
    slotMatcher.addSlotModel("LivingSpaceSize", "models/fi-ner-living-space-size.bin");
    slotMatcher.addSlotModel("ResidentCount", "models/fi-ner-resident-count.bin");
    slotMatcher.addSlotModel("YearBuild", "models/fi-ner-year-build.bin");
    slotMatcher.addSlotModel("PhoneNumber", "models/fi-ner-phone-number.bin");
    OpenNLPIntentMatcher machineLearningIntentMatcher = new OpenNLPIntentMatcher("models/fi-lumme-intents.bin", tokenizer, slotMatcher);

    Intent orderElectricityIntent = new Intent("OrderElectricity");
    machineLearningIntentMatcher.addIntent(orderElectricityIntent);

    Intent yesIntent = new Intent("YesIntent");
    machineLearningIntentMatcher.addIntent(yesIntent);
    
    Intent noIntent = new Intent("NoIntent");
    machineLearningIntentMatcher.addIntent(noIntent);
    
    Intent gaveElectricyComsumptionIntent = new Intent("GaveElectricityConsumption");
    gaveElectricyComsumptionIntent.addSlot(new NumberSlot("ElectricityConsumption"));
    machineLearningIntentMatcher.addIntent(gaveElectricyComsumptionIntent);
    
    Intent houseIntent = new Intent("HouseIntent");
    machineLearningIntentMatcher.addIntent(houseIntent);
    
    Intent rowhouseIntent = new Intent("RowhouseIntent");
    machineLearningIntentMatcher.addIntent(rowhouseIntent);
    
    Intent apartmentHouseIntent = new Intent("ApartmentHouseIntent");
    machineLearningIntentMatcher.addIntent(apartmentHouseIntent);
    
    Intent gaveResidentCountIntent = new Intent("GaveResidentCount");
    gaveResidentCountIntent.addSlot(new NumberSlot("ResidentCount"));
    machineLearningIntentMatcher.addIntent(gaveResidentCountIntent);
    
    Intent gaveLivingSpaceSizeIntent = new Intent("GaveLivingSpaceSize");
    gaveLivingSpaceSizeIntent.addSlot(new NumberSlot("LivingSpaceSize"));
    machineLearningIntentMatcher.addIntent(gaveLivingSpaceSizeIntent);
    
    Intent gaveYearBuildIntent = new Intent("GaveYearBuild");
    gaveYearBuildIntent.addSlot(new NumberSlot("YearBuild"));
    machineLearningIntentMatcher.addIntent(gaveYearBuildIntent);
    
    Intent gavePhoneNumberIntent = new Intent("GavePhoneNumber");
    gavePhoneNumberIntent.addSlot(new LiteralSlot("PhoneNumber"));
    machineLearningIntentMatcher.addIntent(gavePhoneNumberIntent);
    
    return new CompoundIntentMatcher(templatedIntentMatcher, machineLearningIntentMatcher);
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
          
          story.getVariablesState().set("electricityprice", String.format("%.2f", electricityPrice));
        } catch (Exception e) {
          throw new RuntimeException("Unable to set electricityPrice", e);
        }
      }

      @Override
      public String getFunctionName() {
        return "ORDER_ELECTRICITY";
      }

    });
    
    functions.add(new InkBotFunction() {
      @Override
      public void execute(CurrentResponse currentResponse, Session session, IntentMatch intentMatch, Story story,
          String param) {
        try {
          String phoneNumber = SessionUtils.getStringFromSlotOrSession(intentMatch, session, "phoneNumber", null);
          System.out.println(phoneNumber);
        } catch (Exception e) {
          throw new RuntimeException("Unable to get phonenumber", e);
        }
      }

      @Override
      public String getFunctionName() {
        return "SEND_CONTACT_REQUEST";
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
    return new ConfusedKnot(3, "bail_out");
  }

  @Override
  public List<String> getDefaultResponses() {
    return Arrays.asList("Anteeksi, nyt en ihan ymmärtänyt. Suosittelen teitä ottamaan yhteyttä asiakaspalveluumme.");
  }
}
