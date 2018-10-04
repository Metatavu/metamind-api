package fi.metatavu.metamind.bot;

import org.apache.commons.lang3.StringUtils;

import com.rabidgremlin.mutters.core.Tokenizer;

/**
 * Tokenizer wrapper that lower cases text before tokenization
 * 
 * @author Antti Leppä
 */
public class IgnoreCaseTokenizerWrapper implements Tokenizer {
  
  private Tokenizer impl;

  /**
   * Constructor
   * 
   * @param impl actual tokenizer
   */
  public IgnoreCaseTokenizerWrapper(Tokenizer impl) {
    this.impl = impl;
  }
  
  @Override
  public String[] tokenize(String text) {
    return impl.tokenize(StringUtils.lowerCase(text));
  }

}
