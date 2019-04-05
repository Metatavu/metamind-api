package fi.metatavu.metamind.bot.functions.old;

import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.commons.lang3.text.WordUtils;
import org.slf4j.Logger;

import com.bladecoder.ink.runtime.Story;
import com.rabidgremlin.mutters.bot.ink.CurrentResponse;
import com.rabidgremlin.mutters.core.IntentMatch;
import com.rabidgremlin.mutters.core.session.Session;

/**
 * Metabot function for capitalizing words in a String into capitalized words, that is each word is made up of a titlecase character and then a series of lowercase characters.
 * 
 * @author Antti Leppä
 * @author Heikki Kurhinen
 */
@ApplicationScoped
public class CapitalizeFullyMetaBotFunction extends AbstractMetaBotFunction {

  @Inject
  private Logger logger;

  @Override
  public String getFunctionName() {
    return "CAPITALIZE_FULLY";
  }
  
  @Override
  public void execute(CurrentResponse currentResponse, Session session, IntentMatch intentMatch, Story story, String paramString) {
    Map<String, String> params = getParsedParam(paramString);
    
    String variableName = params.get("variable");
    String delimiters = params.get("delimiters");
    if (variableName == null) {
      logger.error("Could not capitalize without variable name");
      return;
    }
    
    if (delimiters == null) {
      delimiters = " ";
    }
    
    String value = getVariableString(story, variableName);
    if (value != null) {
      String capitalized = WordUtils.capitalizeFully((String) value, delimiters.toCharArray());
      try {
        setVariable(story, variableName, capitalized);
      } catch (Exception e) {
        logger.error("Could not set variable state", e);
        return;
      }
    }
  }

}
