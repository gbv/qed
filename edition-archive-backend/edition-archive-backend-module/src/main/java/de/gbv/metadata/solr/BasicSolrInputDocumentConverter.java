package de.gbv.metadata.solr;

import org.apache.solr.common.SolrInputDocument;
import org.mycore.datamodel.metadata.MCRMetaLinkID;
import org.mycore.datamodel.metadata.MCRObject;
import org.mycore.datamodel.metadata.MCRObjectID;
import org.mycore.datamodel.metadata.MCRObjectService;

import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class BasicSolrInputDocumentConverter<T> implements ObjectSolrInputDocumentConverter<T> {
  private static void applyDerivateIDs(MCRObject obj, SolrInputDocument document) {
    obj.getStructure().getDerivates().forEach(der -> {
      MCRObjectID id = der.getXLinkHrefID();
      document.addField("derivates", id.toString());
      document.addField("derivateLink", id.toString());
    });
  }

  private static void setDerCount(MCRObject obj, SolrInputDocument document) {
    document.setField("derCount", obj.getStructure().getDerivates().size());
  }

  private static void setParentID(MCRObject obj, SolrInputDocument document) {
    Optional.ofNullable(obj.getStructure().getParent()).map(MCRMetaLinkID::getXLinkHrefID).ifPresent(id -> {
      document.setField("parent", id.toString());
    });
  }

  @Override
  public SolrInputDocument convertToDocument(MCRObject obj, T classInstance) {
    SolrInputDocument document = new SolrInputDocument();

    document.setField("id", obj.getId().toString());
    document.setField("objectProject", obj.getId().getProjectId());
    document.setField("objectType", obj.getId().getTypeId());
    String modifiedDate = DateTimeFormatter.ISO_INSTANT.format(obj.getService().getDate(MCRObjectService.DATE_TYPE_MODIFYDATE).toInstant());
    String createdDate = DateTimeFormatter.ISO_INSTANT.format(obj.getService().getDate(MCRObjectService.DATE_TYPE_CREATEDATE).toInstant());

    document.setField("modified", modifiedDate);
    document.setField("created", createdDate);

    document.setField("modifiedby", obj.getService().getFlags(MCRObjectService.FLAG_TYPE_MODIFIEDBY));
    document.setField("createdby", obj.getService().getFlags(MCRObjectService.FLAG_TYPE_CREATEDBY));

    document.setField("state", obj.getService().getState().getID());

    setParentID(obj, document);
    setDerCount(obj, document);
    applyDerivateIDs(obj, document);

    return document;
  }
}
