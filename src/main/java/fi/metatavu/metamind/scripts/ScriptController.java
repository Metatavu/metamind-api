package fi.metatavu.metamind.scripts;

import java.util.List;
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
   * @param name name
   * @param content content
   * @param version version
   * @param language language
   * @param creatorId creator's id
   * @return created script
   */
  public Script createScript(String name, String content, String version, String language, UUID creatorId) {
    return scriptDAO.create(UUID.randomUUID(), name, content, version, language, creatorId, creatorId);
  }

  /**
   * Finds script by id
   * 
   * @param id id to find script for
   * 
   * @return found script or null
   */
  public Script findScriptById(UUID id) {
    return scriptDAO.findById(id);
  }

  /**
   * Finds script by name and version
   * 
   * @param name script name
   * @param version script version
   * 
   * @return found script or null
   */
  public Script findScriptByNameAndVersion(String name, String version) {
    return scriptDAO.findByNameAndVersion(name, version);
  }

  /**
   * Lists scripts
   * 
   * @return scripts
   */
  public List<Script> listScripts() {
    return scriptDAO.listAll();
  }

  /**
   * Update script
   *
   * @param script script
   * @param content content
   * @param language language
   * @param lastModifierId last modifier's id
   * @return updated script
   */
  public Script updateScript(Script script, String content, String language, UUID lastModifierId) {
    return scriptDAO.updateLanguage(scriptDAO.updateContent(script, content, lastModifierId), language, lastModifierId);
  }
  
  /**
   * Deletes a script
   * 
   * @param script script
   */
  public void deleteScript(Script script) {
    scriptDAO.delete(script);
  }
  
}