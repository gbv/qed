package de.gbv.metadata;

public class URLType {

  public URLType() {
  }

  public URLType(String type, String URL) {
    this.URL = URL;
    this.type = type;
  }

  public String getURL() {
    return URL;
  }

  public void setURL(String URL) {
    this.URL = URL;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  String URL;
  String type;



}
