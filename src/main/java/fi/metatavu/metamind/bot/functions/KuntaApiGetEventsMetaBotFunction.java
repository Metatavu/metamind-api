package fi.metatavu.metamind.bot.functions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.bladecoder.ink.runtime.Story;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabidgremlin.mutters.bot.ink.CurrentResponse;
import com.rabidgremlin.mutters.bot.ink.functions.FunctionDetails;
import com.rabidgremlin.mutters.bot.ink.functions.FunctionHelper;
import com.rabidgremlin.mutters.core.IntentMatch;
import com.rabidgremlin.mutters.core.session.Session;

import fi.metatavu.metamind.kuntaapi.Events;

/**
 * Metabot function to get events from KuntaAPI
 * 
 * @author Ville Koivukangas
 */
@ApplicationScoped
public class KuntaApiGetEventsMetaBotFunction extends AbstractMetaBotFunction {
  @Inject
  private Logger logger;

  @Override
  public String getFunctionName() {
    return "KUNTA_API_GET_EVENTS";
  }

  @Override
  public void execute(CurrentResponse currentResponse, Session session, IntentMatch intentMatch, Story story,
      String param) {
    FunctionDetails details = FunctionHelper.parseFunctionString(param);

    String authorizationToken = (String) session.getLongTermAttribute("authorizationToken");
    String organizationId = details.getFunctionParams().get("organizationId");
    String route = "events";
    String routeId = "";

    ArrayList<Events> events = new ArrayList<>();

    StringBuilder content = null;
    HttpURLConnection connection = null;

    connection = createHttpConnection(authorizationToken, organizationId, route, routeId);
    content = getContent(connection);
    events = parseContent(content);
    connection.disconnect();

    Random random = new Random();
    int rand = random.nextInt(events.size());

    try {
      story.getVariablesState().set("eventName", events.get(rand).getName());
      story.getVariablesState().set("eventDescription", events.get(rand).getDescription());
      story.getVariablesState().set("eventLocation", events.get(rand).getPlace());
      story.getVariablesState().set("eventAddress", events.get(rand).getAddress());
      story.getVariablesState().set("eventZip", events.get(rand).getZip());
      story.getVariablesState().set("eventCity", events.get(rand).getCity());
    } catch (Exception e) {
      logger.error("Error while setting variables at KUNTA_API_GET_EVENTS", e);
    }
  }

  /**
   * This method returns a HttpURLConnection based on organizationId, route and
   * routeId.
   * 
   * @param organizationId Organization id of kunta-api oranization.
   * @param route String The route where to get data.
   * @param routeId (Id or blank String) Route id to get specific data by id.
   * @return A HttpURLConnection.
   */
  private HttpURLConnection createHttpConnection(String authorizationToken, String organizationId, String route,
      String routeId) {
    URL url = null;

    try {
      url = new URL(String.format("http://api.kunta-api.fi/v1/organizations/%s/%s/%s", organizationId, route, routeId));
    } catch (NullPointerException | MalformedURLException e) {
      logger.error("Failed to create URL at KUNTA_API_GET_EVENTS");
    }

    HttpURLConnection connection = null;

    try {
      connection = (HttpURLConnection) url.openConnection();
    } catch (NullPointerException | IOException e) {
      logger.error("Failed to create connection at KUNTA_API_GET_EVENTS", e);
    }

    String redirect = connection.getHeaderField("Location");

    if (redirect != null) {
      try {
        connection = (HttpURLConnection) new URL(redirect).openConnection();
      } catch (NullPointerException | IOException e) {
        logger.error("Failed to set redirect URL at KUNTA_API_GET_EVENTS", e);
      }
    }

    try {
      connection.setRequestMethod("GET");
    } catch (NullPointerException | ProtocolException e) {
      logger.error("Failed to set request method GET at KUNTA_API_GET_EVENTS", e);
    }

    connection.setRequestProperty("Content-Type", "application/json");
    connection.setRequestProperty("Authorization", authorizationToken);

    return connection;
  }

  /**
   * This method returns a StringBuilder content from given connection.
   * 
   * @param connection HttpURLConnection.
   * @return A StringBuilder content based on connection.
   */
  private StringBuilder getContent(HttpURLConnection connection) {
    BufferedReader input = null;

    try {
      input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
    } catch (NullPointerException | IOException e) {
      logger.error("Failed to create BufferReader at KUNTA_API_GET_EVENTS", e);
    }

    String inputLine;
    StringBuilder content = new StringBuilder();

    try {
      while ((inputLine = input.readLine()) != null) {
        content.append(inputLine);
      }
    } catch (NullPointerException | IOException e) {
      logger.error("Failed to append data to StringBuffer at KUNTA_API_GET_EVENTS", e);
    }

    try {
      input.close();
    } catch (NullPointerException | IOException e) {
      logger.error("Failed to close BufferReader at KUNTA_API_GET_EVENTS", e);
    }
    return content;
  }

  /**
   * This method returns an ArrayList of events.
   * 
   * @param content StringBuilder content.
   * @return An ArrayList of events.
   */
  private ArrayList<Events> parseContent(StringBuilder content) {
    ObjectMapper objectMapper = new ObjectMapper();
    ArrayList<Events> events = null;

    try {
      events = objectMapper.readValue(content.toString(), new TypeReference<List<Events>>(){});
    } catch (NullPointerException | IOException e) {
      logger.error("Failed to read JSON string at parseContent KUNTA_API_GET_EVENTS", e);
    }

    return events;
  }
}
