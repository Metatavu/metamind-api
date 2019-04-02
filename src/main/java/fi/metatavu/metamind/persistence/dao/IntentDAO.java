package fi.metatavu.metamind.persistence.dao;

import java.util.UUID;
import fi.metatavu.metamind.persistence.models.*;
import fi.metatavu.metamind.rest.model.IntentType;

/**
 * DAO class for Intent
 * 
 * @author Antti Leppä
 */
public class IntentDAO extends AbstractDAO<Intent> {

    /**
     * Creates new intent
     *
     * @param type type
     * @param sourceKnot sourceKnot
     * @param targetKnot targetKnot
     * @param global global
     * @param creatorId creator's id
     * @param lastModifierId last modifier's id
     * @return created intent
     */
    public Intent create(UUID id, IntentType type, Knot sourceKnot, Knot targetKnot, Boolean global, UUID creatorId, UUID lastModifierId) {
      Intent intent = new Intent();
      intent.setType(type);
      intent.setSourceKnot(sourceKnot);
      intent.setTargetKnot(targetKnot);
      intent.setGlobal(global);
      intent.setId(id);
      intent.setCreatorId(creatorId);
      intent.setLastModifierId(lastModifierId);
      return persist(intent);
    }

    /**
     * Updates type
     *
     * @param type type
     * @param lastModifierId last modifier's id
     * @return updated intent
     */
    public Intent updateType(Intent intent, IntentType type, UUID lastModifierId) {
      intent.setLastModifierId(lastModifierId);
      intent.setType(type);
      return persist(intent);
    }

    /**
     * Updates sourceKnot
     *
     * @param sourceKnot sourceKnot
     * @param lastModifierId last modifier's id
     * @return updated intent
     */
    public Intent updateSourceKnot(Intent intent, Knot sourceKnot, UUID lastModifierId) {
      intent.setLastModifierId(lastModifierId);
      intent.setSourceKnot(sourceKnot);
      return persist(intent);
    }

    /**
     * Updates targetKnot
     *
     * @param targetKnot targetKnot
     * @param lastModifierId last modifier's id
     * @return updated intent
     */
    public Intent updateTargetKnot(Intent intent, Knot targetKnot, UUID lastModifierId) {
      intent.setLastModifierId(lastModifierId);
      intent.setTargetKnot(targetKnot);
      return persist(intent);
    }

    /**
     * Updates global
     *
     * @param global global
     * @param lastModifierId last modifier's id
     * @return updated intent
     */
    public Intent updateGlobal(Intent intent, Boolean global, UUID lastModifierId) {
      intent.setLastModifierId(lastModifierId);
      intent.setGlobal(global);
      return persist(intent);
    }

}
