package fi.metatavu.metamind.server.rest;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import fi.metatavu.metamind.server.rest.model.Session;

/**
 * REST - endpoints for sessions
 * 
 * @author Heikki Kurhinen
 *
 */
@Path("/sessions")
@Consumes({ "application/json;charset=utf-8" })
@Produces({ "application/json;charset=utf-8" })
@RequestScoped
@Stateful
public class SessionApiImpl extends AbstractRestApi implements SessionsApi {

  @Override
  public Response createSession(Session body) throws Exception {
    // TODO Auto-generated method stub
    return null;
  }

}
