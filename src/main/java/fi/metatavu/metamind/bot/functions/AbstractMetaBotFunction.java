package fi.metatavu.metamind.bot.functions;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

public abstract class AbstractMetaBotFunction implements MetaBotFunction {

  /**
   * Parses param string as map. 
   * 
   * Format is (param1:value1,param2:value)
   * 
   * @param paramString parameter string
   * @return parsed map
   */
  protected Map<String, String> getParsedParam(String paramString) {
    Map<String, String> result = new HashMap<>();
    
    String[] params = StringUtils.split(StringUtils.stripStart(StringUtils.stripEnd(paramString, ")"), "("), ",");
    for (String param : params) {
      String[] parts = StringUtils.split(param, ":", 2);
      if (parts.length == 2) {
        result.put(StringUtils.trim(parts[0]), parts[1]);
      }
    }
    
    return result;
  }
  
}
