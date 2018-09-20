package fi.metatavu.metamind.bot;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.rabidgremlin.mutters.core.Context;
import com.rabidgremlin.mutters.core.IntentMatch;
import com.rabidgremlin.mutters.core.IntentMatcher;

public class MultiIntentMatcher implements IntentMatcher {

  private List<OpenNLPIntentMatcher> matchers;
  private IntentMatcher templateMatcher;
  
  /**
   * Constructor for the class.
   * 
   * @param matchers List of intent matchers.
   * @param templateMatcher templated intent matcher
   */
  public MultiIntentMatcher(List<OpenNLPIntentMatcher> matchers, IntentMatcher templateMatcher) {
    this.matchers = matchers;
    this.templateMatcher = templateMatcher;
  }
  
  @Override
  public IntentMatch match(String utterance, Context context, Set<String> expectedIntents, HashMap<String, Object> debugValues) {
    IntentMatch match = null;
    
    for (IntentMatcher matcher : matchers) {
      match = matcher.match(utterance, context, expectedIntents, debugValues);
      
      if (match != null) {
        return match;
      }
    }
    
    if (match == null) {
      match = templateMatcher.match(utterance, context, expectedIntents, debugValues);
    }
    
    return match;
  }

}
