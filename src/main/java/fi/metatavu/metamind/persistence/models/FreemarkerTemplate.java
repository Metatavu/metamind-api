package fi.metatavu.metamind.persistence.models;

import java.time.OffsetDateTime;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * JPA entity for storing freemarker templates
 * 
 * @author Antti Leppä
 */
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class FreemarkerTemplate {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull
  @NotEmpty
  @Column(nullable = false, unique = true)
  private String name;
  
  @Column(nullable = true)
  @Lob
  private String data;
  
  @Column(nullable = false)
  private OffsetDateTime created;
  
  @Column(nullable = false)
  private OffsetDateTime modified;
  
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getData() {
    return data;
  }
  
  public void setData(String data) {
    this.data = data;
  }
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }

  public OffsetDateTime getCreated() {
    return created;
  }
  
  public void setCreated(OffsetDateTime created) {
    this.created = created;
  }
  
  public OffsetDateTime getModified() {
    return modified;
  }
  
  public void setModified(OffsetDateTime modified) {
    this.modified = modified;
  }
  
  @PrePersist
  public void onCreate() {
    setCreated(OffsetDateTime.now());
  }
  
  @PreUpdate
  public void onUpdate() {
    setModified(OffsetDateTime.now());
  }
  
}
