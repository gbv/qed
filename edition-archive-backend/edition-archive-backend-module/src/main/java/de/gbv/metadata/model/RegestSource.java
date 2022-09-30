package de.gbv.metadata.model;

import de.gbv.metadata.IdentifierType;
import de.gbv.metadata.URLType;
import de.gbv.metadata.solr.RegestSource2Solr;
import de.gbv.metadata.solr.SolrConverter;

import java.util.LinkedList;
import java.util.List;

@SolrConverter(converter = RegestSource2Solr.class)
public class RegestSource {

  List<IdentifierType> identifier;
  List<URLType> urls;
  String shortTitle;
  String editionShortTitle;
  String longTitle;
  Boolean source;


  public RegestSource() {
    urls = new LinkedList<>();
    identifier = new LinkedList<>();
  }

  public List<IdentifierType> getIdentifier() {
    return identifier;
  }

  public List<URLType> getUrls() {
    return urls;
  }

  public String getShortTitle() {
    return shortTitle;
  }

  public String getEditionShortTitle() {
    return editionShortTitle;
  }

  public String getLongTitle() {
    return longTitle;
  }

  public Boolean getSource() {
    return source;
  }


  public void setShortTitle(String shortTitle) {
    this.shortTitle = shortTitle;
  }

  public void setEditionShortTitle(String editionShortTitle) {
    this.editionShortTitle = editionShortTitle;
  }

  public void setLongTitle(String longTitle) {
    this.longTitle = longTitle;
  }

  public void setSource(Boolean source) {
    this.source = source;
  }
}
