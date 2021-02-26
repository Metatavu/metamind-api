package fi.metatavu.metamind.server.rest.translation;

import fi.metatavu.metamind.api.spec.model.Story;
import fi.metatavu.metamind.story.StoryController;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * Translator for translating JPA story entities into REST entities
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class StoryTranslator {
  
  @Inject
  private StoryController storyController;

  /**
   * Translates JPA story into REST story
   * 
   * @param jpaStory JPA story
   * @param quickResponses JPA quick responses
   * @param storyResponses JPA story responses
   * @return REST story
   */
  public Story translateStory(fi.metatavu.metamind.persistence.models.Story jpaStory) {
    if (jpaStory == null) {
      return null;
    }

    Story result = new Story();
    result.setCreatedAt(jpaStory.getCreatedAt());
    result.setId(jpaStory.getId());
    result.setModifiedAt(jpaStory.getModifiedAt());
    result.setName(jpaStory.getName());
    result.setDafaultHint(jpaStory.getDefaultHint());
    result.setLocale(jpaStory.getLocale() != null ? jpaStory.getLocale().getLanguage() : null);
    result.setQuickResponses(storyController.listStoryGlobalQuickResponses(jpaStory));
    
    return result;
  }

}
