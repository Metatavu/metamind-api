package fi.metatavu.metamind.bot;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.slf4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabidgremlin.mutters.bot.ink.InkBotFunction;

import fi.metatavu.metamind.bot.config.StoryConfig;
import fi.metatavu.metamind.bot.functions.MetaBotFunction;
import fi.metatavu.metamind.persistence.models.Session;
import fi.metatavu.metamind.persistence.models.Story;
import fi.metatavu.metamind.settings.SystemConsts;
import fi.metatavu.metamind.settings.SystemSettingController;

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
  private SystemSettingController systemSettingController;

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
    String intentModelUrl = systemSettingController.getSettingValue(SystemConsts.INTENT_MODEL_URL);
    String slotModelUrl = systemSettingController.getSettingValue(SystemConsts.SLOT_MODEL_URL);
    String storyJson = story.getStoryJson();
    
    MetamindBotConfiguration botConfiguration = new MetamindBotConfiguration(storyConfig, functions, storyJson, intentModelUrl, slotModelUrl);
    
    return new MetamindBot(botConfiguration);
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
