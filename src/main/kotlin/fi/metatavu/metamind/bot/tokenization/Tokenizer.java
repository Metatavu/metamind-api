package fi.metatavu.metamind.bot.tokenization;

/**
 * Describes a string tokenizer
 * 
 * @author Antti Leppä
 */
public interface Tokenizer {

  /**
   * Splits string into its atomic parts
   *
   * @param text The string to be tokenized.
   * @return tokenized string.
   */
  public String[] tokenize(String text);
  
}
