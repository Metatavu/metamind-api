package fi.metatavu.metamind.liquibase;

import liquibase.integration.cdi.CDILiquibaseConfig;
import liquibase.integration.cdi.annotations.LiquibaseType;
import liquibase.resource.ClassLoaderResourceAccessor;
import liquibase.resource.ResourceAccessor;

import javax.annotation.Resource;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.sql.DataSource;

/**
 * Liquibase producer
 * 
 * @author Antti Leppä
 */
@Dependent
public class LiquibaseProducer {
  
  @Resource (lookup = "java:jboss/datasources/metamind-api")
  private DataSource dataSource;
  
  @Produces
  @LiquibaseType
  public CDILiquibaseConfig createConfig() {
    CDILiquibaseConfig config = new CDILiquibaseConfig();
    config.setChangeLog("fi/metatavu/metamind/changelog.xml");
    return config;
  }
  
  @Produces
  @LiquibaseType
  public DataSource createDataSource() {
    return dataSource;
  }
  
  @Produces
  @LiquibaseType
  public ResourceAccessor create() {
    return new ClassLoaderResourceAccessor(getClass().getClassLoader());
  }

}
