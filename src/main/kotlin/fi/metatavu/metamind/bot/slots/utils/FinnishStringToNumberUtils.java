package fi.metatavu.metamind.bot.slots.utils;

/**
 * Utility class to convert finnish word strings to numbers
 * 
 * @author Heikki Kurhinen
 *
 */
public class FinnishStringToNumberUtils {

  private static final int MAX_TOKENS = 100;
  
  private FinnishStringToNumberUtils() {
    // Private constructor
  }
  
  /**
   * Converts finnish word string to number
   * 
   * @param token Token to convert
   * @return Value of the word string converted to number or null if conversion cannot be made
   */
  @SuppressWarnings("squid:S3776") //It is complex
  public static Number finnishWordStringToNumber(String token) {
    if (token == null || token.length() < 1) {
      return null;
    }

    token = token.toLowerCase().replaceAll("[\\s+\\-,]", "").trim();
    FinnishWordToNumberResult result = new FinnishWordToNumberResult();
    
    int tokenCount = 0;
    
    while (token.length() > 0 && tokenCount < MAX_TOKENS) {
      tokenCount++;

      if (token.startsWith("miljoonaa")) {
        result.processMultiplierToken("miljoonaa", 1000000l);
      
      } else if (token.startsWith("miljoona")) {
        result.processSingleValueToken("miljoona", 1000000l);
      
      } else if (token.startsWith("tuhatta")) {
        result.processMultiplierToken("tuhatta", 1000l);
      
      } else if (token.startsWith("tuhat")) {
        result.processSingleValueToken("tuhat", 1000l);
      
      } else if (token.startsWith("sataa")) {
        result.processMultiplierToken("sataa", 100l);
      
      } else if (token.startsWith("satayhdeksänkymmentä")) {
        result.processValueToken("satayhdeksänkymmentä", 190l);
      
      } else if (token.startsWith("satakahdeksankymmentä")) {
        result.processValueToken("satakahdeksankymmentä", 180l);
      
      } else if (token.startsWith("sataseitsemänkymmentä")) {
        result.processValueToken("sataseitsemänkymmentä", 170l);
      
      } else if (token.startsWith("satakuusikymmentä")) {
        result.processValueToken("satakuusikymmentä", 160l);
      
      } else if (token.startsWith("sataviisikymmentä")) {
        result.processValueToken("sataviisikymmentä", 150l);
      
      } else if (token.startsWith("sataneljäkymmentä")) {
        result.processValueToken("sataneljäkymmentä", 140l);
      
      } else if (token.startsWith("satakolmekymmentä")) {
        result.processValueToken("satakolmekymmentä", 130l);
      
      } else if (token.startsWith("satakaksikymmentä")) {
        result.processValueToken("satakaksikymmentä", 120l);
      
      } else if (token.startsWith("sata")) {
        result.processValueToken("sata", 100l);
      
      } else if (token.startsWith("kymmentä")) {
        result.processMultiplierToken("kymmentä", 10l);
      
      } else if (token.startsWith("toista")) {
        result.processValueToken("toista", 10l);
      
      } else if (token.startsWith("kymmenen")) {
        result.processValueToken("kymmenen", 10l);
      
      } else if (token.startsWith("yhdeksän")) {
        result.processValueToken("yhdeksän", 9l);
      
      } else if (token.startsWith("kahdeksan")) {
        result.processValueToken("kahdeksan", 8l);
      
      } else if (token.startsWith("seitsemän")) {
        result.processValueToken("seitsemän", 7l);
      
      } else if (token.startsWith("kuusi")) {
        result.processValueToken("kuusi", 6l);
      
      } else if (token.startsWith("viisi")) {
        result.processValueToken("viisi", 5l);
      
      } else if (token.startsWith("neljä")) {
        result.processValueToken("neljä", 4l);
      
      } else if (token.startsWith("kolme")) {
        result.processValueToken("kolme", 3l);
      
      } else if (token.startsWith("kaksi")) {
        result.processValueToken("kaksi", 2l);
      
      } else if (token.startsWith("yksi")) {
        result.processValueToken("yksi", 1l);
      
      } else if (token.startsWith("nolla")) {
        result.processValueToken("nolla", 0l);
      
      } else {
        return null;
      }
      
      token = token.replaceFirst(result.getCurrentToken(), "");
    }
    
    result.finalizeResult();
    return result.getFinalResult();
  }
}
