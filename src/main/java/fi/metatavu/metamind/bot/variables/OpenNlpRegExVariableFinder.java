package fi.metatavu.metamind.bot.variables;

import fi.metatavu.metamind.bot.tokenization.Tokenizer;
import opennlp.tools.namefind.RegexNameFinder;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * Variable finder using OpenNLP token name finder
 * 
 * @author Antti Leppä
 */
public class OpenNlpRegExVariableFinder extends AbstractOpenNlpVariableFinder {

  /**
   * Variable finder using OpenNLP token name finder
   * 
   * @param patterns patterns
   * @param tokenizer tokenizer
   */
  public OpenNlpRegExVariableFinder(Map<String, Pattern[]> patterns, Tokenizer tokenizer) {
    super(tokenizer, new RegexNameFinder(patterns));
  }

}
