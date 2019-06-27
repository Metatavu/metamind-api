package fi.metatavu.metamind.bot.script;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.graalvm.polyglot.Value;

import fi.metatavu.metamind.google.custom_search.CustomSearchController;

/**
 * Bot script binding for custom search service
 * @author simeon
 *
 */
@ApplicationScoped
public class BotCustomSearchServiceScriptBinding {
  @Inject
  private CustomSearchController searchController;
  
  /**
   * Gets the search results from the custom search engine based on the search query
   * 
   * @param APIKey
   * @param searchEngineID id for the custom search engine
   * @param searchQuery
   * @return search results
   */
  public String getSearchResults(Value APIKey, Value searchEngineID, Value searchQuery) {
    if ( !APIKey.isString() || !searchEngineID.isString() || !searchQuery.isString() ) {
      return "";
    }
    
    return searchController.getSearchResults(APIKey.asString(), searchEngineID.asString(), searchQuery.asString());
  }
}
