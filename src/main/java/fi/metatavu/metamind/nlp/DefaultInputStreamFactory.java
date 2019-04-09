package fi.metatavu.metamind.nlp;

import java.io.IOException;
import java.io.InputStream;

import opennlp.tools.util.InputStreamFactory;

/**
 * Default implementation for input stream factory
 * 
 * @author Antti Leppä
 */
public class DefaultInputStreamFactory implements InputStreamFactory {
  
  private InputStream inputStream;
  
  /**
   * Constructor
   * 
   * @param inputStream input stream
   */
  public DefaultInputStreamFactory(InputStream inputStream) {
    super();
    this.inputStream = inputStream;
  }

  @Override
  public InputStream createInputStream() throws IOException {
    return inputStream;
  }

}
