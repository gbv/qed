package de.gbv.metadata.solr;

import de.gbv.metadata.Authenticity;
import de.gbv.metadata.MetaJSONHelper;
import de.gbv.metadata.model.EntityLink;
import de.gbv.metadata.model.Organization;
import de.gbv.metadata.model.Person;
import de.gbv.metadata.model.Regest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.common.SolrInputDocument;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;
import org.mycore.common.MCRException;
import org.mycore.datamodel.metadata.MCRMetaLink;
import org.mycore.datamodel.metadata.MCRMetadataManager;
import org.mycore.datamodel.metadata.MCRObject;
import org.mycore.datamodel.metadata.MCRObjectID;
import org.mycore.datamodel.niofs.MCRPath;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.text.MessageFormat;
import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalQueries;
import java.time.temporal.TemporalQuery;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static de.gbv.metadata.CEIImporter.CEI_NAMESPACE;

public class Regest2Solr extends BasicSolrInputDocumentConverter<Regest> {

  private static final Logger LOGGER = LogManager.getLogger();

  private static void bodyXML(MCRObject obj, SolrInputDocument base) {
    String derivate = obj.getStructure().getDerivates().stream().map(MCRMetaLink::getXLinkHref).findFirst().get();
    MCRPath path = MCRPath.getPath(derivate, "/regest.xml");
    String xmlAsString = null;

    try {
      xmlAsString = Files.readString(path);
    } catch (IOException e) {
      throw new MCRException(e);
    }

    base.setField("regest.xml", xmlAsString);
    base.setField("allMeta", xmlAsString);

    try(InputStream is = Files.newInputStream(path)) {
      Document doc;
      doc = new SAXBuilder().build(is);

      extractSourceLinks(base, doc);
      extractManuscriptLinks(base, doc);
      extractDekretaleLinks(base, doc);
      extractJaffe(base, doc);
    } catch (IOException | JDOMException e) {
      throw new MCRException(e);
    }
  }

  private static void extractJaffe(SolrInputDocument base, Document doc)  {
    LOGGER.info("Extracting Jaffe");
    XPathFactory xpfac = XPathFactory.instance();
    XPathExpression<Element> xp = xpfac.compile(".//cei:bibl[@key='" + Normalizer.normalize("Jaffé_2", Normalizer.Form.NFC) +"']/cei:ref", Filters.element(), null, CEI_NAMESPACE);
    List<Element> jaffeRefElements = xp.evaluate(doc);
    for (Element jaffe : jaffeRefElements) {
      String textContent = jaffe.getText();
      base.addField("jaffe2", textContent);
      LOGGER.info("Jaffe2: " + textContent);
    }

    xpfac = XPathFactory.instance();
    xp = xpfac.compile(".//cei:bibl[@key='J3']/cei:ref", Filters.element(), null, CEI_NAMESPACE);
    jaffeRefElements = xp.evaluate(doc);
    for (Element jaffe : jaffeRefElements) {
      String textContent = jaffe.getText();
      base.addField("jaffe3", textContent);
      LOGGER.info("Jaffe3: " + textContent);
    }
  }

  private static void extractSourceLinks(SolrInputDocument base, Document doc) {
    XPathExpression<Element> biblXP = XPathFactory.instance().compile(".//cei:bibl[@type='Sigle' or @type='Kurztitel']", Filters.element(), null, CEI_NAMESPACE);
    List<Element> biblList = biblXP.evaluate(doc.getRootElement());
    for (Element bibl : biblList) {
      String key = bibl.getAttributeValue("key");
      if (key != null && !key.isBlank()) {
        base.addField("source.key", key);
      }
    }
  }

  private static void extractDekretaleLinks(SolrInputDocument base, Document doc) {
    XPathExpression<Element> biblXP = XPathFactory.instance().compile(".//cei:bibl[@type='Dekretale']", Filters.element(), null, CEI_NAMESPACE);
    List<Element> biblList = biblXP.evaluate(doc.getRootElement());
    for (Element bibl : biblList) {
      String key = bibl.getAttributeValue("key");
      String text = bibl.getTextNormalize();
      if (key != null && !key.isBlank()) {
        base.addField("dekretale.key", key);
        base.addField("dekretale.text", text);
        base.addField("dekretale.keytext", key + "||" + text);
      }
    }
  }

  private static void extractManuscriptLinks(SolrInputDocument base, Document doc) {
    XPathExpression<Element> msIdentifierXP = XPathFactory.instance().compile(".//cei:msIdentifier", Filters.element(), null, CEI_NAMESPACE);
    List<Element> manuscriptList = msIdentifierXP.evaluate(doc.getRootElement());
    for (Element manuscriptIdentifier : manuscriptList) {
      String key = manuscriptIdentifier.getAttributeValue("n");
      if (key != null && !key.isBlank()) {
        base.addField("manuscript.key", key);
      }
    }
  }

