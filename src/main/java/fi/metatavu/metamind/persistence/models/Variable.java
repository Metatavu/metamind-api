package fi.metatavu.metamind.persistence.models;

import java.time.OffsetDateTime;
import java.util.UUID;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import fi.metatavu.metamind.rest.model.VariableType;

/**
 * JPA entity representing a variable
 * 
 * @author Antti Leppä
 */
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class Variable {

  @Id
  private UUID id;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private VariableType type;

  @ManyToOne(optional = false)
  private Story story;

  @NotNull
  @NotEmpty
  @Column(nullable = false)
  private String name;

  @Lob
  private String validationScript;

  @Column(nullable = false)
  @NotNull
  private UUID creatorId;

  @Column(nullable = false)
  @NotNull
  private UUID lastModifierId;

  @Column(nullable = false)
  private OffsetDateTime createdAt;

  @Column(nullable = false)
  private OffsetDateTime modifiedAt;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public VariableType getType() {
    return type;
  }

  public void setType(VariableType type) {
    this.type = type;
  }

  public Story getStory() {
    return story;
  }

  public void setStory(Story story) {
    this.story = story;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getValidationScript() {
    return validationScript;
  }

  public void setValidationScript(String validationScript) {
    this.validationScript = validationScript;
  }

  public UUID getCreatorId() {
    return creatorId;
  }

  public void setCreatorId(UUID creatorId) {
    this.creatorId = creatorId;
  }

  public UUID getLastModifierId() {
    return lastModifierId;
  }

  public void setLastModifierId(UUID lastModifierId) {
    this.lastModifierId = lastModifierId;
  }

  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public OffsetDateTime getModifiedAt() {
    return modifiedAt;
  }

  public void setModifiedAt(OffsetDateTime modifiedAt) {
    this.modifiedAt = modifiedAt;
  }

  @PrePersist
  public void onCreate() {
    setCreatedAt(OffsetDateTime.now());
    setModifiedAt(OffsetDateTime.now());
  }

  @PreUpdate
  public void onUpdate() {
    setModifiedAt(OffsetDateTime.now());
  }
}
