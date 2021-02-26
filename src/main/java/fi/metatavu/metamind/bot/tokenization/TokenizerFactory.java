package fi.metatavu.metamind.bot.tokenization;

/**
 * Factory class for tokenizers
 * 
 * @author Antti Leppä
 */
public class TokenizerFactory {
  
  private TokenizerFactory() {
    // Private constructor
  }
  
  /**
   * Constructs a tokenizer for a type
   * 
   * @param type type
   * @return tokenizer
   */
  public static Tokenizer createTokenizer(fi.metatavu.metamind.rest.model.TokenizerType type) {
    switch (type) {
      case UNTOKENIZED:
        return new UntokenizedTokenizer();
      case WHITESPACE:
        return new WhitespaceTokenizer();
    }
    
    return null;
  }

}
