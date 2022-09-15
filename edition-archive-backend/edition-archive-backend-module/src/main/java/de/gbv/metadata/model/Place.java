package de.gbv.metadata.model;

import de.gbv.metadata.IdentifierType;
import de.gbv.metadata.solr.Place2Solr;
import de.gbv.metadata.solr.SolrConverter;

import java.util.ArrayList;
import java.util.List;

@SolrConverter(converter = Place2Solr.class)
public class Place {
  private String displayName;
  private List<IdentifierType> identifier = new ArrayList<>();


  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public List<IdentifierType> getIdentifier() {
    return identifier;
  }

  public void setIdentifier(List<IdentifierType> identifier) {
    this.identifier = identifier;
  }


}
