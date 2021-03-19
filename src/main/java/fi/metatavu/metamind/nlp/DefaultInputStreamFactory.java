package fi.metatavu.metamind.nlp;

import opennlp.tools.util.InputStreamFactory;

import java.io.IOException;
import java.io.InputStream;

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
