package fi.metatavu.metamind.functional;

import org.apache.commons.lang3.math.NumberUtils;

/**
 * Utility class for retrieving functional test settings
 * 
 * @author Antti Leppä
 */
public class TestSettings {

  public static String basePath = "http://localhost:8080/";

  /**
   * Returns host where the application being tested is running 
   * 
   * @return host where the application being tested is running
   */
  public static String getHost() {
    return System.getenv("IT_HOST") != null ? System.getenv("IT_HOST") : System.getProperty("it.host");
  }  

  /**
   * Returns port where the application being tested is running 
   * 
   * @return port where the application being tested is running
   */
  public static Integer getPort() {
    return System.getenv("IT_PORT_HTTP") != null ? NumberUtils.createInteger(System.getenv("IT_PORT_HTTP")) : NumberUtils.createInteger(System.getProperty("it.port.http"));
  }
  
}
