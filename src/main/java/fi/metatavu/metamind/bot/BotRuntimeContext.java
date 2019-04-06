package fi.metatavu.metamind.bot;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;

import fi.metatavu.metamind.persistence.models.Intent;
import fi.metatavu.metamind.persistence.models.Knot;
import fi.metatavu.metamind.persistence.models.Session;

/**
 * Bot runtime context for holding state within single request
 * 
 * @author Antti Leppä
 */
@RequestScoped
public class BotRuntimeContext {
  
  private UUID loggedUserId;
  private Session session;
  private Intent matchedIntent;
  private Knot currentKnot;
  private String response;
  private List<String> responseBefore;
  private List<String> responseAfter;
  
  /**
   * Post construct method
   */
  @PostConstruct
  public void init() {
    responseBefore = new ArrayList<>();
    responseAfter = new ArrayList<>();
  }
  
  /**
   * Returns the session
   * 
   * @return the session
   */
  public Session getSession() {
    return session;
  }

  /**
   * Sets the session
   * 
   * @param session the session
   */
  public void setSession(Session session) {
    this.session = session;
  }
  
  /**
   * Returns the logged user id
   * 
   * @return the logged user id
   */
  public UUID getLoggedUserId() {
    return loggedUserId;
  }

  /**
   * Sets the logged user id
   * 
   * @param loggedUserId the logged user id
   */
  public void setLoggedUserId(UUID loggedUserId) {
    this.loggedUserId = loggedUserId;
  }
  
  /**
   * Returns all responses in single list
   * 
   * @return all responses in single list
   */
  public List<String> getResponses() {
    List<String> result = new ArrayList<>(responseBefore);
    result.add(response);
    result.addAll(responseAfter);
    return result;
  }
  
  /**
   * Sets main response text
   * 
   * @param response main response text
   */
  public void setResponse(String response) {
    this.response = response;
  }

  /**
   * Prepends a response before the main response
   * 
   * @param response response
   */
  public void prependResponse(String response) {
    this.responseBefore.add(response);
  }
  
  /**
   * Appends a response after the main response
   * 
   * @param response response
   */
  public void appendResponse(String response) {
    this.responseAfter.add(response);
  }
  
  /**
   * Changes the current knot where the message ends up 
   * 
   * @param currentKnot new current knot
   */
  public void setCurrentKnot(Knot currentKnot) {
    this.currentKnot = currentKnot;
  }
  
  /**
   * Returns the knot where user is going to end up
   * 
   * @return the knot where user is going to end up
   */
  public Knot getCurrentKnot() {
    return currentKnot;
  }
  
  /**
   * Returns matched intent
   * 
   * @return matched intent
   */
  public Intent getMatchedIntent() {
    return matchedIntent;
  }
  
  /**
   * Sets the matched intent
   * 
   * @param matchedIntent matched intent
   */
  public void setMatchedIntent(Intent matchedIntent) {
    this.matchedIntent = matchedIntent;
  }
  
}
