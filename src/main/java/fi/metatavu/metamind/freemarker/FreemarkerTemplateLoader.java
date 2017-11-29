package fi.metatavu.metamind.freemarker;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.metamind.persistence.models.FreemarkerTemplate;
import freemarker.cache.TemplateLoader;

/**
 * Freemarker template loader for loading templates from database
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class FreemarkerTemplateLoader implements TemplateLoader {

  @Inject
  private FreemarkerTemplateController freemarkerTemplateController;
 
  @Override
  public Object findTemplateSource(String name) {
    FreemarkerTemplate freemarkerTemplate = freemarkerTemplateController.findFreemarkerTemplateByName(name);
    if (freemarkerTemplate == null) {
      return null;
    }
    
    return freemarkerTemplate.getId();
  }

  @Override
  public long getLastModified(Object templateSource) {
    Long templateId = (Long) templateSource;
    if (templateId != null) {
      FreemarkerTemplate template = freemarkerTemplateController.findFreemarkerTemplateById(templateId);
      return template.getModified().toInstant().toEpochMilli();
    }
    
    return 0;
  }

  @Override
  public Reader getReader(Object templateSource, String encoding) throws IOException {
    Long templateId = (Long) templateSource;
    if (templateId != null) {
      FreemarkerTemplate template = freemarkerTemplateController.findFreemarkerTemplateById(templateId);
      return new StringReader(template.getData());
    }
    
    return null;
  }

  @Override
  public void closeTemplateSource(Object templateSource) throws IOException {
    // Template loader is id, so no need to close
  }
  
}
