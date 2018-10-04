package fi.metatavu.metamind.bot.functions;

import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;

import com.bladecoder.ink.runtime.Story;
import com.rabidgremlin.mutters.bot.ink.CurrentResponse;
import com.rabidgremlin.mutters.core.IntentMatch;
import com.rabidgremlin.mutters.core.session.Session;

import fi.metatavu.metamind.bot.utils.TextLanguageDetector;

/**
 * Metabot function for detecting language from text
 * 
 * @author Heikki Kurhinen
 */
@SuppressWarnings ("squid:S2245")
@ApplicationScoped
public class DetectLanguageMetaBotFunction extends AbstractMetaBotFunction {

  @Inject
  private Logger logger;

  @Override
  public String getFunctionName() {
    return "DETECT_LANGUAGE";
  }

  @Override
  public void execute(CurrentResponse currentResponse, Session session, IntentMatch intentMatch, Story story, String paramString) {
    Map<String, String> params = getParsedParam(paramString);

    String sourceVariableName = params.get("source");
    String targetVariableName = params.get("target");
    if (sourceVariableName == null || targetVariableName == null) {
      logger.error("Could not capitalize without source and target variable names");
      return;
    }

    String value = getVariableString(story, sourceVariableName);
    if (value != null) {
      try {
        String language = TextLanguageDetector.getInstance().getLanguage(value);
        setVariable(story, targetVariableName, language != null ? language : "");
      } catch (Exception e) {
        logger.error("Could not set variable state", e);
      }
    }
  }
}
