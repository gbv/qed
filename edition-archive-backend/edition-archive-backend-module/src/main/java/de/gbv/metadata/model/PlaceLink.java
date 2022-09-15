package de.gbv.metadata.model;

public class PlaceLink {

  private String label;

  private String mycoreId;

  public PlaceLink() {
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public String getMycoreId() {
    return mycoreId;
  }

  public void setMycoreId(String mycoreId) {
    this.mycoreId = mycoreId;
  }


  @Override
  public String toString() {
    return "PlaceLink{" +
      "label='" + label + '\'' +
      ", mycoreId='" + mycoreId + '\'' +
      '}';
  }
}
