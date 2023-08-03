package de.gbv.metadata.pi;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

import de.gbv.metadata.MetaJSONHelper;
import de.gbv.metadata.model.Regest;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.junit.Test;
import org.mycore.common.MCRTestCase;
import org.mycore.datamodel.metadata.MCRObject;
import org.mycore.pi.MCRPersistentIdentifier;
import org.mycore.pi.doi.MCRDOIParser;
import org.mycore.pi.doi.MCRDigitalObjectIdentifier;
import org.mycore.pi.exceptions.MCRPersistentIdentifierException;

import static org.junit.Assert.assertEquals;

public class GPOMetadataServiceTest extends MCRTestCase {

  public MCRObject getTestObject() throws IOException, JDOMException {
    MCRObject mcrObject = new MCRObject(new SAXBuilder().build(new StringReader("<mycoreobject xmlns:xlink=\"http://www.w3.org/1999/xlink\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"datamodel-regest.xsd\" ID=\"gpo_regest_00000001\" version=\"2022.06.2\" label=\"\">\n" +
      "<structure>\n" +
      "<derobjects class=\"MCRMetaEnrichedLinkID\">\n" +
      "<derobject inherited=\"0\" xlink:type=\"locator\" xlink:href=\"gpo_derivate_00000001\">\n" +
      "<order>1</order>\n" +
      "<maindoc/>\n" +
      "<classification classid=\"derivate_types\" categid=\"content\"/>\n" +
      "</derobject>\n" +
      "</derobjects>\n" +
      "</structure>\n" +
      "<metadata>\n" +
      "<def.regest class=\"MetaJSON\" heritable=\"false\" notinherit=\"true\">\n" +
      "<regest inherited=\"0\" class=\"de.gbv.metadata.model.Regest\">{ \"idno\": \"1\", \"issued\": { \"from\": \"0514-01-01T00:00:00.000Z\", \"to\": \"0523-12-31T00:00:00.000Z\", \"text\": \"(514–523)\" }, \"issuer\": [ { \"label\": \"Hormisdas\", \"mycoreId\": \"gpo_person_00000001\", \"type\": \"PERSON\" } ], \"recipient\": [ { \"label\": \"Remigius, Bischof von Reims\", \"mycoreId\": \"gpo_person_00000002\", \"type\": \"PERSON\" } ], \"bodyPersons\": [ { \"label\": \"geistlichen Sohnes Chlodwig\", \"mycoreId\": \"gpo_person_00000003\", \"type\": \"PERSON\" } ], \"bodyOrganizations\": [], \"authenticityStatus\": { \"fake\": true, \"lost\": false, \"certainly\": false }, \"initium\": [ \"Suscipiens plena fraternitatis\" ], \"ueberlieferungsform\": \"\", \"pontifikatPP\": { \"label\": \"Hormisdas (514–523)\", \"mycoreId\": \"gpo_person_00000001\", \"type\": \"PERSON\" }, \"pontifikatAEP\": { \"label\": \"Remigius (459–533)\", \"mycoreId\": \"gpo_person_00000002\", \"type\": \"PERSON\" }, \"bodyPlaces\": [ { \"label\": \"Reich\", \"mycoreId\": \"gpo_place_00000001\", \"type\": \"PLACE\" } ] }</regest>\n" +
      "</def.regest>\n" +
      "</metadata>\n" +
      "<service>\n" +
      "<servdates class=\"MCRMetaISO8601Date\">\n" +
      "<servdate type=\"createdate\" inherited=\"0\">2023-07-28T17:19:43.346Z</servdate>\n" +
      "<servdate type=\"modifydate\" inherited=\"0\">2023-07-28T17:19:44.281Z</servdate>\n" +
      "</servdates>\n" +
      "<servflags class=\"MCRMetaLangText\">\n" +
      "<servflag type=\"createdby\" inherited=\"0\" form=\"plain\">administrator</servflag>\n" +
      "<servflag type=\"modifiedby\" inherited=\"0\" form=\"plain\">administrator</servflag>\n" +
      "</servflags>\n" +
      "<servstates class=\"MCRMetaClassification\">\n" +
      "<servstate inherited=\"0\" classid=\"state\" categid=\"submitted\"/>\n" +
      "</servstates>\n" +
      "</service>\n" +
      "</mycoreobject>")));
    return mcrObject;
  }
  
  @Test
  public void insertIdentifier() throws IOException, JDOMException, MCRPersistentIdentifierException {
    insertIdentifier(null);
  }

  public void insertIdentifier(MCRObject testObj) throws IOException, JDOMException, MCRPersistentIdentifierException {
    MCRObject testObject = testObj==null? getTestObject():testObj;
    GPOMetadataService gpoMetadataService = new GPOMetadataService();
    MCRDigitalObjectIdentifier testDOI = new MCRDOIParser().parse("10.34847/gpo_regest_00000001").get();
    gpoMetadataService.insertIdentifier(testDOI, testObject, "");

    Regest regest = MetaJSONHelper.getMetaJsonObject(testObject, "regest");
    String doi = regest.getDoi();
    
    assertEquals("10.34847/gpo_regest_00000001", doi);
  }

    @Test
    public void getIdentifier() throws IOException, JDOMException, MCRPersistentIdentifierException {
    MCRObject testObject = getTestObject();
    insertIdentifier(testObject);

      Optional<MCRPersistentIdentifier> identifier = new GPOMetadataService().getIdentifier(testObject, "");
      assertEquals("10.34847/gpo_regest_00000001", identifier.get().asString());
    }
}
