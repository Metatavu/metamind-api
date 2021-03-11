package fi.metatavu.metamind.functional.resources;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Starts test container for keycloak
 */
public class KeycloakResource implements QuarkusTestResourceLifecycleManager {
    static final KeycloakContainer keycloak = new KeycloakContainer().withRealmImportFile("kc.json");

    @Override
    public Map<String, String> start() {
        keycloak.start();

        HashMap config = new HashMap<String, String>();
        config.put("quarkus.oidc.auth-server-url", String.format("%s/realms/metamind", keycloak.getAuthServerUrl()));
        config.put("quarkus.oidc.client-id", "ui");
        config.put("metamind.keycloak.admin.host", keycloak.getAuthServerUrl());
        config.put("metamind.keycloak.admin.realm", "metamind");
        config.put("metamind.keycloak.admin.user", "admin");
        config.put("metamind.keycloak.admin.password", "admin");
        config.put("metamind.keycloak.admin.admin_client_id", "admin-cli");
        config.put("metamind.keycloak.admin.secret", "");
        return config;
    }

    @Override
    public void stop() {
        keycloak.stop();
    }

}
