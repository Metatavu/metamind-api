package fi.metatavu.metamind.bot.slots;

import com.rabidgremlin.mutters.core.Context;
import com.rabidgremlin.mutters.core.SlotMatch;
import com.rabidgremlin.mutters.slots.NumberSlot;

import fi.metatavu.metamind.bot.slots.utils.FinnishStringToNumberUtils;

/**
 * Class to extend mutters NumberSlot to support multiple languages
 * 
 * @author Heikki Kurhinen
 *
 */
public class LocalizedNumberSlot extends NumberSlot {

  private static final String[] SUPPORTED_LANGUAGES = {
    "finnish",
    "english"
  };
  
  public LocalizedNumberSlot(String name) {
    super(name);
  }

  @Override
  public SlotMatch match(String token, Context context) {

    try {
      Long value = Long.parseLong(token);

      return new SlotMatch(this, token, value);
    } catch (NumberFormatException de) {
      
      try {
        Double value = Double.parseDouble(token);
        
        return new SlotMatch(this, token, value);
      } catch (NumberFormatException le) {

        Number value = localizedWordStringToNumber(token);
        if (value != null) {
          return new SlotMatch(this, token, value);
        } else {
          return null;
        }
      }
    }
  }
  
  @SuppressWarnings("squid:S1301")
  private Number localizedWordStringToNumber(String token) {
    for (String language : SUPPORTED_LANGUAGES) {
      Number result = null;
      switch (language) {
        case "finnish":
          result = finnishWordStringToNumber(token);
        break;
        case "english":
          result = englishWordStringToNumber(token);
        break;
        default:
          return null;
      }
      
      if (result != null) {
        return result;
      }
    }

    return null;
  }
  
  private Number englishWordStringToNumber(String token) {
    return wordStringToNumber(token);
  }
  
  private Number finnishWordStringToNumber(String token) {
    return FinnishStringToNumberUtils.finnishWordStringToNumber(token);
  }

}
