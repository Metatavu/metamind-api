package fi.metatavu.metamind.server.rest;

import java.util.UUID;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;

import fi.metatavu.metamind.rest.api.ScriptsApi;
import fi.metatavu.metamind.rest.model.Script;
import fi.metatavu.metamind.scripts.ScriptController;
import fi.metatavu.metamind.server.rest.translation.ScriptTranslator;

/**
 * REST - endpoints for scripts
 * 
 * @author Heikki Kurhinen
 * @author Antti Leppä
 */
@RequestScoped
@Stateful
@Consumes({ "application/json;charset=utf-8" })
@Produces({ "application/json;charset=utf-8" })
public class ScriptsApiImpl extends AbstractRestApi implements ScriptsApi {

  @Inject
  private ScriptController scriptController;

  @Inject
  private ScriptTranslator scriptTranslator;

  @Override
  public Response createScript(Script body) {
    // TODO: Permission checks
    
    String name = body.getName();
    String version = body.getVersion();
    String content = body.getContent();
    String language = body.getLanguage();
    
    if (StringUtils.isEmpty(name) || StringUtils.isEmpty(version)) {
      return createBadRequest("Script name and version are required");
    }
    
    UUID loggedUserId = getLoggerUserId();

    fi.metatavu.metamind.persistence.models.Script scriptEntity = scriptController.findScriptByNameAndVersion(name, version);
    if (scriptEntity == null) {
      scriptEntity = scriptController.createScript(name, content, version, language, loggedUserId);
    } else {
      scriptController.updateScript(scriptEntity, body.getContent(), body.getLanguage(), loggedUserId);
    }

    return createOk(scriptTranslator.translateScript(scriptEntity));
  }

  @Override
  public Response deleteScript(UUID scriptId) {
    // TODO: Permission checks
    
    fi.metatavu.metamind.persistence.models.Script script = scriptController.findScriptById(scriptId);
    if (script == null) {
      return createNotFound(String.format("Script %s not found", scriptId));
    }
    
    scriptController.deleteScript(script);
    
    return createNoContent();
  }

  @Override
  public Response findScript(UUID scriptId) {
    // TODO: Permission checks
    
    fi.metatavu.metamind.persistence.models.Script script = scriptController.findScriptById(scriptId);
    if (script == null) {
      return createNotFound(String.format("Script %s not found", scriptId));
    }
    
    return createOk(scriptTranslator.translateScript(script));
  }

  @Override
  public Response listScripts() {
    return createOk(scriptController.listScripts().stream().map(scriptTranslator::translateScript));
  }

  @Override
  public Response updateScript(Script body, UUID scriptId) {
    fi.metatavu.metamind.persistence.models.Script script = scriptController.findScriptById(scriptId);
    if (script == null) {
      return createNotFound(String.format("Script %s not found", scriptId));
    }
    
    UUID loggerUserId = getLoggerUserId();
    
    return createOk(scriptTranslator.translateScript(scriptController.updateScript(script, body.getContent(), body.getLanguage(), loggerUserId)));
  }

}
