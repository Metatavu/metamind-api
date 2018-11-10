package fi.metatavu.metamind.server.rest;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;

import fi.metatavu.metamind.scripts.ScriptController;
import fi.metatavu.metamind.server.rest.model.Script;
import fi.metatavu.metamind.server.rest.translation.ScriptTranslator;

/**
 * REST - endpoints for scripts
 * 
 * @author Heikki Kurhinen
 *
 */
@RequestScoped
@Stateful
public class ScriptsApiImpl extends AbstractRestApi implements ScriptsApi {

  @Inject
  private ScriptController scriptController;

  @Inject
  private ScriptTranslator scriptTranslator;

  @Override
  public Response createScript(Script body) {
    String name = body.getName();
    String version = body.getVersion();
    if (StringUtils.isEmpty(name) || StringUtils.isEmpty(version)) {
      return respondBadRequest("Script name and version are required");
    }

    fi.metatavu.metamind.persistence.models.Script scriptEntity = scriptController.findScript(name, version);
    if (scriptEntity == null) {
      scriptEntity = scriptController.createScript(
        body.getName(),
        body.getVersion(),
        body.getLanguage(),
        body.getContent()
      );
    } else {
      scriptController.updateScript(scriptEntity, body.getContent(), body.getLanguage());
    }

    return respondOk(scriptTranslator.translateScript(scriptEntity));
  }

}
