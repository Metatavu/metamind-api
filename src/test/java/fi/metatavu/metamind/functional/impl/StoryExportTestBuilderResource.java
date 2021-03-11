package fi.metatavu.metamind.functional.impl;

import fi.metatavu.jaxrs.test.functional.builder.AbstractTestBuilder;
import fi.metatavu.jaxrs.test.functional.builder.auth.AccessTokenProvider;
import fi.metatavu.metamind.api.client.apis.StoryExportApi;
import fi.metatavu.metamind.api.client.apis.VariablesApi;
import fi.metatavu.metamind.api.client.infrastructure.ApiClient;
import fi.metatavu.metamind.api.client.models.ExportedStory;
import fi.metatavu.metamind.api.client.models.Story;
import fi.metatavu.metamind.api.client.models.Variable;
import fi.metatavu.metamind.functional.TestSettings;

import java.io.IOException;
import java.util.UUID;

/**
 * Test builder resource for Story export
 */
public class StoryExportTestBuilderResource extends ApiTestBuilderResource<ExportedStory, StoryExportApi> {

    private AccessTokenProvider accessTokenProvider;

    /**
     * Constructor for StoryExportTestBuilderResource
     *
     * @param testBuilder         testBuilder
     * @param accessTokenProvider accessTokenProvider
     * @param apiClient           apiClient
     */
    public StoryExportTestBuilderResource(
            AbstractTestBuilder<ApiClient> testBuilder,
            AccessTokenProvider accessTokenProvider,
            ApiClient apiClient
    ) {
        super(testBuilder, apiClient);
        this.accessTokenProvider = accessTokenProvider;
    }

    /**
     * Builds API client
     *
     * @return API client
     */
    @Override
    protected StoryExportApi getApi() {
        try {
            ApiClient.Companion.setAccessToken(accessTokenProvider.getAccessToken());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new StoryExportApi(TestSettings.basePath);
    }

    /**
     * Cleans given resource
     *
     * @param variable resource
     */
    @Override
    public void clean(ExportedStory variable) {
    }

    /*
     * Exports a story for testing purposes
     *
     * @param id of the story to be exported
     * @return exported story
     */
    public ExportedStory exportStory(UUID storyId) {
        return getApi().exportStory(storyId);
    }

    /**
     * Imports a story for testing purposes
     *
     * @param a previously exported story
     * @return imported story
     */
    public Story importStory(ExportedStory story) {
        return getApi().importStory(story);
    }

}
