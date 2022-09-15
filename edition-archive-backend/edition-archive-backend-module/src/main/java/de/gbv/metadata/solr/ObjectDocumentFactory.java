package de.gbv.metadata.solr;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.common.SolrInputDocument;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.mycore.common.MCRException;
import org.mycore.common.content.MCRContent;
import org.mycore.datamodel.metadata.MCRObject;
import org.mycore.datamodel.metadata.MCRObjectID;
import org.mycore.datamodel.metadata.MetaJSON;
import org.mycore.solr.index.document.MCRSolrInputDocumentFactory;
import org.mycore.solr.index.document.MCRSolrTransformerInputDocumentFactory;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ObjectDocumentFactory extends MCRSolrInputDocumentFactory {

  private static final Logger LOGGER = LogManager.getLogger();

  private final MCRSolrInputDocumentFactory fallback = new MCRSolrTransformerInputDocumentFactory();


  @Override
  public SolrInputDocument getDocument(MCRObjectID mcrObjectID, MCRContent mcrContent) throws SAXException, IOException {
    LOGGER.info("get Document {}", mcrObjectID);
    try {
      Document asXML = mcrContent.asXML();

      if (asXML.getRootElement().getName().equals("mycoreobject")) {
        MCRObject object = new MCRObject(asXML);
        List<MetaJSON> jsons = object.getMetadata().stream().filter(meta -> meta.getClassName().equals(MetaJSON.class.getSimpleName())).map(meta -> (MetaJSON) meta.getElement(0)).toList();

        if (jsons.size() > 0) {
          for (MetaJSON json : jsons) {
            Object o = json.getObject();
            Class<?> clazz = o.getClass();

            LOGGER.info("Found json metadata with class: {}", clazz.getName());

            SolrConverter converter = clazz.getAnnotation(SolrConverter.class);
            SolrInputDocument doc = new SolrInputDocument();
            Class<? extends ObjectSolrInputDocumentConverter> converterClass = converter.converter();
            try {
              ObjectSolrInputDocumentConverter converterInstance = converterClass.getDeclaredConstructor().newInstance();
              return converterInstance.convertToDocument(object, o);
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                     InvocationTargetException e) {
              throw new MCRException(e);
            }
          }
        }
      }
    } catch (JDOMException e) {
      throw new MCRException(e);
    }

    LOGGER.info("Fall back to old Transformer");
    return fallback.getDocument(mcrObjectID, mcrContent);
  }

  @Override
  public Iterator<SolrInputDocument> getDocuments(Map<MCRObjectID, MCRContent> map) throws IOException, SAXException {
    return map.entrySet().stream().map(es -> {
      try {
        return getDocument(es.getKey(), es.getValue());
      } catch (SAXException | IOException e) {
        throw new RuntimeException(e);
      }
    }).toList().iterator();
  }
}
