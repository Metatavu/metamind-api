package fi.metatavu.metamind.server.rest.translation;

import fi.metatavu.metamind.api.spec.model.Coordinates;
import fi.metatavu.metamind.api.spec.model.Knot;

import javax.enterprise.context.ApplicationScoped;

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
   * @return REST knot
   */
  public Knot translateKnot(fi.metatavu.metamind.persistence.models.Knot jpaKnot) {
    if (jpaKnot == null) {
      return null;
    }
    Coordinates coordinates = new Coordinates();
    coordinates.setX(jpaKnot.getCoordinateX());
    coordinates.setY(jpaKnot.getCoordinateY());

    Knot result = new Knot();
    result.setCreatedAt(jpaKnot.getCreatedAt());
    result.setId(jpaKnot.getId());
    result.setModifiedAt(jpaKnot.getModifiedAt());
    result.setContent(jpaKnot.getContent());
    result.setName(jpaKnot.getName());
    result.setStoryId(jpaKnot.getStory() != null ? jpaKnot.getStory().getId() : null);
    result.setType(jpaKnot.getType());
    result.setCoordinates(coordinates);
    result.setHint(jpaKnot.getHint());
    result.setTokenizer(jpaKnot.getTokenizerType());
    result.setScope(jpaKnot.getScope());
      
    return result;
  }

}
