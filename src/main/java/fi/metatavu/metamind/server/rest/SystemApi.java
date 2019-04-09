package fi.metatavu.metamind.server.rest;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * REST system endpoints
 * 
 * @author Antti Leppä
 *
 */
@Path("/system")
@Consumes({ "application/json;charset=utf-8" })
@Produces({ "application/json;charset=utf-8" })
@RequestScoped
@Stateful
public class SystemApi extends AbstractRestApi {

  /**
   * Returns pong
   * 
   * @return pong in plain text
   */
  @GET
  @Path ("/ping")
  @Produces (MediaType.TEXT_PLAIN)
  public Response getPing(@Context HttpServletRequest request) {
    return Response.ok("pong").build();
  }
  
}
