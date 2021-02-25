package fi.metatavu.metamind.persistence.models;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class IntentTrainingMaterial {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  @ManyToOne
  private TrainingMaterial trainingMaterial;
 
  @ManyToOne
  private Intent intent;
  
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }
  
  public Intent getIntent() {
    return intent;
  }
  
  public void setIntent(Intent intent) {
    this.intent = intent;
  }
  
  public TrainingMaterial getTrainingMaterial() {
    return trainingMaterial;
  }
  
  public void setTrainingMaterial(TrainingMaterial trainingMaterial) {
    this.trainingMaterial = trainingMaterial;
  }
  
}
