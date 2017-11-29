package fi.metatavu.metamind.bot.functions;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;

import com.rabidgremlin.mutters.bot.ink.SessionUtils;
import com.rabidgremlin.mutters.core.IntentMatch;
import com.rabidgremlin.mutters.core.Slot;
import com.rabidgremlin.mutters.core.SlotMatch;
import com.rabidgremlin.mutters.core.session.Session;

import fi.metatavu.metamind.freemarker.FreemarkerRenderer;

/**
 * Abstract base class for freemarker metabot functions
 * 
 * @author Antti Leppä
 */
public abstract class AbstractFreemarkerMetaBotFunction extends AbstractMetaBotFunction {
  
  @Inject
  private FreemarkerRenderer freemarkerRenderer;
  
  /**
   * Renders a freemarker template with session data
   * 
   * @param templateName name of the template
   * @param session session
   * @param intentMatch intent match
   * @return rendered template
   */
  protected String getRenderedText(String templateName, Session session, IntentMatch intentMatch) {
    return freemarkerRenderer.render(templateName, getSessionAttributes(session, intentMatch), new Locale("fi"));
  }

  private Map<String, Object> getSessionAttributes(Session session, IntentMatch intentMatch) {
    Map<String, Object> result = new HashMap<>();
    
    Map<Slot, SlotMatch> slotMatches = intentMatch.getSlotMatches();
    for (Slot slot : slotMatches.keySet()) {
      String slotName = slot.getName();
      Object value = SessionUtils.getStringFromSlotOrSession(intentMatch, session, slotName, null);
      result.put(slotName.toLowerCase(), value);
    }
    
    return result;
  }

}
