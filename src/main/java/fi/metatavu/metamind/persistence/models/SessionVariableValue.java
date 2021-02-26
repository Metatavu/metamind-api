package fi.metatavu.metamind.persistence.models;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
@Table (
  uniqueConstraints = @UniqueConstraint (columnNames = { "session_id", "variable_id" }, name = "UN_SESSION_VARIABLE_SESSION_ID_VARIABLE_ID" )
)
public class SessionVariableValue {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false)
  private Session session;

  @ManyToOne(optional = false)
  private Variable variable;

  @NotNull
  @NotEmpty
  @Column(nullable = false)
  @Lob
  private String value;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public Variable getVariable() {
    return variable;
  }

  public void setVariable(Variable variable) {
    this.variable = variable;
  }
  
  public Session getSession() {
    return session;
  }
  
  public void setSession(Session session) {
    this.session = session;
  }

}
