package fi.metatavu.metamind.persistence.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Table (
  uniqueConstraints = {
    @UniqueConstraint (name = "UN_SCRIPT_NAME_VERSION", columnNames = {"name", "version"})
  }
)
public class Script {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull
  @NotEmpty
  @Column(nullable = false)
  private String externalId;
  
  @NotNull
  @NotEmpty
  @Column(nullable = false)
  @Lob
  private String name;

  @NotNull
  @NotEmpty
  @Column(nullable = false)
  @Lob
  private String content;

  @NotNull
  @NotEmpty
  @Column(nullable = false)
  private String version;
  
  @NotNull
  @NotEmpty
  @Column(nullable = false)
  private String language;

  /**
   * @return the id
   */
  public Long getId() {
    return id;
  }

  /**
   * @return the externalId
   */
  public String getExternalId() {
    return externalId;
  }

  /**
   * @param externalId the externalId to set
   */
  public void setExternalId(String externalId) {
    this.externalId = externalId;
  }

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * @param name the name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * @return the content
   */
  public String getContent() {
    return content;
  }

  /**
   * @param content the content to set
   */
  public void setContent(String content) {
    this.content = content;
  }

  /**
   * @return the version
   */
  public String getVersion() {
    return version;
  }

  /**
   * @param version the version to set
   */
  public void setVersion(String version) {
    this.version = version;
  }

  /**
   * @return the language
   */
  public String getLanguage() {
    return language;
  }

  /**
   * @param language the language to set
   */
  public void setLanguage(String language) {
    this.language = language;
  }
}
