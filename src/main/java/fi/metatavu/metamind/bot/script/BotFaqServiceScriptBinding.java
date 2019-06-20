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
  * @param baseAPIURL
  * @return searchResults
  */
  public String getAnswers(Value baseAPIURL, Value searchText) {
    if ( !searchText.isString() || !baseAPIURL.isString() ) {
      return "";
    }
    return faqController.getAnswers(baseAPIURL.asString(), searchText.asString(), null);
  }
  public String getAnswers(Value baseAPIURL, Value searchText,Value filterCategoryId) {
    if ( !searchText.isString() || !baseAPIURL.isString() ) {
      return "";
    }
    String category = null;
    if ( filterCategoryId.isString() ) {
      category = filterCategoryId.asString();
    }
    return faqController.getAnswers(baseAPIURL.asString(), searchText.asString(), category);
  }
  
  /**
   * Gets all FAQ-categories
   * 
   * @param baseAPIURL
   * @return returns categories in JSON format
   */
  public String getCategories(Value baseAPIURL) {
    if ( !baseAPIURL.isString() ) {
      return "";
    }
    return faqController.getCategories(baseAPIURL.asString());
  }
  
  /**
   * Gets a single answer in JSON format
   * 
   * @param id
   * @param baseAPIURL
   * @return answer
   */
  public String getSingleAnswer(Value baseAPIURL, Value id) {
    if ( !id.isString() || !baseAPIURL.isString() ) {
      return "";
    }
    return faqController.getSingleAnswer(baseAPIURL.asString(),id.asString());
  }
}
