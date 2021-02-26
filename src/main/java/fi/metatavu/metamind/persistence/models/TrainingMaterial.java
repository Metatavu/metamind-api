package fi.metatavu.metamind.persistence.models;

import fi.metatavu.metamind.api.spec.model.TrainingMaterialType;
import fi.metatavu.metamind.api.spec.model.TrainingMaterialVisibility;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class TrainingMaterial {

  @Id
  private UUID id;
  
  @Enumerated (EnumType.STRING)
  @Column(nullable = false)
  private TrainingMaterialType type;
  
  @Enumerated (EnumType.STRING)
  @Column(nullable = false)
  private TrainingMaterialVisibility visibility;

  @NotNull
  @NotEmpty
  @Column(nullable = false)
  private String name;

  @NotNull
  @NotEmpty
  @Column(nullable = false)
  @Lob
  private String text;
  
  @ManyToOne
  private Story story;

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
  
  public TrainingMaterialType getType() {
    return type;
  }
  
  public void setType(TrainingMaterialType type) {
    this.type = type;
  }
  
  public TrainingMaterialVisibility getVisibility() {
    return visibility;
  }
  
  public void setVisibility(TrainingMaterialVisibility visibility) {
    this.visibility = visibility;
  }

  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public String getText() {
    return text;
  }
  
  public void setText(String text) {
    this.text = text;
  }
  
  public Story getStory() {
    return story;
  }
  
  public void setStory(Story story) {
    this.story = story;
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
