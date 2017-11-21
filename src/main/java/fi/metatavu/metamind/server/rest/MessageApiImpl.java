package fi.metatavu.metamind.server.rest;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import fi.metatavu.metamind.server.rest.model.Message;

/**
 * REST - endpoints for messages
 * 
 * @author Heikki Kurhinen
 *
 */
@Path("/messages")
@Consumes({ "application/json;charset=utf-8" })
@Produces({ "application/json;charset=utf-8" })
@RequestScoped
@Stateful
public class MessageApiImpl extends AbstractRestApi implements MessagesApi {

  @Override
  public Response postMessage(Message body) throws Exception {
    // TODO Auto-generated method stub
    return null;
  }

}
