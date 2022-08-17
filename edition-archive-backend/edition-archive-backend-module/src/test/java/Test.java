import de.gbv.metadata.CEIImporter;
import de.gbv.metadata.MetaJSONHelper;
import de.gbv.metadata.Regest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.output.XMLOutputter;
import org.mycore.common.MCRTestCase;
import org.mycore.common.MCRUtils;
import org.mycore.common.xml.MCRXMLFunctions;
import org.mycore.common.xml.MCRXMLHelper;
import org.mycore.datamodel.metadata.MCRObject;
import org.mycore.datamodel.metadata.MCRObjectID;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;

import static de.gbv.metadata.CEIImporter.createMetadata;

public class Test extends MCRTestCase {
    private static final Logger LOGGER = LogManager.getLogger();

    @org.junit.Test
    public void testTest() throws IOException, JDOMException {
        /*
        CEIImporter ceiImporter = new CEIImporter(Paths.get("/home/sebastian/workspace.reposis/digitaledition-archive/import/formatted-00-regesten_gesamt_red_v-02.xml"));
        XMLOutputter xmlOutputter = new XMLOutputter();

        while (ceiImporter.hasNext()) {
            Regest next = ceiImporter.next();
            MCRObject object = new MCRObject();
            object.setId(MCRObjectID.getNextFreeId("gpo", "regest"));
            object.setSchema("datamodel-regest.xsd");

            Element metadata = createMetadata(next);
            object.getMetadata().setFromDOM(metadata);

            Regest regest = MetaJSONHelper.getMetaJsonObject(object, Regest.TAG_NAME);
            Document xml = object.createXML();
            String s = xmlOutputter.outputString(xml);
            LOGGER.info(s);
        }*/
    }

    @Override
    protected Map<String, String> getTestProperties() {
        Map<String, String> testProperties = super.getTestProperties();

        testProperties.put("MCR.Metadata.JSON.Type.AllowedClasses","de.gbv.metadata.Regest");

        return testProperties;
    }
}
