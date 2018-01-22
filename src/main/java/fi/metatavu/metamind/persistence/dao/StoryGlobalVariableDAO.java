package fi.metatavu.metamind.persistence.dao;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.metatavu.metamind.persistence.models.Story;
import fi.metatavu.metamind.persistence.models.StoryGlobalVariable;
import fi.metatavu.metamind.persistence.models.StoryGlobalVariable_;

/**
 * DAO for story global variables
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class StoryGlobalVariableDAO extends AbstractDAO<StoryGlobalVariable> {
  
  /**
   * Creates new storyGlobalVariable
   *
   * @param story story
   * @param name name
   * @param value value
   * @return created storyGlobalVariable
   */
   public StoryGlobalVariable create(Story story, String name, String value) {
     StoryGlobalVariable storyGlobalVariable = new StoryGlobalVariable();
     storyGlobalVariable.setStory(story);
     storyGlobalVariable.setName(name);
     storyGlobalVariable.setValue(value);
     return persist(storyGlobalVariable);
   }

   /**
    * Returns a story global variable by name
    * 
    * @param name name
    * @return a story global variable
    */
   public StoryGlobalVariable findByStoryAndName(Story story, String name) {
     EntityManager entityManager = getEntityManager();

     CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
     CriteriaQuery<StoryGlobalVariable> criteria = criteriaBuilder.createQuery(StoryGlobalVariable.class);
     Root<StoryGlobalVariable> root = criteria.from(StoryGlobalVariable.class);
     criteria.select(root);
     criteria.where(
       criteriaBuilder.and(
         criteriaBuilder.equal(root.get(StoryGlobalVariable_.story), story),
         criteriaBuilder.equal(root.get(StoryGlobalVariable_.name), name)
       )
     );
     
     return getSingleResult(entityManager.createQuery(criteria));
   }
  
   /**
   * Updates story
   *
   * @param story story
   * @return updated storyGlobalVariable
   */
   public StoryGlobalVariable updateStory(StoryGlobalVariable storyGlobalVariable, Story story) {
     storyGlobalVariable.setStory(story);
     return persist(storyGlobalVariable);
   }
  
   /**
   * Updates name
   *
   * @param name name
   * @return updated storyGlobalVariable
   */
   public StoryGlobalVariable updateName(StoryGlobalVariable storyGlobalVariable, String name) {
     storyGlobalVariable.setName(name);
     return persist(storyGlobalVariable);
   }
  
   /**
   * Updates value
   *
   * @param value value
   * @return updated storyGlobalVariable
   */
   public StoryGlobalVariable updateValue(StoryGlobalVariable storyGlobalVariable, String value) {
     storyGlobalVariable.setValue(value);
     return persist(storyGlobalVariable);
   }

}
