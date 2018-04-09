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

    token = token.replaceAll("-", " ");
    token = token.replaceAll(",", " ");
    token = token.toLowerCase().replaceAll(" ja", " ");
    String[] splittedParts = token.trim().split("\\s+");

    long result = 0;

    for (String str : splittedParts) {
      str = str.toLowerCase();
      switch (str) {
        case "nolla":
          result += 0;
        break;
        case "yksi":
          result += 1;
        break;
        case "kaksi":
          result += 2;
        break;
        case "kolme":
          result += 3;
        break;
        case "neljä":
          result += 4;
        break;
        case "viisi":
          result += 5;
        break;
        case "kuusi":
          result += 6;
        break;
        case "seitsemän":
          result += 7;
        break;
        case "kahdeksan":
          result += 8;
        break;
        case "yhdeksän":
          result += 9;
        break;
        case "kymmenen":
          result += 10;
        break; 
        case "yksitoista":
          result += 11;
        break; 
        case "kaksitoista":
          result += 12;
        break;
        case "kolmetoista":
          result += 13;
        break;
        case "neljätoista":
          result += 14;
        break;
        case "viisitoista":
          result += 15;
        break;
        case "kuusitoista":
          result += 16;
        break;
        case "seitsemäntoista":
          result += 17;
        break;
        case "kahdeksantoista":
          result += 18;
        break;
        case "yhdeksäntoista":
          result += 19;
        break;
        case "kaksikymmentä":
          result += 20;
        break;
        case "kolmekymmentä":
          result += 30;
        break;
        case "neljäkymmentä":
          result += 40;
        break;
        case "viisikymmentä":
          result += 50;
        break;
        case "kuusikymmentä":
          result += 60;
        break;
        case "seitsemänkymmentä":
          result += 70;
        break;
        case "kahdeksankymmentä":
          result += 80;
        break;
        case "yhdeksänkymmentä":
          result += 90;
        break;
        case "sata":
          result += 100;
        case "sataa":
          result *= 100;
        break;
        case "tuhat":
          result += 1000;
        break;
        case "tuhatta":
          result *= 1000;
        break;
        case "miljoona":
          result += 1000000;
        break;
        case "miljoonaa":
          result *= 1000000;
        break;
        default:
          return null;
      }
    }

    return result;
  }

}
