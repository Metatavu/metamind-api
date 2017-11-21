package fi.metatavu.metamind.server.rest;

import javax.ws.rs.core.Response;

/**
 * Abstract base class for rest services
 * 
 * @author Heikki Kurhinen
 *
 */
public abstract class AbstractRestApi {
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

}
