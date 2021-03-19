package fi.metatavu.metamind.persistence.models;

import fi.metatavu.metamind.api.spec.model.IntentType;
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
public class Intent {

  @Id
  private UUID id;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private IntentType type;

  @NotNull
  @NotEmpty
  @Column(nullable = false)
  private String name;

  @ManyToOne
  private Knot sourceKnot;

  @ManyToOne(optional = false)
  private Knot targetKnot;

  @Column(nullable = false)
  private Boolean global;
  
  private String quickResponse;

  @Column(nullable = false)
  private Integer quickResponseOrder;

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

  public IntentType getType() {
    return type;
  }

  public void setType(IntentType type) {
    this.type = type;
  }
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }

  public Knot getSourceKnot() {
    return sourceKnot;
  }

  public void setSourceKnot(Knot sourceKnot) {
    this.sourceKnot = sourceKnot;
  }

  public Knot getTargetKnot() {
    return targetKnot;
  }

  public void setTargetKnot(Knot targetKnot) {
    this.targetKnot = targetKnot;
  }

  public Boolean getGlobal() {
    return global;
  }

  public void setGlobal(Boolean global) {
    this.global = global;
  }
  
  public String getQuickResponse() {
    return quickResponse;
  }
  
  public void setQuickResponse(String quickResponse) {
    this.quickResponse = quickResponse;
  }
  
  public Integer getQuickResponseOrder() {
    return quickResponseOrder;
  }
  
  public void setQuickResponseOrder(Integer quickResponseOrder) {
    this.quickResponseOrder = quickResponseOrder;
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
