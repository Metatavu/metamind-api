package fi.metatavu.metamind.faq;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
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
   * Gets answers to the question
   * 
   * @param searchString
   * @param category
   * @return searchResults
   * @throws IOException 
   * @throws ParseException 
   */
 
  public String[] getAnswers(String searchText,String filterCategoryId){
    try {
      URIBuilder builder = new URIBuilder("http://localhost/phpmyfaq/api.php");
      builder.addParameter("action", "search");
      builder.addParameter("q", searchText);
      JSONArray jsonArray = getResponseJSONArray(builder.build());
      List<String> answersList = new ArrayList<String>();
        
      for ( int i = 0 ; i < jsonArray.size() ; i++ ) {
        JSONObject result = (JSONObject) jsonArray.get(i);
        String categoryId = result.get("category_id").toString();
        if( filterCategoryId == null || filterCategoryId.equals(categoryId) ) {
          answersList.add(result.get("answer").toString());
        } 
      }
      String[] answers = new String[answersList.size()];
      for( int i = 0 ; i < answersList.size() ; i++ ) {
        answers[i] = answersList.get(i);
      }
      return answers;
    } catch (Exception e) {
      logger.error("Error running script",e);
      return new String[0];
    }

  }
  
  /**
   * Gets all FAQ-categories
   * 
   * @return returns categories in JSON format
   * @throws ParseException
   * @throws IOException
   */
  public String getCategories() {
    try {
      URIBuilder builder = new URIBuilder("http://localhost/phpmyfaq/api.php");
      builder.addParameter("action", "getCategories");
      JSONArray jsonArray = getResponseJSONArray(builder.build());
      return jsonArray.toJSONString();
    } catch (Exception e) {
      logger.error("Error running script",e);
      return "";
    }

  }
  
  /**
   * Sends a get request and returns the response body in JSON-array format 
   * 
   * @param url
   * @return
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
      JSONArray jsonArray = (JSONArray) parser.parse(inline);
      return jsonArray;
    } catch (Exception e) {
      logger.error("Error running script",e);
      return new JSONArray();
    }

  }
}
  
     