package fi.metatavu.metamind.bot.variables;

import java.util.Map;
import java.util.UUID;

/**
 * Interface describing single variable finder
 * 
 * @author Antti Leppä
 */
public interface VariableFinder {
  
  /**
   * Finds variables from the text
   * 
   * @param text text
   * @return found variables or null if finder could not find any
   */
  public Map<UUID, String> findVariables(String text);
  
}
