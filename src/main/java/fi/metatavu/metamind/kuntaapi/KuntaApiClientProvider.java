package fi.metatavu.metamind.kuntaapi;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import fi.metatavu.kuntaapi.ApiClient;
import fi.metatavu.kuntaapi.Configuration;
import fi.metatavu.metamind.settings.SystemSettingController;

@ApplicationScoped
public class KuntaApiClientProvider {
  
  @Inject
  private SystemSettingController systemSettingController;
  
  @Produces
  public ApiClient produceApiClient () {
    String apiUrl = systemSettingController.getSettingValue(KuntaApiConsts.APIURL_SETTING_KEY);
    String username = systemSettingController.getSettingValue(KuntaApiConsts.APIUSER_SETTING_KEY);
    String password = systemSettingController.getSettingValue(KuntaApiConsts.APIPASS_SETTING_KEY);
    
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath(apiUrl);
    defaultClient.setUsername(username);
    defaultClient.setPassword(password);
    
    return defaultClient;
  }
}
