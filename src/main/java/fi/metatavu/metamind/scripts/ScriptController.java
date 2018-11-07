package fi.metatavu.metamind.scripts;

import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.metamind.persistence.dao.ScriptDAO;
import fi.metatavu.metamind.persistence.models.Script;

@ApplicationScoped
public class ScriptController {

  @Inject
  private ScriptDAO scriptDAO;

  /**
   * Creates new script
   * 
   * @param name script name
   * @param version script version
   * @param language script language
   * @param content script content
   * 
   * @return created script
   */
  public Script createScript(String name, String version, String language, String content) {
    return scriptDAO.create(name, version, language, UUID.randomUUID().toString(), content);
  }

  /**
   * Finds script by external id
   * 
   * @param externalId external id to find script for
   * 
   * @return found script or null
   */
  public Script findScript(String externalId) {
    return scriptDAO.findByExternalId(externalId);
  }

  /**
   * Finds script by name and version
   * 
   * @param name script name
   * @param version script version
   * 
   * @return found script or null
   */
  public Script findScript(String name, String version) {
    return scriptDAO.findByNameAndVersion(name, version);
  }

  /**
   * Updates script
   * 
   * @param script script to update
   * @param content updated content
   * @param language updated language
   * 
   * @return updated script
   */
  public Script updateScript(Script script, String content, String language) {
    scriptDAO.updateContent(script, content);
    scriptDAO.updateLanguage(script, language);
    return script;
  }
}