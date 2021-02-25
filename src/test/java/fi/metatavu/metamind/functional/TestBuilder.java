package fi.metatavu.metamind.functional;

import fi.metatavu.jaxrs.test.functional.builder.auth.InvalidAccessTokenProvider;
import fi.metatavu.jaxrs.test.functional.builder.auth.KeycloakAccessTokenProvider;
import fi.metatavu.jaxrs.test.functional.builder.auth.NullAccessTokenProvider;
import fi.metatavu.metamind.functional.auth.TestBuilderAuthentication;
import org.eclipse.microprofile.config.ConfigProvider;
import org.keycloak.Config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Test builder class
 * 
 * @author Antti Leppä
 */
public class TestBuilder implements AutoCloseable {

  private TestBuilderAuthentication admin;
  private TestBuilderAuthentication invalid;
  private TestBuilderAuthentication anonymous;

  private static final String REALM = "metamind";
  private static final String CLIENT_ID = "ui";
  private static final String ADMIN_USER = "admin";
  private static final String ADMIN_PASSWORD = "admin";
  private static final String CLIENT_SECRET = null;

  private List<CloseableResource<?, ?>> closables = new ArrayList<>();

  /**
   * Returns admin authenticated authentication resource
   * 
   * @return admin authenticated authentication resource
   * @throws IOException 
   */
  public TestBuilderAuthentication admin() throws IOException {
    if (admin != null) {
      return admin;
    }

    String serverUrl = ConfigProvider.getConfig().getValue("metamind.keycloak.host", String.class);

    return admin = new TestBuilderAuthentication(this,
            new KeycloakAccessTokenProvider(serverUrl, REALM, CLIENT_ID, ADMIN_USER, ADMIN_PASSWORD, CLIENT_SECRET));
  }

  /**
   * Returns authentication resource with invalid token
   * 
   * @return authentication resource with invalid token
   */
  public TestBuilderAuthentication invalid() {
    if (invalid != null) {
      return invalid;
    }

    return invalid = new TestBuilderAuthentication(this, new InvalidAccessTokenProvider());
  }

  /**
   * Returns authentication resource without token
   * 
   * @return authentication resource without token
   */
  public TestBuilderAuthentication anonymous() {
    if (anonymous != null) {
      return anonymous;
    }

    return anonymous = new TestBuilderAuthentication(this, new NullAccessTokenProvider());
  }

  /**
   * Adds closable to clean queue
   * 
   * @param closable closable
   * @return given instance
   */
  protected <T extends CloseableResource<?, ?>> T addClosable(T closable) {
    closables.add(closable);
    return closable;
  }

  /**
   * Removes a closable from clean queue
   * 
   * @param predicate filter predicate
   */
  protected <T> void removeCloseable(Predicate<Object> predicate) {
    closables = closables.stream().filter((closeable) -> {
//      if (closeable.getResource().getClass().isInstance(clazz)) {
//        T resource = (T) ;
//      return predicate.test(resource);
//      }
//      return false;
//      
//      
      return predicate.test(closeable.getResource());
    }).collect(Collectors.toList());
  }

  @Override
  public void close() throws Exception {
    for (int i = closables.size() - 1; i >= 0; i--) {
      closables.get(i).close();
    }
    admin = null;
  }

}