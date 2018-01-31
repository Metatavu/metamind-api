package fi.metatavu.metamind.bot.functions;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;

import java.util.List;
import java.util.Random;

import com.bladecoder.ink.runtime.Story;
import com.rabidgremlin.mutters.bot.ink.CurrentResponse;
import com.rabidgremlin.mutters.bot.ink.functions.FunctionDetails;
import com.rabidgremlin.mutters.bot.ink.functions.FunctionHelper;
import com.rabidgremlin.mutters.core.IntentMatch;
import com.rabidgremlin.mutters.core.session.Session;

import fi.metatavu.kuntaapi.ApiClient;
import fi.metatavu.kuntaapi.ApiException;
import fi.metatavu.kuntaapi.client.EventsApi;
import fi.metatavu.kuntaapi.client.model.Event;

/**
 * Metabot function to get events from KuntaAPI
 * 
 * @author Ville Koivukangas
 */
@ApplicationScoped
public class KuntaApiGetEventsMetaBotFunction extends AbstractMetaBotFunction {
  @Inject
  private Logger logger;
  
  @Inject
  private ApiClient kuntaApiClient;

  @Override
  public String getFunctionName() {
    return "KUNTA_API_GET_EVENTS";
  }

  @Override
  public void execute(CurrentResponse currentResponse, Session session, IntentMatch intentMatch, Story story,
      String param) {
    
   EventsApi apiInstance = new EventsApi();
   apiInstance.setApiClient(kuntaApiClient);
   
   FunctionDetails details = FunctionHelper.parseFunctionString(param);
   
   if (details == null) {
     logger.error("Function parameters is null at KUNTA_API_GET_EVENTS");
     return;
   }
   
   String organizationId = details.getFunctionParams().get("organizationId");
   String startBefore = details.getFunctionParams().get("startBefore");
   String startAfter = details.getFunctionParams().get("startAfter");
   String endBefore = details.getFunctionParams().get("endBefore");
   String endAfter = details.getFunctionParams().get("endAfter"); 
   String orderBy = details.getFunctionParams().get("orderBy"); 
   String orderDir = details.getFunctionParams().get("orderDir");
   Integer firstResult = NumberUtils.toInt(details.getFunctionParams().get("firstResult"));
   Integer maxResults = NumberUtils.toInt(details.getFunctionParams().get("maxResults"));
   boolean randomResult = false;
   
   if (details.getFunctionParams().get("randomResult") != null) {
     randomResult = Boolean.parseBoolean(details.getFunctionParams().get("randomResult"));
   }
   
   List<Event> result = null;
   
   try {
       result = apiInstance.listOrganizationEvents(organizationId, startBefore, startAfter, endBefore, endAfter, firstResult, maxResults, orderBy, orderDir);
   } catch (ApiException e) {
       logger.error("Exception when calling EventsApi#listOrganizationEvents", e);
   }
   
   if (result == null) {
     logger.error("Results from kuntaApi is null at KUNTA_API_GET_EVENTS");
     return;
   }
   
   int eventIndex = 0;
   
   if (randomResult) {
     Random random = new Random();
     eventIndex = random.nextInt(result.size());
   }
   
   try {
     story.getVariablesState().set("eventName", result.get(eventIndex).getName());
     story.getVariablesState().set("eventDescription", result.get(eventIndex).getDescription());
     story.getVariablesState().set("eventLocation", result.get(eventIndex).getPlace());
     story.getVariablesState().set("eventAddress", result.get(eventIndex).getAddress());
     story.getVariablesState().set("eventZip", result.get(eventIndex).getZip());
     story.getVariablesState().set("eventCity", result.get(eventIndex).getCity());
   } catch (Exception e) {
     logger.error("Error while setting variables at KUNTA_API_GET_EVENTS", e);
   }

  }
}
