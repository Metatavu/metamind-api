package fi.metatavu.metamind.functional;

import fi.metatavu.metamind.ApiClient.Api;

/**
 * Describes closeable resource
 * 
 * @author Antti Leppä
 *
 * @param <T> entity class
 * @param <A> API class
 */
public class CloseableResource<T, A extends Api> implements AutoCloseable {

  private AbstractTestBuilderResource<T, A> builder;
  private T resource;

  /**
   * Constructor
   * 
   * @param builder resource builder
   * @param resource resource
   */
  public CloseableResource(AbstractTestBuilderResource<T, A> builder, T resource) {
    super();
    this.resource = resource;
    this.builder = builder;
  }
  
  /**
   * Returns builder
   * 
   * @return builder
   */
  public AbstractTestBuilderResource<T, A> getBuilder() {
    return builder;
  }
  
  /**
   * Returns resource
   * 
   * @return resource
   */
  public T getResource() {
    return resource;
  }

  @Override
  public void close() throws Exception {
    builder.clean(resource);
  }

}
