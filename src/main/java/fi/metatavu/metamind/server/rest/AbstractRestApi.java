package fi.metatavu.metamind.server.rest;

import fi.metatavu.metamind.api.spec.model.ErrorResponse;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.slf4j.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.StreamingOutput;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Abstract base class for rest services
 * 
 * @author Heikki Kurhinen
 * @author Antti Leppä
 */
@RequestScoped
public abstract class AbstractRestApi {

  private static final String INTERNAL_SERVER_ERROR = "Internal Server Error";
  private static final String FAILED_TO_STREAM_DATA_TO_CLIENT = "Failed to stream data to client";
  protected static final String TRAINING_MATERIAL_GROUP_ERROR = "Training material belongs to different group";
  protected static final String STORY_GROUP_ERROR = "Story belongs to different group";
  protected static final String SCRIPT_GROUP_ERROR = "Script belongs to different group";

  private Logger logger;

  @Inject
  private JsonWebToken jsonWebToken;

  @Context
  private SecurityContext securityContext;

  @Inject
  private KeycloakController keycloakController;

  /**
   * Returns logged user id
   *
   * @return logged user id
   */
  protected UUID getLoggedUserId() {
    if (jsonWebToken.getSubject() == null) {
      return null;
    }

    return UUID.fromString(jsonWebToken.getSubject());
  }

  /**
   * Checks if current user is admin
   *
   * @return if user is admin
   */
  protected boolean isAdmin() {
    return securityContext.isUserInRole("admin");
  }

  /**
   * Checks if user1 and user2 belong to different keycloak groups
   *
   * @param user1 user 1 uuid
   * @param user2 user 2 uuid
   * @return true if different group
   */
  protected boolean isDifferentGroups(UUID user1, UUID user2) {
    return keycloakController.usersShareNoGroups(user1, user2);
  }

  /**
   * Constructs ok response
   * 
   * @param entity payload
   * @return response
   */
  protected Response createOk(Object entity) {
    return Response
      .status(Status.OK)
      .entity(entity)
      .build();
  }

  /**
   * Constructs not found response
   *
   * @param message message
   * @return response
   */
  protected Response createNotFound(String message) {
    ErrorResponse entity = new ErrorResponse();
    entity.setMessage(message);
    return Response
      .status(Status.NOT_FOUND)
      .entity(entity)
      .build();
  }

  /**
   * Constructs forbidden response
   *
   * @param message message
   * @return response
   */
  protected Response createForbidden(String message) {
    ErrorResponse entity = new ErrorResponse();
    entity.setMessage(message);
    return Response
      .status(Status.FORBIDDEN)
      .entity(entity)
      .build();
  }

  /**
   * Constructs unauthorized response
   *
   * @param message message
   * @return response
   */
  protected Response createUnauthorized(String message) {
    ErrorResponse entity = new ErrorResponse();
    entity.setMessage(message);
    return Response
      .status(Status.UNAUTHORIZED)
      .entity(entity)
      .build();
  }

  /**
   * Constructs bad request response
   *
   * @param message message
   * @return response
   */
  protected Response createBadRequest(String message) {
    ErrorResponse entity = new ErrorResponse();
    entity.setMessage(message);
    return Response
      .status(Status.BAD_REQUEST)
      .entity(entity)
      .build();
  }

  /**
   * Constructs internal server error response
   *
   * @param message message
   * @return response
   */
  protected Response createInternalServerError(String message) {
    ErrorResponse entity = new ErrorResponse();
    entity.setMessage(message);
    return Response
      .status(Status.INTERNAL_SERVER_ERROR)
      .entity(entity)
      .build();
  }

  /**
   * Constructs ok response
   *
   * @param entity payload
   * @param totalHits total hits
   * @return response
   */
  protected Response createOk(Object entity, Long totalHits) {
    return Response
      .status(Status.OK)
      .entity(entity)
      .header("Total-Results", totalHits)
      .build();
  }

  /**
   * Constructs no content response
   *
   * @return response
   */
  protected Response createNoContent() {
    return Response
      .status(Status.NO_CONTENT)
      .build();
  }
  
  /**
   * Creates streamed response from byte array
   * 
   * @param data data
   * @param type content type
   * @return Response
   */
  protected Response streamResponse(byte[] data, String type) {
    try (InputStream byteStream = new ByteArrayInputStream(data)) {
      return streamResponse(type, byteStream, data.length);
    } catch (IOException e) {
      logger.error(FAILED_TO_STREAM_DATA_TO_CLIENT, e);
      return Response.status(Status.INTERNAL_SERVER_ERROR)
        .entity(INTERNAL_SERVER_ERROR)
        .build();
    }
  }
  
  /**
   * Creates streamed response from input stream
   * 
   * @param inputStream data
   * @param type content type
   * @param contentLength content length
   * @return Response
   */
  protected Response streamResponse(String type, InputStream inputStream, Integer contentLength) {
    ResponseBuilder responseBuilder = Response.ok(new Stream(inputStream), type);
    if (contentLength != null) {
      responseBuilder.header("Content-Length", contentLength);
    }
    
    return responseBuilder.build();
  }

  private class Stream implements StreamingOutput {
    
    private InputStream inputStream;
    
    public Stream(InputStream inputStream) {
      this.inputStream = inputStream;
    }

    @Override
    public void write(OutputStream output) throws IOException {
      byte[] buffer = new byte[1024 * 100];
      int bytesRead;
      
      while ((bytesRead = inputStream.read(buffer, 0, buffer.length)) != -1) {
        output.write(buffer, 0, bytesRead);
        output.flush();
      }
      
      output.flush();
    }
    
  }
}
