package de.gbv.metadata.solr;

import org.apache.solr.common.SolrInputDocument;
import org.mycore.datamodel.metadata.MCRObject;
public interface ObjectSolrInputDocumentConverter<T> {
  SolrInputDocument convertToDocument(MCRObject obj, T classInstance);
}
