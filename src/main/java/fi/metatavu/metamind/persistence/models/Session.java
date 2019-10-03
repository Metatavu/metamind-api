package fi.metatavu.metamind.persistence.models;

import java.time.OffsetDateTime;
import java.util.UUID;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class Session {
  
  @Id
  private UUID id;

  @ManyToOne(optional = false)
  private Story story;

  @NotNull
  @NotEmpty
  @Column(nullable = false)
  private String locale;

  @NotNull
  @NotEmpty
  @Column(nullable = false)
  private String timeZone;

  private String visitor;

  @ManyToOne
  private Knot currentKnot;

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

  public Story getStory() {
    return story;
  }

  public void setStory(Story story) {
    this.story = story;
  }
  
  public Knot getCurrentKnot() {
    return currentKnot;
  }
  
  public void setCurrentKnot(Knot currentKnot) {
    this.currentKnot = currentKnot;
  }

  public String getLocale() {
    return locale;
  }

  public void setLocale(String locale) {
    this.locale = locale;
  }

  public String getTimeZone() {
    return timeZone;
  }

  public void setTimeZone(String timeZone) {
    this.timeZone = timeZone;
  }

  public String getVisitor() {
    return visitor;
  }

  public void setVisitor(String visitor) {
    this.visitor = visitor;
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
