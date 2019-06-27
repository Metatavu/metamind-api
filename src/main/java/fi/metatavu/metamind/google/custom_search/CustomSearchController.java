package fi.metatavu.metamind.google.custom_search;

import javax.inject.Inject;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;


/**
 * Controller for the custom search functionality
 * @author simeon
 *
 */
public class CustomSearchController {
  
  @Inject
  Logger logger;
  
  /**
   * Gets the search results from the custom search engine based on the search query
   * 
   * @param APIKey
   * @param searchEngineID id for the custom search engine
   * @param searchQuery
   * @return search results
   */
  public String getSearchResults(String APIKey, String searchEngineID, String searchQuery) {
    try {
      URIBuilder uriBuilder = new URIBuilder("https://www.googleapis.com/customsearch/v1");
      uriBuilder.addParameter("key", APIKey);
      uriBuilder.addParameter("cx", searchEngineID);
      uriBuilder.addParameter("q", searchQuery);
      
      HttpGet httpGet = new HttpGet(uriBuilder.build());
      CloseableHttpClient httpclient = HttpClients.custom().build();
      CloseableHttpResponse response = httpclient.execute(httpGet);
         
      String inline = EntityUtils.toString(response.getEntity());  
      httpclient.close();
      response.close();
      JSONParser parser = new JSONParser();
      JSONObject jsonObject = (JSONObject) parser.parse(inline);
      JSONArray results = (JSONArray) jsonObject.get("items");
      return results.toJSONString();
    } catch (Exception e) {
     logger.error("Error running script",e);
     return "";
    }
  }
}
