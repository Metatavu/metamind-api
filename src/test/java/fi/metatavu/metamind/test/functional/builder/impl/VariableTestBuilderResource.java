package fi.metatavu.metamind.test.functional.builder.impl;

import java.util.UUID;

import fi.metatavu.metamind.ApiClient;
import fi.metatavu.metamind.client.VariablesApi;
import fi.metatavu.metamind.client.model.Variable;
import fi.metatavu.metamind.client.model.VariableType;
import fi.metatavu.metamind.test.functional.builder.AbstractTestBuilderResource;
import fi.metatavu.metamind.test.functional.builder.TestBuilder;

public class VariableTestBuilderResource extends AbstractTestBuilderResource<Variable, VariablesApi> {
  public VariableTestBuilderResource(TestBuilder testBuilder, ApiClient apiClient) {
    super(testBuilder, apiClient);
  }
  
  public Variable create(UUID storyId, String name, VariableType type, String validationScript) {
    Variable variable = new Variable();
    variable.setName(name);
    variable.setType(type);
    variable.setValidationScript(validationScript);
    Variable createdVariable = getApi().createVariable(variable, storyId);
    return addClosable(createdVariable);
  }

  @Override
  public void clean(Variable variable) {
    getApi().deleteVariable(variable.getStoryId(), variable.getId());
    
  }
}
