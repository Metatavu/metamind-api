package fi.metatavu.metamind.bot.script;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.metamind.faq.FaqController;

/**
 * Bot script binding for FAQ service
 * 
 * @author Simeon Platonov
 */
@ApplicationScoped
public class BotFaqServiceScriptBinding {
  @Inject
  private FaqController faqController;

  
  /**
  * Gets answers to the question
  * 
  * @param searchString
  * @param category
  * @return searchResults
  */
  public String[] getAnswers(String searchText) {
    return getAnswers(searchText,null);
  }
  public String[] getAnswers(String searchText,String filterCategoryId) {
    return faqController.getAnswers(searchText, filterCategoryId);
  }
  
  /**
   * Gets all FAQ-categories
   * 
   * @return returns categories in JSON format
   */
  public String getCategories() {
    return faqController.getCategories();
  }
}
