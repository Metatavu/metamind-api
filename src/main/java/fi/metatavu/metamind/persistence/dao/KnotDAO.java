package fi.metatavu.metamind.persistence.dao;

import java.util.UUID;
import fi.metatavu.metamind.persistence.models.*;
import fi.metatavu.metamind.rest.model.KnotType;

/**
 * DAO class for Knot
 * 
 * @author Antti Leppä
 */
public class KnotDAO extends AbstractDAO<Knot> {

    /**
     * Creates new knot
     *
     * @param type type
     * @param name name
     * @param content content
     * @param story story
     * @param creatorId creator's id
     * @param lastModifierId last modifier's id
     * @return created knot
     */
    public Knot create(UUID id, KnotType type, String name, String content, Story story, UUID creatorId, UUID lastModifierId) {
      Knot knot = new Knot();
      knot.setType(type);
      knot.setName(name);
      knot.setContent(content);
      knot.setStory(story);
      knot.setId(id);
      knot.setCreatorId(creatorId);
      knot.setLastModifierId(lastModifierId);
      return persist(knot);
    }

    /**
     * Updates type
     *
     * @param type type
     * @param lastModifierId last modifier's id
     * @return updated knot
     */
    public Knot updateType(Knot knot, KnotType type, UUID lastModifierId) {
      knot.setLastModifierId(lastModifierId);
      knot.setType(type);
      return persist(knot);
    }

    /**
     * Updates name
     *
     * @param name name
     * @param lastModifierId last modifier's id
     * @return updated knot
     */
    public Knot updateName(Knot knot, String name, UUID lastModifierId) {
      knot.setLastModifierId(lastModifierId);
      knot.setName(name);
      return persist(knot);
    }

    /**
     * Updates content
     *
     * @param content content
     * @param lastModifierId last modifier's id
     * @return updated knot
     */
    public Knot updateContent(Knot knot, String content, UUID lastModifierId) {
      knot.setLastModifierId(lastModifierId);
      knot.setContent(content);
      return persist(knot);
    }

    /**
     * Updates story
     *
     * @param story story
     * @param lastModifierId last modifier's id
     * @return updated knot
     */
    public Knot updateStory(Knot knot, Story story, UUID lastModifierId) {
      knot.setLastModifierId(lastModifierId);
      knot.setStory(story);
      return persist(knot);
    }

}
