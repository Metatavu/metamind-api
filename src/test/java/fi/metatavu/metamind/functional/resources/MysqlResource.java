package fi.metatavu.metamind.functional.resources;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.testcontainers.containers.MySQLContainer;

import java.util.HashMap;
import java.util.Map;

public class MysqlResource implements QuarkusTestResourceLifecycleManager {
      private static final String DATABASE = "metamind-api";
      private static final String USERNAME = "metamind-api";
      private static final String PASSWORD = "password";

    static final MySQLContainer db = new SpecifiedMySQLContainer("mysql:5.7")
        .withDatabaseName(DATABASE)
        .withUsername(USERNAME)
        .withPassword(PASSWORD)
        .withCommand(
            "--character-set-server=utf8mb4",
                    "--collation-server=utf8mb4_unicode_ci",
                    "--lower_case_table_names=1"
    );

    @Override
    public Map<String, String> start() {
        db.start();

        HashMap config = new HashMap<String, String>();
        config.put("quarkus.datasource.username", USERNAME);
        config.put("quarkus.datasource.password", PASSWORD);
        config.put("quarkus.datasource.jdbc.url", db.getJdbcUrl());
        return config;
    }

    @Override
    public void stop() {
        db.stop();
    }

}

class SpecifiedMySQLContainer extends MySQLContainer<SpecifiedMySQLContainer>
{
    public SpecifiedMySQLContainer(String image){
        super(image);
    }

}
