package fi.metatavu.metamind.test.functional.builder.auth;

import java.io.IOException;
import java.util.UUID;

/**
 * Access token provider that provides invalid access tokens
 * 
 * @author Antti Leppä
 */
public class InvalidAccessTokenProvider implements AccessTokenProvider {

  @Override
  public String getAccessToken() throws IOException {
    return UUID.randomUUID().toString();
  }

}
