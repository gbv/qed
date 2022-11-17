package de.gbv.metadata.solr;

import java.util.Optional;

import org.apache.solr.common.SolrInputDocument;
import org.mycore.datamodel.metadata.MCRObject;

import de.gbv.metadata.model.Manuscript;

public class Manuscript2Solr extends BasicSolrInputDocumentConverter<Manuscript> {

  @Override
  public SolrInputDocument convertToDocument(MCRObject obj, Manuscript manuscript) {
    SolrInputDocument base = super.convertToDocument(obj, manuscript);

    Optional.ofNullable(manuscript.getId()).ifPresent(id -> {
        base.setField("identifier.key", id);
    });

Optional.ofNullable(manuscript.getShelfmark()).ifPresent(shelfmark -> {
        base.setField("shelfmark", shelfmark);
    });

    Optional.ofNullable(manuscript.getDate()).ifPresent(date -> {
        base.setField("date.string", date);
    });

    manuscript.getUrls().forEach(url -> base.setField("url."+url.getType().replace(" ", "_"), url.getURL()));


    return base;
  }


}