  @Override
  public SolrInputDocument convertToDocument(MCRObject obj, Regest regest) {
    SolrInputDocument base = super.convertToDocument(obj, regest);

    Optional.ofNullable(regest.getIdno()).ifPresent(idno -> base.setField("idno", idno));

    indexIssuers(regest, base);

    regest.getInitium().forEach(initium -> {
      base.addField("initium", initium);
      base.addField("initium.facet", initium);
    });

    Optional.ofNullable(regest.getUeberlieferungsform()).ifPresent(ueberlieferungsform -> {
      if(ueberlieferungsform.isBlank()) {
        base.setField("ueberlieferungsform", "Keine Angabe");
        base.setField("ueberlieferungsform.facet", "Keine Angabe");
      } else {
        base.setField("ueberlieferungsform", ueberlieferungsform);
        base.setField("ueberlieferungsform.facet", ueberlieferungsform);
      }

    });

    indexRecipient(regest, base);

    Optional.ofNullable(regest.getIssuedPlace()).ifPresent(issuedPlace -> {
      base.setField("issuedPlace", issuedPlace.getLabel());
      base.setField("issuedPlace.obj", issuedPlace.getMycoreId());
      base.addField("place.obj", issuedPlace.getMycoreId());
      base.addField("place", issuedPlace.getLabel());
    });

    for (EntityLink bodyPerson : regest.getBodyPersons()) {
      this.indexEntityLink(base, bodyPerson, "person");
    }

    for (EntityLink bodyOrganization : regest.getBodyOrganizations()) {
      base.addField("organization.obj", bodyOrganization.getMycoreId());
      base.addField("organization", bodyOrganization.getLabel());
    }

    for (EntityLink bodyPlace : regest.getBodyPlaces()) {
      this.indexEntityLink(base, bodyPlace, "place");
    }

    if(regest.getDoi() != null){
      base.setField("identifier.doi", regest.getDoi());
    }

    Optional.ofNullable(regest.getPontifikatAEP()).ifPresent(pontifikatAEP -> {
      base.setField("pontifikatAEP", pontifikatAEP.getLabel());
      base.setField("pontifikatAEP.obj", pontifikatAEP.getMycoreId());
      base.addField("person.obj", pontifikatAEP.getMycoreId());
      base.addField("person", pontifikatAEP.getLabel());
    });

    Optional.ofNullable(regest.getPontifikatPP()).ifPresent(pontifikatPP -> {
      base.setField("pontifikatPP", pontifikatPP.getLabel());
      base.setField("pontifikatPP.obj", pontifikatPP.getMycoreId());
      base.addField("person.obj", pontifikatPP.getMycoreId());
      base.addField("person", pontifikatPP.getLabel());
    });

    Authenticity authenticityStatus = regest.getAuthenticityStatus();
    base.addField("fake", authenticityStatus.isFake());
    base.addField("lost", authenticityStatus.isLost());
    base.addField("certainly", authenticityStatus.isCertainly());


    issuedDate(regest, base);
    bodyXML(obj, base);


    return base;
  }

  private void indexRecipient(Regest regest, SolrInputDocument base) {
    regest.getRecipients().forEach(recipient -> {
      indexEntityLink(base, recipient, "recipient");
    });
  }

  private void indexIssuers(Regest regest, SolrInputDocument base) {
    regest.getIssuers().forEach(issuer -> {
      indexEntityLink(base, issuer, "issuer");
    });
  }

  private void indexEntityLink(SolrInputDocument base, EntityLink entityLink, String prefix) {
    String issuerObjectId = entityLink.getMycoreId();
    MCRObject entityObject = getObject(issuerObjectId);
    if(prefix != null){
      base.addField(prefix, entityLink.getLabel());
      base.addField(prefix+".obj", issuerObjectId);
    }

    if(EntityLink.Type.PERSON.equals(entityLink.getType())) {
      if(entityObject != null) {
        Person person = MetaJSONHelper.getMetaJsonObject(entityObject, "person");
        String displayName = person.getDisplayName();
        if(displayName != null && !displayName.isBlank()) {
          if(prefix != null) {
            base.addField(prefix+".facet", displayName);
          }
          base.addField("person", displayName);
        }
      }

      base.addField("person.obj", issuerObjectId);
      base.addField("person", entityLink.getLabel());
    } else if (EntityLink.Type.ORGANIZATION.equals(entityLink.getType())) {
      if(entityObject != null) {
        Organization organization = MetaJSONHelper.getMetaJsonObject(entityObject, "organization");
        String displayName = organization.getDisplayName();
        if(displayName != null && !displayName.isBlank()) {
          if(prefix != null){
            base.addField(prefix + ".facet", displayName);
          }
          base.addField("organization", displayName);
        }
      }

      base.addField("organization.obj", issuerObjectId);
      base.addField("organization", entityLink.getLabel());
    }
  }

  /**
   * returns the MCRObject for the given idString or null if the idString is not valid
   * @param idString the object id as string
   * @return the MCRObject or null
   */
  private MCRObject getObject(String idString) {
    if(idString == null) {
      return null;
    }

    if(!MCRObjectID.isValid(idString)){
      return null;
    }

    MCRObjectID objectId = MCRObjectID.getInstance(idString);
    return MCRMetadataManager.retrieveMCRObject(objectId);
  }

  private static void issuedDate(Regest regest, SolrInputDocument base) {
    Optional.ofNullable(regest.getIssued()).ifPresent(issued -> {
      String text = issued.getText();
      if (text != null) {
        base.setField("issued.text", text);
      }

      Date from = issued.getFrom();
      Date to = issued.getTo();
      SimpleDateFormat solrFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

      String dateRange;
      if(from != null || to != null){
        if (from != null && to != null) {
          dateRange = MessageFormat.format("[{0} TO {1}]",solrFormat.format(from), solrFormat.format(to));
        } else if (from != null) {
          dateRange = MessageFormat.format("[{0} TO *]", solrFormat.format(from));
        } else {
          dateRange = MessageFormat.format("[* TO {0}]", solrFormat.format(to));
        }
        base.setField("issued.range", dateRange);
      }
    });
  }


}
