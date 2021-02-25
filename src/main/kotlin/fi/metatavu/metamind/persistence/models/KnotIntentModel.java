package fi.metatavu.metamind.persistence.models;

import fi.metatavu.metamind.api.spec.model.TrainingMaterialType;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class KnotIntentModel {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  @Enumerated (EnumType.STRING)
  @Column(nullable = false)
  private TrainingMaterialType type;

  @Column(nullable = false)
  @Lob
  private byte[] data;
  
  @ManyToOne
  private Knot knot;
  
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }
  
  public byte[] getData() {
    return data;
  }
  
  public void setData(byte[] data) {
    this.data = data;
  }
  
  public Knot getKnot() {
    return knot;
  }
  
  public void setKnot(Knot knot) {
    this.knot = knot;
  }
  
  public TrainingMaterialType getType() {
    return type;
  }
  
  public void setType(TrainingMaterialType type) {
    this.type = type;
  }
  
}
