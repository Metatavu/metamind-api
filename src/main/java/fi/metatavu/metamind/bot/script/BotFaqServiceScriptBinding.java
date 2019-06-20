package fi.metatavu.metamind.bot.script;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.graalvm.polyglot.Value;

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
  * Gets answers to the question in JSON format
  * 
  * @param searchString
  * @param category
  * @return searchResults
  */
  public String getAnswers(Value searchText) {
    if ( !searchText.isString() ) {
      return "";
    }
    return faqController.getAnswers(searchText.asString(), null);
  }
  public String getAnswers(Value searchText,Value filterCategoryId) {
    if ( !searchText.isString() ) {
      return "";
    }
    String category = null;
    if ( filterCategoryId.isString() ) {
      category = filterCategoryId.asString();
    }
    return faqController.getAnswers(searchText.asString(), category);
  }
  
  /**
   * Gets all FAQ-categories
   * 
   * @return returns categories in JSON format
   */
  public String getCategories() {
    return faqController.getCategories();
  }
  
  /**
   * Gets a single answer in JSON format
   * 
   * @param id
   * @return answer
   */
  public String getSingleAnswer(Value id) {
    if(!id.isString()) {
      return "";
    }
    return faqController.getSingleAnswer(id.asString());
  }
}
