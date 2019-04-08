package fi.metatavu.metamind.bot.variables;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.tokenize.Tokenizer;

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
