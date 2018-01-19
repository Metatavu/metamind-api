package fi.metatavu.metamind.kuntaapi;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Events {
  private String id;
  private String name;
  private String originalUrl;
  private String description;
  private String start;
  private String end;
  private String city;
  private String place;
  private String address;
  private String zip;

  public String getZip() {
    return zip;
  }

  public void setZip(String zip) {
    this.zip = zip;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getOriginalUrl() {
    return originalUrl;
  }

  public void setOriginalUrl(String originalUrl) {
    this.originalUrl = originalUrl;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getStart() {
    return start;
  }

  public void setStart(String start) {
    this.start = start;
  }

  public String getEnd() {
    return end;
  }

  public void setEnd(String end) {
    this.end = end;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getPlace() {
    return place;
  }

  public void setPlace(String place) {
    this.place = place;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  @JsonCreator
  public Events(@JsonProperty("id") String id, @JsonProperty("name") String name,
      @JsonProperty("originalUrl") String originalUrl, @JsonProperty("description") String description,
      @JsonProperty("start") String start, @JsonProperty("end") String end, @JsonProperty("city") String city,
      @JsonProperty("place") String place, @JsonProperty("address") String address, @JsonProperty("zip") String zip) {
    super();
    this.id = id;
    this.name = name;
    this.originalUrl = originalUrl;
    this.description = description;
    this.start = start;
    this.end = end;
    this.city = city;
    this.place = place;
    this.address = address;
    this.zip = zip;
  }

  @Override
  public String toString() {
    return "Events [id=" + id + ", name=" + name + ", originalUrl=" + originalUrl + ", description=" + description
        + ", start=" + start + ", end=" + end + ", city=" + city + ", place=" + place + ", address=" + address
        + ", zip=" + zip + "]";
  }
}
