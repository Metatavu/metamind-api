package fi.metatavu.metamind.faq;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.inject.Inject;
import javax.net.ssl.HttpsURLConnection;

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
      URL url = new URL("http://localhost/api.php?action=search&lang=fi&q="+searchText);
      JSONArray jsonArray = getResponseJSONArray(url);
      List<String> answersList = new ArrayList<String>();
        
      for ( int i = 0 ; i < jsonArray.size() ; i++ ) {
        JSONObject result = (JSONObject) jsonArray.get(i);
        String categoryId = result.get("category_id").toString();
        if( filterCategoryId == null || filterCategoryId.equals(categoryId) ) {
          answersList.add(result.get("answer").toString());
        } 
      }
      String[] answers = (String[]) answersList.toArray();
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
      URL url = new URL("http://localhost/api.php?action=getCategories&lang=fi");
      JSONArray jsonArray = getResponseJSONArray(url);
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
  public JSONArray getResponseJSONArray(URL url){
    try {
           
      Scanner scanner = new Scanner(url.openStream());
      String inline = "";
        
      while ( scanner.hasNext() ) {
        inline+=scanner.nextLine();
      }
        
      scanner.close();
      JSONParser parser = new JSONParser();
      JSONArray jsonArray = (JSONArray) parser.parse(inline);
      return jsonArray;
    } catch (Exception e) {
      logger.error("Error running script",e);
      return new JSONArray();
    }

  }
}
  
     