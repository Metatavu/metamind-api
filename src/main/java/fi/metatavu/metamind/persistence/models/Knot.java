package fi.metatavu.metamind.persistence.models;

import fi.metatavu.metamind.api.spec.model.KnotType;
import fi.metatavu.metamind.api.spec.model.TokenizerType;
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
public class Knot {

  @Id
  private UUID id;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private KnotType type;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private TokenizerType tokenizerType;

  @NotNull
  @NotEmpty
  @Column(nullable = false)
  private String name;

  @NotNull
  @NotEmpty
  @Lob
  @Column(nullable = false)
  private String content;
  
  private String hint;

  @ManyToOne(optional = false)
  private Story story;

  @Column(nullable = false)
  @NotNull
  private UUID creatorId;

  @Column(nullable = false)
  @NotNull
  private UUID lastModifierId;
  
  @Column(nullable = true)
  private Double coordinateX;
  
  @Column(nullable = true)
  private Double coordinateY;

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

  public KnotType getType() {
    return type;
  }

  public void setType(KnotType type) {
    this.type = type;
  }
  
  public void setTokenizerType(TokenizerType tokenizerType) {
    this.tokenizerType = tokenizerType;
  }
  
  public TokenizerType getTokenizerType() {
    return tokenizerType;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
  
  public String getHint() {
    return hint;
  }
  
  public void setHint(String hint) {
    this.hint = hint;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
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
  
  public Double getCoordinateX() {
    return coordinateX;
  }
  
  public void setCoordinateX(Double coordinateX) {
    this.coordinateX = coordinateX;
  }
  
  public Double getCoordinateY() {
    return coordinateY;
  }
  
  public void setCoordinateY(Double coordinateY) {
    this.coordinateY = coordinateY;
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
