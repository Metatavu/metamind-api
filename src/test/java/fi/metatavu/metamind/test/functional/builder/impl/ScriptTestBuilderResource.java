package fi.metatavu.metamind.test.functional.builder.impl;

import java.util.List;

import fi.metatavu.metamind.ApiClient;
import fi.metatavu.metamind.client.ScriptsApi;
import fi.metatavu.metamind.client.model.Script;
import fi.metatavu.metamind.test.functional.builder.AbstractTestBuilderResource;
import fi.metatavu.metamind.test.functional.builder.TestBuilder;

public class ScriptTestBuilderResource extends AbstractTestBuilderResource<Script, ScriptsApi>{
  public ScriptTestBuilderResource(TestBuilder testBuilder, ApiClient apiClient) {
    super(testBuilder, apiClient);
  }
  
  public Script create(String content, String language, String name, String version) {
    Script script = new Script();
    script.setContent(content);
    script.setLanguage(language);
    script.setName(name);
    script.setVersion(version);
    Script createdScript = getApi().createScript(script);
    return addClosable(createdScript);
  }
  
  public List<Script> listScripts() {
    return getApi().listScripts();
  }

  @Override
  public void clean(Script script) {
    getApi().deleteScript(script.getId());
  }
}
