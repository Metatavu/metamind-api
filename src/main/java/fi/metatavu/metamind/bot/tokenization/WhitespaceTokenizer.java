package fi.metatavu.metamind.bot.tokenization;

/**
 * Tokenizer that uses OpenNLP whitespace tokenizer
 * 
 * @author Antti Leppä
 */
public class WhitespaceTokenizer implements Tokenizer {

  @Override
  public String[] tokenize(String text) {
    return opennlp.tools.tokenize.WhitespaceTokenizer.INSTANCE.tokenize(text);
  }

}
