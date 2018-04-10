package fi.metatavu.metamind.bot.slots;

import com.rabidgremlin.mutters.core.Context;
import com.rabidgremlin.mutters.core.SlotMatch;
import com.rabidgremlin.mutters.slots.NumberSlot;

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
  
  private static final int MAX_TOKENS = 100;
  
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
    if (token == null || token.length() < 1)
    {
      return null;
    }

    token = token.toLowerCase().replaceAll("[\\s+\\-,]", "").trim();

    long result = 0;
    long finalResult = 0;
    int tokenCount = 0;
    
    while (token.length() > 0 && tokenCount < MAX_TOKENS) {
      tokenCount++;
      String foundToken = null;

      if (token.startsWith("miljoonaa")) {
        result *= 1000000;
        finalResult += result;
        result = 0;
        foundToken = "miljoonaa";
      } else if (token.startsWith("miljoona")) {
        finalResult += 1000000;
        result = 0;
        foundToken = "miljoona";
      } else if (token.startsWith("tuhatta")) {
        result *= 1000;
        finalResult += result;
        result = 0;
        foundToken = "tuhatta";
      } else if (token.startsWith("tuhat")) {
        result += 1000;
        foundToken = "tuhat";
      } else if (token.startsWith("sataa")) {
        result *= 100;
        finalResult += result;
        result = 0;
        foundToken = "sataa";
      } else if (token.startsWith("sata")) {
        result += 100;
        foundToken = "sata";
      } else if (token.startsWith("kymmentä")) {
        result *= 10;
        finalResult += result;
        result = 0;
        foundToken = "kymmentä";
      } else if (token.startsWith("toista")) {
        result += 10;
        foundToken = "toista";
      } else if (token.startsWith("kymmenen")) {
        result += 10;
        foundToken = "kymmenen";
      } else if (token.startsWith("yhdeksän")) {
        result += 9;
        foundToken = "yhdeksän";
      } else if (token.startsWith("kahdeksan")) {
        result += 8;
        foundToken = "kahdeksan";
      } else if (token.startsWith("seitsemän")) {
        result += 7;
        foundToken = "seitsemän";
      } else if (token.startsWith("kuusi")) {
        result += 6;
        foundToken = "kuusi";
      } else if (token.startsWith("viisi")) {
        result += 5;
        foundToken = "viisi";
      } else if (token.startsWith("neljä")) {
        result += 4;
        foundToken = "neljä";
      } else if (token.startsWith("kolme")) {
        result += 3;
        foundToken = "kolme";
      } else if (token.startsWith("kaksi")) {
        result += 2;
        foundToken = "kaksi";
      } else if (token.startsWith("yksi")) {
        result += 1;
        foundToken = "yksi";
      } else if (token.startsWith("nolla")) {
        result += 0;
        foundToken = "nolla";
      } else {
        return null;
      }
      
      token = token.replaceFirst(foundToken, "");
    }
    
    finalResult += result;
    result = 0;
    return finalResult;
  }

}
