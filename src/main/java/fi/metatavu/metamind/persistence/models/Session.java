package fi.metatavu.metamind.persistence.models;

import java.time.OffsetDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.PrePersist;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

@Entity
public class Session {
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull
  @NotEmpty
  @Column(nullable = false)
  private String externalId;
  
  @Column(nullable = false)
  private OffsetDateTime created;
  
  @NotNull
  @NotEmpty
  @Column(nullable = false)
  private String locale;

  @NotNull
  @NotEmpty
  @Column(nullable = false)
  private String timeZone;
  
  private String visitor;
  
  @Lob
  @NotNull
  @Column(nullable = false)
  private byte[] data;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getExternalId() {
    return externalId;
  }

  public void setExternalId(String externalId) {
    this.externalId = externalId;
  }

  public OffsetDateTime getCreated() {
    return created;
  }

  public void setCreated(OffsetDateTime created) {
    this.created = created;
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
  
  public byte[] getData() {
    return data;
  }
  
  public void setData(byte[] data) {
    this.data = data;
  }
  
  public String getVisitor() {
    return visitor;
  }
  
  public void setVisitor(String visitor) {
    this.visitor = visitor;
  }

  @PrePersist
  public void onCreate() {
    setCreated(OffsetDateTime.now());
  }
  
}