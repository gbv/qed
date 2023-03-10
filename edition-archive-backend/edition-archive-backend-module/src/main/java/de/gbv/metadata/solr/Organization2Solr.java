package de.gbv.metadata.solr;

import de.gbv.metadata.model.Organization;
import de.gbv.metadata.model.Place;
import org.apache.solr.common.SolrInputDocument;
import org.mycore.datamodel.metadata.MCRObject;

public class Organization2Solr extends BasicSolrInputDocumentConverter<Organization> {

  @Override
  public SolrInputDocument convertToDocument(MCRObject obj, Organization organization) {
    SolrInputDocument base = super.convertToDocument(obj, organization);

    base.setField("displayName", organization.getDisplayName());

    organization.getIdentifier().forEach(idenitifer -> {
      base.addField("identifier." + idenitifer.getType(), idenitifer.getIdentifier());
    });

    return base;
  }
}
