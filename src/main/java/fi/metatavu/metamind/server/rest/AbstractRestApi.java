package fi.metatavu.metamind.server.rest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.StreamingOutput;

import org.slf4j.Logger;

/**
 * Abstract base class for rest services
 * 
 * @author Heikki Kurhinen
 * @author Antti Leppä
 */
public abstract class AbstractRestApi {

  private static final String INTERNAL_SERVER_ERROR = "Internal Server Error";
  private static final String FAILED_TO_STREAM_DATA_TO_CLIENT = "Failed to stream data to client";
  
  @Inject
  private Logger logger;
  
  /**
   * Send bad request response
   * 
   * @param entity response entity
   * @return response with status 400
   */
  protected Response respondBadRequest(Object entity) {
    return Response.status(Response.Status.BAD_REQUEST).entity(entity).build();
  }
  
  /**
   * Send not found response
   * 
   * @return response with status 404
   */
  protected Response respondNotFound() {
    return Response.status(Response.Status.NOT_FOUND).build();
  }
  
  /**
   * Send internal server error response
   * 
   * @param entity response entity
   * @return response with status 500
   */
  protected Response respondInternalServerError(Object entity) {
    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(entity).build();
  }
  
  /**
   * Send no content response
   * 
   * @return response with status 204
   */
  protected Response respondNoContent() {
    return Response.status(Response.Status.NO_CONTENT).build();
  }
  
  /**
   * Send ok response
   * 
   * @param entity response entity
   * @return response with status 200
   */
  protected Response respondOk(Object entity) {
    return Response.status(Response.Status.OK).entity(entity).build();
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
