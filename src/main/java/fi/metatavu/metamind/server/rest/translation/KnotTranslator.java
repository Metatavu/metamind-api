package fi.metatavu.metamind.server.rest.translation;

import javax.enterprise.context.ApplicationScoped;

import fi.metatavu.metamind.rest.model.Knot;

/**
 * Translator for translating JPA knot entities into REST entities
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class KnotTranslator {

  /**
   * Translates JPA knot into REST knot
   * 
   * @param jpaKnot JPA knot
   * @param quickResponses JPA quick responses
   * @param knotResponses JPA knot responses
   * @return REST knot
   */
  public Knot translateKnot(fi.metatavu.metamind.persistence.models.Knot jpaKnot) {
    if (jpaKnot == null) {
      return null;
    }

    Knot result = new Knot();
    result.setCreatedAt(jpaKnot.getCreatedAt());
    result.setId(jpaKnot.getId());
    result.setModifiedAt(jpaKnot.getModifiedAt());
    result.setContent(jpaKnot.getContent());
    result.setName(jpaKnot.getName());
    result.setStoryId(jpaKnot.getStory() != null ? jpaKnot.getStory().getId() : null);
    result.setType(jpaKnot.getType());
    result.setHint(jpaKnot.getHint());
    result.setTokenizer(jpaKnot.getTokenizerType());
      
    return result;
  }

}
