package fi.metatavu.metamind.functional.impl;

import fi.metatavu.jaxrs.test.functional.builder.AbstractTestBuilder;
import fi.metatavu.jaxrs.test.functional.builder.auth.AccessTokenProvider;
import fi.metatavu.metamind.api.client.apis.IntentsApi;
import fi.metatavu.metamind.api.client.apis.StoriesApi;
import fi.metatavu.metamind.api.client.apis.TrainingMaterialsApi;
import fi.metatavu.metamind.api.client.infrastructure.ApiClient;
import fi.metatavu.metamind.api.client.infrastructure.ClientException;
import fi.metatavu.metamind.api.client.models.Story;
import fi.metatavu.metamind.api.client.models.TrainingMaterial;
import fi.metatavu.metamind.api.client.models.TrainingMaterialType;
import fi.metatavu.metamind.api.client.models.TrainingMaterialVisibility;
import fi.metatavu.metamind.functional.TestSettings;
import org.json.JSONException;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Test builder resource for stories
 *
 * @author Antti Leppä
 */
public class TrainingMaterialTestBuilderResource extends ApiTestBuilderResource<TrainingMaterial, TrainingMaterialsApi> {

    private AccessTokenProvider accessTokenProvider;

    /**
     * Constructor for TrainingMaterialTestBuilderResource
     *
     * @param testBuilder testBuilder
     * @param apiClient   apiClient
     */
    public TrainingMaterialTestBuilderResource(
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
    protected TrainingMaterialsApi getApi() {
        try {
            ApiClient.Companion.setAccessToken(accessTokenProvider.getAccessToken());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new TrainingMaterialsApi(TestSettings.basePath);
    }

    /**
     * Creates new training material
     *
     * @param storyId StoryId UUID
     * @param type    type
     * @param name    training material name
     * @param text    text
     * @return created training material
     */
    public TrainingMaterial create(UUID storyId, TrainingMaterialType type, String name, String text, TrainingMaterialVisibility visibility) {
        TrainingMaterial trainingMaterial = new TrainingMaterial(text, name, null, type, storyId, visibility, null, null);
        TrainingMaterial createdTrainingMaterial = getApi().createTrainingMaterial(trainingMaterial);
        return addClosable(createdTrainingMaterial);
    }

    /**
     * Finds training material
     *
     * @param trainingMaterial trainingMaterial
     * @return found training material
     */
    public TrainingMaterial findTrainingMaterial(TrainingMaterial trainingMaterial) {
        return getApi().findTrainingMaterial(trainingMaterial.getId());
    }

    /**
     * List training material
     *
     * @return training material list
     */
    public TrainingMaterial[] listTrainingMaterial(Story story, TrainingMaterialType type, TrainingMaterialVisibility visibility) {
        return getApi().listTrainingMaterials(story.getId(), type, visibility);
    }

    /**
     * Updates training material
     *
     * @param trainingMaterial trainingMaterial
     * @return updated training material
     */
    public TrainingMaterial updateTrainingMaterial(TrainingMaterial trainingMaterial) {
        return getApi().updateTrainingMaterial(trainingMaterial.getId(), trainingMaterial);
    }

    /**
     * Deletes training material
     *
     * @param trainingMaterial trainingMaterial
     */
    public void delete(TrainingMaterial trainingMaterial) {
        getApi().deleteTrainingMaterial(trainingMaterial.getId());
        removeCloseable(closable -> {
            if (closable instanceof TrainingMaterial) {
                return ((TrainingMaterial) closable).getId().equals(trainingMaterial.getId());
            }

            return false;
        });
    }

    /**
     * Asserts create status fails with given status code
     *
     * @param expectedStatus expectedStatus
     * @param storyId storyId
     * @param type type
     * @param name type
     * @param text text
     */
    public void assertCreateFailStatus(int expectedStatus, UUID storyId, TrainingMaterialType type, String name, String text) {
        try {
            TrainingMaterial trainingMaterial = new TrainingMaterial(text, name, null, type, storyId, null, null, null);
            getApi().createTrainingMaterial(trainingMaterial);
            fail(String.format("Expected find to fail with status %d", expectedStatus));
        } catch (ClientException e) {
            assertEquals(expectedStatus, e.getStatusCode());
        }
    }

    /**
     * Asserts find status fails with given status code
     *
     * @param expectedStatus expectedStatus
     * @param trainingMaterialId trainingMaterialId to find
     */
    public void assertFindFailStatus(int expectedStatus, UUID trainingMaterialId) {
        try {
            getApi().findTrainingMaterial(trainingMaterialId);
            fail(String.format("Expected find to fail with status %d", expectedStatus));
        } catch (ClientException e) {
            assertEquals(expectedStatus, e.getStatusCode());
        }
    }

    /**
     * Asserts update status fails with given status code
     *
     * @param expectedStatus expectedStatus
     * @param trainingMaterial trainingMaterial to update
     */
    public void assertUpdateFailStatus(int expectedStatus, TrainingMaterial trainingMaterial) {
        try {
            getApi().updateTrainingMaterial(trainingMaterial.getId(), trainingMaterial);
            fail(String.format("Expected find to fail with status %d", expectedStatus));
        } catch (ClientException e) {
            assertEquals(expectedStatus, e.getStatusCode());
        }
    }

    /**
     * Asserts delete status fails with given status code
     *
     * @param expectedStatus expectedStatus
     * @param trainingMaterial trainingMaterial to delete
     */
    public void assertDeleteFailStatus(int expectedStatus, TrainingMaterial trainingMaterial) {
        try {
            getApi().deleteTrainingMaterial(trainingMaterial.getId());
            fail(String.format("Expected find to fail with status %d", expectedStatus));
        } catch (ClientException e) {
            assertEquals(expectedStatus, e.getStatusCode());
        }
    }

    /**
     * Asserts Training materials are equal
     *
     * @param expected Training material
     * @param actual   Training material
     * @throws IOException
     * @throws JSONException
     */
    public void assertTrainingMaterialEqual(TrainingMaterial expected, TrainingMaterial actual) throws IOException, JSONException {
        assertJsonsEqual(expected, actual);
    }

    @Override
    public void clean(TrainingMaterial trainingMaterial) {
        getApi().deleteTrainingMaterial(trainingMaterial.getId());
    }
}
