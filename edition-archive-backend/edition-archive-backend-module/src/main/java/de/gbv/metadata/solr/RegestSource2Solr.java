package de.gbv.metadata.solr;

import de.gbv.metadata.model.RegestSource;
import org.apache.solr.common.SolrInputDocument;
import org.mycore.datamodel.metadata.MCRObject;

import java.util.Optional;

public class RegestSource2Solr extends BasicSolrInputDocumentConverter<RegestSource> {

  @Override
  public SolrInputDocument convertToDocument(MCRObject obj, RegestSource source) {
    SolrInputDocument solrInputFields = super.convertToDocument(obj, source);

    Optional.ofNullable(source.getEditionShortTitle()).ifPresent(est -> solrInputFields.setField("editionShortTitle", est));
    Optional.ofNullable(source.getLongTitle()).ifPresent(est -> solrInputFields.setField("longTitle", est));
    Optional.ofNullable(source.getShortTitle()).ifPresent(est -> solrInputFields.setField("shortTitle", est));

    source.getIdentifier().forEach(id -> solrInputFields
      .setField("identifier." + id.getType(), id.getIdentifier()));

    source.getUrls().forEach(url -> solrInputFields
      .setField("url."+url.getType().replace(" ", "_"), url.getURL()));

    return solrInputFields;
  }

}
