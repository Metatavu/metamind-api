package fi.metatavu.metamind.story;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.metamind.persistence.dao.StoryDAO;

/**
 * Story controller
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class StoryController {

  @Inject
  private StoryDAO storyDAO;

}
