package fi.metatavu.metamind.persistence.models;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

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
