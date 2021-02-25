package fi.metatavu.metamind.functional.impl;

import fi.metatavu.metamind.ApiClient;
import fi.metatavu.metamind.client.VariablesApi;
import fi.metatavu.metamind.client.model.Story;
import fi.metatavu.metamind.client.model.Variable;
import fi.metatavu.metamind.client.model.VariableType;
import fi.metatavu.metamind.functional.AbstractTestBuilderResource;
import fi.metatavu.metamind.functional.TestBuilder;

import java.util.List;
import java.util.UUID;

/**
 * Test builder resource for variables
 *
 */
public class VariableTestBuilderResource extends AbstractTestBuilderResource<Variable, VariablesApi> {
  public VariableTestBuilderResource(TestBuilder testBuilder, ApiClient apiClient) {
    super(testBuilder, apiClient);
  }
  
  /*
   * Creates a variable for testing purposes
   * 
   * @param id of the story
   * @param name of the variable
   * @param type of the variable
   * @param validation script of the variable
   * @return created variable
   */
  public Variable create(UUID storyId, String name, VariableType type, String validationScript) {
    Variable variable = new Variable();
    variable.setName(name);
    variable.setType(type);
    variable.setValidationScript(validationScript);
    Variable createdVariable = getApi().createVariable(variable, storyId);
    return addClosable(createdVariable);
  }
  
  /*
   * Lists all variables created during testing
   */
  public List<Variable> listVariables(Story story) {
    return getApi().listVariables(story.getId());
  }

  @Override
  public void clean(Variable variable) {
    getApi().deleteVariable(variable.getStoryId(), variable.getId());
    
  }
}
