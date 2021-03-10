package fi.metatavu.metamind.functional.impl;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import fi.metatavu.jaxrs.test.functional.builder.AbstractApiTestBuilderResource;
import fi.metatavu.jaxrs.test.functional.builder.AbstractTestBuilder;
import fi.metatavu.metamind.api.client.infrastructure.ApiClient;
import fi.metatavu.metamind.api.client.infrastructure.ClientException;
import fi.metatavu.metamind.api.spec.model.ErrorResponse;
import org.junit.Assert;

import java.io.IOException;

/**
 * Abstract base class for API test resource builders
 * @param <T> resource
 * @param <A> ApiClient for the resource
 */
abstract class ApiTestBuilderResource<T, A> extends AbstractApiTestBuilderResource<T, A, ApiClient> {

    private final ApiClient apiClient;

    /**
     * Returns API client
     *
     * @return API client
     */
    @Override
    protected ApiClient getApiClient() {
        return apiClient;
    }

    /**
     * Constructor
     *
     * @param testBuilder testBuilder
     */
    public ApiTestBuilderResource(AbstractTestBuilder<ApiClient> testBuilder, ApiClient apiClient) {
        super(testBuilder);
        this.apiClient = apiClient;
    }

}
