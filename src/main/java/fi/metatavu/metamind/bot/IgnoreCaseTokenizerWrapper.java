package fi.metatavu.metamind.bot;

import org.apache.commons.lang3.StringUtils;

import com.rabidgremlin.mutters.core.Tokenizer;

public class IgnoreCaseTokenizerWrapper implements Tokenizer {
  
  private Tokenizer impl;

  public IgnoreCaseTokenizerWrapper(Tokenizer impl) {
    this.impl = impl;
  }
  
  @Override
  public String[] tokenize(String text) {
    return impl.tokenize(StringUtils.lowerCase(text));
  }

}
