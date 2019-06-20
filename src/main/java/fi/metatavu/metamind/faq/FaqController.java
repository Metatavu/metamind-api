package fi.metatavu.metamind.faq;
import java.io.IOException;
import java.net.URI;
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
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;

/**
 * Controller for FAQ-functionality
 * @author Simeon Platonov
 *
 */
public class FaqController {
  @Inject
  private Logger logger;
  /**
   * Gets answers to the question in JSON format
   * 
   * @param searchString
   * @param category
   * @param baseAPIURL
   * @return searchResults
   * @throws IOException 
   * @throws ParseException 
   */
 
  public String getAnswers(String baseAPIURL, String searchText,String filterCategoryId){
    try {
      URIBuilder builder = new URIBuilder(baseAPIURL);
      builder.addParameter("action", "search");
      builder.addParameter("q", searchText);
      JSONArray answers = getResponseJSONArray(builder.build());
      JSONArray filteredAnswers = new JSONArray();
        
      for ( int i = 0 ; i < answers.size() ; i++ ) {
        JSONObject result = (JSONObject) answers.get(i);
        String categoryId = result.get("category_id").toString();
        if( filterCategoryId == null || filterCategoryId.equals(categoryId) ) {
          filteredAnswers.add(result);
        } 
      }
      return filteredAnswers.toJSONString();
    } catch (Exception e) {
      logger.error("Error running script",e);
      return "";
    }

  }
  
  /**
   * Gets all FAQ-categories in JSON format
   * 
   * @param baseAPIURL
   * @return returns categories
   * @throws ParseException
   * @throws IOException
   */
  public String getCategories(String baseAPIURL) {
    try {
      URIBuilder builder = new URIBuilder(baseAPIURL);
      builder.addParameter("action", "getCategories");
      JSONArray jsonArray = getResponseJSONArray(builder.build());
      return jsonArray.toJSONString();
    } catch (Exception e) {
      logger.error("Error running script",e);
      return "";
    }

  }
  
  /**
   * Gets a single answer
   * 
   * @param baseAPIURL
   * @param id
   * @return answer
   */
  public String getSingleAnswer(String baseAPIURL, String id) {
    try {
      URIBuilder builder = new URIBuilder(baseAPIURL);
      builder.addParameter("action", "getFaq");
      builder.addParameter("recordId", id);
      JSONArray jsonArray = getResponseJSONArray(builder.build());
      String answer = jsonArray.toJSONString();
      return answer;
    } catch (Exception e) {
      logger.error("Error running script",e);
      return "";
    }
  };
  
  /**
   * Sends a get request and returns the response body in JSON-array format 
   * 
   * @param url
   * @return response body in JSON-array format
   * @throws ParseException
   * @throws IOException
   */
  public JSONArray getResponseJSONArray(URI uri){
    try {
      HttpGet httpGet = new HttpGet(uri);
      CloseableHttpClient httpclient = HttpClients.custom().build();
      CloseableHttpResponse response = httpclient.execute(httpGet);  
      String inline = EntityUtils.toString(response.getEntity());
      JSONParser parser = new JSONParser();
      JSONArray jsonArray;
      try {
        jsonArray = (JSONArray) parser.parse(inline);
      } catch (Exception e) {
        JSONObject tempObject = (JSONObject) parser.parse(inline);
        jsonArray = new JSONArray();
        jsonArray.add(tempObject);
      }
      
      return jsonArray;
    } catch (Exception e) {
      logger.error("Error running script",e);
      return new JSONArray();
    }

  }
}
  
     