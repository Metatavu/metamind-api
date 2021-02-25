package fi.metatavu.metamind.bot.variables;

import fi.metatavu.metamind.bot.tokenization.Tokenizer;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;

/**
 * Variable finder using OpenNLP token name finder
 * 
 * @author Antti Leppä
 */
public class OpenNlpNerVariableFinder extends AbstractOpenNlpVariableFinder {

  /**
   * Variable finder using OpenNLP token name finder
   * 
   * @param tokenNameFinderModel model
   * @param tokenizer tokenizer
   */
  public OpenNlpNerVariableFinder(TokenNameFinderModel tokenNameFinderModel, Tokenizer tokenizer) {
    super(tokenizer, new NameFinderME(tokenNameFinderModel));
  }
  
}
