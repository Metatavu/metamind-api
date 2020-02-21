package fi.metatavu.metamind.server.keycloak;

import java.io.FileInputStream;
import java.io.IOException;

import org.keycloak.adapters.KeycloakConfigResolver;
import org.keycloak.adapters.KeycloakDeployment;
import org.keycloak.adapters.KeycloakDeploymentBuilder;
import org.keycloak.adapters.spi.HttpFacade.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Resolves Keycloak config from a file
 * 
 * @author DaniilSmirnov1
 *
 */
public class MetamindKeycloakConfigResolver implements KeycloakConfigResolver{

    private static Logger logger = LoggerFactory.getLogger(MetamindKeycloakConfigResolver.class.getName());

    @Override
    public KeycloakDeployment resolve(Request request) {
      String configFilePath = System.getenv("KEYCLOAK_CONFIG_PATH") != null ? System.getenv("KEYCLOAK_CONFIG_PATH"): System.getProperty("keycloak.config-path");
      FileInputStream configStream;
      try {
        configStream = new FileInputStream(configFilePath);
        try {
          return KeycloakDeploymentBuilder.build(configStream);
        } finally {
          configStream.close();
        }
      } catch (IOException e) {
        logger.warn("Failed to read config file", e);
      }

      return null;
    }


  }

