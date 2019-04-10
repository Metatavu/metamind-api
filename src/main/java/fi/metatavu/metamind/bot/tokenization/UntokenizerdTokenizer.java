package fi.metatavu.metamind.bot.tokenization;

/**
 * Tokenizer that returns string as untokenized
 * 
 * @author Antti Leppä
 */
public class UntokenizerdTokenizer implements Tokenizer {

  @Override
  public String[] tokenize(String text) {
    return new String[] { text };
  }

}
