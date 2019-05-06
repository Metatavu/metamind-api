package fi.metatavu.metamind.test.functional.builder.auth;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.restassured.specification.RequestSpecification;

/**
 * Default access token provider
 * 
 * @author Antti Leppä
 */
public class DefaultAccessTokenProvider implements AccessTokenProvider {
  
  private static final String AUTH_SERVER_URL = "http://localhost:8280";
  private static final int EXPIRE_SLACK = 30;

  private String realm;
  private String clientId;
  private String username;
  private String password;
  private String clientSecret;
  
  private String accessToken;
  private OffsetDateTime expires; 
  
  public DefaultAccessTokenProvider(String realm, String clientId, String username, String password, String clientSecret) {
    super();
    this.realm = realm;
    this.clientId = clientId;
    this.username = username;
    this.password = password;
    this.clientSecret = clientSecret;
    this.accessToken = null;
    this.expires = null;
  }

  @Override
  public String getAccessToken() throws IOException {
    if (accessToken != null && expires != null && expires.isAfter(OffsetDateTime.now())) {
      return accessToken;
    }
    
    String path = String.format("/auth/realms/%s/protocol/openid-connect/token", realm);

    RequestSpecification request = given()
      .baseUri(AUTH_SERVER_URL)
      .formParam("client_id", clientId)
      .formParam("grant_type", "password")
      .formParam("username", username)
      .formParam("password", password);

    if (clientSecret != null) {
      request.formParam("client_secret", clientSecret);
    }

    String response = request.post(path)
      .getBody()
      .asString(); 

    Map<String, Object> responseMap = readJsonMap(response);
    accessToken = (String) responseMap.get("access_token");
    Integer expiresIn = (Integer) responseMap.get("expires_in");

    this.expires = OffsetDateTime.now()
      .plus(expiresIn, ChronoUnit.SECONDS)
      .minus(EXPIRE_SLACK, ChronoUnit.SECONDS);
    
    assertNotNull(response, accessToken);
    
    return accessToken;
  }
  
  /**
   * Returns object mapper with default modules and settings
   * 
   * @return object mapper
   */
  private ObjectMapper getObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    
    return objectMapper;
  }

  /**
   * Reads JSON src into Map
   * 
   * @param src input
   * @return map
   * @throws IOException throws IOException when there is error when reading the input 
   */
  private Map<String, Object> readJsonMap(String src) throws IOException {
    return getObjectMapper().readValue(src, new TypeReference<Map<String, Object>>() {});
  }
}
