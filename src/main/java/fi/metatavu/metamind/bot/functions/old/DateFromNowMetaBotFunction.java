package fi.metatavu.metamind.bot.functions.old;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.commons.lang3.EnumUtils;
import org.slf4j.Logger;

import com.bladecoder.ink.runtime.Story;
import com.rabidgremlin.mutters.bot.ink.CurrentResponse;
import com.rabidgremlin.mutters.core.IntentMatch;
import com.rabidgremlin.mutters.core.session.Session;

/**
 * Metabot function to parse date from now in given variable
 * 
 * @author Heikki Kurhinen
 */
@ApplicationScoped
public class DateFromNowMetaBotFunction extends AbstractMetaBotFunction {

  @Inject
  private Logger logger;

  /**
   * Name to identify then function in ink story by
   * @return function name
   */
  @Override
  public String getFunctionName() {
    return "DATE_FROM_NOW";
  }

  /**
   * Calculates date from current date until date given in target function parameter
   * 
   * @param currentResponse current bot response
   * @param session current bot session
   * @param intentMatch current intent match
   * @param story current story
   * @param paramString bot function parameters as string
   */
  @Override
  public void execute(CurrentResponse currentResponse, Session session, IntentMatch intentMatch, Story story, String paramString) {
    Map<String, String> params = getParsedParam(paramString);
    String format = params.get("format");
    String unit = params.get("unit");
    String source = params.get("source");
    String target = params.get("target");

    if (source == null) {
      logger.error("Cannot get date from now, missing source variable name");
      return;
    }
    
    if (target == null) {
      logger.error("Cannot get date from now, missing target variable name");
      return;
    }
    
    if (format == null) {
      logger.error("Cannot get date from now, missing date input format");
      return;
    }
    
    if (unit == null) {
      logger.error("Cannot get date from now, missing unit");
      return;
    }

    ChronoUnit chronoUnit = EnumUtils.getEnum(ChronoUnit.class, unit);

    if (chronoUnit == null) {
      logger.error("Invalid unit %s", unit);
      return;
    }

    String dateInput = getVariableString(story, source);

    if (dateInput == null) {
      logger.error("Cannot get value from variable %s", source);
      return;
    }

    try {
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
      LocalDate localDate = LocalDate.parse(dateInput, formatter);
      Long unitsFromNow = chronoUnit.between(LocalDate.now(), localDate);
      story.getVariablesState().set(target, unitsFromNow);
    } catch (DateTimeParseException dateTimeParseException) {
      logger.error(String.format("Error while parsing date from input %s", dateInput), dateTimeParseException);
    } catch (Exception exception) {
      logger.error("Error while getting date from now", exception);
    }
  }

}
