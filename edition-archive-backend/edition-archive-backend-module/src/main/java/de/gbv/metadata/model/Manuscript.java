package de.gbv.metadata.model;

import de.gbv.metadata.URLType;
import de.gbv.metadata.solr.Manuscript2Solr;
import de.gbv.metadata.solr.SolrConverter;

import java.util.LinkedList;
import java.util.List;

@SolrConverter(converter = Manuscript2Solr.class)
public class Manuscript {

  private String id;
  private String shelfmark;
  private String date;
  private List<URLType> urls;


  public Manuscript() {
    urls = new LinkedList<>();
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getShelfmark() {
    return shelfmark;
  }

  public void setShelfmark(String shelfmark) {
    this.shelfmark = shelfmark;
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public List<URLType> getUrls() {
    return urls;
  }
}
