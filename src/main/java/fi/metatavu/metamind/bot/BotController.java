package fi.metatavu.metamind.bot;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.slf4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabidgremlin.mutters.bot.ink.InkBotFunction;

import fi.metatavu.metamind.bot.config.MachineLearningConfig;
import fi.metatavu.metamind.bot.config.StoryConfig;
import fi.metatavu.metamind.bot.functions.MetaBotFunction;
import fi.metatavu.metamind.models.ModelsContoller;
import fi.metatavu.metamind.persistence.models.IntentModel;
import fi.metatavu.metamind.persistence.models.Session;
import fi.metatavu.metamind.persistence.models.SlotModel;
import fi.metatavu.metamind.persistence.models.Story;

/**
 * Controller for the bot
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class BotController {
  
  @Any
  @Inject
  private Instance<MetaBotFunction> metabotFunctions;

  @Inject
  private ModelsContoller modelsContoller;

  @Inject
  private Logger logger;
  
  /**
   * Creates new bot session
   * 
   * @return created bot session
   */
  public com.rabidgremlin.mutters.core.session.Session createBotSession() {
    return new com.rabidgremlin.mutters.core.session.Session();
  }
  
  /**
   * Serializes bot session into byte array
   * 
   * @param botSession session to be serialized
   * @return serialized session or null if serialization fails
   */
  @SuppressWarnings ("squid:S1168")
  public byte[] serializeBotSession(com.rabidgremlin.mutters.core.session.Session botSession) {
    try (ByteArrayOutputStream dataStream = new ByteArrayOutputStream()) {
      try (ObjectOutputStream objectStream = new ObjectOutputStream(dataStream)) {
        objectStream.writeObject(botSession);
        return dataStream.toByteArray();
      }
    } catch (IOException e) {
      logger.error("Failed to serialize session from session", e);
      return null;
    }
  }
  
  /**
   * Returns initialized bot session from persisted session
   * 
   * @param session persisted session
   * @return bot session
   */
  public com.rabidgremlin.mutters.core.session.Session getBotSession(Session session) {
    try {
      return deserializeBotSession(session.getData());
    } catch (ClassNotFoundException | IOException e) {
      logger.error(String.format("Failed to deserialize session from session %d", session.getId()), e);
      return null;
    }
  }
  
  /**
   * Returns new bot instance
   * 
   * @param story story
   * 
   * @return new bot instance
   */
  public MetamindBot getBotInstance(Story story) {
    List<InkBotFunction> functions = new ArrayList<>();
    this.metabotFunctions.forEach(functions::add);
    
    StoryConfig storyConfig = loadStoryConfig(story.getConfigJson());
    if (storyConfig == null) {
      logger.error("Story config is missing");
      return null;
    }
    
    String storyJson = story.getStoryJson();
    
    Map<String, SlotModel> slotModels = new HashMap<>();
    Map<String, IntentModel> intentModels = new HashMap<>();
    
    List<MachineLearningConfig> machineLearningConfigs = storyConfig.getMachineLearning();
    if (storyConfig.getMachineLearning() != null) {
      loadSlotModels(storyConfig, slotModels);  
      loadIntentModels(machineLearningConfigs, intentModels);
    }
    
    MetamindBotConfiguration botConfiguration = new MetamindBotConfiguration(storyConfig, functions, storyJson, intentModels, slotModels);
    
    return new MetamindBot(botConfiguration);
  }

  private void loadIntentModels(List<MachineLearningConfig> machineLearningConfigs, Map<String, IntentModel> intentModels) {
    List<String> intentModelNames = new ArrayList<>();
    
    for (MachineLearningConfig machineLearningConfig : machineLearningConfigs) {
      intentModelNames.add(machineLearningConfig.getIntentModel());
    }
    
    for (String intentModelName : intentModelNames) {
      if (intentModelName != null && !intentModels.containsKey(intentModelName)) {
        IntentModel intentModel = modelsContoller.findIntentModelByName(String.format("%s.bin", intentModelName));
        
        if (intentModel == null && logger.isWarnEnabled()) {
          logger.warn(String.format("Failed to load intent model %s", intentModelName));
        } else {
          intentModels.put(intentModelName, intentModel);
        }
      }
    }
  }

  protected void loadSlotModels(StoryConfig storyConfig, Map<String, SlotModel> slotModels) {
    List<MachineLearningConfig> machineLearningConfigs = storyConfig.getMachineLearning();
    
    for (MachineLearningConfig machineLearningConfig : machineLearningConfigs) {
      if (machineLearningConfig.getSlotModels() != null) {
        Collection<String> slotModelNames = machineLearningConfig.getSlotModels().values();
        for (String slotModelName : slotModelNames) {
          SlotModel slotModel = modelsContoller.findSlotModelByName(String.format("%s.bin", slotModelName));
          if (slotModel !=  null) {
            slotModels.put(slotModelName, slotModel);
          } else {
            if (logger.isWarnEnabled()) {
              logger.warn(String.format("Could not find slot model %s", slotModelName));
            }
          }
        }
      }
    }
  }

  private StoryConfig loadStoryConfig(String configJson) {
    ObjectMapper objectMapper = new ObjectMapper();
    try {
      return objectMapper.readValue(configJson, StoryConfig.class);
    } catch (IOException e) {
      logger.error("Failed to read story config", e);
    }
    
    return null;
  }

  private com.rabidgremlin.mutters.core.session.Session deserializeBotSession(byte[] data) throws IOException, ClassNotFoundException {
    try (ByteArrayInputStream dataStream = new ByteArrayInputStream(data)) {
      try (ObjectInputStream objectStream = new ObjectInputStream(dataStream)) {
        return (com.rabidgremlin.mutters.core.session.Session) objectStream.readObject();
      }
    }
  }
  
}
