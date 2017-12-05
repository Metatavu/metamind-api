package fi.metatavu.metamind.story;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.metamind.persistence.dao.StoryDAO;
import fi.metatavu.metamind.persistence.models.Story;

/**
 * Story controller
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class StoryController {
  
  @Inject
  private StoryDAO storyDAO;

  /**
   * Creates new story
   * 
   * @param name name
   * @param configJson config json
   * @param storyJson story json
   * @return created story
   */
  public Story createStory(String name, String configJson, String storyJson) {
    return storyDAO.create(name, configJson, storyJson);
  }
  
  /**
   * Finds a story by name
   * 
   * @param name name
   * @return found story or null if not found
   */
  public Story findStoryByName(String name) {
    return storyDAO.findByName(name);
  }
  
  /**
   * Updates story's data
   * 
   * @param story story
   * @param configJson config json
   * @param storyJson story json
   * @return updated story
   */
  public Story updateStoryData(Story story, String configJson, String storyJson) {
    storyDAO.updateConfigJson(story, configJson);
    storyDAO.updateStoryJson(story, storyJson);
    return story;
  }

  /**
   * Sets slot model's data by name. If model does not exist new is created
   * 
   * @param name name
   * @param configJson config json
   * @param storyJson story json
   * @return updated or created story
   */
  public Story setStoryData(String name, String configJson, String storyJson) {
    Story story = findStoryByName(name);
    if (story != null) {
      storyDAO.updateConfigJson(story, configJson);
      storyDAO.updateStoryJson(story, storyJson);
      return story;
    }
    
    return storyDAO.create(name, configJson, storyJson);
  }
  
}
