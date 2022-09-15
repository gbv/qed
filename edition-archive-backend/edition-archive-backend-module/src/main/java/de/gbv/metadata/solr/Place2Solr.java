package de.gbv.metadata.solr;

import de.gbv.metadata.model.Place;
import org.apache.solr.common.SolrInputDocument;
import org.mycore.datamodel.metadata.MCRObject;

public class Place2Solr extends BasicSolrInputDocumentConverter<Place> {

  @Override
  public SolrInputDocument convertToDocument(MCRObject obj, Place place) {
    SolrInputDocument base = super.convertToDocument(obj, place);

    base.setField("displayName", place.getDisplayName());

    place.getIdentifier().forEach(idenitifer -> {
      base.addField("identifier." + idenitifer.getType(), idenitifer.getIdentifier());
    });

    return base;
  }
}
