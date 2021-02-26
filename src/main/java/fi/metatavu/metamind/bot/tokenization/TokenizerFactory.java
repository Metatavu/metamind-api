package fi.metatavu.metamind.bot.tokenization;

import fi.metatavu.metamind.api.spec.model.TokenizerType;

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
  public static Tokenizer createTokenizer(TokenizerType type) {
    switch (type) {
      case UNTOKENIZED:
        return new UntokenizedTokenizer();
      case WHITESPACE:
        return new WhitespaceTokenizer();
    }
    
    return null;
  }

}
