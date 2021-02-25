package fi.metatavu.metamind.server.rest;

import com.sun.xml.ws.developer.Stateful;

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
  public Response getPing() {
    return Response.ok("pong").build();
  }
  
}
