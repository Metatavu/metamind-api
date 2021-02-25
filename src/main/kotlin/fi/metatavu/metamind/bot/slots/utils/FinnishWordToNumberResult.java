package fi.metatavu.metamind.bot.slots.utils;

/**
 * Class for holding results and data while converting word strings to numbers
 * 
 * @author Heikki Kurhinens
 *
 */
public class FinnishWordToNumberResult {

  private long temporaryResult;
  
  private long finalResult;
  
  private String currentToken;
  
  private boolean lastOperationWasMultiplier;
  
  public FinnishWordToNumberResult() {
    this.temporaryResult = 0;
    this.finalResult = 0;
    this.currentToken = null;
    this.lastOperationWasMultiplier = false;
  }

  /**
   * Processes token which cases result to be multiplied with some multiplier
   * 
   * @param token new token
   * @param multiplier multiplier to multiply the result with
   */
  public void processMultiplierToken(String token, Long multiplier) {
    temporaryResult *= multiplier;
    currentToken = token;
    lastOperationWasMultiplier = true;
  }
  
  /**
   * Processes token which will determine its complete value later
   * 
   * @param token new token
   * @param value value to add to the temporary value
   */
  public void processValueToken(String token, Long value) {
    if (lastOperationWasMultiplier) {
      finalResult += temporaryResult;
      temporaryResult = 0;
    }

    temporaryResult += value;
    currentToken = token;
  }
  
  /**
   * Processes token which will be added to the final result and no later tokens will change that tokens value
   * 
   * @param token new token
   * @param value value to add to the final result
   */
  public void processSingleValueToken(String token, Long value) {
    lastOperationWasMultiplier = false;
    finalResult += value;
    temporaryResult = 0;
    currentToken = token;
  }
  
  /**
   * Finalizes the result by adding the current temporary value to the final result
   * 
   */
  public void finalizeResult() {
    this.finalResult += this.temporaryResult;
  }
  
  public long getFinalResult() {
    return finalResult;
  }
  
  public void setFinalResult(long finalResult) {
    this.finalResult = finalResult;
  }

  public String getCurrentToken() {
    return currentToken;
  }

  public void setCurrentToken(String currentToken) {
    this.currentToken = currentToken;
  }
}
