package fi.metatavu.metamind.functional.impl;

import fi.metatavu.jaxrs.test.functional.builder.AbstractTestBuilder;
import fi.metatavu.jaxrs.test.functional.builder.auth.AccessTokenProvider;
import fi.metatavu.metamind.api.client.apis.IntentsApi;
import fi.metatavu.metamind.api.client.apis.VariablesApi;
import fi.metatavu.metamind.api.client.infrastructure.ApiClient;
import fi.metatavu.metamind.api.client.models.Story;
import fi.metatavu.metamind.api.client.models.Variable;
import fi.metatavu.metamind.api.client.models.VariableType;
import fi.metatavu.metamind.functional.TestSettings;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * Test builder resource for variables
 */
public class VariableTestBuilderResource extends ApiTestBuilderResource<Variable, VariablesApi> {

  private AccessTokenProvider accessTokenProvider;

  /**
   * Constructor for VariableTestBuilderResource
   *
   * @param testBuilder         testBuilder
   * @param accessTokenProvider accessTokenProvider
   * @param apiClient           apiClient
   */
  public VariableTestBuilderResource(
    AbstractTestBuilder<ApiClient> testBuilder,
    AccessTokenProvider accessTokenProvider,
    ApiClient apiClient
  ) {
    super(testBuilder, apiClient);
    this.accessTokenProvider = accessTokenProvider;
  }

  @Override
  protected VariablesApi getApi() {
    try {
      ApiClient.Companion.setAccessToken(accessTokenProvider.getAccessToken());
    } catch (IOException e) {
      e.printStackTrace();
    }
    return new VariablesApi(TestSettings.basePath);
  }

  @Override
  public void clean(Variable variable) {
    getApi().deleteVariable(variable.getStoryId(), variable.getId());
  }

  /**
   * Lists all variables for the story
   *
   * @param story story
   * @return variable array
   */
  public Variable[] listVariables(Story story) {
    return getApi().listVariables(story.getId());
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
    Variable variable = new Variable(name, type, null, null, validationScript, null, null);

    Variable createdVariable = getApi().createVariable(storyId, variable);
    return addClosable(createdVariable);
  }

  /**
   * Deletes variable
   *
   * @param story variable story
   * @param variable variable
   */
  public void delete(Story story, Variable variable) {
    getApi().deleteVariable(story.getId(), variable.getId());
    removeCloseable(closable -> {
      if (closable instanceof Variable) {
        return ((Variable) closable).getId().equals(variable.getId());
      }

      return false;
    });
  }
}
