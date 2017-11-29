package fi.metatavu.metamind.freemarker;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.metamind.persistence.dao.FreemarkerTemplateDAO;
import fi.metatavu.metamind.persistence.models.FreemarkerTemplate;

/**
 * Controller for freemarker templates
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class FreemarkerTemplateController {
  
  @Inject
  private FreemarkerTemplateDAO freemarkerTemplateDAO;

  /**
   * Returns a freemarker template by id or null if not found
   * 
   * @param name template's name
   * @return a freemarker template by id or null if not found
   */
  public FreemarkerTemplate findFreemarkerTemplateById(Long id) {
    return freemarkerTemplateDAO.findById(id);
  }
  
  /**
   * Returns a freemarker template by name or null if not found
   * 
   * @param name template's name
   * @return a freemarker template by name or null if not found
   */
  public FreemarkerTemplate findFreemarkerTemplateByName(String name) {
    return freemarkerTemplateDAO.findByName(name);
  }
  
}
